package com.erickbarbosa.rupturainfinita;

import java.util.EnumSet;
import java.util.Set;

final class VariantProgression {
    private VariantProgression() { }

    static GameVariantTier next(Set<GameVariantTier> owned) {
        for (GameVariantTier tier : GameVariantTier.values()) if (!owned.contains(tier)) return tier;
        return null;
    }

    static Set<GameVariantTier> withOrigin() {
        return EnumSet.of(GameVariantTier.ORIGIN);
    }

    static boolean contains(Set<GameVariantTier> owned, GameVariantTier tier) {
        return owned != null && owned.contains(tier);
    }
}
