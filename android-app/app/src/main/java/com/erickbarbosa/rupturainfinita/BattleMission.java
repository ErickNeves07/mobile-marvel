package com.erickbarbosa.rupturainfinita;

import java.util.Arrays;
import java.util.List;

/** Nine authored campaign encounters in the published Lovable chapter order. */
final class BattleMission {
    private static final int[] RECOMMENDED_POWER =
            {3200, 3800, 4200, 4600, 5100, 5400, 5800, 6400, 7200};
    static final List<BattleMission> ALL = Arrays.asList(
            new BattleMission(1, "Nova York em Ruptura", "rei-do-crime", "Rei do Crime", "Nova York", 0),
            new BattleMission(2, "Complexo de Ultron", "ultron", "Ultron", "Complexo de Ultron", 1),
            new BattleMission(3, "Wakanda sob Cerco", "wakanda-tech", "Ameaça Tecnológica", "Wakanda", 2),
            new BattleMission(4, "Dimensão Espelhada", "dormammu", "Dormammu", "Dimensão Espelhada", 3),
            new BattleMission(5, "Knowhere: Abismo Celestial", "ronan", "Ronan", "Knowhere", 4),
            new BattleMission(6, "Instituto Xavier: Ruptura Genética", "magneto", "Magneto", "Instituto Xavier", 5),
            new BattleMission(7, "Zona Negativa: Horizonte Fantástico", "annihilus", "Annihilus", "Zona Negativa", 0),
            new BattleMission(8, "Latveria: Cidadela da Ordem", "doutor-destino", "Doutor Destino", "Latveria", 2),
            new BattleMission(9, "Titã em Colapso", "thanos", "Thanos", "Titã", 4)
    );

    final String campaignId;
    final int number;
    final String title;
    final String opponentId;
    final String opponentName;
    final String location;
    final int counterOffset;
    final int difficulty;
    final int recommendedPower;

    private BattleMission(int number, String title, String opponentId,
                          String opponentName, String location, int counterOffset) {
        this.campaignId = "rupture";
        this.number = number;
        this.title = title;
        this.opponentId = opponentId;
        this.opponentName = opponentName;
        this.location = location;
        this.counterOffset = counterOffset;
        this.difficulty = number;
        this.recommendedPower = RECOMMENDED_POWER[number - 1];
    }

    static BattleMission forMission(String campaignId, int number) {
        for (BattleMission mission : ALL) {
            if (mission.campaignId.equals(campaignId) && mission.number == number) return mission;
        }
        throw new IllegalArgumentException("Unknown battle mission");
    }
}
