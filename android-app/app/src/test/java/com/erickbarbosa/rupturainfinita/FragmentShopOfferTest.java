package com.erickbarbosa.rupturainfinita;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class FragmentShopOfferTest {
    @Test public void offersUnlockAtCumulativeCampaignXpAndIncreaseInPrice() {
        int[] xp = {0, 840, 1_760, 2_860, 3_720, 4_720};
        long[] prices = {800, 900, 1_000, 1_100, 1_200, 1_300};
        InfinityStone[] stones = InfinityStone.values();
        for (int index = 0; index < stones.length; index++) {
            FragmentShopOffer offer = FragmentShopOffer.forStone(stones[index]);
            assertEquals(xp[index], offer.minimumXp);
            assertEquals(prices[index], offer.priceCredits);
            assertTrue(offer.isUnlocked(xp[index]));
            if (xp[index] > 0) assertFalse(offer.isUnlocked(xp[index] - 1L));
        }
    }
}
