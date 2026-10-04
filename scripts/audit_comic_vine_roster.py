"""Audit game-roster links using the server-side Comic Vine gateway.

Run with COMIC_VINE_API_KEY already in the environment. The script never prints it.
"""

from __future__ import annotations

import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1] / "backend"))

from app.services.comic_vine import ComicVineError, ComicVineGateway

QUERIES = {
    "homem-de-ferro": "Iron Man",
    "capitao-america": "Captain America",
    "thor": "Thor",
    "hulk": "Hulk",
    "feiticeira-escarlate": "Scarlet Witch",
    "pantera-negra": "Black Panther",
    "homem-aranha": "Spider-Man",
    "doutor-estranho": "Doctor Strange",
    "wolverine": "Wolverine",
    "ciclope": "Cyclops",
    "jean-grey": "Jean Grey",
    "professor-xavier": "Professor X",
    "senhor-fantastico": "Mr. Fantastic",
    "mulher-invisivel": "Invisible Woman",
    "tocha-humana": "Human Torch",
    "coisa": "The Thing",
    "rocket-raccoon": "Rocket Raccoon",
    "groot": "Groot",
    "surfista-prateado": "Silver Surfer",
    "loki": "Loki",
    "deadpool": "Deadpool",
}


def main() -> None:
    gateway = ComicVineGateway()
    for game_id, query in QUERIES.items():
        try:
            items = gateway.search_characters(query, limit=5 if game_id == "loki" else 1)["items"]
            if not items:
                print(f"{game_id}\tNO_MARVEL_RESULT")
                continue
            for result in items:
                print(f"{game_id}\t{result['id']}\t{result['name']}\t"
                      f"{result['publisher_name']}\timage={bool(result['image_url'])}")
        except ComicVineError as error:
            print(f"{game_id}\tERROR:{error.code}")


if __name__ == "__main__":
    main()
