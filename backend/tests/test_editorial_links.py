from fastapi.testclient import TestClient

import app.main as main
from app.content.characters import GAME_CHARACTERS
from app.content.editorial_links import (BATTLE_COMIC_VINE_IDS, ROSTER_COMIC_VINE_IDS,
                                         VARIANT_COVER_QUERIES)
from app.services.comic_vine import ComicVineError


client = TestClient(main.app)


def test_every_game_character_has_one_unique_verified_editorial_link():
    assert set(ROSTER_COMIC_VINE_IDS) == {item.id for item in GAME_CHARACTERS.items}
    assert len(set(ROSTER_COMIC_VINE_IDS.values())) == 21
    assert set(VARIANT_COVER_QUERIES) == set(ROSTER_COMIC_VINE_IDS)
    assert all(character_id > 0 for character_id in ROSTER_COMIC_VINE_IDS.values())


def test_game_character_portrait_uses_editorial_image_and_attribution(monkeypatch):
    captured = []

    def detail(character_id):
        captured.append(character_id)
        return {
            "id": character_id,
            "name": "Wolverine",
            "image_url": "https://comicvine.gamespot.com/a/uploads/original/wolverine.jpg",
            "site_url": "https://comicvine.gamespot.com/wolverine/4005-1440/",
        }

    monkeypatch.setattr(main.comic_vine, "get_character", detail)
    response = client.get("/v1/editorial/game-characters/wolverine")
    assert response.status_code == 200
    assert captured == [1440]
    assert response.json() == {
        "game_id": "wolverine",
        "character_id": 1440,
        "character_name": "Wolverine",
        "image_url": "https://comicvine.gamespot.com/a/uploads/original/wolverine.jpg",
        "site_url": "https://comicvine.gamespot.com/wolverine/4005-1440/",
        "source_name": "Comic Vine",
        "image_credit": None,
    }


def test_variant_cover_uses_distinct_issue_and_falls_back_to_character(monkeypatch):
    monkeypatch.setattr(main.comic_vine, "get_character", lambda character_id: {
        "id": character_id, "name": "Wolverine",
        "image_url": "https://comicvine.gamespot.com/a/origin.jpg",
        "site_url": "https://comicvine.gamespot.com/wolverine/4005-1440/",
    })
    monkeypatch.setattr(main.comic_vine, "find_issue_covers", lambda query: ({
        "image_url": "https://comicvine.gamespot.com/a/issue-1.jpg",
        "site_url": "https://comicvine.gamespot.com/wolverine-1/4000-1/",
        "image_credit": "Wolverine #1",
    },))
    origin = client.get("/v1/editorial/game-characters/wolverine/variants/origin")
    ascension = client.get("/v1/editorial/game-characters/wolverine/variants/ascension")
    legendary = client.get("/v1/editorial/game-characters/wolverine/variants/legendary")
    assert origin.status_code == ascension.status_code == legendary.status_code == 200
    assert origin.json()["image_url"] != ascension.json()["image_url"]
    assert ascension.json()["image_credit"] == "Wolverine #1"
    assert legendary.json()["image_url"] == origin.json()["image_url"]
    assert client.get("/v1/editorial/game-characters/wolverine/variants/invalid").status_code == 404


def test_wakanda_uses_credited_scene_cover_without_claiming_a_character(monkeypatch):
    monkeypatch.setattr(main.comic_vine, "find_issue_covers", lambda title: ({
        "image_url": "https://comicvine.gamespot.com/a/wakanda.jpg",
        "site_url": "https://comicvine.gamespot.com/wakanda/4000-3/",
        "image_credit": "Wakanda #3",
    },))
    response = client.get("/v1/editorial/battle-opponents/wakanda-tech")
    assert response.status_code == 200
    assert response.json()["character_id"] is None
    assert response.json()["image_credit"] == "Wakanda #3"


def test_unknown_id_stops_before_upstream(monkeypatch):
    monkeypatch.setattr(main.comic_vine, "get_character",
                        lambda _id: (_ for _ in ()).throw(AssertionError("must not call")))
    response = client.get("/v1/editorial/game-characters/not-in-roster")
    assert response.status_code == 404


def test_non_marvel_or_unavailable_portrait_is_sanitized(monkeypatch):
    monkeypatch.setattr(main.comic_vine, "get_character",
                        lambda _id: (_ for _ in ()).throw(ComicVineError("not_marvel", "outside Marvel")))
    response = client.get("/v1/editorial/game-characters/wolverine")
    assert response.status_code == 404
    assert response.json()["detail"]["code"] == "not_marvel"


def test_battle_opponent_portraits_are_whitelisted_and_separate_from_roster(monkeypatch):
    assert set(BATTLE_COMIC_VINE_IDS).isdisjoint(ROSTER_COMIC_VINE_IDS)
    captured = []

    def detail(character_id):
        captured.append(character_id)
        return {
            "id": character_id,
            "name": "Magneto",
            "image_url": "https://comicvine.gamespot.com/a/uploads/original/magneto.jpg",
            "site_url": "https://comicvine.gamespot.com/magneto/4005-1441/",
        }

    monkeypatch.setattr(main.comic_vine, "get_character", detail)
    response = client.get("/v1/editorial/battle-opponents/magneto")
    assert response.status_code == 200
    assert response.json()["character_id"] == 1441
    assert captured == [1441]
    assert client.get("/v1/editorial/battle-opponents/not-a-boss").status_code == 404
    assert captured == [1441]
