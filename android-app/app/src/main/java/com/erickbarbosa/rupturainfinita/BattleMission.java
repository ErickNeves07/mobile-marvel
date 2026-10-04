package com.erickbarbosa.rupturainfinita;

import java.util.Arrays;
import java.util.List;

/** Existing approved reward missions, each with its own encounter presentation. */
final class BattleMission {
    static final List<BattleMission> ALL = Arrays.asList(
            new BattleMission("xmen", 1, "Magneto", "magneto", "Magneto", "Instituto Xavier", 0, 1),
            new BattleMission("xmen", 2, "Sentinelas", "master-mold", "Master Mold", "Complexo Sentinela", 1, 2),
            new BattleMission("xmen", 3, "A queda de Genosha", "master-mold", "Master Mold", "Genosha", 2, 3),
            new BattleMission("fantastic-four", 1, "Robôs de Latveria", "doombot", "Doombot", "Latveria", 1, 4),
            new BattleMission("fantastic-four", 2, "O cerco de Destino", "doutor-destino", "Doutor Destino", "Cidadela de Latveria", 2, 5),
            new BattleMission("fantastic-four", 3, "O trono de Latveria", "doutor-destino", "Doutor Destino", "Trono de Latveria", 3, 6)
    );

    final String campaignId;
    final int number;
    final String title;
    final String opponentId;
    final String opponentName;
    final String location;
    final int counterOffset;
    final int difficulty;

    private BattleMission(String campaignId, int number, String title, String opponentId,
                          String opponentName, String location, int counterOffset, int difficulty) {
        this.campaignId = campaignId;
        this.number = number;
        this.title = title;
        this.opponentId = opponentId;
        this.opponentName = opponentName;
        this.location = location;
        this.counterOffset = counterOffset;
        this.difficulty = difficulty;
    }

    static BattleMission forMission(String campaignId, int number) {
        for (BattleMission mission : ALL) {
            if (mission.campaignId.equals(campaignId) && mission.number == number) return mission;
        }
        throw new IllegalArgumentException("Unknown battle mission");
    }
}
