"""Bounded, cached Comic Vine gateway with server-side Marvel validation."""

from __future__ import annotations

import json
import os
import re
import threading
import time
import unicodedata
from collections import deque
from dataclasses import dataclass
from typing import Callable
from urllib.error import HTTPError, URLError
from urllib.parse import urlencode
from urllib.parse import urlsplit
from urllib.request import Request, urlopen

BASE_URL = "https://comicvine.gamespot.com/api/"
USER_AGENT = "Marvel-Ruptura-Infinita/0.2 (non-commercial; editorial attribution: Comic Vine)"
SEARCH_FIELDS = "id,name,image,site_detail_url"
DETAIL_FIELDS = "id,name,deck,description,image,site_detail_url,real_name,publisher,powers,teams,count_of_issue_appearances,first_appeared_in_issue"
ISSUE_COVER_FIELDS = "id,name,image,volume,issue_number,site_detail_url"
TIMEOUT_SECONDS = 15
MAX_SEARCH_LIMIT = 10
MAX_OFFSET = 10_000
MAX_PER_RESOURCE_HOUR = 100
MIN_REQUEST_INTERVAL_SECONDS = 1.0
CACHE_TTL_SECONDS = 600


class ComicVineError(RuntimeError):
    def __init__(self, code: str, message: str):
        super().__init__(message)
        self.code = code


@dataclass(frozen=True)
class _Cached:
    expires_at: float
    value: object


class ComicVineGateway:
    def __init__(
        self,
        transport: Callable = urlopen,
        *,
        clock: Callable[[], float] = time.monotonic,
        sleep: Callable[[float], None] = time.sleep,
        key_provider: Callable[[], str] | None = None,
        cache_ttl: float = CACHE_TTL_SECONDS,
        max_per_hour: int = MAX_PER_RESOURCE_HOUR,
        min_interval: float = MIN_REQUEST_INTERVAL_SECONDS,
    ) -> None:
        self._transport = transport
        self._clock = clock
        self._sleep = sleep
        self._key_provider = key_provider or (lambda: os.environ.get("COMIC_VINE_API_KEY", ""))
        self._cache_ttl = cache_ttl
        self._max_per_hour = max_per_hour
        self._min_interval = min_interval
        self._cache: dict[tuple[object, ...], _Cached] = {}
        self._calls: dict[str, deque[float]] = {}
        self._last_call = float("-inf")
        self._lock = threading.RLock()

    def search_characters(self, query: str, limit: int = 5, offset: int = 0) -> dict:
        query = query.strip()
        if len(query) < 2 or len(query) > 100:
            raise ComicVineError("invalid_query", "Search query must contain 2–100 characters")
        if not 1 <= limit <= MAX_SEARCH_LIMIT or not 0 <= offset <= MAX_OFFSET:
            raise ComicVineError("invalid_pagination", "Pagination is outside the supported range")
        cache_key = ("search", query.casefold(), limit, offset)
        cached = self._get_cached(cache_key)
        if cached is not None:
            return cached
        payload, _ = self._request(
            "search",
            "search/",
            {
                "resources": "character",
                "query": query,
                "limit": limit,
                "offset": offset,
                "field_list": SEARCH_FIELDS,
            },
        )
        raw_items = payload.get("results")
        if not isinstance(raw_items, list):
            raise ComicVineError("invalid_upstream", "Comic Vine returned an invalid results list")
        total = payload.get("number_of_total_results", 0)
        if not isinstance(total, int) or total < 0:
            total = 0
        accepted = []
        for item in raw_items:
            if not isinstance(item, dict) or item.get("resource_type") not in (None, "character"):
                continue
            character_id = item.get("id")
            if not isinstance(character_id, int) or character_id <= 0:
                continue
            candidate_publisher = item.get("publisher")
            if isinstance(candidate_publisher, dict) and not self._is_marvel(
                    candidate_publisher.get("id", 0) if isinstance(candidate_publisher.get("id"), int) else 0,
                    candidate_publisher.get("name", "") if isinstance(candidate_publisher.get("name"), str) else ""):
                continue
            try:
                detail = self.get_character(character_id)
            except ComicVineError as exc:
                if exc.code == "not_marvel":
                    continue
                raise
            accepted.append(detail)
        result = {"items": tuple(accepted), "total": total, "limit": limit, "offset": offset}
        self._set_cached(cache_key, result)
        return result

    def get_character(self, character_id: int) -> dict:
        if not isinstance(character_id, int) or character_id <= 0:
            raise ComicVineError("invalid_id", "Character ID must be a positive integer")
        cache_key = ("character", character_id)
        cached = self._get_cached(cache_key)
        if cached is not None:
            return cached
        payload, _ = self._request(
            "characters",
            f"character/4005-{character_id}/",
            {"field_list": DETAIL_FIELDS},
        )
        raw = payload.get("results")
        if not isinstance(raw, dict):
            raise ComicVineError("not_found", "Comic Vine character was not found")
        character = self._map_character(raw)
        if not self._is_marvel(character["publisher_id"], character["publisher_name"]):
            raise ComicVineError("not_marvel", "Character is outside the Marvel Comics catalog")
        self._set_cached(cache_key, character)
        return character

    def find_issue_covers(self, solo_title: str) -> tuple[dict, ...]:
        if not isinstance(solo_title, str) or not 2 <= len(solo_title.strip()) <= 100:
            raise ComicVineError("invalid_query", "Solo title is invalid")
        title = solo_title.strip()
        cache_key = ("issue-covers", title.casefold())
        cached = self._get_cached(cache_key)
        if cached is not None:
            return cached
        payload, _ = self._request("search", "search/", {
            "resources": "issue",
            "query": title,
            "limit": 40,
            "field_list": ISSUE_COVER_FIELDS,
        })
        raw_items = payload.get("results")
        if not isinstance(raw_items, list):
            raise ComicVineError("invalid_upstream", "Comic Vine returned invalid issues")
        covers: list[dict] = []
        seen_images: set[str] = set()
        for item in raw_items:
            if not isinstance(item, dict) or item.get("resource_type") not in (None, "issue"):
                continue
            volume = item.get("volume")
            volume_name = volume.get("name", "") if isinstance(volume, dict) else ""
            if not isinstance(volume_name, str) or not volume_name.casefold().startswith(title.casefold()):
                continue
            image = item.get("image")
            image_url = (image.get("original_url") or image.get("medium_url")) if isinstance(image, dict) else None
            site_url = item.get("site_detail_url")
            if not self._comic_vine_https_url(image_url) or not self._comic_vine_https_url(site_url):
                continue
            if image_url in seen_images:
                continue
            seen_images.add(image_url)
            issue_number = item.get("issue_number")
            credit = volume_name.strip()[:160]
            if isinstance(issue_number, (str, int)) and str(issue_number).strip():
                credit += " #" + str(issue_number).strip()[:20]
            covers.append({"image_url": image_url, "site_url": site_url,
                           "image_credit": credit[:200]})
            if len(covers) == 4:
                break
        result = tuple(covers)
        self._set_cached(cache_key, result)
        return result

    @staticmethod
    def _comic_vine_https_url(value: object) -> bool:
        if not isinstance(value, str):
            return False
        parsed = urlsplit(value)
        return parsed.scheme == "https" and parsed.hostname == "comicvine.gamespot.com" \
            and parsed.username is None and parsed.password is None

    def _request(self, resource: str, path: str, params: dict) -> tuple[dict, str]:
        api_key = self._key_provider().strip()
        if not api_key:
            raise ComicVineError("not_configured", "Comic Vine provider is not configured")
        query = {"api_key": api_key, "format": "json", **params}
        url = BASE_URL + path + "?" + urlencode(query)
        self._reserve(resource)
        request = Request(url, headers={"User-Agent": USER_AGENT, "Accept": "application/json"})
        try:
            try:
                response_context = self._transport(request, timeout=TIMEOUT_SECONDS)
            except TypeError as exc:
                if "unexpected keyword argument 'timeout'" not in str(exc):
                    raise
                response_context = self._transport(request, TIMEOUT_SECONDS)
            with response_context as response:
                body = response.read(2_000_001)
            if len(body) > 2_000_000:
                raise ComicVineError("upstream_too_large", "Comic Vine response exceeded the size limit")
            payload = json.loads(body)
        except ComicVineError:
            raise
        except HTTPError as exc:
            code = "rate_limited" if exc.code == 429 else "upstream_unavailable"
            raise ComicVineError(code, "Comic Vine request failed") from None
        except (URLError, TimeoutError, OSError, ValueError):
            raise ComicVineError("upstream_unavailable", "Comic Vine request failed") from None
        if not isinstance(payload, dict) or payload.get("status_code") != 1:
            status_code = payload.get("status_code") if isinstance(payload, dict) else None
            if status_code == 100:
                raise ComicVineError("upstream_auth", "Comic Vine rejected the configured API key")
            if status_code == 104:
                raise ComicVineError("upstream_filter", "Comic Vine rejected the search request")
            raise ComicVineError("invalid_upstream", "Comic Vine returned an invalid response")
        return payload, BASE_URL + path

    def _reserve(self, resource: str) -> None:
        with self._lock:
            now = self._clock()
            calls = self._calls.setdefault(resource, deque())
            while calls and calls[0] <= now - 3600:
                calls.popleft()
            if len(calls) >= self._max_per_hour:
                raise ComicVineError("rate_limited", "Comic Vine request budget is exhausted")
            wait = max(0.0, self._last_call + self._min_interval - now)
            if wait:
                self._sleep(wait)
                now = self._clock()
            calls.append(now)
            self._last_call = now

    def _get_cached(self, key: tuple[object, ...]) -> object | None:
        with self._lock:
            item = self._cache.get(key)
            if item and item.expires_at > self._clock():
                return item.value
            self._cache.pop(key, None)
            return None

    def _set_cached(self, key: tuple[object, ...], value: object) -> None:
        with self._lock:
            self._cache[key] = _Cached(self._clock() + self._cache_ttl, value)

    @classmethod
    def _map_character(cls, raw: dict) -> dict:
        character_id, name, publisher = raw.get("id"), raw.get("name"), raw.get("publisher")
        if not isinstance(character_id, int) or character_id <= 0 or not isinstance(name, str) or not name.strip():
            raise ComicVineError("invalid_upstream", "Comic Vine character data is invalid")
        if not isinstance(publisher, dict) or not isinstance(publisher.get("name"), str):
            raise ComicVineError("invalid_upstream", "Comic Vine character publisher is missing")
        publisher_id = publisher.get("id")
        if not isinstance(publisher_id, int):
            publisher_id = 0
        publisher_name = publisher["name"].strip()[:200]
        if not publisher_name:
            raise ComicVineError("invalid_upstream", "Comic Vine character publisher is missing")
        image = raw.get("image") if isinstance(raw.get("image"), dict) else {}
        powers = cls._names(raw.get("powers"))
        teams = cls._names(raw.get("teams"))
        issue_count = raw.get("count_of_issue_appearances")
        if not isinstance(issue_count, int) or issue_count < 0:
            issue_count = None
        first_issue = raw.get("first_appeared_in_issue")
        first_appearance = None
        if isinstance(first_issue, dict):
            first_appearance = cls._optional_text(first_issue.get("cover_date"), 40)
        site_url = raw.get("site_detail_url")
        parsed_site = urlsplit(site_url) if isinstance(site_url, str) else None
        if parsed_site is None or parsed_site.scheme != "https" or parsed_site.hostname != "comicvine.gamespot.com":
            site_url = f"https://comicvine.gamespot.com/characters/{character_id}/"
        image_url = image.get("original_url") or image.get("medium_url")
        parsed_image = urlsplit(image_url) if isinstance(image_url, str) else None
        if parsed_image is None or parsed_image.scheme != "https":
            image_url = None
        return {
            "id": character_id,
            "name": name.strip()[:200],
            "deck": cls._optional_text(raw.get("deck"), 2_000),
            "description": cls._optional_text(raw.get("description"), 20_000),
            "image_url": image_url,
            "site_url": (site_url if "comicvine.gamespot.com" in site_url.casefold() else
                         f"https://comicvine.gamespot.com/characters/{character_id}/")[:2_000],
            "real_name": cls._optional_text(raw.get("real_name"), 200),
            "publisher_id": publisher_id,
            "publisher_name": publisher_name,
            "powers": powers,
            "teams": teams,
            "issue_count": issue_count,
            "first_appearance": first_appearance,
        }

    @staticmethod
    def _names(value: object) -> tuple[str, ...]:
        if not isinstance(value, list):
            return ()
        return tuple(
            str(item["name"]).strip()[:200]
            for item in value[:30]
            if isinstance(item, dict) and isinstance(item.get("name"), str) and item["name"].strip()
        )

    @staticmethod
    def _optional_text(value: object, limit: int) -> str | None:
        if not isinstance(value, str) or not value.strip():
            return None
        return value.strip()[:limit]

    @staticmethod
    def _is_marvel(publisher_id: int, publisher_name: str) -> bool:
        normalized = unicodedata.normalize("NFKD", publisher_name)
        normalized = "".join(char for char in normalized if not unicodedata.combining(char))
        normalized = re.sub(r"\s+", " ", normalized).strip().casefold()
        return publisher_id == 31 or normalized in ("marvel", "marvel comics")
