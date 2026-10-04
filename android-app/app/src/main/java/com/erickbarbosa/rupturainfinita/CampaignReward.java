package com.erickbarbosa.rupturainfinita;

/** Authored game rewards; Comic Vine and narrative AI never determine economy. */
final class CampaignReward {
    static final int FRAGMENTS = 4;

    final InfinityStone stone;
    final int credits;
    final int xp;

    private CampaignReward(InfinityStone stone, int credits, int xp) {
        this.stone = stone;
        this.credits = credits;
        this.xp = xp;
    }

    static CampaignReward forMission(String campaignId, int mission) {
        if ("xmen".equals(campaignId)) {
            switch (mission) {
                case 1: return new CampaignReward(InfinityStone.MIND, 3000, 840);
                case 2: return new CampaignReward(InfinityStone.SPACE, 3300, 920);
                case 3: return new CampaignReward(InfinityStone.TIME, 3800, 1100);
                default: break;
            }
        } else if ("fantastic-four".equals(campaignId)) {
            switch (mission) {
                case 1: return new CampaignReward(InfinityStone.POWER, 3100, 860);
                case 2: return new CampaignReward(InfinityStone.REALITY, 3500, 1000);
                case 3: return new CampaignReward(InfinityStone.SOUL, 4000, 1200);
                default: break;
            }
        }
        throw new IllegalArgumentException("Invalid campaign mission");
    }
}
