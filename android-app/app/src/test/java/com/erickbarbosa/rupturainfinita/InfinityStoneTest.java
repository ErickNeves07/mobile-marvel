package com.erickbarbosa.rupturainfinita;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public final class InfinityStoneTest {
    @Test
    public void forgePreviewListsAllSixStonesInApprovedOrder() {
        assertArrayEquals(new InfinityStone[] {
                InfinityStone.SPACE,
                InfinityStone.MIND,
                InfinityStone.REALITY,
                InfinityStone.POWER,
                InfinityStone.TIME,
                InfinityStone.SOUL
        }, InfinityStone.values());
    }
}
