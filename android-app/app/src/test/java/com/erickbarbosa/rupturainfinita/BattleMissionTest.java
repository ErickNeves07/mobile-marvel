package com.erickbarbosa.rupturainfinita;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

public final class BattleMissionTest {
    @Test public void campaignMatchesPublishedOrderAndRecommendedPower() {
        List<String> titles = BattleMission.ALL.stream()
                .map(mission -> mission.title).collect(Collectors.toList());
        assertEquals(Arrays.asList(
                "Nova York em Ruptura", "Complexo de Ultron", "Wakanda sob Cerco",
                "Dimensão Espelhada", "Knowhere: Abismo Celestial",
                "Instituto Xavier: Ruptura Genética", "Zona Negativa: Horizonte Fantástico",
                "Latveria: Cidadela da Ordem", "Titã em Colapso"), titles);
        List<Integer> power = BattleMission.ALL.stream()
                .map(mission -> mission.recommendedPower).collect(Collectors.toList());
        assertEquals(Arrays.asList(3200, 3800, 4200, 4600, 5100, 5400, 5800, 6400, 7200), power);
    }
}
