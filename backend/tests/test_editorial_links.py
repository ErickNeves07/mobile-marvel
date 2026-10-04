from fastapi.testclient import TestClient

import app.main as main
from app.content.characters import GAME_CHARACTERS
from app.content.editorial_links import ROSTER_COMIC_VINE_IDS
from app.services.comic_vine import ComicVineError


client = TestClient(main.app)


def test_every_game_character_has_one_unique_verified_editorial_link():
    assert set(ROSTER_COMIC_VINE_IDS) == {item.id for item in GAME_CHARACTERS.items}
    assert len(set(ROSTER_COMIC_VINE_IDS.values())) == 21
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
    }


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
