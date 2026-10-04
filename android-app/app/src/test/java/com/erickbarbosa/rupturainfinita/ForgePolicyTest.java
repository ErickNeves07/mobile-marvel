package com.erickbarbosa.rupturainfinita;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

public final class ForgePolicyTest {
    @Test
    public void eachNonTerminalStageHasNextStage() {
        assertEquals(ForgeStage.FRAGMENT, ForgePolicy.outputFor(ForgeStage.SHARD));
        assertEquals(ForgeStage.UNSTABLE_CORE, ForgePolicy.outputFor(ForgeStage.FRAGMENT));
        assertEquals(ForgeStage.COMPLETE, ForgePolicy.outputFor(ForgeStage.UNSTABLE_CORE));
    }

    @Test
    public void completeStageCannotMerge() {
        assertThrows(IllegalArgumentException.class,
                () -> ForgePolicy.outputFor(ForgeStage.COMPLETE));
    }

    @Test
    public void countIsLimitedToApprovedRange() {
        ForgePolicy.validateCount(0);
        ForgePolicy.validateCount(999);
        assertThrows(IllegalArgumentException.class, () -> ForgePolicy.validateCount(-1));
        assertThrows(IllegalArgumentException.class, () -> ForgePolicy.validateCount(1000));
    }

}
