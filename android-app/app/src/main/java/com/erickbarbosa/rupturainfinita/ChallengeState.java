package com.erickbarbosa.rupturainfinita;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class ChallengeState {
    final String date;
    final String targetId;
    final List<String> guesses;
    final String status;

    ChallengeState(String date, String targetId, List<String> guesses, String status) {
        this.date = date;
        this.targetId = targetId;
        this.guesses = Collections.unmodifiableList(new ArrayList<>(guesses));
        this.status = status;
    }
}

final class CampaignState {
    final String campaignId;
    final int unlockedMission;
    final List<String> teamIds;

    CampaignState(String campaignId, int unlockedMission, List<String> teamIds) {
        this.campaignId = campaignId;
        this.unlockedMission = unlockedMission;
        this.teamIds = Collections.unmodifiableList(new ArrayList<>(teamIds));
    }
}
