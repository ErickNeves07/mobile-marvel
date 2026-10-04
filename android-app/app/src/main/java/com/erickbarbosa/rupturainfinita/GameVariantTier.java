package com.erickbarbosa.rupturainfinita;

enum GameVariantTier {
    ORIGIN("origin", R.string.catalog_tier_origin, InfinityStone.SPACE),
    ASCENSION("ascension", R.string.catalog_tier_ascension, InfinityStone.MIND),
    LEGENDARY("legendary", R.string.catalog_tier_legendary, InfinityStone.REALITY),
    MULTIVERSAL("multiversal", R.string.catalog_tier_multiversal, InfinityStone.POWER),
    INFINITY("infinity", R.string.catalog_tier_infinity, InfinityStone.TIME);

    final String id;
    final int labelRes;
    final InfinityStone requiredStone;

    GameVariantTier(String id, int labelRes, InfinityStone requiredStone) {
        this.id = id;
        this.labelRes = labelRes;
        this.requiredStone = requiredStone;
    }
}
