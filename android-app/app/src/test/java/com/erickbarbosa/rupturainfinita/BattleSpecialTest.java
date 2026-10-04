package com.erickbarbosa.rupturainfinita;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public final class BattleSpecialTest {
    @Test public void starterTrioGetsItsOwnCoherentMove() {
        String name = BattleSpecial.nameForTeam(Arrays.asList(
                "homem-aranha", "tocha-humana", "wolverine"));
        assertEquals("Teias, Garras e Chamas", name);
        assertFalse(name.contains("Psíquic"));
    }

    @Test public void specialFollowsSelectedLeaderForOtherTeams() {
        assertEquals("Tempestade de Mjolnir", BattleSpecial.nameForTeam(List.of(
                "thor", "wolverine", "groot")));
        assertEquals("Pulso Psíquico", BattleSpecial.nameForTeam(List.of(
                "professor-xavier", "ciclope", "jean-grey")));
    }
}
