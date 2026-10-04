package com.erickbarbosa.rupturainfinita;

/** Authored game rewards; Comic Vine and narrative AI never determine economy. */
final class CampaignReward {
    final int credits;
    final int xp;

    private CampaignReward(int credits, int xp) {
        this.credits = credits;
        this.xp = xp;
    }

    static CampaignReward forMission(String campaignId, int mission) {
        if ("rupture".equals(campaignId)) {
            switch (mission) {
                case 1: return new CampaignReward(3000, 840);
                case 2: return new CampaignReward(3300, 920);
                case 3: return new CampaignReward(3800, 1100);
                case 4: return new CampaignReward(3100, 860);
                case 5: return new CampaignReward(3500, 1000);
                case 6: return new CampaignReward(4000, 1200);
                case 7: return new CampaignReward(4500, 1400);
                case 8: return new CampaignReward(5200, 1650);
                case 9: return new CampaignReward(6200, 2100);
                default: break;
            }
        }
        if ("xmen".equals(campaignId)) {
            switch (mission) {
                case 1: return new CampaignReward(3000, 840);
                case 2: return new CampaignReward(3300, 920);
                case 3: return new CampaignReward(3800, 1100);
                default: break;
            }
        } else if ("fantastic-four".equals(campaignId)) {
            switch (mission) {
                case 1: return new CampaignReward(3100, 860);
                case 2: return new CampaignReward(3500, 1000);
                case 3: return new CampaignReward(4000, 1200);
                default: break;
            }
        }
        throw new IllegalArgumentException("Invalid campaign mission");
    }

    static int missionCount(String campaignId) {
        if ("rupture".equals(campaignId)) return 9;
        if ("xmen".equals(campaignId) || "fantastic-four".equals(campaignId)) return 3;
        throw new IllegalArgumentException("Unknown campaign");
    }
}
