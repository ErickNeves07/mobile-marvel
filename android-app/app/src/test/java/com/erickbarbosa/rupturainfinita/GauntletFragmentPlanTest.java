package com.erickbarbosa.rupturainfinita;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class GauntletFragmentPlanTest {
    @Test public void threeStarterFragmentsNeedOneMore() {
        assertEquals(1, GauntletFragmentPlan.missing(0, 3, 0, 0));
    }

    @Test public void existingMaterialsReduceTheGrant() {
        assertEquals(1, GauntletFragmentPlan.missing(2, 2, 0, 0));
        assertEquals(0, GauntletFragmentPlan.missing(0, 0, 2, 0));
        assertEquals(0, GauntletFragmentPlan.missing(0, 0, 0, 1));
        assertEquals(0, GauntletFragmentPlan.missing(0, 3, 1, 0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNegativeInventory() {
        GauntletFragmentPlan.missing(-1, 0, 0, 0);
    }
}
