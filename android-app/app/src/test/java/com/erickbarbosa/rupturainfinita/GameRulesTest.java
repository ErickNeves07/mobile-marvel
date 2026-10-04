package com.erickbarbosa.rupturainfinita;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

public final class GameRulesTest {
    private GameCatalogCharacter character(String id, String name, String group) {
        return new GameCatalogCharacter(id, name, group, Collections.emptyList());
    }

    @Test public void challengeFeedbackUsesFactionAndAlphabeticalOrder() {
        GameCatalogCharacter target = character("b", "Magneto", "x-men");
        GameCatalogCharacter guess = character("a", "Cyclops", "x-men");
        assertEquals("Mesmo grupo", GameRules.factionFeedback(target, guess));
        assertTrue(GameRules.alphabeticalFeedback(target, guess).contains("depois"));
    }

    @Test public void battleIsDeterministicAndValidatesFactionTeam() {
        java.util.List<GameCatalogCharacter> roster = Arrays.asList(
                character("wolverine", "Wolverine", "x-men"),
                character("ciclope", "Ciclope", "x-men"),
                character("tempestade", "Tempestade", "x-men"));
        java.util.List<String> team = Arrays.asList("wolverine", "ciclope", "tempestade");
        BattleResult first = GameRules.battle(roster, team, "x-men", 1);
        BattleResult second = GameRules.battle(roster, team, "x-men", 1);
        assertEquals(first.victory, second.victory);
        assertEquals(first.turns, second.turns);
        assertThrows(IllegalArgumentException.class, () -> GameRules.battle(roster,
                Arrays.asList("wolverine", "ciclope", "stranger"), "x-men", 1));
    }
}
