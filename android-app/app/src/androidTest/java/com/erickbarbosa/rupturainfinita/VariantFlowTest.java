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
    @Test public void completeGauntletActivatesThenUnlocksNextStarterVariant() throws Exception {
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
            waitFor(instrumentation, root, "Desbloquear com Joia completa");
            assertTrue(repository.isGauntletActivated());
            instrumentation.runOnMainSync(() -> findText(root,
                    "Desbloquear com Joia completa").performClick());
            for (int attempt = 0; attempt < 20
                    && !GameVariantTier.ASCENSION.name().equals(repository.loadEquippedTier("wolverine"));
                    attempt++) {
                Thread.sleep(200);
                instrumentation.waitForIdleSync();
            }
            assertEquals(GameVariantTier.ASCENSION.name(), repository.loadEquippedTier("wolverine"));
            waitFor(instrumentation, root, "Equipada: " + wolverine.variants.get(1).name);
            assertEquals(1, repository.load().count(InfinityStone.MIND, ForgeStage.COMPLETE));
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
