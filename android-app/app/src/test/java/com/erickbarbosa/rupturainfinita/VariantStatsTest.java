package com.erickbarbosa.rupturainfinita;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

public final class VariantStatsTest {
    @Test public void publishedScaleAppliesToAllFiveTiers() {
        assertEquals(21, VariantStats.count());
        assertArrayEquals(new int[]{1010, 370, 260, 260},
                VariantStats.forVariant("wolverine", GameVariantTier.ORIGIN));
        assertArrayEquals(new int[]{1091, 400, 281, 281},
                VariantStats.forVariant("wolverine", GameVariantTier.ASCENSION));
        assertArrayEquals(new int[]{1182, 433, 304, 304},
                VariantStats.forVariant("wolverine", GameVariantTier.LEGENDARY));
        assertArrayEquals(new int[]{1293, 474, 333, 333},
                VariantStats.forVariant("wolverine", GameVariantTier.MULTIVERSAL));
        assertArrayEquals(new int[]{1414, 518, 364, 364},
                VariantStats.forVariant("wolverine", GameVariantTier.INFINITY));
        assertThrows(IllegalArgumentException.class,
                () -> VariantStats.forVariant("unknown", GameVariantTier.ORIGIN));
    }
}
