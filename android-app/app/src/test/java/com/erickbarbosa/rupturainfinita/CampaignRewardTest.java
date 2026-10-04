package com.erickbarbosa.rupturainfinita;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class CampaignRewardTest {
    @Test public void nineChaptersKeepSixApprovedCreditAndXpPairs() {
        int[][] approved = {{3000, 840}, {3300, 920}, {3800, 1100},
                {3100, 860}, {3500, 1000}, {4000, 1200}};
        assertEquals(9, CampaignReward.missionCount("rupture"));
        for (int number = 1; number <= approved.length; number++) {
            CampaignReward reward = CampaignReward.forMission("rupture", number);
            assertEquals(approved[number - 1][0], reward.credits);
            assertEquals(approved[number - 1][1], reward.xp);
        }
        CampaignReward seventh = CampaignReward.forMission("rupture", 7);
        CampaignReward eighth = CampaignReward.forMission("rupture", 8);
        CampaignReward ninth = CampaignReward.forMission("rupture", 9);
        assertTrue(seventh.credits < eighth.credits && eighth.credits < ninth.credits);
        assertTrue(seventh.xp < eighth.xp && eighth.xp < ninth.xp);
    }
}
