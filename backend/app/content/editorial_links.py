"""Verified game-roster to Comic Vine character IDs (audited 2026-10-04)."""

ROSTER_COMIC_VINE_IDS: dict[str, int] = {
    "homem-de-ferro": 1455,
    "capitao-america": 1442,
    "thor": 2268,
    "hulk": 2267,
    "feiticeira-escarlate": 1466,
    "pantera-negra": 1477,
    "homem-aranha": 1443,
    "doutor-estranho": 1456,
    "wolverine": 1440,
    "ciclope": 1459,
    "jean-grey": 3552,
    "professor-xavier": 1505,
    "senhor-fantastico": 2151,
    "mulher-invisivel": 2190,
    "tocha-humana": 2120,
    "coisa": 2114,
    "rocket-raccoon": 32814,
    "groot": 24341,
    "surfista-prateado": 2502,
    "loki": 4324,
    "deadpool": 7606,
}

# Opponents are editorial illustrations, never unlockable roster members.
BATTLE_COMIC_VINE_IDS: dict[str, int] = {
    "magneto": 1441,
    "master-mold": 10254,
    "doombot": 89418,
    "doutor-destino": 1468,
}
