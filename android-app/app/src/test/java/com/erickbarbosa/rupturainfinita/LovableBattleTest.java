package com.erickbarbosa.rupturainfinita;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public final class LovableBattleTest {
    @Test public void switchingIsFreeAndHealthPersistsOnReserves() {
        LovableBattle battle = newBattle(1);
        LovableBattle.Fighter spider = battle.fighter("homem-aranha");
        LovableBattle.Fighter wolverine = battle.fighter("wolverine");
        int initialWolverineHp = wolverine.health;
        battle.selectFighter("wolverine");
        assertEquals(0, battle.round);
        assertEquals(0, battle.charge);
        battle.choose(counterFor(battle));
        int damagedWolverineHp = wolverine.health;
        assertTrue(damagedWolverineHp < wolverine.maxHealth);
        assertEquals(spider.maxHealth, spider.health);
        battle.selectFighter("homem-aranha");
        battle.selectFighter("wolverine");
        assertEquals(damagedWolverineHp, wolverine.health);
        assertTrue(initialWolverineHp > damagedWolverineHp);
    }

    @Test public void sharedSuperCanBeSpentByAnotherHeroAndIsNamedInRecap() {
        LovableBattle battle = newBattle(1);
        battle.choose(LovableBattle.Choice.DEFEND);
        battle.choose(LovableBattle.Choice.CONTROL);
        battle.choose(LovableBattle.Choice.ATTACK);
        battle.choose(LovableBattle.Choice.CONTROL);
        assertTrue(battle.canSpecial());
        int previousBoss = battle.boss;
        battle.selectFighter("wolverine");
        battle.special();
        assertEquals(0, battle.charge);
        assertTrue(battle.boss < previousBoss);
        assertTrue(battle.summary().toDisplayText().contains("Wolverine"));
        assertEquals(1, battle.summary().specials);
    }

    @Test public void onlyActiveHeroTakesBossDamageAndKnockoutRequiresReplacement() {
        LovableBattle battle = newBattle(1);
        LovableBattle.Fighter active = battle.activeFighter();
        LovableBattle.Fighter reserve = battle.fighter("wolverine");
        active.health = 1;
        int reserveHp = reserve.health;
        battle.choose(LovableBattle.Choice.DEFEND);
        assertTrue(battle.replacementRequired);
        assertFalse(battle.canChoose());
        assertEquals(0, active.health);
        assertEquals(reserveHp, reserve.health);
        int round = battle.round;
        battle.selectFighter("wolverine");
        assertEquals(round, battle.round);
        assertTrue(battle.canChoose());
    }

    @Test public void combatContinuesPastSixActionsAndBossDifficultyScales() {
        LovableBattle early = newBattle(1);
        LovableBattle late = newBattle(9);
        assertTrue(late.maxBoss > early.maxBoss);
        for (int i = 0; i < 8; i++) {
            early.activeFighter().health = early.activeFighter().maxHealth;
            early.choose(LovableBattle.Choice.DEFEND);
            if (early.replacementRequired) {
                for (LovableBattle.Fighter fighter : early.fighters()) {
                    if (fighter.alive()) { early.selectFighter(fighter.spec.id); break; }
                }
            }
        }
        assertEquals(8, early.round);
        assertFalse(early.victory);
        assertFalse(early.defeat);
    }

    @Test public void teamDefeatHappensOnlyAfterAllThreeHeroesFall() {
        LovableBattle battle = newBattle(1);
        for (LovableBattle.Fighter fighter : battle.fighters()) fighter.health = 1;
        battle.choose(LovableBattle.Choice.DEFEND);
        assertFalse(battle.defeat);
        assertTrue(battle.replacementRequired);
        battle.selectFighter("wolverine");
        battle.choose(LovableBattle.Choice.DEFEND);
        assertFalse(battle.defeat);
        battle.selectFighter("tocha-humana");
        battle.choose(LovableBattle.Choice.DEFEND);
        assertTrue(battle.defeat);
        assertEquals(3, battle.summary().knockedOut.size());
    }

    @Test public void readingCounterPatternCanBeatBossWithLivingTrio() {
        LovableBattle battle = newBattle(1);
        int actions = 0;
        while (!battle.victory && !battle.defeat && actions++ < 80) {
            if (battle.replacementRequired) {
                for (LovableBattle.Fighter fighter : battle.fighters()) {
                    if (fighter.alive()) { battle.selectFighter(fighter.spec.id); break; }
                }
            }
            if (battle.canSpecial()) battle.special();
            else battle.choose(counterFor(battle));
        }
        assertTrue(battle.victory);
        assertTrue(battle.livingFighters() > 0);
        assertTrue(battle.summary().damageDealt > 0);
    }

    @Test public void everyMissionHasItsOwnSubtleWarningCycle() {
        assertEquals(9, BattleMission.ALL.size());
        for (BattleMission mission : BattleMission.ALL) {
            LovableBattle battle = newBattle(mission.number);
            for (int round = 0; round < LovableBattle.MAX_ROUNDS; round++) {
                battle.round = round;
                String warning = battle.warning();
                assertFalse(warning.isEmpty());
                assertFalse(warning.contains("Ataque agora"));
                for (BattleMission other : BattleMission.ALL) {
                    if (other == mission) continue;
                    assertFalse(warning.contains(other.opponentName));
                }
            }
        }
    }

    @Test public void finishedBattleRejectsFurtherSpecials() {
        LovableBattle battle = newBattle(1);
        for (LovableBattle.Fighter fighter : battle.fighters()) fighter.health = 1;
        battle.choose(LovableBattle.Choice.DEFEND);
        battle.selectFighter("wolverine");
        battle.choose(LovableBattle.Choice.DEFEND);
        battle.selectFighter("tocha-humana");
        battle.choose(LovableBattle.Choice.DEFEND);
        assertThrows(IllegalStateException.class, battle::special);
    }

    private static LovableBattle newBattle(int mission) {
        List<LovableBattle.FighterSpec> team = new ArrayList<>();
        String[] ids = {"homem-aranha", "wolverine", "tocha-humana"};
        String[] names = {"Homem-Aranha", "Wolverine", "Tocha Humana"};
        for (int i = 0; i < ids.length; i++) team.add(new LovableBattle.FighterSpec(ids[i], names[i],
                "ORIGEM", VariantStats.forVariant(ids[i], GameVariantTier.ORIGIN),
                BattleSpecial.nameForCharacter(ids[i])));
        return new LovableBattle(team, BattleMission.forMission("rupture", mission));
    }

    private static LovableBattle.Choice counterFor(LovableBattle battle) {
        LovableBattle.Choice[] counters = {LovableBattle.Choice.DEFEND,
                LovableBattle.Choice.CONTROL, LovableBattle.Choice.ATTACK,
                LovableBattle.Choice.CONTROL, LovableBattle.Choice.ATTACK,
                LovableBattle.Choice.DEFEND};
        return counters[(battle.round + battle.mission.counterOffset) % counters.length];
    }
}
