package com.erickbarbosa.rupturainfinita;

/** Authored fragment prices and cumulative XP gates. XP unlocks listings; it is never spent. */
final class FragmentShopOffer {
    final InfinityStone stone;
    final int minimumXp;
    final long priceCredits;

    private FragmentShopOffer(InfinityStone stone, int minimumXp, long priceCredits) {
        this.stone = stone;
        this.minimumXp = minimumXp;
        this.priceCredits = priceCredits;
    }

    static FragmentShopOffer forStone(InfinityStone stone) {
        if (stone == null) throw new IllegalArgumentException("Stone is required");
        switch (stone) {
            case SPACE: return new FragmentShopOffer(stone, 0, 800);
            case MIND: return new FragmentShopOffer(stone, 840, 900);
            case REALITY: return new FragmentShopOffer(stone, 1_760, 1_000);
            case POWER: return new FragmentShopOffer(stone, 2_860, 1_100);
            case TIME: return new FragmentShopOffer(stone, 3_720, 1_200);
            case SOUL: return new FragmentShopOffer(stone, 4_720, 1_300);
            default: throw new IllegalArgumentException("Unknown stone");
        }
    }

    boolean isUnlocked(long xp) { return xp >= minimumXp; }
}
