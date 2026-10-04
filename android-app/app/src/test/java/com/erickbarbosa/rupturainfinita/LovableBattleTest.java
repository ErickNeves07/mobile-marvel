package com.erickbarbosa.rupturainfinita;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class LovableBattleTest {
    @Test public void correctCountersFollowPublishedDamageAndCharge() {
        LovableBattle scene = new LovableBattle(31);
        scene.choose(LovableBattle.Choice.DEFEND);
        assertEquals(84, scene.boss);
        assertEquals(98, scene.team);
        assertEquals(43, scene.charge);
        scene.choose(LovableBattle.Choice.CONTROL);
        assertEquals(62, scene.boss);
        assertEquals(93, scene.team);
        assertEquals(77, scene.charge);
        scene.choose(LovableBattle.Choice.ATTACK);
        assertEquals(34, scene.boss);
        assertEquals(88, scene.team);
        scene.choose(LovableBattle.Choice.CONTROL);
        assertEquals(12, scene.boss);
        assertEquals(83, scene.team);
        assertEquals(100, scene.charge);
        assertTrue(scene.canSpecial());
        scene.special();
        assertTrue(scene.victory);
        assertEquals(0, scene.boss);
        assertThrows(IllegalStateException.class, scene::special);
    }

    @Test public void poorChoicesStillRequireFourthRoundAndSpecial() {
        LovableBattle scene = new LovableBattle(31);
        for (int i = 0; i < 4; i++) scene.choose(LovableBattle.Choice.ATTACK);
        assertEquals(15, scene.boss);
        assertEquals(53, scene.team);
        assertFalse(scene.victory);
        assertThrows(IllegalStateException.class, () -> scene.choose(LovableBattle.Choice.ATTACK));
        scene.special();
        assertTrue(scene.victory);
    }

    @Test public void selectedTeamPowerAndChoicesCanCauseDefeatAndRetry() {
        LovableBattle weak = new LovableBattle(29);
        LovableBattle stronger = new LovableBattle(31);
        LovableBattle.Choice[] mistakes = {LovableBattle.Choice.CONTROL,
                LovableBattle.Choice.ATTACK, LovableBattle.Choice.CONTROL,
                LovableBattle.Choice.ATTACK};
        for (LovableBattle.Choice choice : mistakes) {
            weak.choose(choice);
            stronger.choose(choice);
        }
        assertTrue(weak.defeat);
        assertEquals(0, weak.team);
        assertFalse(weak.canSpecial());
        assertThrows(IllegalStateException.class, weak::special);
        assertFalse(stronger.defeat);
        assertTrue(stronger.canSpecial());
        stronger.special();
        assertTrue(stronger.victory);

        LovableBattle retried = new LovableBattle(29);
        retried.choose(LovableBattle.Choice.DEFEND);
        retried.choose(LovableBattle.Choice.CONTROL);
        retried.choose(LovableBattle.Choice.ATTACK);
        retried.choose(LovableBattle.Choice.CONTROL);
        assertTrue(retried.canSpecial());
        retried.special();
        assertTrue(retried.victory);
    }
}
