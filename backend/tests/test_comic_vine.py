import json
from urllib.error import HTTPError
from urllib.parse import parse_qs, urlparse

import pytest

from app.services.comic_vine import ComicVineError, ComicVineGateway


class Clock:
    def __init__(self):
        self.value = 1000.0

    def now(self):
        return self.value

    def sleep(self, delay):
        self.value += delay


class Response:
    def __init__(self, body):
        self.body = json.dumps(body).encode() if not isinstance(body, bytes) else body

    def __enter__(self):
        return self

    def __exit__(self, *_args):
        return False

    def read(self, _size=-1):
        return self.body


def upstream(results, total=1):
    return {"status_code": 1, "number_of_total_results": total, "results": results}


def character(character_id=123, publisher_id=31, publisher_name="Marvel"):
    return {
        "id": character_id,
        "name": "Iron Man",
        "deck": "A hero.",
        "description": "Editorial text.",
        "image": {"original_url": "https://images.example.test/iron.jpg"},
        "site_detail_url": "https://comicvine.gamespot.com/iron-man/4005-123/",
        "real_name": "Tony Stark",
        "publisher": {"id": publisher_id, "name": publisher_name},
        "powers": [{"name": "Powered Armor"}],
        "teams": [{"name": "Avengers"}],
    }


def test_missing_key_fails_before_transport():
    called = False

    def transport(*_args, **_kwargs):
        nonlocal called
        called = True

    gateway = ComicVineGateway(transport, key_provider=lambda: "")
    with pytest.raises(ComicVineError, match="not configured"):
        gateway.search_characters("Iron Man")
    assert not called


def test_search_uses_documented_contract_and_filters_via_publisher_detail():
    clock = Clock()
    calls = []

    def transport(request, timeout):
        url = urlparse(request.full_url)
        query = parse_qs(url.query)
        calls.append((url.path, query, request.get_header("User-agent"), timeout))
        if url.path.endswith("/search/"):
            return Response(upstream([{"id": 123, "name": "Iron Man", "resource_type": "character"}]))
        return Response(upstream(character()))

    gateway = ComicVineGateway(transport, clock=clock.now, sleep=clock.sleep,
                               key_provider=lambda: "never-log-this", cache_ttl=30)
    result = gateway.search_characters("Iron Man", limit=2, offset=4)

    assert [path for path, *_ in calls] == ["/api/search/", "/api/character/4005-123/"]
    assert calls[0][1]["api_key"] == ["never-log-this"]
    assert calls[0][1]["format"] == ["json"]
    assert calls[0][1]["resources"] == ["character"]
    assert calls[0][1]["limit"] == ["2"] and calls[0][1]["offset"] == ["4"]
    assert "publisher" in calls[1][1]["field_list"][0]
    assert all(call[2] and call[3] == 15 for call in calls)
    assert result["items"][0]["publisher_id"] == 31
    assert result["items"][0]["teams"] == ("Avengers",)
    assert clock.value >= 1001


@pytest.mark.parametrize("publisher_id,publisher_name", [(7, "Other Publisher"),
                                                   (31, "Marvel"), (0, "Marvel"),
                                                   (0, "Marvel Comics")])
def test_only_marvel_publisher_id_or_name_is_accepted(publisher_id, publisher_name):
    gateway = ComicVineGateway(
        lambda request, timeout: Response(
            upstream(character(publisher_id=publisher_id, publisher_name=publisher_name))
        ),
        key_provider=lambda: "test-key",
    )
    if publisher_id == 31 or publisher_name in ("Marvel", "Marvel Comics"):
        assert gateway.get_character(123)["publisher_name"] == publisher_name
    else:
        with pytest.raises(ComicVineError, match="outside the Marvel"):
            gateway.get_character(123)


def test_cache_prevents_duplicate_upstream_calls():
    calls = 0
    clock = Clock()

    def transport(_request, _timeout):
        nonlocal calls
        calls += 1
        if calls == 1:
            return Response(upstream([{"id": 123, "name": "Iron Man", "resource_type": "character"}]))
        return Response(upstream(character()))

    gateway = ComicVineGateway(transport, clock=clock.now, sleep=clock.sleep,
                               key_provider=lambda: "test-key")
    first = gateway.search_characters("Iron Man")
    second = gateway.search_characters("Iron Man")
    assert first == second
    assert calls == 2


def test_issue_covers_require_solo_volume_comic_vine_urls_and_unique_images():
    calls = 0

    def issue(volume, number, image, site):
        return {"id": number, "resource_type": "issue", "volume": {"name": volume},
                "issue_number": str(number), "image": {"original_url": image},
                "site_detail_url": site}

    def transport(_request, _timeout):
        nonlocal calls
        calls += 1
        return Response(upstream([
            issue("Wolverine", 1, "https://comicvine.gamespot.com/a/1.jpg",
                  "https://comicvine.gamespot.com/wolverine/4000-1/"),
            issue("Wolverine", 2, "https://comicvine.gamespot.com/a/1.jpg",
                  "https://comicvine.gamespot.com/wolverine/4000-2/"),
            issue("Wolverine", 3, "https://evil.example/a/3.jpg",
                  "https://comicvine.gamespot.com/wolverine/4000-3/"),
            issue("Wolverine and the X-Men", 4, "https://comicvine.gamespot.com/a/4.jpg",
                  "https://comicvine.gamespot.com/wolverine/4000-4/"),
            issue("X-Men", 5, "https://comicvine.gamespot.com/a/5.jpg",
                  "https://comicvine.gamespot.com/x-men/4000-5/"),
        ]))

    gateway = ComicVineGateway(transport, key_provider=lambda: "private")
    covers = gateway.find_issue_covers("Wolverine")
    assert len(covers) == 2
    assert covers[0]["image_credit"] == "Wolverine #1"
    assert covers[1]["image_credit"] == "Wolverine and the X-Men #4"
    assert gateway.find_issue_covers("Wolverine") == covers
    assert calls == 1


def test_upstream_429_is_sanitized_and_does_not_leak_url_or_key():
    secret = "private-api-key"

    def transport(request, _timeout):
        raise HTTPError(request.full_url, 429, "blocked", {}, None)

    gateway = ComicVineGateway(transport, key_provider=lambda: secret)
    with pytest.raises(ComicVineError) as error:
        gateway.search_characters("Iron Man")
    assert error.value.code == "rate_limited"
    assert secret not in str(error.value)


def test_request_budget_is_enforced_per_resource():
    clock = Clock()
    gateway = ComicVineGateway(lambda *_args: None, clock=clock.now, sleep=clock.sleep,
                               key_provider=lambda: "test-key", max_per_hour=1)
    gateway._reserve("characters")
    with pytest.raises(ComicVineError) as error:
        gateway._reserve("characters")
    assert error.value.code == "rate_limited"


def test_global_throttle_spaces_different_upstream_resources():
    clock = Clock()
    gateway = ComicVineGateway(lambda *_args, **_kwargs: None, clock=clock.now, sleep=clock.sleep,
                               key_provider=lambda: "test-key")
    gateway._reserve("search")
    gateway._reserve("characters")
    assert clock.value == 1001


@pytest.mark.parametrize("query,limit,offset", [("x", 1, 0), ("Iron Man", 11, 0), ("Iron Man", 5, -1)])
def test_invalid_search_bounds_fail_locally(query, limit, offset):
    gateway = ComicVineGateway(key_provider=lambda: "test-key")
    with pytest.raises(ComicVineError):
        gateway.search_characters(query, limit, offset)
