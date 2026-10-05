package com.erickbarbosa.rupturainfinita;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class LovableBattleTest {
    @Test public void defenseAloneCannotWinOrChargeSpecial() {
        LovableBattle scene = new LovableBattle(31);
        while (scene.canChoose()) scene.choose(LovableBattle.Choice.DEFEND);
        assertTrue(scene.defeat);
        assertEquals(scene.maxBoss, scene.boss);
        assertFalse(scene.canSpecial());
        assertTrue(scene.team > 0);
    }

    @Test public void readingIntentsAndUsingSpecialWinsFirstBattle() {
        LovableBattle scene = new LovableBattle(31);
        scene.choose(LovableBattle.Choice.DEFEND);
        scene.choose(LovableBattle.Choice.CONTROL);
        scene.choose(LovableBattle.Choice.ATTACK);
        scene.choose(LovableBattle.Choice.CONTROL);
        assertTrue(scene.canSpecial());
        scene.special();
        assertTrue(scene.victory);
        assertEquals(0, scene.boss);
        assertTrue(scene.team > 0);
        assertThrows(IllegalStateException.class, scene::special);
    }

    @Test public void attackingBlindlyLosesDespiteSurviving() {
        LovableBattle scene = new LovableBattle(31);
        while (scene.canChoose()) scene.choose(LovableBattle.Choice.ATTACK);
        assertTrue(scene.defeat);
        assertTrue(scene.boss > 0);
    }

    @Test public void laterMissionsAreHarderAndRequireMorePower() {
        assertEquals(9, BattleMission.ALL.size());
        int previousBoss = 0;
        for (BattleMission mission : BattleMission.ALL) {
            LovableBattle scene = new LovableBattle(31, mission);
            assertTrue(scene.maxBoss > previousBoss);
            previousBoss = scene.maxBoss;
        }
        LovableBattle finalWeak = new LovableBattle(31, BattleMission.ALL.get(5));
        LovableBattle finalStrong = new LovableBattle(50, BattleMission.ALL.get(5));
        for (int round = 0; round < 4; round++) {
            LovableBattle.Choice counter = counterFor(finalWeak);
            finalWeak.choose(counter);
            finalStrong.choose(counter);
        }
        assertTrue(finalWeak.canSpecial());
        assertTrue(finalStrong.canSpecial());
        finalWeak.special();
        finalStrong.special();
        if (finalWeak.canChoose()) finalWeak.choose(counterFor(finalWeak));
        if (finalStrong.canChoose()) finalStrong.choose(counterFor(finalStrong));
        assertTrue(finalWeak.defeat);
        assertTrue(finalStrong.victory);
    }

    @Test public void finalChapterRequiresAnUpgradedTeamAndIsStillWinnable() {
        BattleMission finalMission = BattleMission.forMission("rupture", 9);
        assertEquals("Thanos", finalMission.opponentName);
        LovableBattle weak = new LovableBattle(31, finalMission);
        LovableBattle strong = new LovableBattle(80, finalMission);
        while (weak.canChoose()) weak.choose(counterFor(weak));
        while (strong.canChoose()) strong.choose(counterFor(strong));
        assertTrue(weak.defeat);
        assertTrue(strong.victory);
    }

    @Test public void hintsStaySubtleAndTrapIsOnlyNarrativeFeedback() {
        BattleMission ultron = BattleMission.forMission("rupture", 2);
        LovableBattle scene = new LovableBattle(31, ultron);
        for (int i = 0; i < 3; i++) scene.choose(counterFor(scene));
        String hint = scene.warning();
        assertFalse(hint.contains("Ataque agora"));
        assertFalse(hint.contains("Proteja"));
        int bossBefore = scene.boss;
        int teamBefore = scene.team;
        scene.choose(LovableBattle.Choice.DEFEND);
        assertTrue(scene.lastTrap);
        assertTrue(scene.feedback.contains("armadilha"));
        assertTrue(scene.boss <= bossBefore);
        assertTrue(scene.team <= teamBefore);
    }

    @Test public void battleNarrationNamesTheOpeningAndWhatTheSuccessfulMoveDid() {
        LovableBattle scene = new LovableBattle(31);
        assertTrue(scene.warning().contains("investida"));
        scene.choose(LovableBattle.Choice.DEFEND);
        scene.choose(LovableBattle.Choice.CONTROL);
        assertTrue(scene.warning().contains("passagem"));
        int bossBefore = scene.boss;
        scene.choose(LovableBattle.Choice.ATTACK);
        assertTrue(scene.feedback.contains("abertura"));
        assertTrue(scene.boss < bossBefore);
    }

    @Test public void everyCampaignHasSixOpponentSpecificWarnings() {
        for (BattleMission mission : BattleMission.ALL) {
            LovableBattle scene = new LovableBattle(31, mission);
            for (int round = 0; round < LovableBattle.MAX_ROUNDS; round++) {
                scene.round = round;
                String warning = scene.warning();
                assertFalse(warning.isEmpty());
                for (BattleMission other : BattleMission.ALL) {
                    if (other == mission) continue;
                    assertFalse(mission.title + " round " + round + ": " + warning,
                            warning.contains(other.opponentName));
                }
            }
        }
        LovableBattle ultron = new LovableBattle(31, BattleMission.forMission("rupture", 2));
        for (int round = 0; round < LovableBattle.MAX_ROUNDS; round++) {
            ultron.round = round;
            assertFalse(ultron.warning().toLowerCase().contains("metal"));
        }
    }

    private static LovableBattle.Choice counterFor(LovableBattle battle) {
        LovableBattle.Choice[] counters = {LovableBattle.Choice.DEFEND,
                LovableBattle.Choice.CONTROL, LovableBattle.Choice.ATTACK,
                LovableBattle.Choice.CONTROL, LovableBattle.Choice.ATTACK,
                LovableBattle.Choice.DEFEND};
        return counters[(battle.round + battle.mission.counterOffset) % counters.length];
    }
}
