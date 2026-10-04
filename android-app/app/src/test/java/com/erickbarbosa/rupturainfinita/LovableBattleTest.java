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
        int previousBoss = 0;
        for (BattleMission mission : BattleMission.ALL) {
            LovableBattle scene = new LovableBattle(31, mission);
            assertTrue(scene.maxBoss > previousBoss);
            previousBoss = scene.maxBoss;
        }
        LovableBattle finalWeak = new LovableBattle(31, BattleMission.ALL.get(5));
        LovableBattle finalStrong = new LovableBattle(50, BattleMission.ALL.get(5));
        for (int round = 0; round < 4; round++) {
            LovableBattle.Choice counter = counterFor(finalWeak.warning());
            finalWeak.choose(counter);
            finalStrong.choose(counter);
        }
        assertTrue(finalWeak.canSpecial());
        assertTrue(finalStrong.canSpecial());
        finalWeak.special();
        finalStrong.special();
        if (finalWeak.canChoose()) finalWeak.choose(counterFor(finalWeak.warning()));
        if (finalStrong.canChoose()) finalStrong.choose(counterFor(finalStrong.warning()));
        assertTrue(finalWeak.defeat);
        assertTrue(finalStrong.victory);
    }

    private static LovableBattle.Choice counterFor(String warning) {
        if (warning.contains("Proteja")) return LovableBattle.Choice.DEFEND;
        if (warning.contains("Desestabilize")) return LovableBattle.Choice.CONTROL;
        return LovableBattle.Choice.ATTACK;
    }
}
