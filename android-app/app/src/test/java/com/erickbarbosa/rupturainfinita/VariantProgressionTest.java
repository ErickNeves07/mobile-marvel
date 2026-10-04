package com.erickbarbosa.rupturainfinita;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.EnumSet;
import org.junit.Test;

public final class VariantProgressionTest {
    @Test public void ownershipAdvancesExactlyOneTierAtATime() {
        EnumSet<GameVariantTier> owned = EnumSet.of(GameVariantTier.ORIGIN);
        assertEquals(GameVariantTier.ASCENSION, VariantProgression.next(owned));
        owned.add(GameVariantTier.ASCENSION);
        assertEquals(GameVariantTier.LEGENDARY, VariantProgression.next(owned));
        owned.add(GameVariantTier.LEGENDARY);
        owned.add(GameVariantTier.MULTIVERSAL);
        owned.add(GameVariantTier.INFINITY);
        assertNull(VariantProgression.next(owned));
    }

    @Test public void tierStoneMappingUsesFiveCatalogStagesAndLeavesSoulForGauntlet() {
        assertEquals(InfinityStone.SPACE, GameVariantTier.ORIGIN.requiredStone);
        assertEquals(InfinityStone.MIND, GameVariantTier.ASCENSION.requiredStone);
        assertEquals(InfinityStone.REALITY, GameVariantTier.LEGENDARY.requiredStone);
        assertEquals(InfinityStone.POWER, GameVariantTier.MULTIVERSAL.requiredStone);
        assertEquals(InfinityStone.TIME, GameVariantTier.INFINITY.requiredStone);
        assertTrue(java.util.EnumSet.allOf(InfinityStone.class).contains(InfinityStone.SOUL));
    }
}
