package com.erickbarbosa.rupturainfinita;

import static org.junit.Assert.assertTrue;

import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import java.lang.reflect.Method;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public final class LovableScreensTest {
    @Test public void nexusAndDailyChallengeRenderDedicatedVisualSections() throws Exception {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        MainActivity activity = (MainActivity) instrumentation.startActivitySync(
                new Intent(instrumentation.getTargetContext(), MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        Method select = MainActivity.class.getDeclaredMethod("selectDestination", AppDestination.class);
        select.setAccessible(true);
        instrumentation.runOnMainSync(() -> invoke(select, activity, AppDestination.NEXUS));
        Thread.sleep(700);
        instrumentation.waitForIdleSync();
        View root = activity.getWindow().getDecorView();
        assertTrue(hasText(root, "Reed Richards"));
        assertTrue(hasText(root, "Dr. Estranho"));
        assertTrue(findConstellation(root) != null);
        instrumentation.runOnMainSync(() -> capture(activity, root, "nexus-032.png"));
        Method daily = MainActivity.class.getDeclaredMethod("showDailyChallengeScreen");
        daily.setAccessible(true);
        instrumentation.runOnMainSync(() -> invoke(daily, activity));
        Thread.sleep(600);
        instrumentation.waitForIdleSync();
        assertTrue(hasText(root, "DESAFIO DIÁRIO"));
        assertTrue(hasText(root, "Seis tentativas. Eu sei a resposta"));
        TextView submit = findText(root, "Enviar palpite");
        assertTrue(submit != null && !submit.isEnabled());
        instrumentation.runOnMainSync(() -> capture(activity, root, "daily-032.png"));
        instrumentation.runOnMainSync(activity::finish);
    }

    @Test public void campaignCardsOpenOwnedRosterChooserWithEditorialPortraits() throws Exception {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        MainActivity activity = (MainActivity) instrumentation.startActivitySync(
                new Intent(instrumentation.getTargetContext(), MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        Method select = MainActivity.class.getDeclaredMethod("selectDestination", AppDestination.class);
        select.setAccessible(true);
        instrumentation.runOnMainSync(() -> invoke(select, activity, AppDestination.CAMPAIGNS));
        Thread.sleep(800);
        instrumentation.waitForIdleSync();
        View root = activity.getWindow().getDecorView();
        assertTrue(hasText(root, "CAPÍTULO 1"));
        assertTrue(hasText(root, "CAPÍTULO 9"));
        assertTrue(countPortraits(root) == 9);
        instrumentation.runOnMainSync(() -> capture(activity, root, "campaign-nine.png"));
        View missionCard = clickableAncestor(findText(root, "Nova York em Ruptura"));
        for (int attempt = 0; attempt < 20 && missionCard == null; attempt++) {
            Thread.sleep(500);
            instrumentation.waitForIdleSync();
            missionCard = clickableAncestor(findText(root, "Nova York em Ruptura"));
        }
        assertTrue(missionCard != null);
        View firstMission = missionCard;
        instrumentation.runOnMainSync(firstMission::performClick);
        instrumentation.waitForIdleSync();
        TextView enter = findText(root, "ESCOLHER EQUIPE E BATALHAR");
        if (enter == null) enter = findText(root, "REJOGAR BATALHA");
        assertTrue(enter != null);
        TextView openBattle = enter;
        instrumentation.runOnMainSync(openBattle::performClick);
        instrumentation.waitForIdleSync();
        assertTrue(hasText(root, "SELECIONADOS  3/3"));
        assertTrue(hasText(root, "Homem-Aranha"));
        assertTrue(hasText(root, "Wolverine"));
        assertTrue(hasText(root, "Tocha Humana"));
        assertTrue(!hasText(root, "Jean Grey"));
        assertTrue(countPortraits(root) == 3);
        instrumentation.runOnMainSync(() -> capture(activity, root, "team-chooser.png"));
        instrumentation.runOnMainSync(() -> findText(root, "INICIAR BATALHA").performClick());
        for (int attempt = 0; attempt < 10 && !hasText(root, "REI DO CRIME"); attempt++) {
            Thread.sleep(500);
            instrumentation.waitForIdleSync();
        }
        instrumentation.runOnMainSync(() -> capture(activity, root, "battle-after-team-chooser.png"));
        assertTrue(hasText(root, "REI DO CRIME"));
        assertTrue(countPortraits(root) == 4);
        instrumentation.runOnMainSync(() -> capture(activity, root, "battle-with-portraits.png"));
        instrumentation.runOnMainSync(activity::finish);
    }

    @Test public void collectionHasPortraitSlotsAndLoadingOrAttribution() throws Exception {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        MainActivity activity = (MainActivity) instrumentation.startActivitySync(
                new Intent(instrumentation.getTargetContext(), MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        Method select = MainActivity.class.getDeclaredMethod("selectDestination", AppDestination.class);
        select.setAccessible(true);
        instrumentation.runOnMainSync(() -> invoke(select, activity, AppDestination.COLLECTION));
        Thread.sleep(500);
        instrumentation.waitForIdleSync();
        View root = activity.getWindow().getDecorView();
        assertTrue(hasText(root, "Homem de Ferro"));
        assertTrue(countPortraits(root) == 21);
        instrumentation.runOnMainSync(() -> findText(root, "Desbloqueados").performClick());
        instrumentation.waitForIdleSync();
        assertTrue(countPortraits(root) == 3);
        assertTrue(findText(root, "Homem de Ferro") == null);
        instrumentation.runOnMainSync(() -> findText(root, "Desbloqueados").performClick());
        instrumentation.waitForIdleSync();
        assertTrue(countPortraits(root) == 21);
        assertTrue(hasText(root, "Retrato indisponível")
                || hasText(root, "Retrato editorial · Comic Vine")
                || hasText(root, "Carregando retrato editorial"));
        TextView firstName = findText(root, "Homem de Ferro");
        assertTrue(firstName != null);
        View firstCard = (View) firstName.getParent();
        if (!BuildConfig.API_BASE_URL.isEmpty()) {
            for (int attempt = 0; attempt < 25
                    && !hasText(firstCard, "Retrato editorial · Comic Vine"); attempt++) {
                Thread.sleep(1000);
                instrumentation.waitForIdleSync();
            }
            assertTrue("The published backend did not load a Comic Vine portrait",
                    hasText(firstCard, "Retrato editorial · Comic Vine"));
            for (String name : new String[]{"Homem-Aranha", "Tocha Humana"}) {
                TextView label = findText(root, name);
                assertTrue(label != null);
                View characterCard = (View) label.getParent();
                for (int attempt = 0; attempt < 35
                        && !hasText(characterCard, "Retrato editorial · Comic Vine"); attempt++) {
                    Thread.sleep(1000);
                    instrumentation.waitForIdleSync();
                }
                assertTrue(name + " portrait did not load", hasText(characterCard,
                        "Retrato editorial · Comic Vine"));
            }
        }
        instrumentation.runOnMainSync(() -> capture(activity, root, "collection-portraits.png"));
        instrumentation.runOnMainSync(firstCard::performClick);
        instrumentation.waitForIdleSync();
        assertTrue(hasText(root, "Voltar à Coleção"));
        assertTrue(hasText(root, "Armadura de Cerco"));
        assertTrue(countPortraits(root) >= 6);
        instrumentation.runOnMainSync(() -> capture(activity, root, "collection-detail-live.png"));
        instrumentation.runOnMainSync(() -> findText(root, "Voltar à Coleção").performClick());
        instrumentation.waitForIdleSync();
        assertTrue(countPortraits(root) == 21);
        instrumentation.runOnMainSync(() -> findText(root, "Comparar").performClick());
        instrumentation.waitForIdleSync();
        assertTrue(hasText(root, "COMPARAR VARIANTES"));
        instrumentation.runOnMainSync(activity::finish);
    }

    @Test public void gauntletShowsRealStoneStatesInConstellation() throws Exception {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        MainActivity activity = (MainActivity) instrumentation.startActivitySync(
                new Intent(instrumentation.getTargetContext(), MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        Method open = MainActivity.class.getDeclaredMethod("showGauntletScreen");
        open.setAccessible(true);
        instrumentation.runOnMainSync(() -> invoke(open, activity));
        Thread.sleep(1200);
        instrumentation.waitForIdleSync();
        View root = activity.getWindow().getDecorView();
        assertTrue(hasText(root, "MANOPLA DE CONTENÇÃO"));
        assertTrue(hasText(root, "RESSONÂNCIA DE CONTENÇÃO"));
        GauntletConstellationView artwork = findConstellation(root);
        assertTrue(artwork != null && artwork.getContentDescription().toString().contains("de 6 Joias completas"));
        instrumentation.runOnMainSync(() -> capture(activity, root, "gauntlet.png"));
        instrumentation.runOnMainSync(activity::finish);
    }

    @Test public void comparisonSelectsIndividualVariantAndBattleRequiresSpecial() throws Exception {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        MainActivity activity = (MainActivity) instrumentation.startActivitySync(
                new Intent(instrumentation.getTargetContext(), MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        List<GameCatalogCharacter> roster = GameCatalogParser.read(activity.getAssets());
        Method compare = MainActivity.class.getDeclaredMethod("showVariantComparison", List.class);
        compare.setAccessible(true);
        instrumentation.runOnMainSync(() -> invoke(compare, activity, roster));
        instrumentation.waitForIdleSync();
        View root = activity.getWindow().getDecorView();
        assertTrue(hasText(root, "COMPARAR VARIANTES"));
        List<Spinner> spinners = new ArrayList<>();
        collectSpinners(root, spinners);
        assertTrue(spinners.size() >= 4);
        instrumentation.runOnMainSync(() -> spinners.get(1).setSelection(4));
        instrumentation.waitForIdleSync();
        assertTrue(hasText(root, "Fera da Alma"));
        if (!BuildConfig.API_BASE_URL.isEmpty()) {
            for (int attempt = 0; attempt < 25
                    && countText(root, "Retrato editorial · Comic Vine") < 2; attempt++) {
                Thread.sleep(1000);
                instrumentation.waitForIdleSync();
            }
            assertTrue("Both comparison portraits should load from Comic Vine",
                    countText(root, "Retrato editorial · Comic Vine") >= 2);
        }
        instrumentation.runOnMainSync(() -> capture(activity, root, "compare-variant.png"));

        Method battle = MainActivity.class.getDeclaredMethod("showMagnetoBattle",
                LovableBattle.class, CampaignState.class);
        battle.setAccessible(true);
        CampaignState campaign = new CampaignState("rupture", 1,
                Arrays.asList("homem-aranha", "wolverine", "tocha-humana"));
        try (ForgeRepository repository = new ForgeRepository(activity)) {
            repository.loadCampaign("rupture", campaign.teamIds);
        }
        instrumentation.runOnMainSync(() -> invoke(battle, activity, new LovableBattle(31), campaign));
        instrumentation.waitForIdleSync();
        assertTrue(hasText(root, "INVESTIDA DIRETA"));
        instrumentation.runOnMainSync(() -> capture(activity, root, "battle-initial.png"));
        for (String choice : new String[]{"PROTEGER", "DESESTABILIZAR", "INVESTIR", "DESESTABILIZAR"}) {
            instrumentation.runOnMainSync(() -> {
                TextView button = findText(root, choice);
                if (button == null) throw new AssertionError("Missing battle choice " + choice);
                button.performClick();
            });
            Thread.sleep(950);
            instrumentation.waitForIdleSync();
        }
        assertTrue(hasText(root, "ESPECIAL · TEIAS, GARRAS E CHAMAS"));
        instrumentation.runOnMainSync(() -> findText(root, "ESPECIAL · TEIAS, GARRAS E CHAMAS").performClick());
        Thread.sleep(1300);
        instrumentation.waitForIdleSync();
        assertTrue(hasText(root, "VITÓRIA"));
        assertTrue(hasText(root, "COLETAR RECOMPENSA"));
        instrumentation.runOnMainSync(() -> capture(activity, root, "battle-victory.png"));
        instrumentation.runOnMainSync(() -> findText(root, "COLETAR RECOMPENSA").performClick());
        Thread.sleep(800);
        instrumentation.waitForIdleSync();
        assertTrue(hasText(root, "RECOMPENSAS"));
        assertTrue(hasText(root, "Fragmentos da Mente  +1"));
        assertTrue(hasText(root, "Fragmentos do Espaço  +1"));
        assertTrue(hasText(root, "Créditos"));
        assertTrue(hasText(root, "+3.000"));
        assertTrue(hasText(root, "Experiência"));
        assertTrue(hasText(root, "+840 XP"));
        assertTrue(!hasText(root, "Homem-Aranha +1"));
        instrumentation.runOnMainSync(() -> capture(activity, root, "campaign-reward.png"));
        instrumentation.runOnMainSync(() -> findText(root, "VOLTAR AO NEXUS").performClick());
        Thread.sleep(500);
        instrumentation.waitForIdleSync();
        assertTrue(hasText(root, "Saldo: 3.000 créditos · 840 XP"));
        instrumentation.runOnMainSync(activity::finish);
    }

    @Test public void chosenTeamCanLoseAndRetryWithoutReward() throws Exception {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        MainActivity activity = (MainActivity) instrumentation.startActivitySync(
                new Intent(instrumentation.getTargetContext(), MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        View root = activity.getWindow().getDecorView();
        Method battle = MainActivity.class.getDeclaredMethod("showMagnetoBattle",
                LovableBattle.class, CampaignState.class);
        battle.setAccessible(true);
        CampaignState campaign = new CampaignState("rupture", 1,
                Arrays.asList("homem-aranha", "wolverine", "tocha-humana"));
        instrumentation.runOnMainSync(() -> invoke(battle, activity, new LovableBattle(29), campaign));
        instrumentation.waitForIdleSync();
        assertTrue(hasText(root, "Homem-Aranha"));
        assertTrue(hasText(root, "Wolverine"));
        assertTrue(hasText(root, "Tocha Humana"));
        for (String choice : new String[]{"PROTEGER", "PROTEGER", "PROTEGER",
                "PROTEGER", "PROTEGER", "PROTEGER"}) {
            instrumentation.runOnMainSync(() -> findText(root, choice).performClick());
            Thread.sleep(950);
            instrumentation.waitForIdleSync();
        }
        assertTrue(hasText(root, "DERROTA"));
        assertTrue(hasText(root, "TENTAR NOVAMENTE"));
        assertTrue(!hasText(root, "COLETAR RECOMPENSA"));
        instrumentation.runOnMainSync(() -> capture(activity, root, "battle-defeat.png"));
        instrumentation.runOnMainSync(() -> findText(root, "TENTAR NOVAMENTE").performClick());
        instrumentation.waitForIdleSync();
        assertTrue(hasText(root, "INVESTIDA DIRETA"));
        assertTrue(hasText(root, "Homem-Aranha"));
        instrumentation.runOnMainSync(activity::finish);
    }

    private static void capture(MainActivity activity, View root, String name) {
        Bitmap bitmap = Bitmap.createBitmap(root.getWidth(), root.getHeight(), Bitmap.Config.ARGB_8888);
        root.draw(new Canvas(bitmap));
        File destination = new File(activity.getExternalFilesDir(null), name);
        try (FileOutputStream output = new FileOutputStream(destination)) {
            if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, output))
                throw new AssertionError("Screenshot encoding failed");
        } catch (java.io.IOException error) { throw new AssertionError(error); }
        finally { bitmap.recycle(); }
    }

    private static void invoke(Method method, MainActivity activity, Object... args) {
        try { method.invoke(activity, args); }
        catch (ReflectiveOperationException error) { throw new AssertionError(error); }
    }

    private static boolean hasText(View view, String target) { return findText(view, target) != null; }

    private static int countText(View view, String target) {
        int count = view instanceof TextView
                && ((TextView) view).getText().toString().contains(target) ? 1 : 0;
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int index = 0; index < group.getChildCount(); index++) {
                count += countText(group.getChildAt(index), target);
            }
        }
        return count;
    }

    private static TextView findText(View view, String target) {
        if (view instanceof TextView && ((TextView) view).getText().toString().contains(target))
            return (TextView) view;
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                TextView found = findText(group.getChildAt(i), target);
                if (found != null) return found;
            }
        }
        return null;
    }

    private static View clickableAncestor(View view) {
        while (view != null && !view.isClickable()) {
            if (!(view.getParent() instanceof View)) return null;
            view = (View) view.getParent();
        }
        return view;
    }

    private static void collectSpinners(View view, List<Spinner> result) {
        if (view instanceof Spinner) result.add((Spinner) view);
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) collectSpinners(group.getChildAt(i), result);
        }
    }

    private static GauntletConstellationView findConstellation(View view) {
        if (view instanceof GauntletConstellationView) return (GauntletConstellationView) view;
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                GauntletConstellationView found = findConstellation(group.getChildAt(i));
                if (found != null) return found;
            }
        }
        return null;
    }

    private static int countPortraits(View view) {
        int count = view instanceof android.widget.ImageView && view.getTag() instanceof String ? 1 : 0;
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                count += countPortraits(group.getChildAt(i));
            }
        }
        return count;
    }
}
