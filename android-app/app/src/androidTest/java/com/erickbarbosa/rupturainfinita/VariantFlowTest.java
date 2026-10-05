package com.erickbarbosa.rupturainfinita;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Instrumentation;
import android.content.ContentValues;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Method;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public final class VariantFlowTest {
    @Test public void completeGauntletOffersChoiceThenConsumesOnVariantUnlock() throws Exception {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        android.content.Context context = instrumentation.getTargetContext();
        context.deleteDatabase("forge_inventory.db");
        MainActivity activity = null;
        try (ForgeRepository repository = new ForgeRepository(context)) {
            for (InfinityStone stone : InfinityStone.values()) {
                ContentValues row = new ContentValues();
                row.put("count", 1);
                assertEquals(1, repository.getWritableDatabase().update("inventory", row,
                        "stone=? AND stage=?", new String[]{stone.name(), ForgeStage.COMPLETE.name()}));
            }
            assertFalse(repository.isGauntletActivated());
            activity = (MainActivity) instrumentation.startActivitySync(
                    new Intent(context, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            List<GameCatalogCharacter> roster = GameCatalogParser.read(context.getAssets());
            GameCatalogCharacter wolverine = null;
            for (GameCatalogCharacter candidate : roster) {
                if ("wolverine".equals(candidate.id)) wolverine = candidate;
            }
            assertTrue(wolverine != null);
            Method detail = MainActivity.class.getDeclaredMethod("showCharacterVariants",
                    GameCatalogCharacter.class, List.class, GameVariantTier.class);
            detail.setAccessible(true);
            MainActivity target = activity;
            GameCatalogCharacter character = wolverine;
            instrumentation.runOnMainSync(() -> {
                try { detail.invoke(target, character, roster, GameVariantTier.ASCENSION); }
                catch (Exception error) { throw new AssertionError(error); }
            });
            View root = activity.getWindow().getDecorView();
            assertTrue(findText(root, "ATIVAR MANOPLA") != null);
            instrumentation.runOnMainSync(() -> findText(root, "ATIVAR MANOPLA").performClick());
            waitForActivation(instrumentation, repository);
            assertTrue(repository.isGauntletActivated());
            Method openGauntlet = MainActivity.class.getDeclaredMethod("showGauntletScreen");
            openGauntlet.setAccessible(true);
            instrumentation.runOnMainSync(() -> {
                try { openGauntlet.invoke(target); }
                catch (Exception error) { throw new AssertionError(error); }
            });
            waitFor(instrumentation, root, "ESCOLHER PERSONAGEM OU VARIANTE");
            instrumentation.runOnMainSync(() -> findText(root,
                    "ESCOLHER PERSONAGEM OU VARIANTE").performClick());
            waitFor(instrumentation, root, "EVOLUIR VARIANTE");
            TextView targetCardLabel = findText(root, "Wolverine · " + context.getString(
                    GameVariantTier.ASCENSION.labelRes));
            assertTrue(targetCardLabel != null);
            assertTrue(((View) targetCardLabel.getParent()).isClickable());
            assertTrue(findText(root, "NOVO PERSONAGEM") != null);
            assertTrue(repository.unlockNextVariant("wolverine", GameVariantTier.ASCENSION, roster));
            assertEquals(GameVariantTier.ASCENSION.name(), repository.loadEquippedTier("wolverine"));
            Method celebrate = MainActivity.class.getDeclaredMethod("showUnlockCelebration",
                    GameCatalogCharacter.class, List.class, GameVariantTier.class);
            celebrate.setAccessible(true);
            instrumentation.runOnMainSync(() -> {
                try { celebrate.invoke(target, character, roster, GameVariantTier.ASCENSION); }
                catch (Exception error) { throw new AssertionError(error); }
            });
            assertTrue(findText(root, "CURIOSIDADES") != null);
            assertTrue(findText(root, "Wolverine") != null);
            for (InfinityStone stone : InfinityStone.values()) {
                assertEquals(0, repository.load().count(stone, ForgeStage.COMPLETE));
            }
        } finally {
            if (activity != null) {
                MainActivity target = activity;
                instrumentation.runOnMainSync(target::finish);
                instrumentation.waitForIdleSync();
            }
            context.deleteDatabase("forge_inventory.db");
        }
    }

    private static void waitFor(Instrumentation instrumentation, View root, String text)
            throws InterruptedException {
        for (int attempt = 0; attempt < 20 && findText(root, text) == null; attempt++) {
            Thread.sleep(200);
            instrumentation.waitForIdleSync();
        }
        assertTrue(text + " did not appear", findText(root, text) != null);
    }

    private static void waitForActivation(Instrumentation instrumentation,
            ForgeRepository repository) throws InterruptedException {
        for (int attempt = 0; attempt < 30 && !repository.isGauntletActivated(); attempt++) {
            Thread.sleep(100);
            instrumentation.waitForIdleSync();
        }
        assertTrue("Gauntlet should activate after all six stones are complete",
                repository.isGauntletActivated());
    }

    private static TextView findText(View view, String target) {
        if (view instanceof TextView && ((TextView) view).getText().toString().contains(target))
            return (TextView) view;
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int index = 0; index < group.getChildCount(); index++) {
                TextView found = findText(group.getChildAt(index), target);
                if (found != null) return found;
            }
        }
        return null;
    }
}
