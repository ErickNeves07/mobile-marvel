package com.erickbarbosa.rupturainfinita;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

import android.app.Instrumentation;
import android.content.ContentValues;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public final class LovableScreensTest {
    @Test public void sharedActionButtonsRespectDarkAndLightPalettes() throws Exception {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        MainActivity activity = (MainActivity) instrumentation.startActivitySync(
                new Intent(instrumentation.getTargetContext(), MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        Method action = MainActivity.class.getDeclaredMethod("action", String.class, Runnable.class);
        action.setAccessible(true);
        android.content.SharedPreferences prefs = activity.getPreferences(android.content.Context.MODE_PRIVATE);
        prefs.edit().putBoolean("dark_mode", true).commit();
        TextView darkButton = (TextView) action.invoke(activity, "TESTE", (Runnable) () -> { });
        assertEquals(activity.getColor(R.color.accent_gold), darkButton.getCurrentTextColor());
        assertTrue(darkButton.getBackground() instanceof AngularPanelDrawable);
        prefs.edit().putBoolean("dark_mode", false).commit();
        TextView lightButton = (TextView) action.invoke(activity, "TESTE", (Runnable) () -> { });
        assertEquals(activity.getColor(R.color.canvas), lightButton.getCurrentTextColor());
        prefs.edit().putBoolean("dark_mode", true).commit();
        instrumentation.runOnMainSync(activity::finish);
    }

    @Test public void deadpoolContextTracksSelectedMissionAndSavedTeam() throws Exception {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        MainActivity activity = (MainActivity) instrumentation.startActivitySync(
                new Intent(instrumentation.getTargetContext(), MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        Field mission = MainActivity.class.getDeclaredField("selectedBattleMission");
        mission.setAccessible(true);
        Method context = MainActivity.class.getDeclaredMethod("deadpoolGameContext");
        context.setAccessible(true);
        String[] result = {""};
        instrumentation.runOnMainSync(() -> {
            try {
                mission.set(activity, BattleMission.forMission("rupture", 9));
                result[0] = (String) context.invoke(activity);
            } catch (Exception error) { throw new AssertionError(error); }
        });
        assertTrue(result[0].contains("Titã em Colapso contra Thanos"));
        assertTrue(result[0].contains("Homem-Aranha"));
        assertTrue(result[0].contains("Wolverine"));
        assertTrue(result[0].contains("Tocha Humana"));
        assertTrue(result[0].length() <= 6_000);
        assertTrue(result[0].contains("Vida/Ataque/Defesa/Velocidade"));
        instrumentation.runOnMainSync(activity::finish);
    }

    @Test public void coldLaunchUsesBrandedSplashAndReturnsToIntro() throws Exception {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        android.content.Context context = instrumentation.getTargetContext();
        ActivityInfo main = context.getPackageManager().getActivityInfo(
                new ComponentName(context, MainActivity.class), 0);
        assertTrue(main.theme == R.style.Theme_RupturaInfinita_Starting);
        android.content.res.Resources.Theme startingTheme = context.getResources().newTheme();
        startingTheme.applyStyle(main.theme, true);
        try (TypedArray attrs = startingTheme.obtainStyledAttributes(new int[]{
                androidx.core.splashscreen.R.attr.windowSplashScreenAnimatedIcon})) {
            assertTrue(attrs.getResourceId(0, 0) == R.drawable.splash_rift_animated);
        }
        assertTrue(androidx.appcompat.content.res.AppCompatResources.getDrawable(context,
                R.drawable.splash_rift_animated) != null);

        MainActivity activity = (MainActivity) instrumentation.startActivitySync(
                new Intent(context, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        assertTrue(hasText(activity.getWindow().getDecorView(), "Entrar no Nexus"));
        instrumentation.runOnMainSync(activity::finish);
    }

    @Test public void forgeShowsTopModeControlsAndCompletedStoneAnimation() throws Exception {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        MainActivity activity = (MainActivity) instrumentation.startActivitySync(
                new Intent(instrumentation.getTargetContext(), MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        Method shell = MainActivity.class.getDeclaredMethod("renderShell");
        shell.setAccessible(true);
        instrumentation.runOnMainSync(() -> invoke(shell, activity));
        View root = activity.getWindow().getDecorView();
        assertTrue(hasText(root, "MODO CLARO"));
        assertTrue(hasText(root, "ÁUDIO ON"));
        assertTrue(hasText(root, "LOJA"));
        Method forge = MainActivity.class.getDeclaredMethod("selectDestination", AppDestination.class);
        forge.setAccessible(true);
        instrumentation.runOnMainSync(() -> invoke(forge, activity, AppDestination.FORGE));
        instrumentation.waitForIdleSync();
        assertTrue(hasText(root, "Seu inventário"));
        assertFalse(hasText(root, "Cadeia de estágios"));
        assertFalse(hasText(root, "Estilhaço → Fragmento → Núcleo Instável → Joia Completa"));
        assertFalse(hasText(root, "0 de 999"));
        Method animation = MainActivity.class.getDeclaredMethod("showForgeMergeAnimation",
                boolean.class, InfinityStone.class, ForgeStage.class);
        animation.setAccessible(true);
        instrumentation.runOnMainSync(() -> {
            try { animation.invoke(activity, true, InfinityStone.SPACE, ForgeStage.UNSTABLE_CORE); }
            catch (Exception error) { throw new AssertionError(error); }
        });
        assertTrue(hasText(root, "🧤"));
        assertTrue(hasText(root, "JOIA FORJADA"));
        Thread.sleep(450);
        instrumentation.runOnMainSync(() -> capture(activity, root, "forge-merge-visual.png"));
        instrumentation.runOnMainSync(activity::finish);
    }

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
        assertTrue(hasText(root, "CÂMARA DE VARIANTES"));
        assertTrue(hasText(root, "Reed Richards"));
        assertTrue(hasText(root, "Doutor Estranho"));
        assertFalse(hasText(root, "Abrir %1$s"));
        assertTrue(hasContentDescription(root, "Abrir:"));
        assertTrue(findConstellation(root) != null);
        instrumentation.runOnMainSync(() -> capture(activity, root, "nexus-032.png"));
        Method daily = MainActivity.class.getDeclaredMethod("showDailyChallengeScreen");
        daily.setAccessible(true);
        instrumentation.runOnMainSync(() -> invoke(daily, activity));
        for (int attempt = 0; attempt < 20
                && !hasText(root, "Seis tentativas. Eu sei a resposta"); attempt++) {
            Thread.sleep(150);
            instrumentation.waitForIdleSync();
        }
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
        if (shouldAssertLiveEditorial()) {
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
        if (shouldAssertLiveEditorial()) {
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
        LovableBattle battleState = new LovableBattle(31);
        instrumentation.runOnMainSync(() -> invoke(battle, activity, battleState, campaign));
        instrumentation.waitForIdleSync();
        assertTrue(hasText(root, "INVESTIDA DIRETA"));
        View activeFighter = findTag(root, "battle-active-fighter");
        assertTrue("The active fighter needs a visible selected state", activeFighter != null);
        assertTrue(activeFighter.getContentDescription().toString().contains("SUA VEZ"));
        assertTrue(hasText(root, "SUA VEZ"));
        instrumentation.runOnMainSync(() -> capture(activity, root, "battle-initial.png"));
        for (String choice : new String[]{"PROTEGER", "DESESTABILIZAR", "INVESTIR", "DESESTABILIZAR"}) {
            instrumentation.runOnMainSync(() -> {
                TextView button = findText(root, choice);
                if (button == null) throw new AssertionError("Missing battle choice " + choice);
                button.performClick();
            });
            Thread.sleep(1900);
            instrumentation.waitForIdleSync();
        }
        assertTrue(hasText(root, "SUPER GLOBAL · RAJADA DE TEIAS"));
        View superButton = findTag(root, "battle-super-button");
        assertTrue("The charged super needs its own visual control", superButton != null);
        assertTrue(superButton.getContentDescription().toString().contains("Toque para liberar"));
        assertTrue(hasText(root, "CARGA COMPARTILHADA  ·  LIBERAR AGORA"));
        instrumentation.runOnMainSync(() -> findText(root, "SUPER GLOBAL · RAJADA DE TEIAS").performClick());
        Thread.sleep(2600);
        instrumentation.waitForIdleSync();
        driveBattleToVictory(battleState);
        Method render = MainActivity.class.getDeclaredMethod("renderMagnetoBattle",
                LovableBattle.class, CampaignState.class);
        render.setAccessible(true);
        instrumentation.runOnMainSync(() -> invoke(render, activity, battleState, campaign));
        instrumentation.waitForIdleSync();
        assertTrue(hasText(root, "VITÓRIA"));
        assertTrue(hasText(root, "COLETAR RECOMPENSA"));
        instrumentation.runOnMainSync(() -> capture(activity, root, "battle-victory.png"));
        instrumentation.runOnMainSync(() -> findText(root, "COLETAR RECOMPENSA").performClick());
        Thread.sleep(800);
        instrumentation.waitForIdleSync();
        assertTrue("The authored post-battle scene must remain visible", findTag(root,
                "campaign-story-sequence") != null);
        Method finishAnimation = MainActivity.class.getDeclaredMethod("finishBattleActionAnimation",
                LovableBattle.class, CampaignState.class);
        finishAnimation.setAccessible(true);
        instrumentation.runOnMainSync(() -> invoke(finishAnimation, activity, battleState, campaign));
        assertTrue("A stale action timer must not replace the story", findTag(root,
                "campaign-story-sequence") != null);
        while (findText(root, "CONTINUAR") != null) {
            instrumentation.runOnMainSync(() -> findText(root, "CONTINUAR").performClick());
            instrumentation.waitForIdleSync();
        }
        assertTrue(findText(root, "ENCERRAR CENA") != null);
        instrumentation.runOnMainSync(() -> findText(root, "ENCERRAR CENA").performClick());
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
        Method receipt = MainActivity.class.getDeclaredMethod("showCampaignRewardScreen",
                String.class, int.class, boolean.class);
        receipt.setAccessible(true);
        instrumentation.runOnMainSync(() -> invoke(receipt, activity, "rupture", 1, false));
        assertTrue(hasText(root, "RECOMPENSA JÁ REGISTRADA"));
        assertFalse(hasText(root, "+3.000"));
        assertFalse(hasText(root, "+840 XP"));
        instrumentation.runOnMainSync(() -> findText(root, "VOLTAR AO NEXUS").performClick());
        Thread.sleep(500);
        instrumentation.waitForIdleSync();
        assertTrue(hasText(root, "Saldo: 3.000 créditos · 840 XP"));
        instrumentation.runOnMainSync(activity::finish);
    }

    private static boolean shouldAssertLiveEditorial() {
        return InstrumentationRegistry.getArguments().getBoolean("liveEditorial", false);
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
        LovableBattle battleState = new LovableBattle(29);
        for (LovableBattle.Fighter fighter : battleState.fighters()) fighter.health = 1;
        battleState.choose(LovableBattle.Choice.DEFEND);
        battleState.selectFighter("wolverine");
        battleState.choose(LovableBattle.Choice.DEFEND);
        battleState.selectFighter("tocha-humana");
        battleState.choose(LovableBattle.Choice.DEFEND);
        instrumentation.runOnMainSync(() -> invoke(battle, activity, battleState, campaign));
        instrumentation.waitForIdleSync();
        assertTrue(hasText(root, "Homem-Aranha"));
        assertTrue(hasText(root, "Wolverine"));
        assertTrue(hasText(root, "Tocha Humana"));
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

    @Test public void fragmentShopIsReachableFromTopAndShowsLockedStoneCatalog() throws Exception {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        MainActivity activity = (MainActivity) instrumentation.startActivitySync(
                new Intent(instrumentation.getTargetContext(), MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        Method shell = MainActivity.class.getDeclaredMethod("renderShell");
        shell.setAccessible(true);
        instrumentation.runOnMainSync(() -> invoke(shell, activity));
        View root = activity.getWindow().getDecorView();
        TextView shop = findText(root, "◈  LOJA");
        assertTrue(shop != null);
        instrumentation.runOnMainSync(shop::performClick);
        for (int attempt = 0; attempt < 30 && !hasText(root, "JOIA DO ESPAÇO"); attempt++) {
            Thread.sleep(100);
            instrumentation.waitForIdleSync();
        }
        assertTrue(hasText(root, "LOJA DE FRAGMENTOS"));
        assertTrue(hasText(root, "ESPAÇO"));
        assertTrue(hasText(root, "4.720 XP"));
        TextView buy = findText(root, "COMPRAR 1 FRAGMENTO");
        assertTrue(buy != null && !buy.isEnabled());
        assertTrue(findTag(root, "fragment-shop-buy-button") != null);
        assertTrue(buy.getBackground() instanceof AngularPanelDrawable);
        instrumentation.runOnMainSync(activity::finish);
    }

    @Test public void firstChamberEntryExplainsReedAndStrangeAndBackFinishesTheScene() throws Exception {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        MainActivity activity = (MainActivity) instrumentation.startActivitySync(
                new Intent(instrumentation.getTargetContext(), MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        View root = activity.getWindow().getDecorView();
        java.lang.reflect.Field repositoryField = MainActivity.class.getDeclaredField("forgeRepository");
        repositoryField.setAccessible(true);
        ForgeRepository repository = (ForgeRepository) repositoryField.get(activity);
        for (InfinityStone stone : InfinityStone.values()) {
            ContentValues row = new ContentValues();
            row.put("count", 1);
            repository.getWritableDatabase().update("inventory", row,
                    "stone=? AND stage=?", new String[]{stone.name(), ForgeStage.COMPLETE.name()});
        }
        assertTrue(repository.activateGauntlet());
        activity.getPreferences(android.content.Context.MODE_PRIVATE).edit()
                .remove("chamber_briefing_seen_v2").commit();

        java.lang.reflect.Method loadRoster = MainActivity.class.getDeclaredMethod("loadRoster");
        loadRoster.setAccessible(true);
        @SuppressWarnings("unchecked") List<GameCatalogCharacter> roster =
                (List<GameCatalogCharacter>) loadRoster.invoke(activity);
        GameCatalogCharacter wolverine = null;
        for (GameCatalogCharacter character : roster) {
            if ("wolverine".equals(character.id)) wolverine = character;
        }
        assertTrue(wolverine != null);
        java.lang.reflect.Method variants = MainActivity.class.getDeclaredMethod("showCharacterVariants",
                GameCatalogCharacter.class, List.class, GameVariantTier.class);
        variants.setAccessible(true);
        GameCatalogCharacter selected = wolverine;
        instrumentation.runOnMainSync(() -> invoke(variants, activity, selected, roster,
                GameVariantTier.ORIGIN));
        assertTrue(hasText(root, "POR TRÁS DA CÂMARA"));
        instrumentation.runOnMainSync(() -> findText(root, "CONTINUAR").performClick());
        assertTrue(hasText(root, "Meus selos mantêm"));
        instrumentation.runOnMainSync(() -> findText(root, "CONTINUAR").performClick());
        assertTrue(hasText(root, "Reed cuida dos números"));
        instrumentation.runOnMainSync(() -> findText(root, "ENCERRAR CENA").performClick());
        assertTrue(hasText(root, "Wolverine"));
        assertEquals(View.VISIBLE, activity.findViewById(R.id.navigation_bar).getVisibility());
        assertFalse(hasText(root, "POR QUE EXISTE A CÂMARA?"));
        instrumentation.runOnMainSync(() -> invoke(variants, activity, selected, roster,
                GameVariantTier.ORIGIN));
        assertFalse(hasText(root, "POR TRÁS DA CÂMARA"));
        instrumentation.runOnMainSync(activity::finish);
    }

    @Test public void androidBackRunsPostBattleContinuationOnlyOnce() throws Exception {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        MainActivity activity = (MainActivity) instrumentation.startActivitySync(
                new Intent(instrumentation.getTargetContext(), MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        Method story = MainActivity.class.getDeclaredMethod("showStorySequence",
                CampaignStory.Scene.class, List.class, Runnable.class);
        story.setAccessible(true);
        AtomicInteger continuations = new AtomicInteger();
        instrumentation.runOnMainSync(() -> invoke(story, activity,
                CampaignStory.afterMission(1), null, (Runnable) continuations::incrementAndGet));
        instrumentation.runOnMainSync(() -> activity.getOnBackPressedDispatcher().onBackPressed());
        instrumentation.runOnMainSync(() -> activity.getOnBackPressedDispatcher().onBackPressed());
        assertEquals(1, continuations.get());
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

    private static void driveBattleToVictory(LovableBattle battle) {
        int actions = 0;
        LovableBattle.Choice[] counters = {LovableBattle.Choice.DEFEND,
                LovableBattle.Choice.CONTROL, LovableBattle.Choice.ATTACK,
                LovableBattle.Choice.CONTROL, LovableBattle.Choice.ATTACK,
                LovableBattle.Choice.DEFEND};
        while (!battle.victory && !battle.defeat && actions++ < 80) {
            if (battle.replacementRequired) {
                for (LovableBattle.Fighter fighter : battle.fighters()) {
                    if (fighter.alive()) { battle.selectFighter(fighter.spec.id); break; }
                }
            }
            if (battle.canSpecial()) battle.special();
            else battle.choose(counters[(battle.round + battle.mission.counterOffset) % counters.length]);
        }
        if (!battle.victory) throw new AssertionError("Counter strategy did not finish the encounter");
    }

    private static boolean hasText(View view, String target) { return findText(view, target) != null; }

    private static boolean hasContentDescription(View view, String target) {
        CharSequence description = view.getContentDescription();
        if (description != null && description.toString().contains(target)) return true;
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int index = 0; index < group.getChildCount(); index++) {
                if (hasContentDescription(group.getChildAt(index), target)) return true;
            }
        }
        return false;
    }

    private static View findTag(View view, String target) {
        if (target.equals(view.getTag())) return view;
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int index = 0; index < group.getChildCount(); index++) {
                View found = findTag(group.getChildAt(index), target);
                if (found != null) return found;
            }
        }
        return null;
    }

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
