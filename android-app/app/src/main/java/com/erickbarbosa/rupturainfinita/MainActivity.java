package com.erickbarbosa.rupturainfinita;

import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.content.res.AssetManager;
import android.app.AlertDialog;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.activity.OnBackPressedCallback;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.media.MediaPlayer;
public final class MainActivity extends AppCompatActivity {
    private AppDestination selectedDestination = AppDestination.initial();
    private boolean introVisible = true;
    private LinearLayout navigationBar;
    private FrameLayout contentContainer;
    private final ExecutorService forgeExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService editorialExecutor = Executors.newFixedThreadPool(2);
    private ForgeRepository forgeRepository;
    private EditorialPortraitLoader portraitLoader;
    private boolean transientScreen;
    private OnBackPressedCallback transientBack;
    private boolean battleAnimating;
    private boolean battleClaiming;
    private ToneGenerator battleTones;
    private MediaPlayer battleMusic;
    private boolean battleOpen;
    private boolean storyOpen;
    private int deadpoolPortraitTurn;
    private int deadpoolOfflineTurn;
    private Runnable storyFinish;
    private String currentBattleContext = "Nenhuma batalha está em andamento.";
    private String currentTeamContext = "Equipe ainda não escolhida para uma batalha.";
    private BattleMission selectedBattleMission;
    private LovableBattle currentBattle;
    private final Map<String, View> battleFighterViews = new HashMap<>();
    private View battleBossView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean dark = getPreferences(MODE_PRIVATE).getBoolean("dark_mode", true);
        AppCompatDelegate.setDefaultNightMode(dark ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setOnExitAnimationListener(splash -> {
            if (!ValueAnimator.areAnimatorsEnabled()) {
                splash.remove();
                return;
            }
            splash.getIconView().animate().alpha(0f).scaleX(1.14f).scaleY(1.14f)
                    .setDuration(220L)
                    .withEndAction(splash::remove)
                    .start();
        });
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            introVisible = savedInstanceState.getBoolean("intro_visible", true);
            String savedDestination = savedInstanceState.getString("selected_destination");
            if (savedDestination != null) {
                try {
                    selectedDestination = AppDestination.valueOf(savedDestination);
                } catch (IllegalArgumentException ignored) {
                    selectedDestination = AppDestination.initial();
                }
            }
        }

        View root = findViewById(R.id.root);
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, windowInsets) -> {
            Insets systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return windowInsets;
        });

        navigationBar = findViewById(R.id.navigation_bar);
        contentContainer = findViewById(R.id.content_container);
        forgeRepository = new ForgeRepository(this);
        battleTones = new ToneGenerator(AudioManager.STREAM_MUSIC, 68);
        portraitLoader = new EditorialPortraitLoader(backendClient());
        preloadEditorialPortraits();
        transientBack = new OnBackPressedCallback(false) {
            @Override public void handleOnBackPressed() {
                if (storyOpen && storyFinish != null) storyFinish.run();
                else renderShell();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, transientBack);
        if (introVisible) renderIntro(); else renderShell();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("selected_destination", selectedDestination.name());
        outState.putBoolean("intro_visible", introVisible);
        super.onSaveInstanceState(outState);
    }

    private void renderShell() {
        battleOpen = false;
        storyOpen = false;
        storyFinish = null;
        currentBattleContext = "Nenhuma batalha está em andamento.";
        transientScreen = false;
        if (transientBack != null) transientBack.setEnabled(false);
        navigationBar.setVisibility(View.VISIBLE);
        renderNavigation();
        renderDestination(selectedDestination);
        startBattleMusic();
    }

    private void renderIntro() {
        navigationBar.setVisibility(View.GONE);
        contentContainer.removeAllViews();
        LinearLayout intro = new LinearLayout(this);
        intro.setOrientation(LinearLayout.VERTICAL);
        intro.setGravity(Gravity.CENTER_HORIZONTAL);
        intro.setPadding(dimension(R.dimen.space_5), dimension(R.dimen.space_5),
                dimension(R.dimen.space_5), dimension(R.dimen.space_6));
        intro.setBackground(new IntroRiftDrawable(this));

        TextView skip = text(R.string.intro_skip, R.style.TextAppearance_Ruptura_Label,
                R.color.text_primary, true);
        skip.setGravity(Gravity.CENTER);
        skip.setMinimumHeight(dimension(R.dimen.target_min));
        skip.setPadding(dimension(R.dimen.space_4), 0, dimension(R.dimen.space_4), 0);
        skip.setBackground(background(R.color.surface_primary, R.color.surface_primary,
                dimension(R.dimen.radius_pill)));
        skip.setClickable(true);
        skip.setFocusable(true);
        skip.setOnClickListener(view -> enterNexus());
        LinearLayout.LayoutParams skipParams = new LinearLayout.LayoutParams(-2, -2);
        skipParams.gravity = Gravity.END;
        intro.addView(skip, skipParams);

        LinearLayout titleBlock = new LinearLayout(this);
        titleBlock.setOrientation(LinearLayout.VERTICAL);
        titleBlock.setGravity(Gravity.CENTER);
        intro.addView(titleBlock, new LinearLayout.LayoutParams(-1, 0, 1f));
        PlayfulOrbitView orbit = new PlayfulOrbitView(this);
        LinearLayout.LayoutParams orbitParams = new LinearLayout.LayoutParams(
                dimension(R.dimen.space_8) * 7, dimension(R.dimen.space_8) * 7);
        orbitParams.gravity = Gravity.CENTER;
        orbitParams.bottomMargin = dimension(R.dimen.space_4);
        titleBlock.addView(orbit, orbitParams);
        TextView phase = text(R.string.intro_phase, R.style.TextAppearance_Ruptura_Label,
                R.color.play_night, true);
        phase.setAllCaps(true);
        phase.setLetterSpacing(0.04f);
        phase.setGravity(Gravity.CENTER);
        phase.setPadding(dimension(R.dimen.space_4), dimension(R.dimen.space_1),
                dimension(R.dimen.space_4), dimension(R.dimen.space_1));
        phase.setBackground(background(R.color.play_sun, R.color.play_sun,
                dimension(R.dimen.radius_pill)));
        titleBlock.addView(phase);
        for (int label : new int[]{R.string.intro_ruptura, R.string.intro_infinita}) {
            TextView line = text(label, R.style.TextAppearance_Ruptura_Display,
                    label == R.string.intro_infinita ? R.color.accent_cyan : R.color.text_primary, true);
            line.setTextSize(42f);
            line.setGravity(Gravity.CENTER);
            titleBlock.addView(line);
        }
        TextView tagline = text(R.string.intro_tagline, R.style.TextAppearance_Ruptura_Body,
                R.color.text_secondary, false);
        tagline.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams taglineParams = new LinearLayout.LayoutParams(-1, -2);
        taglineParams.topMargin = dimension(R.dimen.space_4);
        titleBlock.addView(tagline, taglineParams);

        TextView enter = action(getString(R.string.intro_enter), this::enterNexus);
        enter.setContentDescription(getString(R.string.intro_enter));
        LinearLayout.LayoutParams enterParams = new LinearLayout.LayoutParams(-1, -2);
        enterParams.topMargin = dimension(R.dimen.space_6);
        intro.addView(enter, enterParams);
        TextView demo = text(R.string.intro_demo, R.style.TextAppearance_Ruptura_Caption,
                R.color.text_secondary, false);
        demo.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams demoParams = new LinearLayout.LayoutParams(-1, -2);
        demoParams.topMargin = dimension(R.dimen.space_3);
        intro.addView(demo, demoParams);
        contentContainer.addView(intro, new FrameLayout.LayoutParams(-1, -1));
    }

    private void enterNexus() {
        introVisible = false;
        selectedDestination = AppDestination.NEXUS;
        if (!getPreferences(MODE_PRIVATE).getBoolean("opening_story_seen", false)) {
            showStorySequence(CampaignStory.opening(), null, () -> {
                getPreferences(MODE_PRIVATE).edit().putBoolean("opening_story_seen", true).apply();
                renderShell();
            });
        } else {
            renderShell();
        }
    }

    private void showStorySequence(CampaignStory.Scene scene, List<String> teamIds, Runnable after) {
        storyOpen = true;
        transientScreen = true;
        if (transientBack != null) transientBack.setEnabled(true);
        navigationBar.setVisibility(View.GONE);
        contentContainer.removeAllViews();
        startBattleMusic();

        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setGravity(Gravity.CENTER_HORIZONTAL);
        page.setTag("campaign-story-sequence");
        page.setPadding(dimension(R.dimen.space_4), dimension(R.dimen.space_5),
                dimension(R.dimen.space_4), dimension(R.dimen.space_4));
        page.setBackground(new IntroRiftDrawable(this));
        contentContainer.addView(page, new FrameLayout.LayoutParams(-1, -1));

        TextView skip = text("PULAR CENA", R.style.TextAppearance_Ruptura_Label,
                R.color.text_secondary, true);
        skip.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        skip.setMinimumHeight(dimension(R.dimen.target_min));
        page.addView(skip, new LinearLayout.LayoutParams(-1, -2));
        LinearLayout.LayoutParams spacerParams = new LinearLayout.LayoutParams(1, 0, .25f);
        page.addView(new View(this), spacerParams);
        TextView place = text(scene.place, R.style.TextAppearance_Ruptura_Label,
                R.color.accent_cyan, true);
        place.setGravity(Gravity.CENTER);
        place.setAllCaps(true);
        place.setLetterSpacing(.18f);
        page.addView(place, new LinearLayout.LayoutParams(-1, -2));
        TextView title = text(scene.title, R.style.TextAppearance_Ruptura_Display,
                R.color.text_primary, true);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(32f);
        page.addView(title, new LinearLayout.LayoutParams(-1, -2));

        FrameLayout portraitFrame = new FrameLayout(this);
        LinearLayout.LayoutParams portraitParams = new LinearLayout.LayoutParams(-1,
                dimension(R.dimen.space_8) * 4);
        portraitParams.topMargin = dimension(R.dimen.space_3);
        portraitParams.bottomMargin = dimension(R.dimen.space_3);
        page.addView(portraitFrame, portraitParams);
        ImageView portrait = new ImageView(this);
        portrait.setScaleType(ImageView.ScaleType.FIT_CENTER);
        portrait.setContentDescription("Arte editorial do personagem em cena");
        portraitFrame.addView(portrait, new FrameLayout.LayoutParams(-1, -1));
        TextView attribution = text(R.string.editorial_portrait_loading,
                R.style.TextAppearance_Ruptura_Caption, R.color.text_secondary, false);
        attribution.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        portraitFrame.addView(attribution, new FrameLayout.LayoutParams(-1, -2, Gravity.BOTTOM));

        TextView speaker = text("", R.style.TextAppearance_Ruptura_Label,
                R.color.accent_gold, true);
        speaker.setGravity(Gravity.CENTER);
        page.addView(speaker, new LinearLayout.LayoutParams(-1, -2));
        TextView dialogue = text("", R.style.TextAppearance_Ruptura_Body,
                R.color.text_primary, false);
        dialogue.setGravity(Gravity.CENTER);
        dialogue.setPadding(dimension(R.dimen.space_4), dimension(R.dimen.space_3),
                dimension(R.dimen.space_4), dimension(R.dimen.space_3));
        dialogue.setBackground(background(R.color.surface_primary, R.color.border_subtle,
                dimension(R.dimen.radius_card)));
        LinearLayout.LayoutParams dialogueParams = new LinearLayout.LayoutParams(-1, -2);
        dialogueParams.topMargin = dimension(R.dimen.space_2);
        page.addView(dialogue, dialogueParams);
        LinearLayout.LayoutParams bottomSpacer = new LinearLayout.LayoutParams(1, 0, .25f);
        page.addView(new View(this), bottomSpacer);
        TextView progress = text("", R.style.TextAppearance_Ruptura_Caption,
                R.color.text_secondary, true);
        progress.setGravity(Gravity.CENTER);
        page.addView(progress, new LinearLayout.LayoutParams(-1, -2));
        TextView next = action("CONTINUAR", () -> { });
        LinearLayout.LayoutParams nextParams = new LinearLayout.LayoutParams(-1, -2);
        nextParams.topMargin = dimension(R.dimen.space_2);
        page.addView(next, nextParams);

        final int[] index = {0};
        final boolean[] finished = {false};
        Runnable finish = () -> {
            if (finished[0]) return;
            finished[0] = true;
            storyOpen = false;
            storyFinish = null;
            if (after != null) after.run();
            else renderShell();
        };
        storyFinish = finish;
        Runnable[] renderLine = new Runnable[1];
        renderLine[0] = () -> {
            if (index[0] >= scene.lines.size()) { finish.run(); return; }
            CampaignStory.Line line = scene.lines.get(index[0]);
            String speakerName = line.speakerName;
            String characterId = line.speakerId;
            if ("team".equals(characterId)) {
                List<GameCatalogCharacter> roster = loadRoster();
                characterId = teamIds != null && !teamIds.isEmpty() ? teamIds.get(0) : "homem-aranha";
                for (GameCatalogCharacter character : roster) {
                    if (character.id.equals(characterId)) { speakerName = character.name.toUpperCase(java.util.Locale.ROOT); break; }
                }
            }
            speaker.setText(speakerName);
            dialogue.setText(line.text);
            progress.setText((index[0] + 1) + " / " + scene.lines.size());
            next.setText(index[0] + 1 == scene.lines.size() ? "ENCERRAR CENA" : "CONTINUAR");
            if (line.opponent) portraitLoader.loadOpponent(characterId, speakerName, portrait, attribution);
            else portraitLoader.load(characterId, speakerName, portrait, attribution);
            if (ValueAnimator.areAnimatorsEnabled()) {
                portrait.animate().cancel();
                portrait.setAlpha(0f);
                portrait.setScaleX(.94f);
                portrait.setScaleY(.94f);
                portrait.animate().alpha(1f).scaleX(1f).scaleY(1f)
                        .setDuration(animationDelay(300)).start();
                speaker.animate().cancel();
                speaker.setAlpha(0f);
                speaker.animate().alpha(1f).setDuration(animationDelay(180)).start();
                dialogue.animate().cancel();
                dialogue.setAlpha(0f);
                dialogue.setTranslationY(dimension(R.dimen.space_2));
                dialogue.animate().alpha(1f).translationY(0f).setDuration(animationDelay(240)).start();
            }
        };
        next.setOnClickListener(view -> { playUiSound(); index[0]++; renderLine[0].run(); });
        skip.setOnClickListener(view -> { playUiSound(); finish.run(); });
        renderLine[0].run();
    }

    private void renderNavigation() {
        navigationBar.removeAllViews();
        navigationBar.setBackgroundColor(getColor(R.color.nav_surface));
        navigationBar.setElevation(dimension(R.dimen.space_2));
        for (AppDestination destination : AppDestination.values()) {
            boolean selected = destination == selectedDestination;
            int selectedColor = destination == AppDestination.DEADPOOL
                    ? R.color.accent_deadpool : R.color.accent_cyan;
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.VERTICAL);
            item.setGravity(Gravity.CENTER);
            item.setMinimumHeight(dimension(R.dimen.nav_height));
            item.setPadding(0, dimension(R.dimen.space_2), 0, dimension(R.dimen.space_1));
            item.setFocusable(true);
            item.setClickable(true);
            item.setSelected(selected);
            item.setBackgroundColor(getColor(R.color.nav_surface));

            ImageView icon = new ImageView(this);
            icon.setImageResource(destination.iconRes);
            icon.setColorFilter(getColor(selected ? selectedColor : R.color.text_secondary));
            icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            icon.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);

            TextView label = new TextView(this);
            label.setText(destination.labelRes);
            label.setTextAppearance(R.style.TextAppearance_Ruptura_Caption);
            label.setTextColor(getColor(selected ? selectedColor : R.color.text_secondary));
            label.setTypeface(getResources().getFont(R.font.sora_variable), Typeface.BOLD);
            label.setGravity(Gravity.CENTER);
            label.setMaxLines(2);
            label.setAllCaps(true);
            label.setLetterSpacing(0.14f);
            label.setIncludeFontPadding(false);

            item.addView(icon, new LinearLayout.LayoutParams(-1, dimension(R.dimen.nav_icon_height)));
            item.addView(label, new LinearLayout.LayoutParams(-1, -2));
            View indicator = new View(this);
            indicator.setBackgroundColor(selected ? getColor(selectedColor) : getColor(R.color.nav_surface));
            indicator.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
            LinearLayout.LayoutParams indicatorParams = new LinearLayout.LayoutParams(
                    dimension(R.dimen.space_6), dimension(R.dimen.border_width) * 2);
            indicatorParams.topMargin = dimension(R.dimen.space_1);
            item.addView(indicator, indicatorParams);
            String spokenLabel = getString(destination.labelRes);
            item.setContentDescription(selected
                    ? getString(R.string.nav_selected, spokenLabel)
                    : spokenLabel);
            item.setOnClickListener(view -> {
                selectDestination(destination);
            });

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, -2, 1f);
            navigationBar.addView(item, params);
        }
    }

    private void renderDestination(AppDestination destination) {
        contentContainer.removeAllViews();
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(getColor(destination == AppDestination.DEADPOOL
                ? R.color.deadpool_paper : R.color.canvas));
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        LinearLayout themeRow = new LinearLayout(this);
        themeRow.setGravity(Gravity.CENTER_VERTICAL);
        page.addView(themeRow, new LinearLayout.LayoutParams(-1, -2));
        addShopButton(themeRow);
        themeRow.addView(new View(this), new LinearLayout.LayoutParams(0, 1, 1f));
        addThemeToggle(themeRow);
        addSoundToggle(themeRow);
        if (destination == AppDestination.NEXUS) {
            page.setPadding(0, 0, 0, dimension(R.dimen.space_6));
            renderNexusHero(page);
        } else if (destination == AppDestination.DEADPOOL) {
            page.setPadding(dimension(R.dimen.space_4), dimension(R.dimen.space_4),
                    dimension(R.dimen.space_4), dimension(R.dimen.space_6));
            renderDeadpoolHeader(page);
        } else {
            page.setPadding(dimension(R.dimen.space_4), dimension(R.dimen.space_4),
                    dimension(R.dimen.space_4), dimension(R.dimen.space_6));
            renderPageHeader(page, destination);
        }

        if (destination == AppDestination.CAMPAIGNS) {
            renderCampaigns(page);
        }
        if (destination == AppDestination.NEXUS) {
            LinearLayout nexusContent = new LinearLayout(this);
            nexusContent.setOrientation(LinearLayout.VERTICAL);
            nexusContent.setPadding(dimension(R.dimen.space_4), 0,
                    dimension(R.dimen.space_4), 0);
            page.addView(nexusContent);
            renderNexusResources(nexusContent);
            renderNexusGauntletPreview(nexusContent);
            renderNexusNextMission(nexusContent);
            renderNexusShortcuts(nexusContent);
            renderNexusChamberCard(nexusContent);
        }
        if (destination == AppDestination.FORGE) {
            renderForgeInventory(page);
            renderGauntlet(page);
        }
        if (destination == AppDestination.COLLECTION) {
            renderGameCatalog(page);
        }
        if (destination == AppDestination.DEADPOOL) {
            renderDeadpool(page);
        }

        scroll.addView(page);
        contentContainer.addView(scroll, new FrameLayout.LayoutParams(-1, -1));
        startBattleMusic();
    }

    private void addThemeToggle(LinearLayout parent) {
        boolean dark = isDarkMode();
        TextView toggle = text(dark ? "☼  CLARO" : "☾  ESCURO",
                R.style.TextAppearance_Ruptura_Label, R.color.accent_gold, true);
        toggle.setGravity(Gravity.CENTER);
        toggle.setMinimumHeight(dimension(R.dimen.target_min));
        toggle.setPadding(dimension(R.dimen.space_2), 0, dimension(R.dimen.space_2), 0);
        toggle.setBackground(background(R.color.surface_primary, R.color.border_subtle,
                dimension(R.dimen.radius_pill)));
        toggle.setFocusable(true);
        toggle.setClickable(true);
        toggle.setContentDescription(dark ? "Ativar modo claro" : "Ativar modo escuro");
        toggle.setOnClickListener(view -> {
            getPreferences(MODE_PRIVATE).edit().putBoolean("dark_mode", !dark).apply();
            AppCompatDelegate.setDefaultNightMode(!dark ? AppCompatDelegate.MODE_NIGHT_YES
                    : AppCompatDelegate.MODE_NIGHT_NO);
            recreate();
        });
        parent.addView(toggle, new LinearLayout.LayoutParams(-2, -2));
    }

    private void addSoundToggle(LinearLayout parent) {
        boolean enabled = getPreferences(MODE_PRIVATE).getBoolean("battle_sounds", true);
        TextView toggle = text(enabled ? "♫  ON" : "♫  OFF",
                R.style.TextAppearance_Ruptura_Label,
                enabled ? R.color.accent_cyan : R.color.text_secondary, true);
        toggle.setGravity(Gravity.CENTER);
        toggle.setMinimumHeight(dimension(R.dimen.target_min));
        toggle.setPadding(dimension(R.dimen.space_2), 0, dimension(R.dimen.space_2), 0);
        toggle.setBackground(background(R.color.surface_primary, R.color.border_subtle,
                dimension(R.dimen.radius_pill)));
        toggle.setContentDescription(enabled ? "Desativar sons e música" : "Ativar sons e música");
        toggle.setFocusable(true); toggle.setClickable(true);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-2, -2);
        p.leftMargin = dimension(R.dimen.space_2);
        parent.addView(toggle, p);
        toggle.setOnClickListener(view -> {
            getPreferences(MODE_PRIVATE).edit().putBoolean("battle_sounds", !enabled).apply();
            if (enabled) stopBattleMusic();
            renderShell();
        });
    }

    private void playBattleSound(int sound) {
        if (!getPreferences(MODE_PRIVATE).getBoolean("battle_sounds", true) || battleTones == null) return;
        battleTones.startTone(sound, 115);
    }

    private void playUiSound() {
        if (!getPreferences(MODE_PRIVATE).getBoolean("battle_sounds", true) || battleTones == null) return;
        battleTones.startTone(ToneGenerator.TONE_PROP_ACK, 90);
    }

    private void addShopButton(LinearLayout parent) {
        TextView shop = text("◈ LOJA", R.style.TextAppearance_Ruptura_Label,
                R.color.accent_gold, true);
        shop.setGravity(Gravity.CENTER);
        shop.setMinimumHeight(dimension(R.dimen.target_min));
        shop.setPadding(dimension(R.dimen.space_2), 0, dimension(R.dimen.space_2), 0);
        shop.setBackground(background(R.color.surface_primary, R.color.accent_gold,
                dimension(R.dimen.radius_pill)));
        shop.setContentDescription("Abrir loja de fragmentos");
        shop.setFocusable(true);
        shop.setClickable(true);
        shop.setOnClickListener(view -> {
            playUiSound();
            showFragmentShopScreen();
        });
        parent.addView(shop, new LinearLayout.LayoutParams(-2, -2));
    }

    private void showFragmentShopScreen() {
        transientScreen = true;
        transientBack.setEnabled(true);
        navigationBar.setVisibility(View.VISIBLE);
        contentContainer.removeAllViews();
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(getColor(R.color.canvas));
        contentContainer.addView(scroll, new FrameLayout.LayoutParams(-1, -1));
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dimension(R.dimen.space_4), dimension(R.dimen.space_4),
                dimension(R.dimen.space_4), dimension(R.dimen.space_6));
        scroll.addView(page);

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        page.addView(header, new LinearLayout.LayoutParams(-1, -2));
        TextView back = text("‹", R.style.TextAppearance_Ruptura_Title,
                R.color.text_primary, true);
        back.setGravity(Gravity.CENTER);
        back.setMinWidth(dimension(R.dimen.target_min));
        back.setMinHeight(dimension(R.dimen.target_min));
        back.setContentDescription("Voltar ao jogo");
        back.setBackground(background(R.color.surface_primary, R.color.border_subtle,
                dimension(R.dimen.radius_pill)));
        back.setOnClickListener(view -> renderShell());
        header.addView(back);
        TextView title = text("LOJA DE FRAGMENTOS", R.style.TextAppearance_Ruptura_Display,
                R.color.text_primary, true);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(0, -2, 1f);
        titleParams.leftMargin = dimension(R.dimen.space_3);
        header.addView(title, titleParams);
        page.addView(text("XP libera novas cores. Créditos compram um Fragmento por vez; as fusões continuam na Forja.",
                R.style.TextAppearance_Ruptura_Body, R.color.text_secondary, false));

        LinearLayout state = new LinearLayout(this);
        state.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams stateParams = new LinearLayout.LayoutParams(-1, -2);
        stateParams.topMargin = dimension(R.dimen.space_3);
        page.addView(state, stateParams);
        state.addView(text("CARREGANDO SALDO E CATÁLOGO…",
                R.style.TextAppearance_Ruptura_Caption, R.color.text_secondary, false));
        forgeExecutor.execute(() -> {
            PlayerResources resources = forgeRepository.loadPlayerResources();
            ForgeInventory inventory = forgeRepository.load();
            runOnUiThread(() -> {
                if (state.getParent() == page) renderFragmentShopState(state, resources, inventory);
            });
        });
    }

    private void renderFragmentShopState(LinearLayout state, PlayerResources resources,
                                        ForgeInventory inventory) {
        state.removeAllViews();
        LinearLayout balance = card();
        state.addView(balance, new LinearLayout.LayoutParams(-1, -2));
        balance.addView(text("SALDO DISPONÍVEL", R.style.TextAppearance_Ruptura_Label,
                R.color.accent_gold, true));
        balance.addView(text(formatAmount(resources.credits) + " créditos  ·  "
                        + formatAmount(resources.xp) + " XP",
                R.style.TextAppearance_Ruptura_Title, R.color.text_primary, true));

        for (InfinityStone stone : InfinityStone.values()) {
            FragmentShopOffer offer = FragmentShopOffer.forStone(stone);
            LinearLayout item = card();
            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(-1, -2);
            itemParams.topMargin = dimension(R.dimen.space_3);
            state.addView(item, itemParams);
            item.addView(text(getString(stone.labelRes).toUpperCase(java.util.Locale.ROOT),
                    R.style.TextAppearance_Ruptura_Label, stoneColor(stone), true));
            item.addView(text(inventory.count(stone, ForgeStage.FRAGMENT) + " Fragmentos · "
                            + formatAmount(offer.priceCredits) + " créditos cada",
                    R.style.TextAppearance_Ruptura_Body, R.color.text_primary, false));
            boolean unlocked = offer.isUnlocked(resources.xp);
            boolean canBuy = unlocked && resources.credits >= offer.priceCredits
                    && inventory.count(stone, ForgeStage.FRAGMENT) < ForgePolicy.MAX_COUNT;
            String stateText = unlocked ? "Disponível com " + offer.minimumXp + " XP"
                    : "Bloqueado · requer " + formatAmount(offer.minimumXp) + " XP";
            item.addView(text(stateText, R.style.TextAppearance_Ruptura_Caption,
                    unlocked ? R.color.text_secondary : R.color.accent_gold, false));
            String buyLabel = !unlocked ? "DESBLOQUEIA COM XP"
                    : inventory.count(stone, ForgeStage.FRAGMENT) >= ForgePolicy.MAX_COUNT
                    ? "INVENTÁRIO CHEIO" : "COMPRAR 1 FRAGMENTO";
            TextView buy = shopButton(buyLabel, canBuy,
                    () -> purchaseShopFragment(stone, state));
            buy.setTag("fragment-shop-buy-button");
            LinearLayout.LayoutParams buyParams = new LinearLayout.LayoutParams(-1, -2);
            buyParams.topMargin = dimension(R.dimen.space_2);
            item.addView(buy, buyParams);
            if (unlocked && resources.credits < offer.priceCredits) {
                item.addView(text("Créditos insuficientes para esta compra.",
                        R.style.TextAppearance_Ruptura_Caption, R.color.text_secondary, false));
            }
        }
    }

    private void purchaseShopFragment(InfinityStone stone, LinearLayout state) {
        String operationId = java.util.UUID.randomUUID().toString();
        forgeExecutor.execute(() -> {
            try {
                boolean purchased = forgeRepository.purchaseFragment(operationId, stone);
                PlayerResources updatedResources = forgeRepository.loadPlayerResources();
                ForgeInventory updatedInventory = forgeRepository.load();
                runOnUiThread(() -> {
                    if (state.getParent() == null) return;
                    renderFragmentShopState(state, updatedResources, updatedInventory);
                    android.widget.Toast.makeText(this, purchased ? "Fragmento adicionado à Forja"
                                    : "Compra já registrada", android.widget.Toast.LENGTH_SHORT).show();
                });
            } catch (ForgeException error) {
                PlayerResources updatedResources = forgeRepository.loadPlayerResources();
                ForgeInventory updatedInventory = forgeRepository.load();
                runOnUiThread(() -> {
                    if (state.getParent() != null)
                        renderFragmentShopState(state, updatedResources, updatedInventory);
                    String message = error.reason == ForgeException.Reason.INSUFFICIENT_CREDITS
                            ? "Créditos insuficientes" : error.reason == ForgeException.Reason.INSUFFICIENT_XP
                            ? "Ainda falta XP para liberar esta Joia" : "Inventário cheio";
                    android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private boolean isDarkMode() {
        return getPreferences(MODE_PRIVATE).getBoolean("dark_mode", true);
    }

    private TextView shopButton(String label, boolean enabled, Runnable task) {
        TextView button = action(label, task);
        button.setEnabled(enabled);
        button.setAlpha(enabled ? 1f : .55f);
        return button;
    }

    private void startBattleMusic() {
        boolean enabled = getPreferences(MODE_PRIVATE).getBoolean("battle_sounds", true);
        boolean ambientDestination = selectedDestination == AppDestination.NEXUS
                || selectedDestination == AppDestination.CAMPAIGNS
                || selectedDestination == AppDestination.FORGE
                || selectedDestination == AppDestination.COLLECTION
                || selectedDestination == AppDestination.DEADPOOL;
        if (!enabled || (!battleOpen && !ambientDestination)) { stopBattleMusic(); return; }
        if (battleMusic == null) {
            battleMusic = MediaPlayer.create(this, R.raw.battle_ambience);
            if (battleMusic == null) return;
            battleMusic.setLooping(true);
            battleMusic.start();
        }
        float volume = battleOpen ? .52f : .24f;
        battleMusic.setVolume(volume, volume);
    }

    private void stopBattleMusic() {
        if (battleMusic == null) return;
        if (battleMusic.isPlaying()) battleMusic.stop();
        battleMusic.release();
        battleMusic = null;
    }

    @Override protected void onPause() {
        stopBattleMusic();
        super.onPause();
    }

    @Override protected void onResume() {
        super.onResume();
        startBattleMusic();
    }

    private void preloadEditorialPortraits() {
        List<String> ids = new ArrayList<>();
        for (String id : new String[]{"homem-aranha", "wolverine", "tocha-humana",
                "doutor-estranho", "senhor-fantastico", "deadpool"}) {
            if (!ids.contains(id)) ids.add(id);
        }
        for (BattleMission mission : BattleMission.ALL) {
            String id = "battle:" + mission.opponentId;
            if (!ids.contains(id)) ids.add(id);
        }
        for (GameCatalogCharacter character : loadRoster()) {
            if (!ids.contains(character.id)) ids.add(character.id);
        }
        portraitLoader.prefetchAll(ids);
    }

    private void renderPageHeader(LinearLayout page, AppDestination destination) {
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        TextView back = text("‹", R.style.TextAppearance_Ruptura_Title, R.color.text_primary, true);
        back.setTextSize(30f);
        back.setGravity(Gravity.CENTER);
        back.setBackground(background(R.color.surface_primary, R.color.border_subtle,
                dimension(R.dimen.radius_pill)));
        back.setClickable(true);
        back.setFocusable(true);
        back.setContentDescription(getString(R.string.header_back));
        back.setOnClickListener(view -> selectDestination(AppDestination.NEXUS));
        header.addView(back, new LinearLayout.LayoutParams(dimension(R.dimen.target_min),
                dimension(R.dimen.target_min)));
        LinearLayout labels = new LinearLayout(this);
        labels.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams labelsParams = new LinearLayout.LayoutParams(0, -2, 1f);
        labelsParams.leftMargin = dimension(R.dimen.space_3);
        header.addView(labels, labelsParams);
        int kickerRes = destination == AppDestination.FORGE ? R.string.forge_kicker
                : destination == AppDestination.CAMPAIGNS ? R.string.campaigns_kicker
                : R.string.collection_kicker;
        TextView kicker = text(kickerRes, R.style.TextAppearance_Ruptura_Label,
                R.color.accent_cyan, true);
        kicker.setAllCaps(true);
        kicker.setLetterSpacing(0.06f);
        labels.addView(kicker);
        int titleRes = destination == AppDestination.FORGE ? R.string.forge_page_title
                : destination == AppDestination.CAMPAIGNS ? R.string.campaigns_page_title
                : R.string.nav_collection;
        TextView title = text(titleRes, R.style.TextAppearance_Ruptura_Display,
                R.color.text_primary, true);
        title.setAllCaps(false);
        title.setSingleLine(true);
        title.setEllipsize(android.text.TextUtils.TruncateAt.END);
        labels.addView(title);
        if (destination == AppDestination.COLLECTION) {
            TextView compare = text(R.string.collection_compare_action,
                    R.style.TextAppearance_Ruptura_Label, R.color.accent_cyan, true);
            compare.setAllCaps(true);
            compare.setGravity(Gravity.CENTER);
            compare.setMinHeight(dimension(R.dimen.target_min));
            compare.setPadding(dimension(R.dimen.space_2), 0,
                    dimension(R.dimen.space_2), 0);
            compare.setBackground(background(R.color.surface_selected,
                    R.color.accent_cyan, 0));
            compare.setClickable(true);
            compare.setFocusable(true);
            compare.setContentDescription(getString(R.string.collection_compare_full));
            compare.setOnClickListener(view -> showVariantComparison(loadRoster()));
            header.addView(compare, new LinearLayout.LayoutParams(-2, -2));
        }
        LinearLayout.LayoutParams headerParams = new LinearLayout.LayoutParams(-1, -2);
        headerParams.bottomMargin = dimension(R.dimen.space_4);
        page.addView(header, headerParams);
    }

    private void renderNexusHero(LinearLayout page) {
        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setPadding(dimension(R.dimen.space_5), dimension(R.dimen.space_3),
                dimension(R.dimen.space_5), dimension(R.dimen.space_4));
        TextView kicker = text("Olá, herói!", R.style.TextAppearance_Ruptura_Body,
                R.color.text_secondary, true);
        hero.addView(kicker);
        TextView title = text("Pronto para explorar?", R.style.TextAppearance_Ruptura_Display,
                R.color.text_primary, true);
        title.setTextSize(30f);
        title.setMaxWidth(dimension(R.dimen.space_8) * 9);
        hero.addView(title);
        page.addView(hero, new LinearLayout.LayoutParams(-1, -2));
    }

    private void addNexusCompanion(LinearLayout people, String gameId, String name, int labelRes) {
        LinearLayout companion = new LinearLayout(this);
        companion.setOrientation(LinearLayout.VERTICAL);
        companion.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams companionParams = new LinearLayout.LayoutParams(0, -2, 1f);
        companionParams.leftMargin = dimension(R.dimen.space_1);
        companionParams.rightMargin = dimension(R.dimen.space_1);
        people.addView(companion, companionParams);
        ImageView portrait = new ImageView(this);
        portrait.setScaleType(ImageView.ScaleType.FIT_CENTER);
        portrait.setContentDescription("Imagem Comic Vine de " + name);
        portrait.setBackground(background(R.color.surface_primary, R.color.border_subtle,
                dimension(R.dimen.radius_card)));
        companion.addView(portrait, new LinearLayout.LayoutParams(-1,
                dimension(R.dimen.space_8) * 3));
        TextView label = text(labelRes, R.style.TextAppearance_Ruptura_Caption,
                R.color.text_primary, true);
        label.setGravity(Gravity.CENTER);
        companion.addView(label, new LinearLayout.LayoutParams(-1, -2));
        TextView credit = text(R.string.editorial_portrait_loading,
                R.style.TextAppearance_Ruptura_Caption, R.color.text_secondary, false);
        credit.setGravity(Gravity.CENTER);
        credit.setMinHeight(dimension(R.dimen.target_min));
        companion.addView(credit, new LinearLayout.LayoutParams(-1, -2));
        portraitLoader.load(gameId, name, portrait, credit);
    }

    private void renderNexusResources(LinearLayout page) {
        LinearLayout stats = new LinearLayout(this);
        stats.setGravity(Gravity.CENTER_VERTICAL);
        page.addView(stats, new LinearLayout.LayoutParams(-1, -2));
        TextView xp = addNexusStat(stats, "✦", "XP", R.color.play_mint);
        TextView stones = addNexusStat(stats, "⬡", "JOIAS", R.color.play_sun);
        TextView credits = addNexusStat(stats, "◉", "CRÉDITOS", R.color.play_coral);
        forgeExecutor.execute(() -> {
            PlayerResources resources = forgeRepository.loadPlayerResources();
            ForgeInventory inventory = forgeRepository.load();
            int completed = 0;
            for (InfinityStone stone : InfinityStone.values()) {
                if (inventory.count(stone, ForgeStage.COMPLETE) > 0) completed++;
            }
            int count = completed;
            runOnUiThread(() -> {
                if (stats.getParent() != page) return;
                xp.setText(formatAmount(resources.xp));
                stones.setText(count + "/6");
                credits.setText(formatAmount(resources.credits));
            });
        });
    }

    private TextView addNexusStat(LinearLayout parent, String icon, String label, int accent) {
        LinearLayout stat = new LinearLayout(this);
        stat.setOrientation(LinearLayout.VERTICAL);
        stat.setGravity(Gravity.CENTER);
        stat.setMinimumHeight(dimension(R.dimen.space_8) * 3);
        stat.setBackground(background(R.color.surface_primary, R.color.border_subtle,
                dimension(R.dimen.radius_card)));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, -2, 1f);
        params.leftMargin = dimension(R.dimen.space_1);
        params.rightMargin = dimension(R.dimen.space_1);
        parent.addView(stat, params);
        TextView iconView = text(icon, R.style.TextAppearance_Ruptura_Title, accent, true);
        iconView.setGravity(Gravity.CENTER);
        iconView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        stat.addView(iconView);
        TextView caption = text(label, R.style.TextAppearance_Ruptura_Caption,
                R.color.text_secondary, true);
        caption.setGravity(Gravity.CENTER);
        stat.addView(caption);
        TextView value = text("…", R.style.TextAppearance_Ruptura_Body,
                R.color.text_primary, true);
        value.setGravity(Gravity.CENTER);
        value.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);
        stat.addView(value);
        return value;
    }

    private String formatAmount(long amount) {
        return String.format(java.util.Locale.forLanguageTag("pt-BR"), "%,d", amount);
    }

    private void renderNexusGauntletPreview(LinearLayout page) {
        LinearLayout preview = card();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.topMargin = dimension(R.dimen.space_4);
        page.addView(preview, params);
        TextView label = text("COLEÇÃO DE JOIAS", R.style.TextAppearance_Ruptura_Label,
                R.color.accent_cyan, true);
        label.setAllCaps(true);
        preview.addView(label);
        TextView count = text("Carregando Joias…", R.style.TextAppearance_Ruptura_Title,
                R.color.text_primary, true);
        preview.addView(count);
        LinearLayout gems = new LinearLayout(this);
        gems.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams gemsParams = new LinearLayout.LayoutParams(-1, -2);
        gemsParams.topMargin = dimension(R.dimen.space_4);
        preview.addView(gems, gemsParams);
        TextView[] gemIcons = new TextView[InfinityStone.values().length];
        for (InfinityStone stone : InfinityStone.values()) {
            LinearLayout slot = new LinearLayout(this);
            slot.setOrientation(LinearLayout.VERTICAL);
            slot.setGravity(Gravity.CENTER);
            gems.addView(slot, new LinearLayout.LayoutParams(0, -2, 1f));
            TextView gem = text("◆", R.style.TextAppearance_Ruptura_Title,
                    stoneColor(stone), true);
            gem.setGravity(Gravity.CENTER);
            gem.setTextSize(26f);
            gem.setBackground(background(R.color.canvas, R.color.border_subtle,
                    dimension(R.dimen.radius_pill)));
            slot.addView(gem, new LinearLayout.LayoutParams(dimension(R.dimen.target_min),
                    dimension(R.dimen.target_min)));
            String name = getString(stone.labelRes).replace("Joia do ", "")
                    .replace("Joia da ", "");
            TextView caption = text(name, R.style.TextAppearance_Ruptura_Caption,
                    R.color.text_secondary, true);
            caption.setGravity(Gravity.CENTER);
            caption.setSingleLine(true);
            slot.addView(caption);
            gemIcons[stone.ordinal()] = gem;
        }
        TextView open = text("Ver manopla  ›", R.style.TextAppearance_Ruptura_Body,
                R.color.accent_cyan, true);
        open.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams openParams = new LinearLayout.LayoutParams(-1, -2);
        openParams.topMargin = dimension(R.dimen.space_3);
        preview.addView(open, openParams);
        preview.setClickable(true);
        preview.setFocusable(true);
        preview.setContentDescription("Abrir: Forja das Joias e Manopla de Contenção");
        preview.setOnClickListener(view -> selectDestination(AppDestination.FORGE));
        forgeExecutor.execute(() -> {
            ForgeInventory inventory = forgeRepository.load();
            int complete = 0;
            boolean[] state = new boolean[InfinityStone.values().length];
            for (InfinityStone stone : InfinityStone.values()) {
                state[stone.ordinal()] = inventory.count(stone, ForgeStage.COMPLETE) > 0;
                if (state[stone.ordinal()]) complete++;
            }
            int total = complete;
            runOnUiThread(() -> {
                if (preview.getParent() != page) return;
                count.setText(total + " de 6 completas!");
                for (InfinityStone stone : InfinityStone.values()) {
                    TextView gem = gemIcons[stone.ordinal()];
                    gem.setAlpha(state[stone.ordinal()] ? 1f : .45f);
                    gem.setContentDescription(getString(stone.labelRes) + (state[stone.ordinal()]
                            ? " completa" : " em formação"));
                }
            });
        });
    }

    private void renderDeadpoolHeader(LinearLayout page) {
        TextView kicker = text(R.string.deadpool_kicker, R.style.TextAppearance_Ruptura_Label,
                R.color.accent_deadpool, true);
        kicker.setAllCaps(true);
        kicker.setLetterSpacing(0.3f);
        page.addView(kicker);
        TextView title = text(R.string.deadpool_page_title, R.style.TextAppearance_Ruptura_Display,
                R.color.deadpool_ink, true);
        title.setTextSize(34f);
        title.setAllCaps(true);
        page.addView(title);
        TextView intro = text(R.string.deadpool_page_intro, R.style.TextAppearance_Ruptura_Body,
                R.color.deadpool_body, false);
        LinearLayout.LayoutParams introParams = new LinearLayout.LayoutParams(-1, -2);
        introParams.topMargin = dimension(R.dimen.space_2);
        introParams.bottomMargin = dimension(R.dimen.space_4);
        page.addView(intro, introParams);
    }

    private void renderNexusNextMission(LinearLayout page) {
        LinearLayout panel = card();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.topMargin = dimension(R.dimen.space_4);
        page.addView(panel, params);

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);
        panel.addView(top);
        LinearLayout copy = new LinearLayout(this);
        copy.setOrientation(LinearLayout.VERTICAL);
        top.addView(copy, new LinearLayout.LayoutParams(0, -2, 1f));
        TextView chip = text("PRÓXIMA MISSÃO", R.style.TextAppearance_Ruptura_Caption,
                R.color.play_night, true);
        chip.setPadding(dimension(R.dimen.space_3), dimension(R.dimen.space_1),
                dimension(R.dimen.space_3), dimension(R.dimen.space_1));
        chip.setBackground(background(R.color.play_coral, R.color.play_coral,
                dimension(R.dimen.radius_pill)));
        copy.addView(chip, new LinearLayout.LayoutParams(-2, -2));
        TextView title = text("Preparando aventura…", R.style.TextAppearance_Ruptura_Title,
                R.color.text_primary, true);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(-1, -2);
        titleParams.topMargin = dimension(R.dimen.space_2);
        copy.addView(title, titleParams);
        TextView subtitle = text("Seu próximo capítulo está chegando.",
                R.style.TextAppearance_Ruptura_Caption, R.color.text_secondary, false);
        copy.addView(subtitle);

        FrameLayout portraitFrame = new FrameLayout(this);
        portraitFrame.setBackground(background(R.color.play_sun, R.color.play_sun,
                dimension(R.dimen.radius_card)));
        portraitFrame.setClipToOutline(true);
        TextView portraitFallback = text("?", R.style.TextAppearance_Ruptura_Display,
                R.color.play_night, true);
        portraitFallback.setGravity(Gravity.CENTER);
        portraitFrame.addView(portraitFallback, new FrameLayout.LayoutParams(-1, -1));
        ImageView portrait = new ImageView(this);
        portrait.setScaleType(ImageView.ScaleType.CENTER_CROP);
        portrait.setClipToOutline(true);
        portraitFrame.addView(portrait, new FrameLayout.LayoutParams(-1, -1));
        LinearLayout.LayoutParams portraitParams = new LinearLayout.LayoutParams(
                dimension(R.dimen.space_8) * 3, dimension(R.dimen.space_8) * 3);
        portraitParams.leftMargin = dimension(R.dimen.space_2);
        top.addView(portraitFrame, portraitParams);

        TextView description = text("", R.style.TextAppearance_Ruptura_Body,
                R.color.text_secondary, false);
        LinearLayout.LayoutParams descriptionParams = new LinearLayout.LayoutParams(-1, -2);
        descriptionParams.topMargin = dimension(R.dimen.space_3);
        panel.addView(description, descriptionParams);
        android.widget.ProgressBar progress = new android.widget.ProgressBar(this, null,
                android.R.attr.progressBarStyleHorizontal);
        progress.setMax(BattleMission.ALL.size());
        progress.setProgressTintList(android.content.res.ColorStateList.valueOf(
                getColor(R.color.play_sun)));
        progress.setProgressBackgroundTintList(android.content.res.ColorStateList.valueOf(
                getColor(R.color.play_soft)));
        LinearLayout.LayoutParams progressParams = new LinearLayout.LayoutParams(-1,
                dimension(R.dimen.space_2));
        progressParams.topMargin = dimension(R.dimen.space_3);
        panel.addView(progress, progressParams);
        TextView open = action("CONTINUAR MISSÃO  ›",
                () -> selectDestination(AppDestination.CAMPAIGNS));
        panel.addView(open);

        forgeExecutor.execute(() -> {
            BattleMission next = BattleMission.ALL.get(BattleMission.ALL.size() - 1);
            int wins = 0;
            for (BattleMission mission : BattleMission.ALL) {
                if (forgeRepository.hasCompletedMission(mission.campaignId, mission.number)) wins++;
                else if (next == BattleMission.ALL.get(BattleMission.ALL.size() - 1)) next = mission;
            }
            BattleMission selected = next;
            int completed = wins;
            runOnUiThread(() -> {
                if (panel.getParent() != page) return;
                boolean finished = completed == BattleMission.ALL.size();
                chip.setText(finished ? "JORNADA CONCLUÍDA" : "PRÓXIMA MISSÃO");
                title.setText(finished ? "Você fechou a ruptura!" : selected.title);
                subtitle.setText("Capítulo " + selected.number + " · Chefe: "
                        + selected.opponentName);
                description.setText(finished
                        ? "Reviva os capítulos e monte novas equipes para cada batalha."
                        : "Enfrente " + selected.opponentName + " em " + selected.location
                        + ". Escolha heróis desbloqueados para sua equipe.");
                progress.setProgress(completed);
                progress.setContentDescription(completed + " de " + BattleMission.ALL.size()
                        + " capítulos concluídos");
                open.setText(finished ? "REVER CAMPANHAS  ›" : "CONTINUAR MISSÃO  ›");
                portraitFallback.setText(selected.opponentName.substring(0, 1));
                portraitLoader.loadOpponent(selected.opponentId, selected.opponentName,
                        portrait, null);
            });
        });
    }

    private void renderNexusShortcuts(LinearLayout page) {
        TextView heading = text("Escolha uma atividade",
                R.style.TextAppearance_Ruptura_Title, R.color.text_primary, true);
        LinearLayout.LayoutParams headingParams = new LinearLayout.LayoutParams(-1, -2);
        headingParams.topMargin = dimension(R.dimen.space_5);
        page.addView(heading, headingParams);
        LinearLayout tiles = new LinearLayout(this);
        LinearLayout.LayoutParams tilesParams = new LinearLayout.LayoutParams(-1, -2);
        tilesParams.topMargin = dimension(R.dimen.space_3);
        page.addView(tiles, tilesParams);
        addNexusActivityTile(tiles, "▱", "Mapa", R.color.play_mint,
                () -> selectDestination(AppDestination.CAMPAIGNS), "Abrir: Campanhas");
        addNexusActivityTile(tiles, "?", "Desafio", R.color.play_sun,
                this::showDailyChallengeScreen, "Abrir: Desafio diário");
        addNexusActivityTile(tiles, "✦", "Joias", R.color.play_coral,
                () -> selectDestination(AppDestination.FORGE), "Abrir: Forja das Joias");
    }

    private void addNexusActivityTile(LinearLayout row, String icon, String title,
                                      int color, Runnable task, String spokenLabel) {
        LinearLayout tile = new LinearLayout(this);
        tile.setOrientation(LinearLayout.VERTICAL);
        tile.setGravity(Gravity.CENTER);
        tile.setMinimumHeight(dimension(R.dimen.space_8) * 3);
        tile.setBackground(background(color, color, dimension(R.dimen.radius_card)));
        tile.setElevation(dimension(R.dimen.space_1));
        tile.setClickable(true);
        tile.setFocusable(true);
        tile.setContentDescription(spokenLabel);
        tile.setOnClickListener(view -> { playUiSound(); task.run(); });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, -2, 1f);
        params.leftMargin = dimension(R.dimen.space_1);
        params.rightMargin = dimension(R.dimen.space_1);
        row.addView(tile, params);
        TextView symbol = text(icon, R.style.TextAppearance_Ruptura_Title,
                R.color.play_night, true);
        symbol.setTextSize(27f);
        symbol.setGravity(Gravity.CENTER);
        symbol.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        tile.addView(symbol);
        TextView label = text(title, R.style.TextAppearance_Ruptura_Body,
                R.color.play_night, true);
        label.setGravity(Gravity.CENTER);
        tile.addView(label);
    }

    private void renderNexusChamberCard(LinearLayout page) {
        LinearLayout chamber = card();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.topMargin = dimension(R.dimen.space_6);
        page.addView(chamber, params);
        chamber.addView(text("CÂMARA DE VARIANTES", R.style.TextAppearance_Ruptura_Label,
                R.color.accent_cyan, true));
        TextView explanation = text("Reed mapeia cada variante. Os selos do Doutor Estranho "
                        + "mantêm as realidades separadas enquanto você usa a Manopla.",
                R.style.TextAppearance_Ruptura_Body, R.color.text_secondary, false);
        LinearLayout.LayoutParams explanationParams = new LinearLayout.LayoutParams(-1, -2);
        explanationParams.topMargin = dimension(R.dimen.space_2);
        chamber.addView(explanation, explanationParams);
        LinearLayout companions = new LinearLayout(this);
        companions.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams companionParams = new LinearLayout.LayoutParams(-1, -2);
        companionParams.topMargin = dimension(R.dimen.space_3);
        chamber.addView(companions, companionParams);
        addNexusCompanion(companions, "senhor-fantastico", "Reed Richards", R.string.nexus_reed);
        addNexusCompanion(companions, "doutor-estranho", "Doutor Estranho", R.string.nexus_strange);
        GauntletConstellationView constellation = new GauntletConstellationView(this);
        LinearLayout.LayoutParams visualParams = new LinearLayout.LayoutParams(-1,
                dimension(R.dimen.space_8) * 4);
        visualParams.topMargin = dimension(R.dimen.space_2);
        chamber.addView(constellation, visualParams);
        chamber.addView(action("VER MANOPLA  ›", () -> selectDestination(AppDestination.FORGE)));
        forgeExecutor.execute(() -> {
            ForgeInventory inventory = forgeRepository.load();
            boolean[] state = new boolean[InfinityStone.values().length];
            for (InfinityStone stone : InfinityStone.values()) {
                state[stone.ordinal()] = inventory.count(stone, ForgeStage.COMPLETE) > 0;
            }
            runOnUiThread(() -> {
                if (chamber.getParent() == page) constellation.setCompleted(state);
            });
        });
    }

    private void renderForgeInventory(LinearLayout page) {
        renderForgeSockets(page);
        LinearLayout inventoryPanel = card();
        LinearLayout.LayoutParams inventoryParams = new LinearLayout.LayoutParams(-1, -2);
        inventoryParams.topMargin = dimension(R.dimen.space_3);
        page.addView(inventoryPanel, inventoryParams);
        TextView inventoryTitle = text(R.string.forge_inventory_title,
                R.style.TextAppearance_Ruptura_Label, R.color.accent_gold, true);
        inventoryTitle.setAllCaps(true);
        inventoryTitle.setLetterSpacing(.16f);
        inventoryPanel.addView(inventoryTitle);
        LinearLayout inventoryGrid = new LinearLayout(this);
        inventoryGrid.setOrientation(LinearLayout.VERTICAL);
        inventoryPanel.addView(inventoryGrid, new LinearLayout.LayoutParams(-1, -2));
        TextView empty = text(R.string.forge_empty_inventory,
                R.style.TextAppearance_Ruptura_Body, R.color.text_secondary, false);
        LinearLayout.LayoutParams emptyParams = new LinearLayout.LayoutParams(-1, -2);
        emptyParams.topMargin = dimension(R.dimen.space_3);
        inventoryPanel.addView(empty, emptyParams);
        refreshForgeInventory(empty, inventoryGrid);
    }

    private void renderForgeSockets(LinearLayout page) {
        LinearLayout sockets = card();
        page.addView(sockets, new LinearLayout.LayoutParams(-1, -2));
        TextView label = text(R.string.forge_socket_heading, R.style.TextAppearance_Ruptura_Label,
                R.color.accent_gold, true);
        label.setAllCaps(true);
        label.setLetterSpacing(.16f);
        sockets.addView(label);
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(-1, -2);
        rowParams.topMargin = dimension(R.dimen.space_3);
        sockets.addView(row, rowParams);
        int[] colors = {R.color.stone_space, R.color.stone_mind, R.color.stone_reality,
                R.color.stone_power, R.color.stone_time, R.color.stone_soul};
        InfinityStone[] stones = InfinityStone.values();
        TextView[] icons = new TextView[stones.length];
        for (int index = 0; index < stones.length; index++) {
            LinearLayout socket = new LinearLayout(this);
            socket.setOrientation(LinearLayout.VERTICAL);
            socket.setGravity(Gravity.CENTER);
            TextView icon = text("⬡", R.style.TextAppearance_Ruptura_Title, colors[index], true);
            icon.setTextSize(27f);
            icon.setGravity(Gravity.CENTER);
            icon.setBackground(background(R.color.surface_primary, R.color.border_subtle,
                    dimension(R.dimen.radius_pill)));
            socket.addView(icon, new LinearLayout.LayoutParams(dimension(R.dimen.target_min),
                    dimension(R.dimen.target_min)));
            icons[index] = icon;
            String stoneName = getString(stones[index].labelRes).replace("Joia do ", "")
                    .replace("Joia da ", "");
            TextView name = text(stoneName, R.style.TextAppearance_Ruptura_Caption,
                    R.color.text_secondary, false);
            name.setGravity(Gravity.CENTER);
            socket.addView(name);
            row.addView(socket, new LinearLayout.LayoutParams(0, -2, 1f));
        }
        TextView openGauntlet = action("ABRIR MANOPLA DE CONTENÇÃO", this::showGauntletScreen);
        LinearLayout.LayoutParams openGauntletParams = new LinearLayout.LayoutParams(-1, -2);
        openGauntletParams.topMargin = dimension(R.dimen.space_3);
        sockets.addView(openGauntlet, openGauntletParams);
        forgeExecutor.execute(() -> {
            ForgeInventory inventory = forgeRepository.load();
            runOnUiThread(() -> {
                for (int index = 0; index < stones.length; index++) {
                    boolean complete = inventory.count(stones[index], ForgeStage.COMPLETE) > 0;
                    icons[index].setBackground(background(complete ? colors[index]
                                    : R.color.surface_primary,
                            complete ? colors[index] : R.color.border_subtle,
                            dimension(R.dimen.radius_pill)));
                    icons[index].setTextColor(getColor(complete ? R.color.canvas : colors[index]));
                    icons[index].setContentDescription(getString(stones[index].labelRes) + ": "
                            + (complete ? getString(R.string.variant_owned)
                            : getString(R.string.variant_locked)));
                }
            });
        });
    }

    private void refreshForgeInventory(TextView empty, LinearLayout inventoryGrid) {
        forgeExecutor.execute(() -> {
            try {
                ForgeInventory inventory = forgeRepository.load();
                runOnUiThread(() -> {
                    boolean hasItems = false;
                    inventoryGrid.removeAllViews();
                    LinearLayout gridRow = null;
                    int shown = 0;
                    int[] colors = {R.color.stone_space, R.color.stone_mind,
                            R.color.stone_reality, R.color.stone_power,
                            R.color.stone_time, R.color.stone_soul};
                    for (InfinityStone stone : InfinityStone.values()) {
                        for (ForgeStage stage : ForgeStage.values()) {
                            int count = inventory.count(stone, stage);
                            if (count <= 0) continue;
                            hasItems = true;
                            if (shown % 2 == 0) {
                                gridRow = new LinearLayout(this);
                                LinearLayout.LayoutParams rowParams =
                                        new LinearLayout.LayoutParams(-1, -2);
                                rowParams.topMargin = dimension(R.dimen.space_2);
                                inventoryGrid.addView(gridRow, rowParams);
                            }
                            boolean canMerge = stage != ForgeStage.COMPLETE
                                    && count >= ForgePolicy.INPUT_COUNT;
                            LinearLayout item = new LinearLayout(this);
                            item.setOrientation(LinearLayout.VERTICAL);
                            item.setGravity(Gravity.CENTER);
                            item.setPadding(dimension(R.dimen.space_2), dimension(R.dimen.space_2),
                                    dimension(R.dimen.space_2), dimension(R.dimen.space_2));
                            item.setMinimumHeight(dimension(R.dimen.target_min)
                                    + dimension(R.dimen.space_4));
                            item.setBackground(background(canMerge ? colors[stone.ordinal()]
                                            : R.color.surface_primary,
                                    canMerge ? colors[stone.ordinal()] : R.color.border_subtle,
                                    dimension(R.dimen.radius_card)));
                            TextView icon = text("\u2726", R.style.TextAppearance_Ruptura_Title,
                                    canMerge ? R.color.canvas : colors[stone.ordinal()], true);
                            icon.setGravity(Gravity.CENTER);
                            item.addView(icon);
                            TextView title = text(getString(stageSingleLabel(stage)) + " \u00b7 "
                                            + getString(stone.labelRes),
                                    R.style.TextAppearance_Ruptura_Caption,
                                    canMerge ? R.color.canvas : R.color.text_secondary, canMerge);
                            title.setGravity(Gravity.CENTER);
                            item.addView(title);
                            TextView amount = text("\u00d7" + count,
                                    R.style.TextAppearance_Ruptura_Title,
                                    canMerge ? R.color.canvas : R.color.text_primary, true);
                            amount.setGravity(Gravity.CENTER);
                            item.addView(amount);
                            if (canMerge) {
                                TextView hint = text("TOQUE PARA FUNDIR",
                                        R.style.TextAppearance_Ruptura_Caption,
                                        R.color.canvas, true);
                                hint.setGravity(Gravity.CENTER);
                                item.addView(hint);
                                item.setFocusable(true);
                                item.setClickable(true);
                                item.setContentDescription(getString(stone.labelRes) + ", "
                                        + getString(stageSingleLabel(stage)) + ": " + count
                                        + ". Tocar para fundir.");
                                item.setOnClickListener(view -> {
                                    playUiSound();
                                    mergeWithEffect(stone, stage);
                                });
                            } else {
                                item.setContentDescription(getString(stone.labelRes) + ", "
                                        + getString(stageSingleLabel(stage)) + ": " + count);
                            }
                            LinearLayout.LayoutParams itemParams =
                                    new LinearLayout.LayoutParams(0, -2, 1f);
                            itemParams.setMargins(dimension(R.dimen.space_1), 0,
                                    dimension(R.dimen.space_1), 0);
                            gridRow.addView(item, itemParams);
                            shown++;
                        }
                    }
                    empty.setVisibility(hasItems ? View.GONE : View.VISIBLE);
                });
            } catch (RuntimeException exception) {
                runOnUiThread(() -> empty.setText(R.string.forge_inventory_load_error));
            }
        });
    }

    private int stageSingleLabel(ForgeStage stage) {
        switch (stage) {
            case SHARD: return R.string.forge_stage_shard_single;
            case FRAGMENT: return R.string.forge_stage_fragment_single;
            case UNSTABLE_CORE: return R.string.forge_stage_core_single;
            default: return R.string.forge_stage_complete_single;
        }
    }

    private void mergeWithEffect(InfinityStone stone, ForgeStage input) {
        forgeExecutor.execute(() -> {
            try {
                forgeRepository.merge(java.util.UUID.randomUUID().toString(), stone, input);
                boolean completedStone = input.next() == ForgeStage.COMPLETE;
                runOnUiThread(() -> {
                    playBattleSound(completedStone ? ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD
                            : ToneGenerator.TONE_PROP_ACK);
                    showForgeMergeAnimation(completedStone, stone, input);
                    contentContainer.postDelayed(() -> {
                        if (selectedDestination == AppDestination.FORGE) renderShell();
                    }, animationDelay(completedStone ? 2600 : 1450));
                });
            } catch (ForgeException exception) {
                runOnUiThread(() -> android.widget.Toast.makeText(this,
                        exception.reason == ForgeException.Reason.INSUFFICIENT_ITEMS
                                ? R.string.forge_insufficient_items
                                : R.string.forge_inventory_full_error,
                        android.widget.Toast.LENGTH_LONG).show());
            }
        });
    }

    private void showForgeMergeAnimation(boolean completedStone, InfinityStone stone,
                                         ForgeStage input) {
        if (animationDelay(1) == 0) return;
        FrameLayout effect = new FrameLayout(this);
        effect.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        effect.setBackgroundColor(completedStone ? 0xAA05070C : 0x4405070C);
        contentContainer.addView(effect, new FrameLayout.LayoutParams(-1, -1));
        if (completedStone) {
            LinearLayout scene = new LinearLayout(this);
            scene.setOrientation(LinearLayout.VERTICAL);
            scene.setGravity(Gravity.CENTER);
            scene.setPadding(dimension(R.dimen.space_4), dimension(R.dimen.space_4),
                    dimension(R.dimen.space_4), dimension(R.dimen.space_4));
            scene.setBackground(new AngularPanelDrawable(getColor(R.color.surface_elevated),
                    getColor(R.color.surface_primary), getColor(stoneColor(stone)),
                    dimension(R.dimen.angular_cut), true));
            FrameLayout.LayoutParams panel = new FrameLayout.LayoutParams(
                    dimension(R.dimen.space_8) * 8, -2, Gravity.CENTER);
            effect.addView(scene, panel);

            FrameLayout arrival = new FrameLayout(this);
            scene.addView(arrival, new LinearLayout.LayoutParams(-1,
                    dimension(R.dimen.space_8) * 5));
            TextView glove = text("🧤", R.style.TextAppearance_Ruptura_Display,
                    R.color.text_primary, true);
            glove.setTextSize(58);
            glove.setGravity(Gravity.CENTER);
            arrival.addView(glove, new FrameLayout.LayoutParams(-1, -1, Gravity.CENTER));
            TextView gem = text("◆", R.style.TextAppearance_Ruptura_Title,
                    stoneColor(stone), true);
            gem.setTextSize(38);
            gem.setGravity(Gravity.CENTER);
            gem.setShadowLayer(dimension(R.dimen.space_2), 0f, 0f,
                    getColor(R.color.accent_gold));
            gem.setElevation(dimension(R.dimen.space_2));
            FrameLayout.LayoutParams gemPosition = new FrameLayout.LayoutParams(-1,
                    dimension(R.dimen.space_8) * 2, Gravity.TOP | Gravity.CENTER_HORIZONTAL);
            arrival.addView(gem, gemPosition);
            gem.setTranslationY(-dimension(R.dimen.space_8) * 2.5f);
            gem.setScaleX(1.5f);
            gem.setScaleY(1.5f);

            TextView caption = text("JOIA FORJADA", R.style.TextAppearance_Ruptura_Label,
                    R.color.accent_gold, true);
            caption.setGravity(Gravity.CENTER);
            caption.setPadding(0, dimension(R.dimen.space_2), 0, 0);
            scene.addView(caption, new LinearLayout.LayoutParams(-1, -2));
            gem.animate().translationY(dimension(R.dimen.space_8) * 1.4f)
                    .scaleX(.8f).scaleY(.8f).setStartDelay(90).setDuration(650);
            glove.setScaleX(.9f);
            glove.setScaleY(.9f);
            glove.animate().scaleX(1.08f).scaleY(1.08f).setStartDelay(590)
                    .setDuration(220).withEndAction(() -> glove.animate()
                            .scaleX(1f).scaleY(1f).setDuration(180));
            scene.setAlpha(0f);
            scene.setScaleX(.86f);
            scene.setScaleY(.86f);
            scene.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(240);
            effect.postDelayed(() -> effect.animate().alpha(0f).setDuration(420)
                    .withEndAction(() -> contentContainer.removeView(effect)), 2300);
            return;
        }

        TextView core = text("\u2727  " + getString(stageSingleLabel(input.next()))
                        .toUpperCase(java.util.Locale.ROOT) + " FORJADO  \u2727",
                R.style.TextAppearance_Ruptura_Title, R.color.accent_gold, true);
        core.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams center = new FrameLayout.LayoutParams(-1,
                dimension(R.dimen.space_8) * 2, Gravity.CENTER);
        effect.addView(core, center);
        for (int i = 0; i < 6; i++) {
            TextView spark = text("✦", R.style.TextAppearance_Ruptura_Title,
                    i % 2 == 0 ? R.color.accent_cyan : R.color.accent_gold, true);
            FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(dimension(R.dimen.space_8),
                    dimension(R.dimen.space_8), Gravity.CENTER);
            effect.addView(spark, p);
            spark.setTranslationX((i - 2.5f) * dimension(R.dimen.space_8) * 1.6f);
            spark.setTranslationY(i % 2 == 0 ? -dimension(R.dimen.space_8) * 2
                    : dimension(R.dimen.space_8) * 2);
            spark.animate().translationX(0).translationY(0).scaleX(.2f).scaleY(.2f)
                    .alpha(0).setDuration(1100);
        }
        core.setScaleX(.7f);
        core.setScaleY(.7f);
        core.animate().scaleX(1.12f).scaleY(1.12f).alpha(0).setDuration(1250)
                .withEndAction(() -> contentContainer.removeView(effect));
    }

    private void renderGameCatalog(LinearLayout page) {
        final List<GameCatalogCharacter> characters;
        try {
            AssetManager assets = getAssets();
            characters = GameCatalogParser.read(assets);
        } catch (IOException | JSONException exception) {
            throw new IllegalStateException("The bundled game catalog is invalid", exception);
        }

        boolean gauntletActive = forgeRepository.isGauntletActivated();
        int ownedCount = 0;
        for (GameCatalogCharacter character : characters) {
            ownedCount += forgeRepository.loadOwnedTiers(character.id, gauntletActive).size();
        }
        LinearLayout progressPanel = card();
        page.addView(progressPanel, new LinearLayout.LayoutParams(-1, -2));
        LinearLayout progressHeading = new LinearLayout(this);
        progressHeading.setGravity(Gravity.CENTER_VERTICAL);
        progressPanel.addView(progressHeading);
        TextView discovered = text(R.string.collection_discovered,
                R.style.TextAppearance_Ruptura_Label, R.color.text_secondary, true);
        discovered.setAllCaps(true);
        progressHeading.addView(discovered, new LinearLayout.LayoutParams(0, -2, 1f));
        progressHeading.addView(text(getString(R.string.collection_discovered_count,
                        ownedCount, characters.size() * 5),
                R.style.TextAppearance_Ruptura_Title, R.color.accent_cyan, true));
        LinearLayout progressBar = new LinearLayout(this);
        LinearLayout.LayoutParams progressParams = new LinearLayout.LayoutParams(-1,
                dimension(R.dimen.space_2));
        progressParams.topMargin = dimension(R.dimen.space_2);
        progressPanel.addView(progressBar, progressParams);
        int total = characters.size() * 5;
        View achieved = new View(this);
        achieved.setBackgroundColor(getColor(R.color.accent_cyan));
        achieved.setVisibility(ownedCount == 0 ? View.GONE : View.VISIBLE);
        progressBar.addView(achieved, new LinearLayout.LayoutParams(0, -1,
                Math.max(0.001f, ownedCount)));
        View remaining = new View(this);
        remaining.setBackgroundColor(getColor(R.color.surface_elevated));
        progressBar.addView(remaining, new LinearLayout.LayoutParams(0, -1,
                Math.max(0.001f, total - ownedCount)));

        android.widget.EditText search = new android.widget.EditText(this);
        search.setSingleLine(true);
        search.setHint(R.string.collection_search_hint);
        search.setTextColor(getColor(R.color.text_primary));
        search.setHintTextColor(getColor(R.color.text_secondary));
        search.setBackground(background(R.color.surface_primary, R.color.border_subtle, 0));
        search.setPadding(dimension(R.dimen.space_3), dimension(R.dimen.space_3),
                dimension(R.dimen.space_3), dimension(R.dimen.space_3));
        LinearLayout.LayoutParams searchParams = new LinearLayout.LayoutParams(-1, -2);
        searchParams.topMargin = dimension(R.dimen.space_3);
        page.addView(search, searchParams);

        android.widget.HorizontalScrollView filterScroll = new android.widget.HorizontalScrollView(this);
        filterScroll.setHorizontalScrollBarEnabled(false);
        LinearLayout filters = new LinearLayout(this);
        filterScroll.addView(filters);
        LinearLayout.LayoutParams filterParams = new LinearLayout.LayoutParams(-1, -2);
        filterParams.topMargin = dimension(R.dimen.space_3);
        page.addView(filterScroll, filterParams);

        LinearLayout gallery = new LinearLayout(this);
        gallery.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams galleryParams = new LinearLayout.LayoutParams(-1, -2);
        galleryParams.topMargin = dimension(R.dimen.space_3);
        page.addView(gallery, galleryParams);

        java.util.Map<String, java.util.Set<GameVariantTier>> ownedByCharacter =
                new java.util.HashMap<>();
        for (GameCatalogCharacter character : characters) {
            ownedByCharacter.put(character.id,
                    forgeRepository.loadOwnedTiers(character.id, gauntletActive));
        }
        String[] selectedGroup = {null};
        boolean[] onlyOwned = {false};
        Runnable update = () -> {
            gallery.removeAllViews();
            String query = search.getText().toString().trim().toLowerCase(java.util.Locale.ROOT);
            int shown = 0;
            LinearLayout pair = null;
            for (GameCatalogCharacter character : characters) {
                    if (selectedGroup[0] != null
                            && !selectedGroup[0].equals(character.groupId)) continue;
                    java.util.Set<GameVariantTier> owned = ownedByCharacter.get(character.id);
                    if (onlyOwned[0] && owned.isEmpty()) continue;
                    GameVariantTier tier = owned.isEmpty() ? GameVariantTier.ORIGIN
                            : tierById(forgeRepository.loadEquippedTier(character.id));
                    GameCatalogVariant variant = null;
                    for (GameCatalogVariant candidate : character.variants) {
                        if (candidate.tier == tier) {
                            variant = candidate;
                            break;
                        }
                    }
                    if (variant == null) continue;
                    if (!query.isEmpty()
                            && !character.name.toLowerCase(java.util.Locale.ROOT).contains(query)
                            && !variant.name.toLowerCase(java.util.Locale.ROOT).contains(query)) {
                        continue;
                    }
                    boolean featured = shown % 5 == 0;
                    LinearLayout variantCard = catalogVariantCard(character, variant,
                            featured, owned, characters);
                    if (featured) {
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
                        params.topMargin = dimension(R.dimen.space_3);
                        gallery.addView(variantCard, params);
                        pair = null;
                    } else {
                        if (pair == null || shown % 5 == 1 || shown % 5 == 3) {
                            pair = new LinearLayout(this);
                            pair.setOrientation(LinearLayout.HORIZONTAL);
                            LinearLayout.LayoutParams rowParams =
                                    new LinearLayout.LayoutParams(-1, -2);
                            rowParams.topMargin = dimension(R.dimen.space_3);
                            gallery.addView(pair, rowParams);
                        }
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, -2, 1f);
                        if (shown % 2 == 0) params.leftMargin = dimension(R.dimen.space_2);
                        else params.rightMargin = dimension(R.dimen.space_2);
                        pair.addView(variantCard, params);
                    }
                    shown++;
            }
            if (shown == 0) {
                gallery.addView(text(R.string.collection_empty,
                        R.style.TextAppearance_Ruptura_Body, R.color.text_secondary, false));
            }
        };
        search.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                update.run();
            }
            @Override public void afterTextChanged(android.text.Editable s) { }
        });
        filterFilters(filters, selectedGroup, onlyOwned, update);
        update.run();
    }

    private LinearLayout catalogVariantCard(GameCatalogCharacter character,
                                              GameCatalogVariant variant, boolean featured,
                                              java.util.Set<GameVariantTier> owned,
                                              List<GameCatalogCharacter> roster) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dimension(R.dimen.space_2), dimension(R.dimen.space_2),
                dimension(R.dimen.space_2), dimension(R.dimen.space_2));
        card.setBackground(background(R.color.surface_elevated, R.color.border_subtle,
                dimension(R.dimen.radius_card)));
        ImageView portrait = editorialImage(card,
                dimension(R.dimen.space_8) * (featured ? 5 : 4));
        TextView source = text(R.string.editorial_portrait_loading,
                R.style.TextAppearance_Ruptura_Caption, R.color.text_secondary, false);
        source.setGravity(Gravity.CENTER_VERTICAL);
        source.setMinimumHeight(dimension(R.dimen.target_min));
        card.addView(source);
        portraitLoader.loadVariant(character.id, variant.tier, character.name, portrait, source);
        TextView name = text(character.name, featured
                        ? R.style.TextAppearance_Ruptura_Title
                        : R.style.TextAppearance_Ruptura_Label,
                R.color.text_primary, true);
        name.setMaxLines(2);
        card.addView(name);
        TextView subtitle = text(getString(groupLabel(character.groupId)) + " · " + variant.name,
                R.style.TextAppearance_Ruptura_Caption, R.color.text_secondary, false);
        subtitle.setMaxLines(2);
        card.addView(subtitle);
        TextView power = text("PODER · JOGO  "
                        + VariantStats.total(VariantStats.forVariant(character.id, variant.tier)),
                R.style.TextAppearance_Ruptura_Caption, R.color.accent_gold, true);
        LinearLayout.LayoutParams powerParams = new LinearLayout.LayoutParams(-1, -2);
        powerParams.topMargin = dimension(R.dimen.space_2);
        card.addView(power, powerParams);
        boolean isOwned = owned.contains(variant.tier);
        TextView state = text(isOwned ? R.string.variant_owned : R.string.variant_locked,
                R.style.TextAppearance_Ruptura_Caption,
                isOwned ? R.color.accent_cyan : R.color.text_secondary, true);
        card.addView(state);
        LinearLayout meter = new LinearLayout(this);
        LinearLayout.LayoutParams meterParams = new LinearLayout.LayoutParams(-1,
                dimension(R.dimen.space_1));
        meterParams.topMargin = dimension(R.dimen.space_2);
        card.addView(meter, meterParams);
        for (GameVariantTier tier : GameVariantTier.values()) {
            View segment = new View(this);
            segment.setBackgroundColor(getColor(owned.contains(tier)
                    ? R.color.accent_cyan : R.color.border_subtle));
            LinearLayout.LayoutParams segmentParams = new LinearLayout.LayoutParams(0, -1, 1f);
            segmentParams.rightMargin = dimension(R.dimen.space_1);
            meter.addView(segment, segmentParams);
        }
        card.setClickable(true);
        card.setFocusable(true);
        card.setContentDescription(character.name + ", " + variant.name + ", "
                + getString(isOwned ? R.string.variant_owned : R.string.variant_locked));
        card.setOnClickListener(view -> showCharacterVariants(character, roster, variant.tier));
        return card;
    }

    private void filterTierFilters(LinearLayout filters, GameVariantTier[] selected,
                                   Runnable update) {
        filters.removeAllViews();
        GameVariantTier[] tiers = {null, GameVariantTier.ORIGIN, GameVariantTier.ASCENSION,
                GameVariantTier.LEGENDARY, GameVariantTier.MULTIVERSAL, GameVariantTier.INFINITY};
        for (GameVariantTier tier : tiers) {
            boolean active = selected[0] == tier;
            TextView chip = text(tier == null ? R.string.collection_all_tiers : tier.labelRes,
                    R.style.TextAppearance_Ruptura_Label,
                    active ? R.color.accent_cyan : R.color.text_secondary, true);
            chip.setAllCaps(true);
            chip.setGravity(Gravity.CENTER);
            chip.setMinHeight(dimension(R.dimen.target_min));
            chip.setPadding(dimension(R.dimen.space_3), 0, dimension(R.dimen.space_3), 0);
            chip.setBackground(background(active ? R.color.surface_selected
                    : R.color.surface_primary, active ? R.color.accent_cyan
                    : R.color.border_subtle, 0));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);
            params.rightMargin = dimension(R.dimen.space_2);
            filters.addView(chip, params);
            chip.setClickable(true);
            chip.setFocusable(true);
            chip.setOnClickListener(view -> {
                selected[0] = tier;
                filterTierFilters(filters, selected, update);
                update.run();
            });
        }
    }

    private void showCharacterVariants(GameCatalogCharacter character,
                                       List<GameCatalogCharacter> roster,
                                       GameVariantTier focusTier) {
        if (forgeRepository.isGauntletActivated()
                && !getPreferences(MODE_PRIVATE).getBoolean("chamber_briefing_seen_v2", false)) {
            showStorySequence(CampaignStory.chamberOpening(), null, () -> {
                getPreferences(MODE_PRIVATE).edit().putBoolean("chamber_briefing_seen_v2", true).apply();
                showCharacterVariants(character, roster, focusTier);
            });
            return;
        }
        navigationBar.setVisibility(View.VISIBLE);
        contentContainer.removeAllViews();
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(getColor(R.color.canvas));
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dimension(R.dimen.space_4), dimension(R.dimen.space_4),
                dimension(R.dimen.space_4), dimension(R.dimen.space_6));
        page.addView(action(getString(R.string.collection_back),
                () -> renderDestination(AppDestination.COLLECTION)));
        TextView title = text(character.name, R.style.TextAppearance_Ruptura_Display,
                R.color.text_primary, true);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(-1, -2);
        titleParams.topMargin = dimension(R.dimen.space_4);
        page.addView(title, titleParams);
        ImageView cover = editorialImage(page, dimension(R.dimen.space_8) * 6);
        TextView source = text(R.string.editorial_portrait_loading,
                R.style.TextAppearance_Ruptura_Caption, R.color.text_secondary, false);
        source.setGravity(Gravity.CENTER_VERTICAL);
        source.setMinimumHeight(dimension(R.dimen.target_min));
        page.addView(source);
        portraitLoader.loadVariant(character.id, focusTier, character.name, cover, source);

        boolean active = forgeRepository.isGauntletActivated();
        boolean characterOwned = forgeRepository.ownsCharacter(character.id);
        java.util.Set<GameVariantTier> owned = forgeRepository.loadOwnedTiers(character.id, active);
        String equippedTier = characterOwned ? forgeRepository.loadEquippedTier(character.id) : null;
        if (!characterOwned) {
            page.addView(text("PERSONAGEM AINDA NÃO DESBLOQUEADO",
                    R.style.TextAppearance_Ruptura_Label, R.color.text_secondary, true));
        } else if (active) {
            page.addView(text(getString(R.string.variant_equipped,
                            variantName(character, tierById(equippedTier))),
                    R.style.TextAppearance_Ruptura_Label, R.color.accent_gold, true));
        } else {
            page.addView(text(R.string.variant_gauntlet_required,
                    R.style.TextAppearance_Ruptura_Caption, R.color.text_secondary, false));
            boolean ready = completeCount(forgeRepository.load()) == InfinityStone.values().length;
            TextView activate = action(ready ? "ATIVAR MANOPLA E ABRIR VARIANTES"
                    : "VER MANOPLA E REQUISITOS", () -> {
                if (!ready) { showGauntletScreen(); return; }
                forgeExecutor.execute(() -> {
                    boolean activated = forgeRepository.activateGauntlet();
                    runOnUiThread(() -> {
                        if (activated || forgeRepository.isGauntletActivated()) {
                            showCharacterVariants(character, roster, focusTier);
                        } else {
                            android.widget.Toast.makeText(this,
                                    R.string.variant_gauntlet_required,
                                    android.widget.Toast.LENGTH_LONG).show();
                        }
                    });
                });
            });
            LinearLayout.LayoutParams activateParams = new LinearLayout.LayoutParams(-1, -2);
            activateParams.topMargin = dimension(R.dimen.space_3);
            page.addView(activate, activateParams);
        }
        LinearLayout detailTabs = new LinearLayout(this);
        detailTabs.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams detailTabsParams = new LinearLayout.LayoutParams(-1, -2);
        detailTabsParams.topMargin = dimension(R.dimen.space_4);
        page.addView(detailTabs, detailTabsParams);
        TextView variantsTab = text("VARIANTES", R.style.TextAppearance_Ruptura_Label,
                R.color.accent_gold, true);
        TextView factsTab = text("CURIOSIDADES", R.style.TextAppearance_Ruptura_Label,
                R.color.text_secondary, true);
        for (TextView tab : new TextView[]{variantsTab, factsTab}) {
            tab.setGravity(Gravity.CENTER);
            tab.setMinHeight(dimension(R.dimen.target_min));
            tab.setFocusable(true); tab.setClickable(true);
            detailTabs.addView(tab, new LinearLayout.LayoutParams(0, -2, 1f));
        }
        LinearLayout detailSections = new LinearLayout(this);
        detailSections.setOrientation(LinearLayout.VERTICAL);
        page.addView(detailSections, new LinearLayout.LayoutParams(-1, -2));
        LinearLayout variantSection = new LinearLayout(this);
        variantSection.setOrientation(LinearLayout.VERTICAL);
        LinearLayout factsSection = new LinearLayout(this);
        factsSection.setOrientation(LinearLayout.VERTICAL);
        detailSections.addView(variantSection);
        detailSections.addView(factsSection);
        factsSection.setVisibility(View.GONE);
        Runnable[] selectFacts = {null};
        selectFacts[0] = () -> {
            boolean showFacts = factsSection.getVisibility() != View.VISIBLE;
            variantSection.setVisibility(showFacts ? View.GONE : View.VISIBLE);
            factsSection.setVisibility(showFacts ? View.VISIBLE : View.GONE);
            variantsTab.setTextColor(getColor(showFacts ? R.color.text_secondary : R.color.accent_gold));
            factsTab.setTextColor(getColor(showFacts ? R.color.accent_gold : R.color.text_secondary));
            variantsTab.setBackground(background(showFacts ? R.color.surface_primary : R.color.surface_selected,
                    showFacts ? R.color.border_subtle : R.color.accent_gold, dimension(R.dimen.radius_card)));
            factsTab.setBackground(background(showFacts ? R.color.surface_selected : R.color.surface_primary,
                    showFacts ? R.color.accent_gold : R.color.border_subtle, dimension(R.dimen.radius_card)));
        };
        factsTab.setOnClickListener(view -> selectFacts[0].run());
        variantsTab.setOnClickListener(view -> {
            if (variantSection.getVisibility() != View.VISIBLE) selectFacts[0].run();
        });
        variantsTab.setTextColor(getColor(R.color.accent_gold));
        variantsTab.setBackground(background(R.color.surface_selected, R.color.accent_gold,
                dimension(R.dimen.radius_card)));
        factsTab.setBackground(background(R.color.surface_primary, R.color.border_subtle,
                dimension(R.dimen.radius_card)));
        LinearLayout stats = card();
        LinearLayout.LayoutParams statsParams = new LinearLayout.LayoutParams(-1, -2);
        statsParams.topMargin = dimension(R.dimen.space_4);
        variantSection.addView(stats, statsParams);
        stats.addView(text("ATRIBUTOS DE JOGO · " + getString(focusTier.labelRes)
                        + (owned.contains(focusTier) ? "" : " · PRÉVIA"),
                R.style.TextAppearance_Ruptura_Label, R.color.accent_gold, true));
        int[] values = VariantStats.forVariant(character.id, focusTier);
        String[] labels = {"VIDA", "ATAQUE", "DEFESA", "VELOCIDADE"};
        for (int i = 0; i < labels.length; i++) {
            TextView stat = text(labels[i] + "  " + values[i],
                    R.style.TextAppearance_Ruptura_Body, R.color.text_primary, false);
            LinearLayout.LayoutParams statParams = new LinearLayout.LayoutParams(-1, -2);
            statParams.topMargin = dimension(R.dimen.space_2);
            stats.addView(stat, statParams);
        }
        stats.addView(text("PODER TOTAL  " + VariantStats.total(values),
                R.style.TextAppearance_Ruptura_Title, R.color.accent_cyan, true));
        renderEditorialFacts(factsSection, character.id);
        for (GameCatalogVariant variant : character.variants) {
            boolean isOwned = owned.contains(variant.tier);
            LinearLayout variantCard = card();
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(-1, -2);
            cardParams.topMargin = dimension(R.dimen.space_3);
            variantSection.addView(variantCard, cardParams);
            variantCard.setBackground(background(variant.tier == focusTier ? R.color.surface_selected
                            : R.color.surface_elevated,
                    variant.tier == focusTier ? R.color.accent_gold : R.color.border_subtle,
                    dimension(R.dimen.radius_card)));
            LinearLayout row = new LinearLayout(this);
            row.setGravity(Gravity.CENTER_VERTICAL);
            variantCard.addView(row);
            variantCard.setClickable(true);
            variantCard.setFocusable(true);
            variantCard.setContentDescription(getString(variant.tier.labelRes) + " · " + variant.name
                    + (isOwned ? " · desbloqueada" : " · bloqueada")
                    + (variant.tier == focusTier ? " · selecionada" : "")
                    + (active && isOwned && !variant.tier.name().equals(equippedTier)
                    ? " · toque para equipar" : ""));
            variantCard.setOnClickListener(view -> {
                if (active && characterOwned && isOwned
                        && !variant.tier.name().equals(forgeRepository.loadEquippedTier(character.id))) {
                    forgeExecutor.execute(() -> {
                        forgeRepository.equipVariant(character.id, variant.tier, roster);
                        runOnUiThread(() -> showCharacterVariants(character, roster, variant.tier));
                    });
                } else showCharacterVariants(character, roster, variant.tier);
            });
            ImageView thumbnail = new ImageView(this);
            thumbnail.setScaleType(ImageView.ScaleType.FIT_CENTER);
            thumbnail.setBackgroundColor(getColor(R.color.surface_primary));
            row.addView(thumbnail, new LinearLayout.LayoutParams(
                    dimension(R.dimen.target_min), dimension(R.dimen.target_min)));
            portraitLoader.loadVariant(character.id, variant.tier, character.name, thumbnail, null);
            String status = getString(isOwned ? R.string.variant_owned : R.string.variant_locked);
            TextView label = text(getString(variant.tier.labelRes) + ": " + variant.name
                            + " · " + status,
                    R.style.TextAppearance_Ruptura_Body,
                    variant.tier == focusTier ? R.color.accent_gold
                            : isOwned ? R.color.accent_cyan : R.color.text_secondary, false);
            LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(0, -2, 1f);
            labelParams.leftMargin = dimension(R.dimen.space_3);
            row.addView(label, labelParams);
            if (active && characterOwned && variant.tier == VariantProgression.next(owned)) {
                variantCard.addView(text("EVOLUÇÃO DISPONÍVEL · ACESSE A MANOPLA",
                        R.style.TextAppearance_Ruptura_Caption, R.color.accent_gold, true));
            }
            if (active && isOwned && !variant.tier.name().equals(equippedTier)) {
                variantCard.addView(text("TOQUE NO CARD PARA EQUIPAR",
                        R.style.TextAppearance_Ruptura_Caption, R.color.accent_cyan, true));
            }
        }
        scroll.addView(page);
        contentContainer.addView(scroll, new FrameLayout.LayoutParams(-1, -1));
    }

    private void renderEditorialFacts(LinearLayout page, String gameId) {
        LinearLayout facts = card();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.topMargin = dimension(R.dimen.space_4);
        page.addView(facts, params);
        facts.addView(text("CURIOSIDADES · COMIC VINE",
                R.style.TextAppearance_Ruptura_Label, R.color.accent_cyan, true));
        loadEditorialFacts(facts, gameId);
    }

    private void loadEditorialFacts(LinearLayout facts, String gameId) {
        while (facts.getChildCount() > 1) facts.removeViewAt(facts.getChildCount() - 1);
        facts.addView(text("Carregando fatos editoriais…",
                R.style.TextAppearance_Ruptura_Body, R.color.text_secondary, false));
        editorialExecutor.execute(() -> {
            try {
                BackendClient client = backendClient();
                org.json.JSONObject metadata = client.cachedGetFresh(
                        "/v1/editorial/game-characters/" + gameId, 24L * 60 * 60 * 1000);
                org.json.JSONObject details = client.cachedGetFresh(
                        "/v1/editorial/characters/" + metadata.getInt("character_id"),
                        24L * 60 * 60 * 1000);
                String realName = details.optString("real_name", "").trim();
                String summary = details.optString("deck", "").trim();
                if (summary.length() > 500) summary = summary.substring(0, 500) + "…";
                String powers = editorialList(details.optJSONArray("powers"), 5);
                String teams = editorialList(details.optJSONArray("teams"), 4);
                int issueCount = details.optInt("issue_count", -1);
                String firstAppearance = details.optString("first_appearance", "").trim();
                String siteUrl = metadata.getString("site_url");
                String finalSummary = summary;
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    while (facts.getChildCount() > 1) facts.removeViewAt(facts.getChildCount() - 1);
                    if (!realName.isEmpty()) addEditorialLine(facts, "IDENTIDADE", realName);
                    if (issueCount >= 0) addEditorialLine(facts, "APARIÇÕES EM REVISTAS", String.valueOf(issueCount));
                    if (!firstAppearance.isEmpty()) addEditorialLine(facts, "PRIMEIRA EDIÇÃO", firstAppearance);
                    if (!finalSummary.isEmpty()) addEditorialLine(facts, "PERFIL", finalSummary);
                    addEditorialLine(facts, "PODERES", powers);
                    addEditorialLine(facts, "EQUIPES", teams);
                    TextView source = text("FONTE: COMIC VINE · ABRIR FICHA",
                            R.style.TextAppearance_Ruptura_Label, R.color.accent_cyan, true);
                    source.setMinimumHeight(dimension(R.dimen.target_min));
                    source.setClickable(true);
                    source.setFocusable(true);
                    source.setOnClickListener(view -> {
                        try {
                            startActivity(new android.content.Intent(android.content.Intent.ACTION_VIEW,
                                    android.net.Uri.parse(siteUrl)));
                        } catch (android.content.ActivityNotFoundException ignored) { }
                    });
                    facts.addView(source);
                });
            } catch (Exception error) {
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    while (facts.getChildCount() > 1) facts.removeViewAt(facts.getChildCount() - 1);
                    TextView retry = text("Arquivo Comic Vine indisponível · toque para tentar novamente",
                            R.style.TextAppearance_Ruptura_Body, R.color.text_secondary, false);
                    retry.setMinimumHeight(dimension(R.dimen.target_min));
                    retry.setClickable(true);
                    retry.setFocusable(true);
                    retry.setOnClickListener(view -> loadEditorialFacts(facts, gameId));
                    facts.addView(retry);
                });
            }
        });
    }

    private void addEditorialLine(LinearLayout facts, String label, String value) {
        TextView line = text(label + "  " + value,
                R.style.TextAppearance_Ruptura_Body, R.color.text_primary, false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.topMargin = dimension(R.dimen.space_2);
        facts.addView(line, params);
    }

    private String editorialList(org.json.JSONArray values, int limit) {
        if (values == null || values.length() == 0) return "Não informado pela fonte";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < values.length() && i < limit; i++) {
            if (result.length() > 0) result.append(" · ");
            result.append(values.optString(i));
        }
        return result.toString();
    }

    private void filterFilters(LinearLayout filters, String[] selectedGroup,
                               boolean[] onlyOwned, Runnable update) {
        filters.removeAllViews();
        String[] ids = {null, "avengers-allies", "x-men", "fantastic-four", "cosmic-specials"};
        int[] labels = {R.string.collection_filter_all, R.string.catalog_group_avengers_allies,
                R.string.catalog_group_xmen, R.string.catalog_group_fantastic_four,
                R.string.catalog_group_cosmic_specials};
        for (int index = 0; index < ids.length; index++) {
            String id = ids[index];
            boolean selected = selectedGroup == null || java.util.Objects.equals(selectedGroup[0], id);
            TextView chip = text(labels[index], R.style.TextAppearance_Ruptura_Label,
                    selected ? R.color.accent_cyan : R.color.text_secondary, true);
            chip.setAllCaps(true);
            chip.setGravity(Gravity.CENTER);
            chip.setMinHeight(dimension(R.dimen.target_min));
            chip.setPadding(dimension(R.dimen.space_3), 0, dimension(R.dimen.space_3), 0);
            chip.setBackground(background(selected ? R.color.surface_selected
                    : R.color.surface_primary, selected ? R.color.accent_cyan
                    : R.color.border_subtle, 0));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);
            params.rightMargin = dimension(R.dimen.space_2);
            filters.addView(chip, params);
            if (selectedGroup != null) {
                chip.setClickable(true);
                chip.setFocusable(true);
                chip.setOnClickListener(view -> {
                    selectedGroup[0] = id;
                    filterFilters(filters, selectedGroup, onlyOwned, update);
                    update.run();
                });
            }
        }
        TextView ownedChip = text(R.string.collection_filter_owned,
                R.style.TextAppearance_Ruptura_Label,
                onlyOwned[0] ? R.color.accent_cyan : R.color.text_secondary, true);
        ownedChip.setAllCaps(true);
        ownedChip.setGravity(Gravity.CENTER);
        ownedChip.setMinHeight(dimension(R.dimen.target_min));
        ownedChip.setPadding(dimension(R.dimen.space_3), 0, dimension(R.dimen.space_3), 0);
        ownedChip.setBackground(background(onlyOwned[0] ? R.color.surface_selected
                : R.color.surface_primary, onlyOwned[0] ? R.color.accent_cyan
                : R.color.border_subtle, 0));
        ownedChip.setClickable(true);
        ownedChip.setFocusable(true);
        filters.addView(ownedChip, new LinearLayout.LayoutParams(-2, -2));
        ownedChip.setOnClickListener(view -> {
            onlyOwned[0] = !onlyOwned[0];
            filterFilters(filters, selectedGroup, onlyOwned, update);
            update.run();
        });
    }

    private BackendClient backendClient() { return new BackendClient(this, BuildConfig.API_BASE_URL); }

    private void renderEditorialSearch(LinearLayout page) {
        TextView heading = text(R.string.editorial_heading, R.style.TextAppearance_Ruptura_Title, R.color.text_primary, true);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2); params.topMargin = dimension(R.dimen.space_8); page.addView(heading, params);
        android.widget.EditText query = new android.widget.EditText(this);
        query.setTextColor(getColor(R.color.text_primary));
        query.setHintTextColor(getColor(R.color.text_secondary));
        query.setSingleLine(true); query.setHint(R.string.editorial_search_hint); query.setMinimumHeight(dimension(R.dimen.target_min));
        page.addView(query, new LinearLayout.LayoutParams(-1, -2));
        TextView results = text(R.string.editorial_not_configured, R.style.TextAppearance_Ruptura_Body, R.color.text_secondary, false);
        page.addView(results, new LinearLayout.LayoutParams(-1, -2));
        page.addView(action(getString(R.string.editorial_search), () -> {
            String value = query.getText().toString().trim();
            if (value.length() < 2) { results.setText(R.string.editorial_query_short); return; }
            for (int index = page.getChildCount() - 1; index >= 0; index--) {
                if (Boolean.TRUE.equals(page.getChildAt(index).getTag())) page.removeViewAt(index);
            }
            results.setText(R.string.editorial_loading);
            forgeExecutor.execute(() -> {
                try {
                    org.json.JSONObject response = backendClient().cachedGet("/v1/editorial/characters?q=" + BackendClient.encode(value));
                    org.json.JSONArray items = response.getJSONArray("items");
                    StringBuilder output = new StringBuilder(getString(R.string.editorial_source));
                    ArrayList<String> resultLinks = new ArrayList<>();
                    for (int i = 0; i < items.length(); i++) {
                        org.json.JSONObject item = items.getJSONObject(i);
                        output.append("\n\n").append(item.optString("name"));
                        String deck = item.optString("deck", "");
                        if (!deck.isEmpty()) output.append("\n").append(deck);
                        output.append("\n").append(getString(R.string.editorial_open_detail));
                        resultLinks.add(item.optString("site_url"));
                    }
                    runOnUiThread(() -> {
                        for (int index = page.getChildCount() - 1; index >= 0; index--) {
                            if (Boolean.TRUE.equals(page.getChildAt(index).getTag())) page.removeViewAt(index);
                        }
                        results.setText(items.length() == 0 ? getString(R.string.editorial_empty) : output.toString());
                        for (String link : resultLinks) {
                            TextView open = action(getString(R.string.editorial_open_detail), () -> {
                                try { startActivity(new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(link))); }
                                catch (android.content.ActivityNotFoundException ignored) { results.setText(R.string.editorial_error); }
                            });
                            open.setTag(Boolean.TRUE);
                            page.addView(open, new LinearLayout.LayoutParams(-1, -2));
                        }
                    });
                } catch (Exception error) { runOnUiThread(() -> results.setText(R.string.editorial_error)); }
            });
        }));
    }

    private void renderDeadpool(LinearLayout page) {
        String[] contextId = {"app"};
        String[] cardPrompt = {""};
        Runnable[] sendRequest = {null};
        ImageView deadpoolAvatar = new ImageView(this);
        deadpoolAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
        deadpoolAvatar.setBackground(background(R.color.deadpool_white,
                R.color.accent_deadpool, dimension(R.dimen.radius_card)));
        deadpoolAvatar.setClipToOutline(true);
        portraitLoader.loadVariant("deadpool", GameVariantTier.ORIGIN, "Deadpool", deadpoolAvatar, null);
        TextView bubble = text(R.string.deadpool_bubble, R.style.TextAppearance_Ruptura_Body,
                R.color.deadpool_ink, false);
        bubble.setPadding(dimension(R.dimen.space_3), dimension(R.dimen.space_3),
                dimension(R.dimen.space_3), dimension(R.dimen.space_3));
        bubble.setBackground(background(R.color.deadpool_white, R.color.border_subtle,
                dimension(R.dimen.radius_card)));
        bubble.setElevation(dimension(R.dimen.space_1));
        LinearLayout speech = new LinearLayout(this);
        speech.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams speechParams = new LinearLayout.LayoutParams(-1, -2);
        speechParams.bottomMargin = dimension(R.dimen.space_4);
        page.addView(speech, speechParams);
        speech.addView(deadpoolAvatar, new LinearLayout.LayoutParams(dimension(R.dimen.space_8) * 3,
                dimension(R.dimen.space_8) * 4));
        LinearLayout.LayoutParams bubbleParams = new LinearLayout.LayoutParams(0, -2, 1f);
        bubbleParams.leftMargin = dimension(R.dimen.space_2);
        speech.addView(bubble, bubbleParams);

        LinearLayout firstRow = new LinearLayout(this);
        firstRow.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout secondRow = new LinearLayout(this);
        secondRow.setOrientation(LinearLayout.HORIZONTAL);
        addComicCard(firstRow, R.string.deadpool_briefing_label, R.string.deadpool_briefing_detail,
                true, () -> {
                    contextId[0] = "app";
                    cardPrompt[0] = "Faça um briefing curto para " + deadpoolMission().title
                            + ", contra " + deadpoolMission().opponentName + ".";
                    if (sendRequest[0] != null) sendRequest[0].run();
                });
        addComicCard(firstRow, R.string.deadpool_team_label, R.string.deadpool_team_detail,
                true, () -> {
                    contextId[0] = "app";
                    CampaignState saved = forgeRepository.loadCampaign("rupture", java.util.Arrays.asList(
                            "homem-aranha", "wolverine", "tocha-humana"));
                    cardPrompt[0] = "Comente minha equipe " + describeTeam(saved.teamIds)
                            + " para enfrentar " + deadpoolMission().opponentName + ".";
                    if (sendRequest[0] != null) sendRequest[0].run();
                });
        addComicCard(secondRow, R.string.deadpool_daily_label, R.string.deadpool_daily_detail,
                true, this::showDailyChallengeScreen);
        addComicCard(secondRow, R.string.deadpool_strange_label, R.string.deadpool_strange_detail,
                true, () -> {
                    contextId[0] = "app";
                    cardPrompt[0] = "Comente algo estranho sobre as Joias e a fusão 2 para 1.";
                    if (sendRequest[0] != null) sendRequest[0].run();
                });
        page.addView(firstRow, new LinearLayout.LayoutParams(-1, -2));
        LinearLayout.LayoutParams secondParams = new LinearLayout.LayoutParams(-1, -2);
        secondParams.topMargin = dimension(R.dimen.space_2);
        secondParams.bottomMargin = dimension(R.dimen.space_4);
        page.addView(secondRow, secondParams);

        TextView promptLabel = text(R.string.deadpool_prompt_label,
                R.style.TextAppearance_Ruptura_Label, R.color.deadpool_ink, true);
        promptLabel.setAllCaps(true);
        page.addView(promptLabel);
        android.widget.EditText prompt = new android.widget.EditText(this);
        prompt.setTextColor(getColor(R.color.deadpool_ink));
        prompt.setHintTextColor(getColor(R.color.deadpool_body));
        prompt.setBackground(background(R.color.deadpool_white, R.color.border_subtle,
                dimension(R.dimen.radius_card)));
        prompt.setPadding(dimension(R.dimen.space_3), dimension(R.dimen.space_3),
                dimension(R.dimen.space_3), dimension(R.dimen.space_3));
        prompt.setHint(R.string.deadpool_prompt_hint);
        prompt.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(300)});
        prompt.setMinLines(2);
        prompt.setMaxLines(4);
        LinearLayout.LayoutParams promptParams = new LinearLayout.LayoutParams(-1, -2);
        promptParams.topMargin = dimension(R.dimen.space_2);
        page.addView(prompt, promptParams);
        TextView answer = text(R.string.deadpool_ready, R.style.TextAppearance_Ruptura_Body,
                R.color.deadpool_body, false);
        LinearLayout.LayoutParams answerParams = new LinearLayout.LayoutParams(-1, -2);
        answerParams.topMargin = dimension(R.dimen.space_3);
        page.addView(answer, answerParams);
        TextView ask = text(R.string.deadpool_action, R.style.TextAppearance_Ruptura_Label,
                R.color.deadpool_white, true);
        ask.setAllCaps(true);
        ask.setLetterSpacing(0.16f);
        ask.setGravity(Gravity.CENTER);
        ask.setMinimumHeight(dimension(R.dimen.target_min));
        ask.setBackgroundColor(getColor(R.color.accent_deadpool));
        ask.setClickable(true);
        ask.setFocusable(true);
        sendRequest[0] = () -> {
            String message = cardPrompt[0].isEmpty() ? prompt.getText().toString() : cardPrompt[0];
            cardPrompt[0] = "";
            requestDeadpoolLine(contextId[0], message, answer, ask, deadpoolAvatar);
        };
        ask.setOnClickListener(view -> sendRequest[0].run());
        LinearLayout.LayoutParams askParams = new LinearLayout.LayoutParams(-1, -2);
        askParams.topMargin = dimension(R.dimen.space_3);
        page.addView(ask, askParams);
    }

    private void requestDeadpoolLine(String contextId, String prompt, TextView answer,
                                     TextView ask, ImageView avatar) {
        if (!ask.isEnabled()) return;
        ask.setEnabled(false);
        answer.setText(R.string.deadpool_loading);
        final int offlineTurn = deadpoolOfflineTurn++;
        editorialExecutor.execute(() -> {
            String gameContext;
            try { gameContext = deadpoolGameContext(); }
            catch (RuntimeException ignored) { gameContext = ""; }
            try {
                org.json.JSONObject request = new org.json.JSONObject();
                request.put("context_id", contextId);
                request.put("prompt", prompt);
                request.put("game_context", gameContext);
                BackendClient client = backendClient();
                org.json.JSONObject response;
                try {
                    response = client.post("/v1/ai/deadpool-line", request);
                } catch (BackendClient.HttpStatusException error) {
                    if (!BackendClient.shouldRetryDeadpoolWithoutContext(error.statusCode, request))
                        throw error;
                    org.json.JSONObject legacy = new org.json.JSONObject();
                    legacy.put("context_id", contextId);
                    legacy.put("prompt", DeadpoolPrompt.legacy(prompt, gameContext));
                    response = client.post("/v1/ai/deadpool-line", legacy);
                }
                String line = response.getString("text");
                boolean fallback = response.optBoolean("fallback", true);
                if (fallback) line = DeadpoolOfflineReply.respond(prompt, gameContext, offlineTurn);
                String answerText = fallback ? line + "\n\n" + getString(R.string.deadpool_offline)
                        : line;
                runOnUiThread(() -> {
                    answer.setText(answerText);
                    deadpoolPortraitTurn++;
                    GameVariantTier[] tiers = GameVariantTier.values();
                    portraitLoader.loadVariant("deadpool", tiers[deadpoolPortraitTurn % tiers.length],
                            "Deadpool", avatar, null);
                    ask.setEnabled(true);
                });
            } catch (Exception error) {
                String localLine = DeadpoolOfflineReply.respond(prompt, gameContext, offlineTurn);
                runOnUiThread(() -> {
                    answer.setText(localLine + "\n\n"
                            + getString(R.string.deadpool_network_offline));
                    deadpoolPortraitTurn++;
                    GameVariantTier[] tiers = GameVariantTier.values();
                    portraitLoader.loadVariant("deadpool", tiers[deadpoolPortraitTurn % tiers.length],
                            "Deadpool", avatar, null);
                    ask.setEnabled(true);
                });
            }
        });
    }

    private String deadpoolGameContext() {
        List<GameCatalogCharacter> roster = loadRoster();
        CampaignState saved = forgeRepository.loadCampaign("rupture", java.util.Arrays.asList(
                "homem-aranha", "wolverine", "tocha-humana"));
        BattleMission focusedMission = deadpoolMission();
        StringBuilder summary = new StringBuilder("Fatos autorais do jogo (use estes para mecânicas e dicas; Comic Vine fornece apenas dados editoriais): ")
                .append("batalhas usam três heróis; um ativo age por turno; troca é grátis; HP individual persiste; ")
                .append("chefe atinge só o ativo; super é global, carrega com ações e pode ser usado por qualquer vivo; ")
                .append("ataque, defesa e desestabilização são opções; batalha segue até chefe ou trio cair. ")
                .append("Recompensas e resultados são fixos pelo app, não pela IA. ")
                .append("Missão selecionada/próxima: ").append(focusedMission.title).append(" contra ")
                .append(focusedMission.opponentName).append(" em ").append(focusedMission.location)
                .append(". Equipe salva em ordem: ").append(describeTeam(saved.teamIds)).append(". ");
        if (currentBattle != null) {
            LovableBattle b = currentBattle;
            summary.append("Batalha atual: ").append(b.mission.title).append("; ação ")
                    .append(b.round + 1).append("; chefe ").append(b.boss).append('/')
                    .append(b.maxBoss).append(" HP; super global ").append(b.charge).append("%; ativo ");
            LovableBattle.Fighter active = b.activeFighter();
            summary.append(active == null ? "nenhum" : active.spec.name + " HP " + active.health
                    + "/" + active.maxHealth).append("; status ")
                    .append(b.victory ? "vitória" : b.defeat ? "derrota" : "em andamento")
                    .append("; equipe em combate: ");
            for (LovableBattle.Fighter fighter : b.fighters()) {
                summary.append(fighter.spec.name).append(' ')
                        .append(fighter.alive() ? fighter.health + "/" + fighter.maxHealth + " HP" : "KO")
                        .append("; ");
            }
            summary.append("resumo: ").append(b.summary().toDisplayText())
                    .append("; último evento: ").append(b.feedback).append(". ");
        }
        summary.append("Elenco do jogo; atributos são balanceamento autoral, não fatos editoriais: ");
        for (GameCatalogCharacter character : roster) {
            if (summary.length() > 4_700) break;
            boolean owned = forgeRepository.ownsCharacter(character.id);
            summary.append(character.name).append(owned ? " [liberado; grupo " : " [bloqueado; grupo ")
                    .append(getString(groupLabel(character.groupId))).append("; variantes ");
            java.util.Set<GameVariantTier> tiers = owned
                    ? forgeRepository.loadOwnedTiers(character.id) : java.util.Collections.emptySet();
            if (tiers.isEmpty()) summary.append("nenhuma");
            else {
                int tierCount = 0;
                for (GameVariantTier tier : tiers) {
                    if (tierCount++ > 0) summary.append('/');
                    summary.append(getString(tier.labelRes));
                }
            }
            GameVariantTier equipped = owned
                    ? tierById(forgeRepository.loadEquippedTier(character.id)) : GameVariantTier.ORIGIN;
            int[] stats = VariantStats.forVariant(character.id, equipped);
            summary.append("; ").append(owned ? "equipada " : "base ").append(getString(equipped.labelRes))
                    .append("; Vida/Ataque/Defesa/Velocidade ")
                    .append(stats[0]).append('/').append(stats[1]).append('/')
                    .append(stats[2]).append('/').append(stats[3])
                    .append("; especial ").append(BattleSpecial.nameForCharacter(character.id))
                    .append("]; ");
        }
        PlayerResources resources = forgeRepository.loadPlayerResources();
        summary.append("Recursos: ").append(resources.credits).append(" créditos, ")
                .append(resources.xp).append(" XP. Manopla ")
                .append(forgeRepository.isGauntletActivated() ? "ativa" : "inativa").append(". ");
        ForgeInventory inventory = forgeRepository.load();
        summary.append("Inventário autoral (estilhaços/fragmentos/núcleos instáveis/joias): ");
        for (InfinityStone stone : InfinityStone.values()) {
            summary.append(stone.name()).append(' ')
                    .append(inventory.count(stone, ForgeStage.SHARD)).append('/')
                    .append(inventory.count(stone, ForgeStage.FRAGMENT)).append('/')
                    .append(inventory.count(stone, ForgeStage.UNSTABLE_CORE)).append('/')
                    .append(inventory.count(stone, ForgeStage.COMPLETE)).append("; ");
        }
        ChallengeState daily = forgeRepository.loadExistingChallenge(LocalDate.now());
        summary.append("Desafio diário: ").append(daily == null ? "não iniciado hoje" : daily.status
                + ", " + daily.guesses.size() + " tentativas feitas de 6; palpites ");
        if (daily != null) {
            for (String guessId : daily.guesses) {
                GameCatalogCharacter guessed = findCharacter(roster, guessId);
                if (guessed != null) summary.append(guessed.name).append('/');
            }
        }
        summary.append(". ");
        summary.append("Campanhas, na ordem: Rei do Crime/Nova York; Ultron/Complexo; ameaça tecnológica/Wakanda; ")
                .append("Dormammu/Dimensão Espelhada; Ronan/Knowhere; Magneto/Instituto Xavier; ")
                .append("Annihilus/Zona Negativa; Doutor Destino/Latveria; Thanos/Titã. Concluídas: ");
        int wins = 0;
        for (BattleMission mission : BattleMission.ALL) {
            if (!forgeRepository.hasCompletedMission(mission.campaignId, mission.number)) continue;
            if (wins++ > 0) summary.append(", ");
            summary.append(mission.title);
        }
        summary.append(" ( ").append(wins).append(" de ").append(BattleMission.ALL.size()).append(" )");
        return summary.substring(0, Math.min(summary.length(), 6_000));
    }

    private BattleMission deadpoolMission() {
        if (currentBattle != null && !currentBattle.victory && !currentBattle.defeat)
            return currentBattle.mission;
        if (selectedBattleMission != null && currentBattle == null) return selectedBattleMission;
        CampaignState campaign = forgeRepository.loadCampaign("rupture", java.util.Arrays.asList(
                "homem-aranha", "wolverine", "tocha-humana"));
        return BattleMission.forMission("rupture", Math.min(campaign.unlockedMission,
                BattleMission.ALL.size()));
    }

    private void addComicCard(LinearLayout row, int titleRes, int descriptionRes,
                              boolean enabled, Runnable onClick) {
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dimension(R.dimen.space_3), dimension(R.dimen.space_3),
                dimension(R.dimen.space_3), dimension(R.dimen.space_3));
        panel.setMinimumHeight(dimension(R.dimen.space_8) * 3);
        panel.setBackground(background(R.color.deadpool_white, R.color.deadpool_ink, 0));
        panel.setElevation(dimension(R.dimen.space_1));
        panel.setAlpha(enabled ? 1f : .55f);
        TextView title = text(titleRes, R.style.TextAppearance_Ruptura_Label,
                R.color.deadpool_ink, true);
        title.setAllCaps(true);
        panel.addView(title);
        TextView description = text(descriptionRes, R.style.TextAppearance_Ruptura_Caption,
                R.color.deadpool_body, false);
        panel.addView(description);
        if (enabled && onClick != null) {
            panel.setClickable(true);
            panel.setFocusable(true);
            panel.setOnClickListener(view -> onClick.run());
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, -2, 1f);
        params.leftMargin = dimension(R.dimen.space_1);
        params.rightMargin = dimension(R.dimen.space_1);
        row.addView(panel, params);
    }

    private int groupLabel(String groupId) {
        switch (groupId) {
            case "avengers-allies":
                return R.string.catalog_group_avengers_allies;
            case "x-men":
                return R.string.catalog_group_xmen;
            case "fantastic-four":
                return R.string.catalog_group_fantastic_four;
            case "cosmic-specials":
                return R.string.catalog_group_cosmic_specials;
            default:
                throw new IllegalStateException("Unknown game catalog group: " + groupId);
        }
    }

    private GameVariantTier tierById(String tierId) {
        try { return GameVariantTier.valueOf(tierId); }
        catch (IllegalArgumentException error) { return GameVariantTier.ORIGIN; }
    }

    private String variantName(GameCatalogCharacter character, GameVariantTier tier) {
        for (GameCatalogVariant variant : character.variants) if (variant.tier == tier) return variant.name;
        return character.name;
    }

    private void renderGauntlet(LinearLayout page) {
        TextView title = text(R.string.gauntlet_heading, R.style.TextAppearance_Ruptura_Title, R.color.text_primary, true);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2); params.topMargin = dimension(R.dimen.space_8); page.addView(title, params);
        TextView state = text(R.string.gauntlet_loading, R.style.TextAppearance_Ruptura_Body, R.color.text_secondary, false);
        page.addView(state, new LinearLayout.LayoutParams(-1, -2));
        forgeExecutor.execute(() -> {
            boolean active = forgeRepository.isGauntletActivated();
            ForgeInventory inventory = forgeRepository.load();
            boolean ready = true; for (InfinityStone stone : InfinityStone.values()) ready &= inventory.count(stone, ForgeStage.COMPLETE) > 0;
            final boolean gauntletReady = ready;
            runOnUiThread(() -> {
                state.setText(active ? R.string.gauntlet_active : gauntletReady ? R.string.gauntlet_ready : R.string.gauntlet_locked);
                if (active) return;
                page.addView(action(getString(gauntletReady ? R.string.gauntlet_activate : R.string.gauntlet_requirements), () -> {
                    forgeExecutor.execute(() -> {
                        boolean activated = forgeRepository.activateGauntlet();
                        runOnUiThread(() -> { state.setText(activated ? R.string.gauntlet_active : R.string.gauntlet_locked);
                            if (activated) android.widget.Toast.makeText(this, R.string.gauntlet_unlocked, android.widget.Toast.LENGTH_LONG).show(); });
                    });
                }), new LinearLayout.LayoutParams(-1, -2));
            });
        });
    }

    private void showGauntletScreen() {
        transientScreen = true;
        transientBack.setEnabled(true);
        navigationBar.setVisibility(View.VISIBLE);
        contentContainer.removeAllViews();
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(getColor(R.color.canvas));
        contentContainer.addView(scroll, new FrameLayout.LayoutParams(-1, -1));
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dimension(R.dimen.space_4), dimension(R.dimen.space_4),
                dimension(R.dimen.space_4), dimension(R.dimen.space_6));
        scroll.addView(page);
        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        page.addView(header, new LinearLayout.LayoutParams(-1, -2));
        TextView back = text("‹", R.style.TextAppearance_Ruptura_Title,
                R.color.text_primary, true);
        back.setGravity(Gravity.CENTER);
        back.setMinWidth(dimension(R.dimen.target_min));
        back.setMinHeight(dimension(R.dimen.target_min));
        back.setContentDescription("Voltar à Forja");
        back.setBackground(background(R.color.surface_primary, R.color.border_subtle,
                dimension(R.dimen.target_min)));
        back.setOnClickListener(view -> renderShell());
        header.addView(back);
        LinearLayout title = new LinearLayout(this);
        title.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(0, -2, 1f);
        titleParams.leftMargin = dimension(R.dimen.space_3);
        header.addView(title, titleParams);
        title.addView(text("ENGENHARIA BAXTER + SELOS MÍSTICOS",
                R.style.TextAppearance_Ruptura_Caption, R.color.text_secondary, true));
        title.addView(text("MANOPLA DE CONTENÇÃO", R.style.TextAppearance_Ruptura_Title,
                R.color.text_primary, true));
        GauntletConstellationView constellation = new GauntletConstellationView(this);
        LinearLayout.LayoutParams constellationParams = new LinearLayout.LayoutParams(-1,
                dimension(R.dimen.space_8) * 9 + dimension(R.dimen.space_3));
        constellationParams.topMargin = dimension(R.dimen.space_3);
        page.addView(constellation, constellationParams);
        LinearLayout state = new LinearLayout(this);
        state.setOrientation(LinearLayout.VERTICAL);
        page.addView(state, new LinearLayout.LayoutParams(-1, -2));
        state.addView(text(R.string.gauntlet_loading, R.style.TextAppearance_Ruptura_Body,
                R.color.text_secondary, false));
        forgeExecutor.execute(() -> {
            ForgeInventory inventory = forgeRepository.load();
            boolean active = forgeRepository.isGauntletActivated();
            runOnUiThread(() -> {
                if (scroll.getParent() == contentContainer)
                    renderGauntletScreenState(state, constellation, inventory, active);
            });
        });
    }

    private void renderGauntletScreenState(LinearLayout state,
            GauntletConstellationView constellation, ForgeInventory inventory, boolean active) {
        state.removeAllViews();
        InfinityStone[] stones = InfinityStone.values();
        int[] colors = {R.color.stone_space, R.color.stone_mind, R.color.stone_reality,
                R.color.stone_power, R.color.stone_time, R.color.stone_soul};
        String[] mottos = {"Dobra distâncias em silêncio.", "Escuta o que ninguém disse.",
                "Reescreve o que já aconteceu.", "Não negocia, apenas rompe.",
                "Guarda instantes sobrepostos.", "Reconhece quem já se perdeu."};
        boolean[] complete = new boolean[stones.length];
        int count = 0;
        for (int i = 0; i < stones.length; i++) {
            complete[i] = inventory.count(stones[i], ForgeStage.COMPLETE) > 0;
            if (complete[i]) count++;
        }
        constellation.setCompleted(complete);
        LinearLayout resonance = card();
        LinearLayout.LayoutParams resonanceParams = new LinearLayout.LayoutParams(-1, -2);
        resonanceParams.topMargin = dimension(R.dimen.space_3);
        state.addView(resonance, resonanceParams);
        LinearLayout heading = new LinearLayout(this);
        heading.setGravity(Gravity.CENTER_VERTICAL);
        resonance.addView(heading, new LinearLayout.LayoutParams(-1, -2));
        heading.addView(text("RESSONÂNCIA DE CONTENÇÃO", R.style.TextAppearance_Ruptura_Caption,
                R.color.text_secondary, true), new LinearLayout.LayoutParams(0, -2, 1f));
        int progress = Math.round(count * 100f / stones.length);
        TextView percent = text(progress + "%", R.style.TextAppearance_Ruptura_Title,
                R.color.accent_gold, true);
        heading.addView(percent);
        LinearLayout track = new LinearLayout(this);
        LinearLayout.LayoutParams trackParams = new LinearLayout.LayoutParams(-1,
                dimension(R.dimen.space_1));
        trackParams.topMargin = dimension(R.dimen.space_2);
        resonance.addView(track, trackParams);
        track.setBackgroundColor(getColor(R.color.surface_primary));
        View fill = new View(this);
        fill.setBackgroundColor(getColor(R.color.accent_gold));
        track.addView(fill, new LinearLayout.LayoutParams(0, -1, Math.max(1, progress)));
        track.addView(new View(this), new LinearLayout.LayoutParams(0, -1,
                Math.max(1, 100 - progress)));
        TextView lore = text("Reed projetou a estrutura de contenção; Estranho manteve os selos. "
                        + "A Câmara mede e estabiliza a assinatura da variante escolhida. Reed "
                        + "calibra os instrumentos; Estranho mantém cada realidade isolada por selos. "
                        + "Eles protegem a passagem entre realidades, sem controlar os heróis.",
                R.style.TextAppearance_Ruptura_Body, R.color.text_secondary, false);
        LinearLayout.LayoutParams loreParams = new LinearLayout.LayoutParams(-1, -2);
        loreParams.topMargin = dimension(R.dimen.space_3);
        resonance.addView(lore, loreParams);

        LinearLayout roles = new LinearLayout(this);
        roles.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rolesParams = new LinearLayout.LayoutParams(-1, -2);
        rolesParams.topMargin = dimension(R.dimen.space_3);
        state.addView(roles, rolesParams);
        addChamberRole(roles, "senhor-fantastico", "Reed Richards",
                "Mapeia a assinatura e calibra a estabilidade da variante.");
        addChamberRole(roles, "doutor-estranho", "Doutor Estranho",
                "Sela as ramificações para que uma realidade não invada outra.");

        for (int i = 0; i < stones.length; i++) {
            final int index = i;
            LinearLayout row = card();
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(-1, -2);
            rowParams.topMargin = dimension(R.dimen.space_2);
            state.addView(row, rowParams);
            TextView gem = text(complete[i] ? "⬢" : "⬡", R.style.TextAppearance_Ruptura_Title,
                    colors[i], true);
            gem.setGravity(Gravity.CENTER);
            gem.setMinWidth(dimension(R.dimen.target_min));
            row.addView(gem);
            LinearLayout labels = new LinearLayout(this);
            labels.setOrientation(LinearLayout.VERTICAL);
            row.addView(labels, new LinearLayout.LayoutParams(0, -2, 1f));
            labels.addView(text(getString(stones[i].labelRes).replace("Joia do ", "")
                            .replace("Joia da ", "").toUpperCase(java.util.Locale.ROOT),
                    R.style.TextAppearance_Ruptura_Label, R.color.text_primary, true));
            labels.addView(text(mottos[i], R.style.TextAppearance_Ruptura_Caption,
                    R.color.text_secondary, false));
            row.addView(text(complete[i] ? "100%" : "0%", R.style.TextAppearance_Ruptura_Label,
                    colors[i], true));
            row.setContentDescription(getString(stones[index].labelRes) + ": "
                    + (complete[i] ? "Joia Completa presente" : "Joia Completa ausente"));
        }
        TextView note = text("LEITURA ANÔMALA · Um eco de 0,4 Hz chega de uma câmara distante "
                        + "sempre que uma Joia é encaixada.", R.style.TextAppearance_Ruptura_Caption,
                R.color.text_secondary, false);
        LinearLayout.LayoutParams noteParams = new LinearLayout.LayoutParams(-1, -2);
        noteParams.topMargin = dimension(R.dimen.space_3);
        state.addView(note, noteParams);
        final boolean fullGauntlet = count == stones.length;
        TextView action = action(active && fullGauntlet
                        ? "ESCOLHER PERSONAGEM OU VARIANTE"
                        : active ? "IR À FORJA PARA COMPLETAR"
                        : fullGauntlet ? "ATIVAR MANOPLA DE CONTENÇÃO"
                        : "REÚNA AS SEIS JOIAS COMPLETAS", () -> {
            if (active && fullGauntlet) {
                showGauntletUnlockChoice();
            } else if (active) {
                selectedDestination = AppDestination.FORGE;
                renderShell();
            } else if (completeCount(inventory) == stones.length) {
                forgeExecutor.execute(() -> {
                    boolean activated = forgeRepository.activateGauntlet();
                    runOnUiThread(() -> {
                        if (activated) showGauntletScreen();
                    });
                });
            }
        });
        action.setEnabled(active || fullGauntlet);
        LinearLayout.LayoutParams actionParams = new LinearLayout.LayoutParams(-1, -2);
        actionParams.topMargin = dimension(R.dimen.space_3);
        state.addView(action, actionParams);
    }

    private void addChamberRole(LinearLayout parent, String id, String name, String role) {
        LinearLayout card = card();
        card.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, -2, 1f);
        params.setMargins(dimension(R.dimen.space_1), 0, dimension(R.dimen.space_1), 0);
        parent.addView(card, params);
        ImageView portrait = new ImageView(this);
        portrait.setScaleType(ImageView.ScaleType.CENTER_CROP);
        portrait.setBackground(background(R.color.surface_elevated, R.color.border_subtle,
                dimension(R.dimen.radius_card)));
        card.addView(portrait, new LinearLayout.LayoutParams(-1, dimension(R.dimen.space_8) * 3));
        TextView credit = text(R.string.editorial_portrait_loading,
                R.style.TextAppearance_Ruptura_Caption, R.color.text_secondary, false);
        credit.setGravity(Gravity.CENTER);
        card.addView(credit, new LinearLayout.LayoutParams(-1, -2));
        portraitLoader.load(id, name, portrait, credit);
        TextView label = text(name.toUpperCase(java.util.Locale.ROOT),
                R.style.TextAppearance_Ruptura_Label, R.color.accent_cyan, true);
        label.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(-1, -2);
        labelParams.topMargin = dimension(R.dimen.space_2);
        card.addView(label, labelParams);
        TextView description = text(role, R.style.TextAppearance_Ruptura_Caption,
                R.color.text_secondary, false);
        description.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams descriptionParams = new LinearLayout.LayoutParams(-1, -2);
        descriptionParams.topMargin = dimension(R.dimen.space_1);
        card.addView(description, descriptionParams);
    }

    private void showGauntletUnlockChoice() {
        if (!forgeRepository.isGauntletActivated() || !forgeRepository.hasCompleteGauntlet()) {
            showGauntletScreen();
            return;
        }
        transientScreen = true;
        transientBack.setEnabled(true);
        navigationBar.setVisibility(View.VISIBLE);
        contentContainer.removeAllViews();
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(getColor(R.color.canvas));
        contentContainer.addView(scroll, new FrameLayout.LayoutParams(-1, -1));
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dimension(R.dimen.space_4), dimension(R.dimen.space_4),
                dimension(R.dimen.space_4), dimension(R.dimen.space_6));
        scroll.addView(page);
        page.addView(action("‹  VOLTAR À MANOPLA", this::showGauntletScreen));
        page.addView(text("UM CICLO · UMA ESCOLHA", R.style.TextAppearance_Ruptura_Label,
                R.color.accent_gold, true));
        page.addView(text("DESPERTAR DA MANOPLA", R.style.TextAppearance_Ruptura_Display,
                R.color.text_primary, true));
        page.addView(text("Escolha um novo personagem ou evolua a variante de alguém que você já possui. "
                        + "A escolha consome uma Joia Completa de cada tipo.",
                R.style.TextAppearance_Ruptura_Body, R.color.text_secondary, false));
        List<GameCatalogCharacter> roster = loadRoster();
        page.addView(text("DESBLOQUEAR PERSONAGEM", R.style.TextAppearance_Ruptura_Title,
                R.color.accent_cyan, true));
        for (GameCatalogCharacter character : roster) {
            if (forgeRepository.ownsCharacter(character.id)) continue;
            LinearLayout candidate = card();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
            params.topMargin = dimension(R.dimen.space_3);
            page.addView(candidate, params);
            ImageView portrait = editorialImage(candidate, dimension(R.dimen.space_8) * 2);
            portraitLoader.load(character.id, character.name, portrait, null);
            candidate.setClickable(true); candidate.setFocusable(true);
            candidate.setContentDescription("Desbloquear " + character.name + " com a Manopla completa");
            candidate.setOnClickListener(view -> confirmGauntletChoice(character.name, character,
                    GameVariantTier.ORIGIN, roster,
                    () -> forgeRepository.unlockCharacter(character.id, roster)));
            candidate.addView(text("🔒  NOVO PERSONAGEM",
                    R.style.TextAppearance_Ruptura_Label, R.color.accent_cyan, true));
            candidate.addView(text(character.name + " · ORIGEM",
                    R.style.TextAppearance_Ruptura_Label, R.color.text_primary, true));
        }
        page.addView(text("EVOLUIR VARIANTE", R.style.TextAppearance_Ruptura_Title,
                R.color.accent_gold, true));
        for (GameCatalogCharacter character : roster) {
            if (!forgeRepository.ownsCharacter(character.id)) continue;
            GameVariantTier next = VariantProgression.next(
                    forgeRepository.loadOwnedTiers(character.id));
            if (next == null) continue;
            LinearLayout candidate = card();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
            params.topMargin = dimension(R.dimen.space_3);
            page.addView(candidate, params);
            ImageView portrait = editorialImage(candidate, dimension(R.dimen.space_8) * 2);
            portraitLoader.load(character.id, character.name, portrait, null);
            candidate.setClickable(true); candidate.setFocusable(true);
            candidate.setContentDescription("Evoluir " + character.name + " para " + getString(next.labelRes));
            candidate.setOnClickListener(view -> confirmGauntletChoice(character.name + " · "
                    + getString(next.labelRes), character, next, roster,
                    () -> forgeRepository.unlockNextVariant(character.id, next, roster)));
            candidate.addView(text("⚡  EVOLUÇÃO DE VARIANTE",
                    R.style.TextAppearance_Ruptura_Label, R.color.accent_gold, true));
            candidate.addView(text(character.name + " · " + getString(next.labelRes),
                    R.style.TextAppearance_Ruptura_Label, R.color.text_primary, true));
        }
    }

    private void confirmGauntletChoice(String target, GameCatalogCharacter character,
            GameVariantTier tier, List<GameCatalogCharacter> roster,
            java.util.concurrent.Callable<Boolean> unlock) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Confirmar despertar")
                .setMessage(target + " será desbloqueado ao consumir uma Joia Completa de cada tipo.")
                .setNegativeButton("CANCELAR", null)
                .setPositiveButton("CONSUMIR MANOPLA", (dialog, which) -> forgeExecutor.execute(() -> {
                    try {
                        boolean granted = unlock.call();
                        runOnUiThread(() -> {
                            if (granted) showUnlockCelebration(character, roster, tier);
                            else showGauntletScreen();
                            android.widget.Toast.makeText(this, granted ? "Desbloqueio concluído"
                                    : "Alvo já desbloqueado", android.widget.Toast.LENGTH_LONG).show();
                        });
                    } catch (Exception error) {
                        runOnUiThread(() -> {
                            showGauntletScreen();
                            android.widget.Toast.makeText(this, "Manopla incompleta ou alvo indisponível",
                                    android.widget.Toast.LENGTH_LONG).show();
                        });
                    }
                }))
                .show();
    }

    private int completeCount(ForgeInventory inventory) {
        int count = 0;
        for (InfinityStone stone : InfinityStone.values())
            if (inventory.count(stone, ForgeStage.COMPLETE) > 0) count++;
        return count;
    }

    private void selectDestination(AppDestination destination) {
        if (selectedDestination != destination) {
            playUiSound();
            selectedDestination = destination;
            renderShell();
        }
    }

    private void showUnlockCelebration(GameCatalogCharacter character,
            List<GameCatalogCharacter> roster, GameVariantTier tier) {
        selectedDestination = AppDestination.COLLECTION;
        showCharacterVariants(character, roster, tier);
        if (animationDelay(1) == 0) return;
        FrameLayout overlay = new FrameLayout(this);
        overlay.setBackgroundColor(0x6605070C);
        overlay.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        contentContainer.addView(overlay, new FrameLayout.LayoutParams(-1, -1));
        TextView flare = text(tier == GameVariantTier.ORIGIN ? "🔓  PERSONAGEM DESPERTADO"
                        : "⚡  NOVA VARIANTE",
                R.style.TextAppearance_Ruptura_Title, R.color.accent_gold, true);
        flare.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(-1,
                dimension(R.dimen.space_8) * 2, Gravity.CENTER);
        overlay.addView(flare, p);
        flare.setAlpha(0f);
        flare.animate().alpha(1f).scaleX(1.12f).scaleY(1.12f).setDuration(380)
                .withEndAction(() -> flare.animate().alpha(0f).setStartDelay(450).setDuration(350)
                        .withEndAction(() -> contentContainer.removeView(overlay)));
    }

    private List<GameCatalogCharacter> loadRoster() {
        try { return GameCatalogParser.read(getAssets()); }
        catch (IOException | JSONException exception) { throw new IllegalStateException(exception); }
    }

    private LinearLayout card() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dimension(R.dimen.space_5), dimension(R.dimen.space_5),
                dimension(R.dimen.space_5), dimension(R.dimen.space_5));
        card.setBackground(background(R.color.surface_primary, R.color.border_subtle,
                dimension(R.dimen.radius_card)));
        card.setElevation(dimension(R.dimen.space_1));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.topMargin = dimension(R.dimen.space_4);
        return card;
    }

    private ImageView editorialImage(LinearLayout parent, int height) {
        ImageView image = new ImageView(this);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        image.setBackground(background(R.color.surface_primary, R.color.border_subtle,
                dimension(R.dimen.radius_card)));
        image.setClipToOutline(true);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, height);
        params.bottomMargin = dimension(R.dimen.space_2);
        parent.addView(image, params);
        return image;
    }

    private TextView action(String label, Runnable task) {
        TextView button = text(label, R.style.TextAppearance_Ruptura_Label,
                R.color.play_night, true);
        button.setFocusable(true);
        button.setClickable(true);
        button.setMinimumHeight(dimension(R.dimen.space_8) * 2);
        button.setPadding(dimension(R.dimen.space_5), dimension(R.dimen.space_3),
                dimension(R.dimen.space_5), dimension(R.dimen.space_3));
        button.setGravity(Gravity.CENTER);
        button.setAllCaps(true);
        button.setTextSize(14f);
        button.setLetterSpacing(0.07f);
        button.setOnClickListener(view -> { playUiSound(); task.run(); });
        button.setBackground(background(R.color.play_mint, R.color.play_mint,
                dimension(R.dimen.radius_card)));
        button.setElevation(dimension(R.dimen.space_1));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.topMargin = dimension(R.dimen.space_3);
        params.bottomMargin = dimension(R.dimen.space_2);
        button.setLayoutParams(params);
        return button;
    }

    private void renderDailyChallenge(LinearLayout page) {
        LinearLayout challengeCard = card();
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(-1, -2);
        cardParams.topMargin = dimension(R.dimen.space_4);
        page.addView(challengeCard, cardParams);
        challengeCard.addView(text("DESAFIO DIÁRIO · ESTILO TERMO",
                R.style.TextAppearance_Ruptura_Label, R.color.accent_cyan, true));
        challengeCard.addView(text("Descubra o herói em até seis tentativas e ganhe os Fragmentos faltantes para completar a Manopla.",
                R.style.TextAppearance_Ruptura_Body, R.color.text_secondary, false));
        TextView open = action("JOGAR DESAFIO DIÁRIO", this::showDailyChallengeScreen);
        LinearLayout.LayoutParams openParams = new LinearLayout.LayoutParams(-1, -2);
        openParams.topMargin = dimension(R.dimen.space_4);
        challengeCard.addView(open, openParams);
    }

    private void showDailyChallengeScreen() {
        transientScreen = true;
        transientBack.setEnabled(true);
        navigationBar.setVisibility(View.VISIBLE);
        contentContainer.removeAllViews();
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(getColor(R.color.canvas));
        contentContainer.addView(scroll, new FrameLayout.LayoutParams(-1, -1));
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dimension(R.dimen.space_4), dimension(R.dimen.space_4),
                dimension(R.dimen.space_4), dimension(R.dimen.space_6));
        scroll.addView(page);
        TextView back = text("‹  VOLTAR AO NEXUS", R.style.TextAppearance_Ruptura_Label,
                R.color.text_secondary, true);
        back.setMinimumHeight(dimension(R.dimen.target_min));
        back.setClickable(true);
        back.setFocusable(true);
        back.setOnClickListener(view -> renderShell());
        page.addView(back);
        page.addView(text("ESTILO TERMO", R.style.TextAppearance_Ruptura_Caption,
                R.color.accent_cyan, true));
        page.addView(text("DESAFIO DIÁRIO", R.style.TextAppearance_Ruptura_Display,
                R.color.text_primary, true));
        LinearLayout challengeCard = new LinearLayout(this);
        challengeCard.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(-1, -2);
        cardParams.topMargin = dimension(R.dimen.space_4);
        page.addView(challengeCard, cardParams);
        List<GameCatalogCharacter> roster = loadRoster();
        forgeExecutor.execute(() -> {
            ChallengeState state = forgeRepository.loadOrCreateChallenge(roster, LocalDate.now());
            runOnUiThread(() -> renderChallengeState(challengeCard, roster, state));
        });
    }

    private void renderChallengeState(LinearLayout panel, List<GameCatalogCharacter> roster, ChallengeState state) {
        panel.removeAllViews();
        LinearLayout mystery = card();
        mystery.setGravity(Gravity.BOTTOM);
        mystery.setMinimumHeight(dimension(R.dimen.space_8) * 7);
        mystery.setBackground(background(R.color.surface_primary, R.color.border_subtle,
                dimension(R.dimen.radius_card)));
        panel.addView(mystery, new LinearLayout.LayoutParams(-1, -2));
        if (!"ACTIVE".equals(state.status)) {
            GameCatalogCharacter revealed = findCharacter(roster, state.targetId);
            ImageView portrait = editorialImage(mystery, dimension(R.dimen.space_8) * 4);
            portraitLoader.load(revealed.id, revealed.name, portrait, null);
        } else {
            TextView mysteryMark = text("?", R.style.TextAppearance_Ruptura_Display,
                    R.color.accent_cyan, true);
            mysteryMark.setTextSize(76f);
            mysteryMark.setGravity(Gravity.CENTER);
            mysteryMark.setBackground(background(R.color.surface_elevated,
                    R.color.border_subtle, dimension(R.dimen.radius_card)));
            mysteryMark.setContentDescription("Personagem misterioso ainda não revelado");
            LinearLayout.LayoutParams markParams = new LinearLayout.LayoutParams(-1,
                    dimension(R.dimen.space_8) * 4);
            markParams.bottomMargin = dimension(R.dimen.space_2);
            mystery.addView(mysteryMark, markParams);
        }
        mystery.addView(text("GRUPO: " + ("ACTIVE".equals(state.status) ? "???"
                        : getString(groupLabel(findCharacter(roster, state.targetId).groupId)))
                        + "      PERSONAGEM: " + ("ACTIVE".equals(state.status) ? "???"
                        : findCharacter(roster, state.targetId).name),
                R.style.TextAppearance_Ruptura_Caption, R.color.text_secondary, true));
        TextView attempts = text(getString(R.string.challenge_attempts, state.guesses.size(), 6),
                R.style.TextAppearance_Ruptura_Label, R.color.accent_gold, true);
        LinearLayout.LayoutParams attemptsParams = new LinearLayout.LayoutParams(-1, -2);
        attemptsParams.topMargin = dimension(R.dimen.space_2);
        panel.addView(attempts, attemptsParams);
        LinearLayout quote = card();
        quote.setBackground(new AngularPanelDrawable(getColor(R.color.surface_primary),
                getColor(R.color.surface_primary), getColor(R.color.accent_deadpool),
                dimension(R.dimen.angular_cut), false));
        TextView quoteText = text("“Seis tentativas. Eu sei a resposta. Você não. Adoro esse equilíbrio.”",
                R.style.TextAppearance_Ruptura_Body, R.color.text_primary, false);
        quoteText.setTypeface(quoteText.getTypeface(), Typeface.ITALIC);
        quote.addView(quoteText);
        LinearLayout.LayoutParams quoteParams = new LinearLayout.LayoutParams(-1, -2);
        quoteParams.topMargin = dimension(R.dimen.space_3);
        panel.addView(quote, quoteParams);
        for (String id : state.guesses) {
            GameCatalogCharacter guess = findCharacter(roster, id);
            GameCatalogCharacter target = findCharacter(roster, state.targetId);
            TextView feedback = text(getString(R.string.challenge_feedback, guess.name,
                            GameRules.factionFeedback(target, guess), GameRules.alphabeticalFeedback(target, guess)),
                    R.style.TextAppearance_Ruptura_Body, R.color.text_secondary, false);
            feedback.setPadding(dimension(R.dimen.space_3), dimension(R.dimen.space_3),
                    dimension(R.dimen.space_3), dimension(R.dimen.space_3));
            feedback.setBackground(background(R.color.surface_elevated,
                    R.color.border_subtle, dimension(R.dimen.radius_card)));
            LinearLayout.LayoutParams feedbackParams = new LinearLayout.LayoutParams(-1, -2);
            feedbackParams.topMargin = dimension(R.dimen.space_2);
            panel.addView(feedback, feedbackParams);
        }
        if ("WON".equals(state.status)) {
            panel.addView(text(R.string.challenge_won, R.style.TextAppearance_Ruptura_Title, R.color.accent_cyan, true));
            addFragmentReceipt(panel, forgeRepository.loadRewardFragments("daily:" + state.date));
            return;
        }
        if ("LOST".equals(state.status)) {
            panel.addView(text(getString(R.string.challenge_lost, findCharacter(roster, state.targetId).name),
                    R.style.TextAppearance_Ruptura_Title, R.color.accent_gold, true));
            return;
        }
        LinearLayout.LayoutParams spinnerParams = new LinearLayout.LayoutParams(-1, -2);
        android.widget.Spinner spinner = new android.widget.Spinner(this);
        spinner.setContentDescription(getString(R.string.challenge_guess_label));
        ArrayList<GameCatalogCharacter> options = new ArrayList<>();
        for (GameCatalogCharacter character : roster) if (!state.guesses.contains(character.id)) options.add(character);
        ArrayList<String> names = new ArrayList<>();
        names.add("Escolha um herói (6 tentativas)");
        names.addAll(characterNames(options));
        android.widget.ArrayAdapter<String> adapter = darkSpinnerAdapter(names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setMinimumHeight(dimension(R.dimen.target_min));
        spinner.setBackground(background(R.color.surface_elevated,
                R.color.border_subtle, 0));
        spinnerParams.topMargin = dimension(R.dimen.space_4);
        panel.addView(spinner, spinnerParams);
        TextView submit = action(getString(R.string.challenge_submit), () -> {
            if (spinner.getSelectedItemPosition() <= 0) return;
            String id = options.get(spinner.getSelectedItemPosition() - 1).id;
            forgeExecutor.execute(() -> {
                ChallengeState next = forgeRepository.submitGuess(state, id, roster);
                runOnUiThread(() -> renderChallengeState(panel, roster, next));
            });
        });
        LinearLayout.LayoutParams submitParams = new LinearLayout.LayoutParams(-1, -2);
        submitParams.topMargin = dimension(R.dimen.space_3);
        panel.addView(submit, submitParams);
        submit.setEnabled(false);
        spinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view,
                                                 int position, long itemId) {
                submit.setEnabled(position > 0);
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {
                submit.setEnabled(false);
            }
        });
    }

    private List<String> characterNames(List<GameCatalogCharacter> items) {
        ArrayList<String> names = new ArrayList<>(); for (GameCatalogCharacter item : items) names.add(item.name); return names;
    }

    private android.widget.ArrayAdapter<String> darkSpinnerAdapter(List<String> names) {
        return new android.widget.ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, names) {
            @Override public View getView(int position, View convertView, android.view.ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTextColor(getColor(R.color.text_primary));
                view.setPadding(dimension(R.dimen.space_2), dimension(R.dimen.space_2),
                        dimension(R.dimen.space_2), dimension(R.dimen.space_2));
                return view;
            }

            @Override public View getDropDownView(int position, View convertView,
                                                   android.view.ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setTextColor(getColor(R.color.text_primary));
                view.setBackgroundColor(getColor(R.color.surface_elevated));
                view.setPadding(dimension(R.dimen.space_3), dimension(R.dimen.space_3),
                        dimension(R.dimen.space_3), dimension(R.dimen.space_3));
                return view;
            }
        };
    }

    private GameCatalogCharacter findCharacter(List<GameCatalogCharacter> items, String id) {
        for (GameCatalogCharacter character : items) if (character.id.equals(id)) return character;
        return null;
    }

    private void renderCampaigns(LinearLayout page) {
        List<GameCatalogCharacter> roster = loadRoster();
        LinearLayout guide = card();
        LinearLayout.LayoutParams guideParams = new LinearLayout.LayoutParams(-1, -2);
        guideParams.bottomMargin = dimension(R.dimen.space_4);
        page.addView(guide, guideParams);
        guide.addView(text("A trilha está aberta!", R.style.TextAppearance_Ruptura_Title,
                R.color.text_primary, true));
        guide.addView(text("Toque em um capítulo para ver a missão e montar seu trio desbloqueado.",
                R.style.TextAppearance_Ruptura_Body, R.color.text_secondary, false));
        int chapter = 1;
        for (BattleMission mission : BattleMission.ALL) {
            addBattleMissionCard(page, roster, mission, chapter++);
            if (chapter <= BattleMission.ALL.size()) {
                View connector = new View(this);
                connector.setBackgroundColor(getColor(R.color.border_subtle));
                LinearLayout.LayoutParams connectorParams = new LinearLayout.LayoutParams(
                        Math.max(2, dimension(R.dimen.space_1) / 2),
                        dimension(R.dimen.space_5));
                connectorParams.leftMargin = dimension(R.dimen.space_8) - dimension(R.dimen.space_1);
                page.addView(connector, connectorParams);
            }
        }
    }

    private void addBattleMissionCard(LinearLayout page, List<GameCatalogCharacter> roster,
                                      BattleMission mission, int chapter) {
        int accent = chapterColor(chapter);
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(-1, -2);
        rowParams.topMargin = chapter == 1 ? dimension(R.dimen.space_2) : 0;
        page.addView(row, rowParams);

        TextView node = text(String.valueOf(chapter), R.style.TextAppearance_Ruptura_Title,
                R.color.play_night, true);
        node.setGravity(Gravity.CENTER);
        node.setBackground(background(R.color.play_sun, R.color.play_sun,
                dimension(R.dimen.radius_card)));
        node.setContentDescription("Nó " + chapter + " do mapa de campanhas");
        row.addView(node, new LinearLayout.LayoutParams(dimension(R.dimen.space_8) * 2,
                dimension(R.dimen.space_8) * 2));

        LinearLayout item = card();
        item.setClickable(true);
        item.setFocusable(true);
        item.setContentDescription("Abrir capítulo " + chapter + ": " + mission.title);
        item.setOnClickListener(view -> { });
        LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(0, -2, 1f);
        itemParams.leftMargin = dimension(R.dimen.space_2);
        row.addView(item, itemParams);

        LinearLayout artAndInfo = new LinearLayout(this);
        artAndInfo.setGravity(Gravity.CENTER_VERTICAL);
        item.addView(artAndInfo, new LinearLayout.LayoutParams(-1, -2));
        FrameLayout artPanel = new FrameLayout(this);
        artPanel.setBackground(background(R.color.surface_elevated, accent,
                dimension(R.dimen.radius_card)));
        artPanel.setClipToOutline(true);
        artAndInfo.addView(artPanel, new LinearLayout.LayoutParams(
                dimension(R.dimen.space_8) * 2, dimension(R.dimen.space_8) * 2));
        TextView fallback = text(mission.opponentName.substring(0, 1),
                R.style.TextAppearance_Ruptura_Title, accent, true);
        fallback.setGravity(Gravity.CENTER);
        artPanel.addView(fallback, new FrameLayout.LayoutParams(-1, -1));
        ImageView image = new ImageView(this);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setClipToOutline(true);
        artPanel.addView(image, new FrameLayout.LayoutParams(-1, -1));
        portraitLoader.loadOpponent(mission.opponentId, mission.opponentName, image, null);

        LinearLayout info = new LinearLayout(this);
        info.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(0, -2, 1f);
        infoParams.leftMargin = dimension(R.dimen.space_3);
        artAndInfo.addView(info, infoParams);
        LinearLayout copy = new LinearLayout(this);
        copy.setOrientation(LinearLayout.VERTICAL);
        info.addView(copy, new LinearLayout.LayoutParams(0, -2, 1f));
        copy.addView(text("CAPÍTULO " + chapter, R.style.TextAppearance_Ruptura_Caption,
                R.color.text_secondary, true));
        TextView missionTitle = text(mission.title, R.style.TextAppearance_Ruptura_Title,
                R.color.text_primary, true);
        missionTitle.setTextSize(17f);
        copy.addView(missionTitle);
        copy.addView(text("Chefe: " + mission.opponentName + " · Poder "
                        + formatAmount(mission.recommendedPower),
                R.style.TextAppearance_Ruptura_Caption, R.color.text_secondary, false));
        TextView stateIcon = text("›", R.style.TextAppearance_Ruptura_Title, accent, true);
        stateIcon.setGravity(Gravity.CENTER);
        stateIcon.setContentDescription("Progresso do capítulo " + chapter);
        info.addView(stateIcon, new LinearLayout.LayoutParams(dimension(R.dimen.space_8),
                dimension(R.dimen.space_8)));
        TextView status = text("Carregando progresso…", R.style.TextAppearance_Ruptura_Caption,
                R.color.text_secondary, true);
        LinearLayout.LayoutParams statusParams = new LinearLayout.LayoutParams(-1, -2);
        statusParams.topMargin = dimension(R.dimen.space_3);
        item.addView(status, statusParams);
        android.widget.ProgressBar progress = new android.widget.ProgressBar(this, null,
                android.R.attr.progressBarStyleHorizontal);
        progress.setMax(100);
        progress.setProgress(0);
        progress.setProgressTintList(android.content.res.ColorStateList.valueOf(getColor(accent)));
        progress.setProgressBackgroundTintList(android.content.res.ColorStateList.valueOf(
                getColor(R.color.surface_elevated)));
        LinearLayout.LayoutParams progressParams = new LinearLayout.LayoutParams(-1,
                dimension(R.dimen.space_1));
        progressParams.topMargin = dimension(R.dimen.space_2);
        item.addView(progress, progressParams);

        CampaignReward reward = CampaignReward.forMission(mission.campaignId, mission.number);
        forgeExecutor.execute(() -> {
            CampaignState state = forgeRepository.loadCampaign(mission.campaignId,
                    java.util.Arrays.asList("homem-aranha", "wolverine", "tocha-humana"));
            boolean completed = forgeRepository.hasCompletedMission(mission.campaignId, mission.number);
            boolean gauntletRequired = mission.number == 9 && !completed
                    && !forgeRepository.hasCompleteGauntlet();
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) return;
                boolean available = mission.number <= state.unlockedMission && !gauntletRequired;
                String label = completed ? "CONCLUÍDA" : available ? "DISPONÍVEL" : "BLOQUEADA";
                stateIcon.setText(completed ? "✓" : available ? "›" : "•");
                stateIcon.setTextColor(getColor(completed ? R.color.accent_latveria
                        : available ? accent : R.color.text_secondary));
                node.setText(completed ? "✓" : String.valueOf(chapter));
                node.setAlpha(available || completed ? 1f : .48f);
                status.setText(completed ? "Vitória registrada · toque para rejogar"
                        : available ? "Pronto para jogar · escolha seu trio"
                        : gauntletRequired ? "Complete a Manopla para entrar"
                        : "Conclua o capítulo anterior");
                status.setTextColor(getColor(completed ? R.color.accent_latveria
                        : available ? accent : R.color.text_secondary));
                progress.setProgress(completed ? 100 : available ? 32 : 0);
                item.setOnClickListener(view -> showCampaignMissionDetails(roster, mission,
                        state, completed, available, gauntletRequired, reward, accent, label));
            });
        });
    }

    private int chapterColor(int chapter) {
        int[] colors = {R.color.stone_space, R.color.stone_power, R.color.stone_soul,
                R.color.stone_time, R.color.accent_latveria, R.color.accent_xmen,
                R.color.stone_reality, R.color.stone_mind, R.color.accent_deadpool};
        return colors[Math.max(0, Math.min(colors.length - 1, chapter - 1))];
    }

    private void showCampaignMissionDetails(List<GameCatalogCharacter> roster, BattleMission mission,
            CampaignState state, boolean completed, boolean available, boolean gauntletRequired,
            CampaignReward reward, int accent, String stateLabel) {
        transientScreen = true;
        transientBack.setEnabled(true);
        navigationBar.setVisibility(View.GONE);
        contentContainer.removeAllViews();
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(getColor(R.color.canvas));
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dimension(R.dimen.space_4), dimension(R.dimen.space_4),
                dimension(R.dimen.space_4), dimension(R.dimen.space_6));
        scroll.addView(page);
        contentContainer.addView(scroll, new FrameLayout.LayoutParams(-1, -1));

        TextView back = action("‹  VOLTAR AO MAPA", this::renderShell);
        back.setTextColor(getColor(R.color.text_primary));
        back.setBackground(background(R.color.surface_elevated, R.color.border_subtle,
                dimension(R.dimen.angular_cut)));
        page.addView(back);
        ImageView enemy = editorialImage(page, dimension(R.dimen.space_8) * 12);
        TextView source = text(R.string.editorial_portrait_loading,
                R.style.TextAppearance_Ruptura_Caption, R.color.text_secondary, false);
        page.addView(source);
        portraitLoader.loadOpponent(mission.opponentId, mission.opponentName, enemy, source);
        page.addView(text("CAPÍTULO " + mission.number + " · AMEAÇA " + mission.difficulty + "/9",
                R.style.TextAppearance_Ruptura_Caption, accent, true));
        page.addView(text(mission.title, R.style.TextAppearance_Ruptura_Display,
                R.color.text_primary, true));
        page.addView(text(mission.location + " · Chefe: " + mission.opponentName,
                R.style.TextAppearance_Ruptura_Body, R.color.text_secondary, false));
        page.addView(text(stateLabel, R.style.TextAppearance_Ruptura_Label,
                completed ? R.color.accent_latveria : available ? accent : R.color.text_secondary,
                true));
        LinearLayout rewards = card();
        page.addView(rewards, new LinearLayout.LayoutParams(-1, -2));
        rewards.addView(text(completed ? "RECOMPENSA JÁ COLETADA" : "PRIMEIRA VITÓRIA",
                R.style.TextAppearance_Ruptura_Label, R.color.accent_gold, true));
        rewards.addView(text("Fragmentos faltantes para completar as seis Joias · "
                        + formatAmount(reward.credits) + " cr · " + formatAmount(reward.xp) + " XP",
                R.style.TextAppearance_Ruptura_Body, R.color.text_primary, true));
        String detail = completed
                ? "Vitória registrada. Você pode rejogar sem receber a recompensa novamente."
                : available ? "Escolha três personagens desbloqueados da sua coleção."
                : gauntletRequired ? "Complete uma Manopla para liberar este confronto."
                : "Vença o capítulo anterior para abrir este confronto.";
        page.addView(text(detail, R.style.TextAppearance_Ruptura_Body,
                R.color.text_secondary, false));
        TextView enter = action(completed ? "REJOGAR BATALHA" : "ESCOLHER EQUIPE E BATALHAR",
                () -> showTeamChooser(roster, state, mission.number));
        enter.setEnabled(available);
        LinearLayout.LayoutParams enterParams = new LinearLayout.LayoutParams(-1, -2);
        enterParams.topMargin = dimension(R.dimen.space_4);
        page.addView(enter, enterParams);
    }

    private void showVariantComparison(List<GameCatalogCharacter> roster) {
        transientScreen = true;
        transientBack.setEnabled(true);
        navigationBar.setVisibility(View.VISIBLE);
        contentContainer.removeAllViews();
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(getColor(R.color.canvas));
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dimension(R.dimen.space_4), dimension(R.dimen.space_4),
                dimension(R.dimen.space_4), dimension(R.dimen.space_6));
        scroll.addView(page);
        contentContainer.addView(scroll, new FrameLayout.LayoutParams(-1, -1));
        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);
        page.addView(top, new LinearLayout.LayoutParams(-1, -2));
        TextView back = text("‹", R.style.TextAppearance_Ruptura_Title,
                R.color.text_primary, true);
        back.setGravity(Gravity.CENTER);
        back.setContentDescription("Voltar à Coleção");
        back.setClickable(true);
        back.setFocusable(true);
        back.setMinWidth(dimension(R.dimen.target_min));
        back.setMinHeight(dimension(R.dimen.target_min));
        back.setBackground(background(R.color.surface_primary, R.color.border_subtle,
                dimension(R.dimen.target_min)));
        back.setOnClickListener(view -> renderShell());
        top.addView(back);
        LinearLayout topText = new LinearLayout(this);
        topText.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams topTextParams = new LinearLayout.LayoutParams(0, -2, 1f);
        topTextParams.leftMargin = dimension(R.dimen.space_3);
        top.addView(topText, topTextParams);
        topText.addView(text("PERSONAGEM + EVOLUÇÃO", R.style.TextAppearance_Ruptura_Caption,
                R.color.text_secondary, true));
        topText.addView(text("COMPARAR VARIANTES", R.style.TextAppearance_Ruptura_Title,
                R.color.text_primary, true));

        LinearLayout heroes = new LinearLayout(this);
        heroes.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams heroesParams = new LinearLayout.LayoutParams(-1, -2);
        heroesParams.topMargin = dimension(R.dimen.space_3);
        page.addView(heroes, heroesParams);
        TextView[] heroLabels = new TextView[2];
        ImageView[] heroImages = new ImageView[2];
        TextView[] heroSources = new TextView[2];
        android.widget.Spinner[] characterSelectors = new android.widget.Spinner[2];
        android.widget.Spinner[] tierSelectors = new android.widget.Spinner[2];
        LinearLayout choices = new LinearLayout(this);
        choices.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams choiceParams = new LinearLayout.LayoutParams(-1, -2);
        choiceParams.topMargin = dimension(R.dimen.space_3);
        page.addView(choices, choiceParams);
        int[] initial = {indexOfCharacter(roster, "wolverine"),
                indexOfCharacter(roster, "professor-xavier")};
        for (int side = 0; side < 2; side++) {
            LinearLayout hero = card();
            LinearLayout.LayoutParams heroParams = new LinearLayout.LayoutParams(0, -2, 1f);
            heroParams.setMargins(side == 0 ? 0 : dimension(R.dimen.space_1), 0,
                    side == 0 ? dimension(R.dimen.space_1) : 0, 0);
            heroes.addView(hero, heroParams);
            heroImages[side] = editorialImage(hero, dimension(R.dimen.space_8) * 4);
            TextView label = text("", R.style.TextAppearance_Ruptura_Title,
                    side == 0 ? R.color.accent_cyan : R.color.accent_gold, true);
            label.setMinHeight(dimension(R.dimen.space_8) * 2);
            hero.addView(label);
            heroLabels[side] = label;
            TextView source = text(R.string.editorial_portrait_loading,
                    R.style.TextAppearance_Ruptura_Caption, R.color.text_secondary, false);
            source.setMinimumHeight(dimension(R.dimen.target_min));
            source.setGravity(Gravity.CENTER_VERTICAL);
            hero.addView(source);
            heroSources[side] = source;

            LinearLayout column = new LinearLayout(this);
            column.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams columnParams = new LinearLayout.LayoutParams(0, -2, 1f);
            columnParams.setMargins(side == 0 ? 0 : dimension(R.dimen.space_1), 0,
                    side == 0 ? dimension(R.dimen.space_1) : 0, 0);
            choices.addView(column, columnParams);
            column.addView(text("LADO " + (side == 0 ? "A" : "B"),
                    R.style.TextAppearance_Ruptura_Caption, R.color.text_secondary, true));
            characterSelectors[side] = new android.widget.Spinner(this);
            characterSelectors[side].setContentDescription("Personagem lado " + (side == 0 ? "A" : "B"));
            characterSelectors[side].setAdapter(darkSpinnerAdapter(characterNames(roster)));
            characterSelectors[side].setSelection(Math.max(0, initial[side]));
            column.addView(characterSelectors[side], new LinearLayout.LayoutParams(-1,
                    dimension(R.dimen.target_min)));
            tierSelectors[side] = new android.widget.Spinner(this);
            tierSelectors[side].setContentDescription("Variante lado " + (side == 0 ? "A" : "B"));
            tierSelectors[side].setAdapter(darkSpinnerAdapter(
                    comparisonTierNames(roster.get(Math.max(0, initial[side])))));
            column.addView(tierSelectors[side], new LinearLayout.LayoutParams(-1,
                    dimension(R.dimen.target_min)));
        }

        LinearLayout details = new LinearLayout(this);
        details.setOrientation(LinearLayout.VERTICAL);
        page.addView(details, new LinearLayout.LayoutParams(-1, -2));
        Runnable update = () -> renderComparisonDetails(details, roster, characterSelectors,
                tierSelectors, heroLabels, heroImages, heroSources);
        for (int side = 0; side < 2; side++) {
            final int index = side;
            characterSelectors[side].setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view,
                        int position, long id) {
                    tierSelectors[index].setAdapter(darkSpinnerAdapter(comparisonTierNames(roster.get(position))));
                    tierSelectors[index].setSelection(0);
                    update.run();
                }
                @Override public void onNothingSelected(android.widget.AdapterView<?> parent) { }
            });
            tierSelectors[side].setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view,
                        int position, long id) { update.run(); }
                @Override public void onNothingSelected(android.widget.AdapterView<?> parent) { }
            });
        }
        update.run();
    }

    private int indexOfCharacter(List<GameCatalogCharacter> roster, String id) {
        for (int i = 0; i < roster.size(); i++) if (id.equals(roster.get(i).id)) return i;
        return 0;
    }

    private List<String> comparisonTierNames(GameCatalogCharacter character) {
        List<String> result = new ArrayList<>();
        for (GameCatalogVariant variant : character.variants) {
            result.add(getString(variant.tier.labelRes) + " · " + variant.name);
        }
        return result;
    }

    private void renderComparisonDetails(LinearLayout details, List<GameCatalogCharacter> roster,
            android.widget.Spinner[] characters, android.widget.Spinner[] tiers, TextView[] heroLabels,
            ImageView[] heroImages, TextView[] heroSources) {
        details.removeAllViews();
        GameCatalogCharacter left = roster.get(characters[0].getSelectedItemPosition());
        GameCatalogCharacter right = roster.get(characters[1].getSelectedItemPosition());
        int leftTier = Math.max(0, tiers[0].getSelectedItemPosition());
        int rightTier = Math.max(0, tiers[1].getSelectedItemPosition());
        heroLabels[0].setText(left.name.toUpperCase(java.util.Locale.ROOT) + "\n"
                + left.variants.get(leftTier).name);
        heroLabels[1].setText(right.name.toUpperCase(java.util.Locale.ROOT) + "\n"
                + right.variants.get(rightTier).name);
        portraitLoader.loadVariant(left.id, left.variants.get(leftTier).tier,
                left.name, heroImages[0], heroSources[0]);
        portraitLoader.loadVariant(right.id, right.variants.get(rightTier).tier,
                right.name, heroImages[1], heroSources[1]);
        int[] a = VariantStats.forVariant(left.id, left.variants.get(leftTier).tier);
        int[] b = VariantStats.forVariant(right.id, right.variants.get(rightTier).tier);
        LinearLayout panel = card();
        LinearLayout.LayoutParams panelParams = new LinearLayout.LayoutParams(-1, -2);
        panelParams.topMargin = dimension(R.dimen.space_4);
        details.addView(panel, panelParams);
        panel.addView(text("▥  ATRIBUTOS DA VARIANTE", R.style.TextAppearance_Ruptura_Caption,
                R.color.accent_cyan, true));
        String[] labels = {"Vida", "Ataque", "Defesa", "Velocidade"};
        int[] benchmarks = {1900, 650, 600, 600};
        for (int i = 0; i < labels.length; i++) {
            LinearLayout row = new LinearLayout(this);
            row.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(-1, -2);
            rowParams.topMargin = dimension(R.dimen.space_3);
            panel.addView(row, rowParams);
            row.addView(text(String.format(java.util.Locale.forLanguageTag("pt-BR"), "%,d", a[i]),
                    R.style.TextAppearance_Ruptura_Body, R.color.accent_cyan, true),
                    new LinearLayout.LayoutParams(0, -2, 1f));
            TextView attribute = text(labels[i] + "\n" + (a[i] == b[i] ? "="
                            : (a[i] > b[i] ? "+" : "") + (a[i] - b[i])),
                    R.style.TextAppearance_Ruptura_Caption, R.color.text_secondary, false);
            attribute.setGravity(Gravity.CENTER);
            row.addView(attribute, new LinearLayout.LayoutParams(0, -2, 1f));
            TextView valueB = text(String.format(java.util.Locale.forLanguageTag("pt-BR"), "%,d", b[i]),
                    R.style.TextAppearance_Ruptura_Body, R.color.accent_gold, true);
            valueB.setGravity(Gravity.END);
            row.addView(valueB, new LinearLayout.LayoutParams(0, -2, 1f));
            LinearLayout bars = new LinearLayout(this);
            bars.setGravity(Gravity.CENTER);
            panel.addView(bars, new LinearLayout.LayoutParams(-1, dimension(R.dimen.space_1)));
            LinearLayout leftTrack = new LinearLayout(this);
            leftTrack.setBackgroundColor(getColor(R.color.surface_primary));
            bars.addView(leftTrack, new LinearLayout.LayoutParams(0, -1, 1f));
            leftTrack.addView(new View(this), new LinearLayout.LayoutParams(0, -1,
                    Math.max(0, benchmarks[i] - a[i])));
            View barA = new View(this); barA.setBackgroundColor(getColor(R.color.accent_cyan));
            leftTrack.addView(barA, new LinearLayout.LayoutParams(0, -1, a[i]));
            bars.addView(new View(this), new LinearLayout.LayoutParams(dimension(R.dimen.space_3), -1));
            LinearLayout rightTrack = new LinearLayout(this);
            rightTrack.setBackgroundColor(getColor(R.color.surface_primary));
            bars.addView(rightTrack, new LinearLayout.LayoutParams(0, -1, 1f));
            View barB = new View(this); barB.setBackgroundColor(getColor(R.color.accent_gold));
            rightTrack.addView(barB, new LinearLayout.LayoutParams(0, -1, b[i]));
            rightTrack.addView(new View(this), new LinearLayout.LayoutParams(0, -1,
                    Math.max(0, benchmarks[i] - b[i])));
        }
        LinearLayout editorial = card();
        details.addView(editorial, new LinearLayout.LayoutParams(-1, -2));
        editorial.addView(text("ARQUIVO MARVEL · DADOS DO PERSONAGEM",
                R.style.TextAppearance_Ruptura_Caption, R.color.text_secondary, true));
        editorial.addView(text(left.name + "  ·  " + right.name,
                R.style.TextAppearance_Ruptura_Body, R.color.text_primary, true));
        editorial.addView(text("Dados editoriais são independentes da variante. Consulte a Comic Vine para detalhes atuais.",
                R.style.TextAppearance_Ruptura_Caption, R.color.text_secondary, false));
        LinearLayout analysis = card();
        details.addView(analysis, new LinearLayout.LayoutParams(-1, -2));
        analysis.addView(text("✦  ANÁLISE TÁTICA DINÂMICA", R.style.TextAppearance_Ruptura_Caption,
                R.color.accent_deadpool, true));
        int diff = Math.abs(VariantStats.total(a) - VariantStats.total(b));
        String assessment = diff == 0
                ? "As variantes estão equilibradas em poder total; função, Joia e composição da equipe decidem a vantagem."
                : (VariantStats.total(a) > VariantStats.total(b)
                        ? left.variants.get(leftTier).name : right.variants.get(rightTier).name)
                        + " lidera por " + String.format(java.util.Locale.forLanguageTag("pt-BR"), "%,d", diff)
                        + " pontos totais. " + (a[3] > b[3] ? left.name : right.name)
                        + " age primeiro; " + (a[2] > b[2] ? left.name : right.name)
                        + " sustenta melhor confrontos longos.";
        analysis.addView(text(assessment, R.style.TextAppearance_Ruptura_Body,
                R.color.text_primary, false));
    }

    private void showCampaignRewardScreen(String campaignId, int mission, boolean awardedNow) {
        CampaignReward reward = CampaignReward.forMission(campaignId, mission);
        java.util.Map<InfinityStone, Integer> fragments = forgeRepository.loadRewardFragments(
                "campaign:" + campaignId + ":" + mission);
        transientScreen = true;
        transientBack.setEnabled(true);
        navigationBar.setVisibility(View.GONE);
        contentContainer.removeAllViews();
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(getColor(R.color.canvas));
        contentContainer.addView(scroll, new FrameLayout.LayoutParams(-1, -1));
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dimension(R.dimen.space_4), dimension(R.dimen.space_6),
                dimension(R.dimen.space_4), dimension(R.dimen.space_6));
        scroll.addView(page);
        TextView kicker = text("OPERAÇÃO CONCLUÍDA", R.style.TextAppearance_Ruptura_Label,
                R.color.accent_gold, true);
        kicker.setLetterSpacing(0.22f);
        kicker.setGravity(Gravity.CENTER);
        page.addView(kicker);
        TextView title = text("RECOMPENSAS", R.style.TextAppearance_Ruptura_Display,
                R.color.text_primary, true);
        title.setGravity(Gravity.CENTER);
        page.addView(title);
        if (!awardedNow) {
            TextView receipt = text("RECOMPENSA JÁ REGISTRADA · SEM NOVOS CRÉDITOS OU XP",
                    R.style.TextAppearance_Ruptura_Label, R.color.accent_cyan, true);
            receipt.setGravity(Gravity.CENTER);
            page.addView(receipt);
        }
        if (currentBattle != null) {
            LinearLayout recap = card();
            LinearLayout.LayoutParams recapParams = new LinearLayout.LayoutParams(-1, -2);
            recapParams.topMargin = dimension(R.dimen.space_4);
            page.addView(recap, recapParams);
            recap.addView(text("RELATÓRIO DA BATALHA", R.style.TextAppearance_Ruptura_Label,
                    R.color.accent_cyan, true));
            recap.addView(text(currentBattle.summary().toDisplayText(),
                    R.style.TextAppearance_Ruptura_Body, R.color.text_primary, false));
        }
        LinearLayout.LayoutParams firstRow = new LinearLayout.LayoutParams(-1, -2);
        firstRow.topMargin = dimension(R.dimen.space_6);
        LinearLayout fragmentsCard = card();
        page.addView(fragmentsCard, firstRow);
        fragmentsCard.addView(text("FRAGMENTOS PARA COMPLETAR A MANOPLA",
                R.style.TextAppearance_Ruptura_Label, R.color.accent_gold, true));
        addFragmentReceipt(fragmentsCard, fragments);
        if (awardedNow) {
        LinearLayout.LayoutParams nextRow = new LinearLayout.LayoutParams(-1, -2);
        nextRow.topMargin = dimension(R.dimen.space_3);
        page.addView(rewardRow("◇", "Créditos", "+" + formatAmount(reward.credits),
                R.color.accent_gold), nextRow);
        LinearLayout.LayoutParams xpRow = new LinearLayout.LayoutParams(-1, -2);
        xpRow.topMargin = dimension(R.dimen.space_3);
        page.addView(rewardRow("◇", "Experiência", "+" + formatAmount(reward.xp) + " XP",
                R.color.accent_cyan), xpRow);

        }
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(-1, -2);
        buttonParams.topMargin = dimension(R.dimen.space_8);
        page.addView(rewardAction("IR PARA A FORJA", true, () -> {
            selectedDestination = AppDestination.FORGE;
            renderShell();
        }), buttonParams);
        LinearLayout.LayoutParams nexusParams = new LinearLayout.LayoutParams(-1, -2);
        nexusParams.topMargin = dimension(R.dimen.space_3);
        page.addView(rewardAction("VOLTAR AO NEXUS", false, () -> {
            selectedDestination = AppDestination.NEXUS;
            renderShell();
        }), nexusParams);
    }

    private void addFragmentReceipt(LinearLayout parent,
                                    java.util.Map<InfinityStone, Integer> fragments) {
        if (fragments.isEmpty()) {
            parent.addView(text("Recompensa anterior já registrada.",
                    R.style.TextAppearance_Ruptura_Caption, R.color.text_secondary, false));
            return;
        }
        for (InfinityStone stone : InfinityStone.values()) {
            int amount = fragments.getOrDefault(stone, 0);
            parent.addView(text(fragmentName(stone) + "  +" + amount,
                    R.style.TextAppearance_Ruptura_Body,
                    amount > 0 ? stoneColor(stone) : R.color.text_secondary, amount > 0));
        }
        parent.addView(text("Faça as fusões 2:1 na Forja para formar as seis Joias.",
                R.style.TextAppearance_Ruptura_Caption, R.color.text_secondary, false));
    }

    private LinearLayout rewardRow(String icon, String label, String value, int accent) {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setMinimumHeight(dimension(R.dimen.space_8) * 2 + dimension(R.dimen.space_4));
        row.setPadding(dimension(R.dimen.space_4), dimension(R.dimen.space_2),
                dimension(R.dimen.space_4), dimension(R.dimen.space_2));
        int tint = androidx.core.graphics.ColorUtils.blendARGB(
                getColor(R.color.surface_primary), getColor(accent), .12f);
        row.setBackground(new AngularPanelDrawable(tint, getColor(R.color.surface_primary),
                getColor(R.color.border_subtle), dimension(R.dimen.angular_cut), false));
        TextView glyph = text(icon, R.style.TextAppearance_Ruptura_Title, accent, true);
        glyph.setGravity(Gravity.CENTER);
        glyph.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        row.addView(glyph, new LinearLayout.LayoutParams(
                dimension(R.dimen.space_8) + dimension(R.dimen.space_2), -2));
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(0, -2, 1f);
        contentParams.leftMargin = dimension(R.dimen.space_2);
        row.addView(content, contentParams);
        content.addView(text(label, R.style.TextAppearance_Ruptura_Body,
                R.color.text_secondary, false));
        content.addView(text(value, R.style.TextAppearance_Ruptura_Title, accent, true));
        return row;
    }

    private TextView rewardAction(String label, boolean primary, Runnable task) {
        TextView button = action(label, task);
        button.setBackground(new AngularPanelDrawable(
                getColor(primary ? R.color.accent_gold : R.color.surface_primary),
                getColor(primary ? R.color.infinity_gold : R.color.canvas),
                getColor(primary ? R.color.accent_gold : R.color.border_subtle),
                dimension(R.dimen.angular_cut), false));
        button.setTextColor(getColor(primary ? R.color.canvas : R.color.text_primary));
        return button;
    }

    private int stoneColor(InfinityStone stone) {
        switch (stone) {
            case SPACE: return R.color.stone_space;
            case MIND: return R.color.stone_mind;
            case REALITY: return R.color.stone_reality;
            case POWER: return R.color.stone_power;
            case TIME: return R.color.stone_time;
            case SOUL: return R.color.stone_soul;
            default: throw new IllegalArgumentException("Unknown stone");
        }
    }

    private String fragmentName(InfinityStone stone) {
        switch (stone) {
            case SPACE: return getString(R.string.reward_fragment_space);
            case MIND: return getString(R.string.reward_fragment_mind);
            case REALITY: return getString(R.string.reward_fragment_reality);
            case POWER: return getString(R.string.reward_fragment_power);
            case TIME: return getString(R.string.reward_fragment_time);
            case SOUL: return getString(R.string.reward_fragment_soul);
            default: throw new IllegalArgumentException("Unknown stone");
        }
    }

    private void showTeamChooser(List<GameCatalogCharacter> roster, CampaignState campaign, int mission) {
        selectedBattleMission = BattleMission.forMission(campaign.campaignId, mission);
        transientScreen = true;
        transientBack.setEnabled(true);
        navigationBar.setVisibility(View.GONE);
        List<String> selected = new ArrayList<>(campaign.teamIds);
        renderTeamChooser(roster, campaign, mission, selected);
    }

    private void renderTeamChooser(List<GameCatalogCharacter> roster, CampaignState campaign,
                                   int mission, List<String> selected) {
        contentContainer.removeAllViews();
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(getColor(R.color.canvas));
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dimension(R.dimen.space_4), dimension(R.dimen.space_4),
                dimension(R.dimen.space_4), dimension(R.dimen.space_6));
        page.addView(text("ESCOLHA SUA EQUIPE", R.style.TextAppearance_Ruptura_Display,
                R.color.text_primary, true));
        page.addView(text("Toque em três personagens da sua coleção. A variante equipada altera o poder da equipe.",
                R.style.TextAppearance_Ruptura_Body, R.color.text_secondary, false));
        LinearLayout slotRow = new LinearLayout(this);
        slotRow.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams slotsParams = new LinearLayout.LayoutParams(-1, -2);
        slotsParams.topMargin = dimension(R.dimen.space_4);
        page.addView(slotRow, slotsParams);
        for (int slotIndex = 0; slotIndex < 3; slotIndex++) {
            int position = slotIndex;
            LinearLayout slot = card();
            slot.setPadding(dimension(R.dimen.space_2), dimension(R.dimen.space_2),
                    dimension(R.dimen.space_2), dimension(R.dimen.space_2));
            slot.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams slotParams = new LinearLayout.LayoutParams(0, -2, 1f);
            slotParams.setMargins(dimension(R.dimen.space_1), 0,
                    dimension(R.dimen.space_1), 0);
            slotRow.addView(slot, slotParams);
            slot.setMinimumHeight(dimension(R.dimen.space_8) * 3);
            if (slotIndex < selected.size()) {
                String chosenId = selected.get(slotIndex);
                GameCatalogCharacter chosenCharacter = findCharacter(roster, chosenId);
                if (chosenCharacter != null) {
                    TextView slotNumber = text("SLOT " + (slotIndex + 1),
                            R.style.TextAppearance_Ruptura_Caption, R.color.accent_gold, true);
                    slotNumber.setGravity(Gravity.CENTER);
                    slot.addView(slotNumber);
                    GameVariantTier chosenTier = tierById(forgeRepository.loadEquippedTier(chosenId));
                    ImageView chosenPortrait = editorialImage(slot, dimension(R.dimen.space_8) * 2);
                    portraitLoader.loadVariant(chosenId, chosenTier, chosenCharacter.name,
                            chosenPortrait, null);
                    TextView chosenName = text(chosenCharacter.name,
                            R.style.TextAppearance_Ruptura_Caption, R.color.accent_cyan, true);
                    chosenName.setGravity(Gravity.CENTER);
                    slot.addView(chosenName);
                    slot.addView(text("TOQUE PARA REMOVER", R.style.TextAppearance_Ruptura_Caption,
                            R.color.text_secondary, false));
                    slot.setClickable(true);
                    slot.setFocusable(true);
                    slot.setOnClickListener(view -> {
                        selected.remove(chosenId);
                        renderTeamChooser(roster, campaign, mission, selected);
                    });
                }
            } else {
                TextView emptySlot = text("+\nSLOT " + (slotIndex + 1),
                        R.style.TextAppearance_Ruptura_Title, R.color.text_secondary, true);
                emptySlot.setGravity(Gravity.CENTER);
                slot.addView(emptySlot);
            }
        }
        TextView slotHint = text("Escolha três heróis. Toque em um slot preenchido para liberar a vaga.",
                R.style.TextAppearance_Ruptura_Caption, R.color.text_secondary, false);
        page.addView(slotHint);
        LinearLayout row = null;
        int shown = 0;
        for (GameCatalogCharacter character : roster) {
            if (!forgeRepository.ownsCharacter(character.id)) continue;
            if (shown % 2 == 0) {
                row = new LinearLayout(this);
                LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(-1, -2);
                rowParams.topMargin = dimension(R.dimen.space_3);
                page.addView(row, rowParams);
            }
            GameVariantTier tier = tierById(forgeRepository.loadEquippedTier(character.id));
            int[] stats = VariantStats.forVariant(character.id, tier);
            LinearLayout item = card();
            boolean chosen = selected.contains(character.id);
            item.setBackground(new AngularPanelDrawable(getColor(R.color.surface_primary),
                    getColor(R.color.surface_elevated), getColor(chosen
                            ? R.color.accent_cyan : R.color.border_subtle),
                    dimension(R.dimen.angular_cut), false));
            ImageView image = editorialImage(item, dimension(R.dimen.space_8) * 3);
            portraitLoader.loadVariant(character.id, tier, character.name, image, null);
            item.addView(text(character.name, R.style.TextAppearance_Ruptura_Label,
                    R.color.text_primary, true));
            item.addView(text(getString(tier.labelRes) + " · PODER " + VariantStats.total(stats),
                    R.style.TextAppearance_Ruptura_Caption, R.color.accent_gold, false));
            item.addView(text(chosen ? "✓ NA EQUIPE" : "TOQUE PARA ESCOLHER",
                    R.style.TextAppearance_Ruptura_Caption,
                    chosen ? R.color.accent_cyan : R.color.text_secondary, true));
            item.setClickable(true);
            item.setFocusable(true);
            item.setOnClickListener(view -> {
                if (selected.contains(character.id)) selected.remove(character.id);
                else if (selected.size() < 3) selected.add(character.id);
                renderTeamChooser(roster, campaign, mission, selected);
            });
            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(0, -2, 1f);
            if (shown % 2 == 0) itemParams.rightMargin = dimension(R.dimen.space_2);
            else itemParams.leftMargin = dimension(R.dimen.space_2);
            row.addView(item, itemParams);
            shown++;
        }
        TextView enter = action("INICIAR BATALHA", () -> {
            if (selected.size() != 3) return;
            forgeExecutor.execute(() -> {
                try {
                    CampaignState saved = forgeRepository.saveTeam(campaign.campaignId,
                            new ArrayList<>(selected), roster);
                    List<LovableBattle.FighterSpec> fighters = buildBattleFighters(roster, saved.teamIds);
                    runOnUiThread(() -> showMagnetoBattle(new LovableBattle(fighters,
                            BattleMission.forMission(saved.campaignId, mission)), saved));
                } catch (RuntimeException error) {
                    runOnUiThread(() -> android.widget.Toast.makeText(this,
                            R.string.campaign_team_invalid, android.widget.Toast.LENGTH_LONG).show());
                }
            });
        });
        enter.setEnabled(selected.size() == 3);
        page.addView(enter);
        page.addView(action("VOLTAR ÀS BATALHAS", this::renderShell));
        scroll.addView(page);
        contentContainer.addView(scroll, new FrameLayout.LayoutParams(-1, -1));
    }

    private void showMagnetoBattle(LovableBattle battle, CampaignState campaign) {
        currentBattle = battle;
        selectedBattleMission = battle.mission;
        currentBattleContext = battle.mission.title + " — " + battle.mission.opponentName
                + ", ação " + (battle.round + 1)
                + (battle.victory ? ", concluída" : battle.defeat ? ", derrota" : ", em andamento");
        currentTeamContext = describeTeam(campaign.teamIds);
        transientScreen = true;
        battleOpen = !battle.victory && !battle.defeat;
        if (battleOpen) startBattleMusic(); else stopBattleMusic();
        transientBack.setEnabled(true);
        battleAnimating = false;
        battleClaiming = false;
        renderMagnetoBattle(battle, campaign);
    }

    private List<LovableBattle.FighterSpec> buildBattleFighters(
            List<GameCatalogCharacter> roster, List<String> teamIds) {
        List<LovableBattle.FighterSpec> specs = new ArrayList<>();
        for (String id : teamIds) {
            GameCatalogCharacter character = findCharacter(roster, id);
            if (character == null || !forgeRepository.ownsCharacter(id))
                throw new IllegalArgumentException("Battle character is unavailable");
            GameVariantTier tier = tierById(forgeRepository.loadEquippedTier(id));
            if (!forgeRepository.loadOwnedTiers(id).contains(tier))
                throw new IllegalStateException("Equipped variant is not owned");
            specs.add(new LovableBattle.FighterSpec(id, character.name,
                    getString(tier.labelRes).toUpperCase(java.util.Locale.ROOT),
                    VariantStats.forVariant(id, tier), BattleSpecial.nameForCharacter(id)));
        }
        return specs;
    }

    private List<LovableBattle.FighterSpec> copyBattleFighters(LovableBattle battle) {
        List<LovableBattle.FighterSpec> specs = new ArrayList<>();
        for (LovableBattle.Fighter fighter : battle.fighters()) specs.add(fighter.spec);
        return specs;
    }

    private String describeTeam(List<String> ids) {
        List<GameCatalogCharacter> roster = loadRoster();
        StringBuilder result = new StringBuilder();
        for (String id : ids) {
            GameCatalogCharacter character = findCharacter(roster, id);
            if (result.length() > 0) result.append(", ");
            if (character == null) { result.append(id); continue; }
            GameVariantTier equipped = tierById(forgeRepository.loadEquippedTier(character.id));
            result.append(character.name).append(" (").append(getString(equipped.labelRes)).append(')');
        }
        return result.length() == 0 ? "nenhum personagem salvo" : result.toString();
    }

    private void renderMagnetoBattle(LovableBattle battle, CampaignState campaign) {
        battleOpen = !battle.victory && !battle.defeat;
        if (battleOpen) startBattleMusic(); else stopBattleMusic();
        navigationBar.setVisibility(View.GONE);
        contentContainer.removeAllViews();
        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setTag("campaign-battle-screen");
        screen.setPadding(dimension(R.dimen.space_4), dimension(R.dimen.space_3),
                dimension(R.dimen.space_4), dimension(R.dimen.space_4));
        screen.setBackgroundColor(getColor(R.color.canvas));
        contentContainer.addView(screen, new FrameLayout.LayoutParams(-1, -1));
        LinearLayout heading = new LinearLayout(this);
        heading.setGravity(Gravity.CENTER_VERTICAL);
        screen.addView(heading, new LinearLayout.LayoutParams(-1, -2));
        LinearLayout titles = new LinearLayout(this);
        titles.setOrientation(LinearLayout.VERTICAL);
        heading.addView(titles, new LinearLayout.LayoutParams(0, -2, 1f));
        titles.addView(text("AÇÃO " + (battle.round + 1) + "  ·  "
                        + battle.mission.title.toUpperCase(java.util.Locale.ROOT),
                R.style.TextAppearance_Ruptura_Caption, R.color.accent_xmen, true));
        titles.addView(text(battle.mission.location.toUpperCase(java.util.Locale.ROOT),
                R.style.TextAppearance_Ruptura_Title,
                R.color.text_primary, true));
        TextView exit = text("SAIR", R.style.TextAppearance_Ruptura_Caption,
                R.color.text_secondary, true);
        exit.setMinimumHeight(dimension(R.dimen.target_min));
        exit.setGravity(Gravity.CENTER);
        exit.setOnClickListener(view -> renderShell());
        heading.addView(exit);

        LinearLayout health = new LinearLayout(this);
        LinearLayout.LayoutParams healthParams = new LinearLayout.LayoutParams(-1, -2);
        healthParams.topMargin = dimension(R.dimen.space_3);
        screen.addView(health, healthParams);
        battleMeter(health, battle.mission.opponentName.toUpperCase(java.util.Locale.ROOT),
                battle.boss, battle.maxBoss, R.color.accent_deadpool);
        battleMeter(health, "EQUIPE", battle.team, R.color.accent_latveria);

        ScrollView central = new ScrollView(this);
        central.setFillViewport(true);
        screen.addView(central, new LinearLayout.LayoutParams(-1, 0, 1f));
        LinearLayout scene = new LinearLayout(this);
        scene.setOrientation(LinearLayout.VERTICAL);
        central.addView(scene);
        LinearLayout boss = card();
        battleBossView = boss;
        boss.setGravity(Gravity.CENTER);
        boss.setMinimumHeight(dimension(R.dimen.space_8) * 5);
        boss.setBackground(new AngularPanelDrawable(getColor(R.color.surface_primary),
                getColor(R.color.surface_elevated), getColor(R.color.accent_deadpool),
                dimension(R.dimen.angular_cut), false));
        scene.addView(boss, new LinearLayout.LayoutParams(-1, -2));
        ImageView bossPortrait = editorialImage(boss, dimension(R.dimen.space_8) * 4);
        TextView bossSource = text(R.string.editorial_portrait_loading,
                R.style.TextAppearance_Ruptura_Caption, R.color.text_secondary, false);
        bossSource.setGravity(Gravity.CENTER);
        boss.addView(bossSource);
        portraitLoader.loadOpponent(battle.mission.opponentId, battle.mission.opponentName,
                bossPortrait, bossSource);
        TextView bossLabel = text("CHEFE\n" + battle.mission.opponentName.toUpperCase(java.util.Locale.ROOT),
                R.style.TextAppearance_Ruptura_Title,
                R.color.text_primary, true);
        bossLabel.setGravity(Gravity.CENTER);
        boss.addView(bossLabel, new LinearLayout.LayoutParams(-1, -2));
        LinearLayout team = new LinearLayout(this);
        scene.addView(team, new LinearLayout.LayoutParams(-1, -2));
        List<GameCatalogCharacter> battleRoster = loadRoster();
        battleFighterViews.clear();
        for (LovableBattle.Fighter fighter : battle.fighters()) {
            LovableBattle.FighterSpec spec = fighter.spec;
            boolean active = spec.id.equals(battle.activeFighterId);
            FrameLayout frame = new FrameLayout(this);
            LinearLayout member = card();
            member.setPadding(dimension(R.dimen.space_1), dimension(R.dimen.space_1),
                    dimension(R.dimen.space_1), dimension(R.dimen.space_1));
            member.setBackground(new AngularPanelDrawable(getColor(R.color.surface_primary),
                    getColor(R.color.surface_elevated), getColor(fighter.alive()
                    ? active ? R.color.accent_gold : R.color.border_subtle : R.color.accent_deadpool),
                    dimension(R.dimen.angular_cut), false));
            if (active) {
                member.setElevation(dimension(R.dimen.space_3));
                member.setBackground(new AngularPanelDrawable(getColor(R.color.surface_elevated),
                        getColor(R.color.surface_primary), getColor(R.color.accent_cyan),
                        dimension(R.dimen.angular_cut), false));
            }
            frame.addView(member, new FrameLayout.LayoutParams(-1, -2));
            ImageView portrait = editorialImage(member, dimension(R.dimen.space_8) * 2);
            portraitLoader.loadVariant(spec.id, tierById(forgeRepository.loadEquippedTier(spec.id)),
                    spec.name, portrait, null);
            TextView state = text(!fighter.alive() ? "CAIU"
                            : active ? "● ATIVO" : "TOQUE PARA ATIVAR",
                    R.style.TextAppearance_Ruptura_Caption,
                    !fighter.alive() ? R.color.accent_deadpool : active ? R.color.accent_gold : R.color.text_secondary,
                    true);
            state.setGravity(Gravity.CENTER);
            if (active) {
                state.setPadding(dimension(R.dimen.space_1), dimension(R.dimen.space_1),
                        dimension(R.dimen.space_1), dimension(R.dimen.space_1));
                state.setBackground(background(R.color.surface_primary, R.color.accent_gold,
                        dimension(R.dimen.space_2)));
            }
            member.addView(state);
            if (active) {
                TextView activeHint = text("SUA VEZ", R.style.TextAppearance_Ruptura_Caption,
                        R.color.accent_cyan, true);
                activeHint.setGravity(Gravity.CENTER);
                member.addView(activeHint);
            }
            TextView nameLabel = text(spec.name, R.style.TextAppearance_Ruptura_Caption,
                    R.color.text_primary, true);
            nameLabel.setGravity(Gravity.CENTER);
            member.addView(nameLabel);
            TextView variantLabel = text(spec.tierName, R.style.TextAppearance_Ruptura_Caption,
                    R.color.text_secondary, false);
            variantLabel.setGravity(Gravity.CENTER);
            member.addView(variantLabel);
            member.addView(text(spec.specialName, R.style.TextAppearance_Ruptura_Caption,
                    R.color.accent_gold, false));
            member.addView(text("HP " + fighter.health + " / " + fighter.maxHealth,
                    R.style.TextAppearance_Ruptura_Caption, R.color.text_secondary, false));
            LinearLayout hp = new LinearLayout(this);
            member.addView(hp, new LinearLayout.LayoutParams(-1, dimension(R.dimen.space_1)));
            addBattleBar(hp, Math.round(fighter.health * 100f / fighter.maxHealth),
                    fighter.alive() ? active ? R.color.accent_cyan : R.color.accent_gold
                            : R.color.accent_deadpool);
            LinearLayout.LayoutParams memberParams = new LinearLayout.LayoutParams(0, -2, 1f);
            memberParams.setMargins(dimension(R.dimen.space_1), dimension(R.dimen.space_2),
                    dimension(R.dimen.space_1), 0);
            team.addView(frame, memberParams);
            battleFighterViews.put(spec.id, frame);
            frame.setTag(active ? "battle-active-fighter" : "battle-fighter");
            frame.setContentDescription(spec.name + (active ? ", PERSONAGEM ATIVO, SUA VEZ" : "")
                    + ", " + spec.tierName + ", HP "
                    + fighter.health + " de " + fighter.maxHealth + ", especial " + spec.specialName
                    + (active ? ", personagem ativo" : fighter.alive()
                    ? ", toque para ativar sem gastar ação" : ", fora de combate"));
            if (fighter.alive() && !battle.victory && !battle.defeat) {
                frame.setClickable(true);
                frame.setFocusable(true);
                frame.setOnClickListener(view -> {
                    if (battleAnimating || spec.id.equals(battle.activeFighterId)) return;
                    try {
                        battle.selectFighter(spec.id);
                        playUiSound();
                        renderMagnetoBattle(battle, campaign);
                    } catch (IllegalStateException | IllegalArgumentException ignored) { }
                });
            }
        }
        if (battle.victory) {
            scene.addView(text("RESSONÂNCIA INTERROMPIDA", R.style.TextAppearance_Ruptura_Caption,
                    R.color.accent_latveria, true));
            scene.addView(text("VITÓRIA", R.style.TextAppearance_Ruptura_Display,
                    R.color.text_primary, true));
            scene.addView(text(battle.mission.opponentName + " recuou. Vitória registrada.",
                    R.style.TextAppearance_Ruptura_Body, R.color.text_secondary, false));
        } else if (battle.defeat) {
            scene.addView(text("EQUIPE DERRUBADA", R.style.TextAppearance_Ruptura_Caption,
                    R.color.accent_deadpool, true));
            scene.addView(text("DERROTA", R.style.TextAppearance_Ruptura_Display,
                    R.color.text_primary, true));
            scene.addView(text(battle.mission.opponentName
                            + " manteve o controle do campo. Uma nova tentativa pode mudar o resultado.",
                    R.style.TextAppearance_Ruptura_Body, R.color.text_secondary, false));
            scene.addView(text("RELATÓRIO DA BATALHA", R.style.TextAppearance_Ruptura_Label,
                    R.color.accent_cyan, true));
            scene.addView(text(battle.summary().toDisplayText(),
                    R.style.TextAppearance_Ruptura_Body, R.color.text_primary, false));
        } else if (battle.replacementRequired) {
            scene.addView(text("ESCOLHA UM HERÓI VIVO PARA CONTINUAR",
                    R.style.TextAppearance_Ruptura_Label, R.color.accent_cyan, true));
        } else if (battle.canChoose()) {
            LinearLayout intent = card();
            LinearLayout.LayoutParams intentParams = new LinearLayout.LayoutParams(-1, -2);
            intentParams.topMargin = dimension(R.dimen.space_3);
            scene.addView(intent, intentParams);
            intent.addView(text("⊕  " + battle.intent().toUpperCase(java.util.Locale.ROOT),
                    R.style.TextAppearance_Ruptura_Label, R.color.accent_xmen, true));
            intent.addView(text(battle.warning(),
                    R.style.TextAppearance_Ruptura_Caption, R.color.text_secondary, false));
        }
        TextView feedback = text(battle.feedback, R.style.TextAppearance_Ruptura_Body,
                R.color.text_primary, true);
        feedback.setGravity(Gravity.CENTER);
        feedback.setPadding(dimension(R.dimen.space_4), dimension(R.dimen.space_3),
                dimension(R.dimen.space_4), dimension(R.dimen.space_3));
        feedback.setBackground(new AngularPanelDrawable(getColor(R.color.surface_elevated),
                getColor(R.color.surface_primary), getColor(battle.lastTrap
                        ? R.color.accent_deadpool : battle.lastCounter
                        ? R.color.accent_cyan : R.color.accent_gold),
                dimension(R.dimen.angular_cut), false));
        feedback.setElevation(dimension(R.dimen.space_2));
        feedback.setMinHeight(dimension(R.dimen.space_8) * 3);
        feedback.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);
        scene.addView(feedback, new LinearLayout.LayoutParams(-1, -2));

        if (battle.defeat) {
            screen.addView(action("TENTAR NOVAMENTE", () -> showMagnetoBattle(
                    new LovableBattle(copyBattleFighters(battle), battle.mission),
                    campaign)), new LinearLayout.LayoutParams(-1, -2));
        } else if (!battle.victory) {
            LinearLayout charge = new LinearLayout(this);
            charge.setGravity(Gravity.CENTER_VERTICAL);
            screen.addView(charge, new LinearLayout.LayoutParams(-1, -2));
            charge.addView(text("ϟ", R.style.TextAppearance_Ruptura_Title,
                    R.color.accent_gold, true));
            addBattleBar(charge, battle.charge, R.color.accent_gold);
            charge.addView(text(battle.charge + "%", R.style.TextAppearance_Ruptura_Caption,
                    R.color.accent_gold, true));
            if (battle.replacementRequired) {
                screen.addView(text("Toque em um dos personagens vivos para trocar. A troca não consome ação.",
                        R.style.TextAppearance_Ruptura_Caption, R.color.text_secondary, false));
            }
            if (battle.canSpecial()) {
                LinearLayout special = new LinearLayout(this);
                special.setOrientation(LinearLayout.VERTICAL);
                special.setGravity(Gravity.CENTER_VERTICAL);
                special.setPadding(dimension(R.dimen.space_3), dimension(R.dimen.space_2),
                        dimension(R.dimen.space_3), dimension(R.dimen.space_2));
                GradientDrawable superBackground = new GradientDrawable(
                        GradientDrawable.Orientation.TL_BR,
                        new int[]{getColor(R.color.surface_elevated), getColor(R.color.surface_primary)});
                superBackground.setCornerRadius(dimension(R.dimen.angular_cut));
                superBackground.setStroke(dimension(R.dimen.space_1) / 4,
                        getColor(R.color.accent_gold));
                special.setBackground(superBackground);
                special.setElevation(dimension(R.dimen.space_2));
                special.setMinimumHeight(dimension(R.dimen.target_min));
                special.setFocusable(true);
                special.setClickable(true);
                special.addView(text("∞  SUPER GLOBAL · " + battle.activeFighter().spec.specialName
                                .toUpperCase(java.util.Locale.ROOT),
                        R.style.TextAppearance_Ruptura_Label, R.color.accent_gold, true));
                special.addView(text("CARGA COMPARTILHADA  ·  LIBERAR AGORA",
                        R.style.TextAppearance_Ruptura_Caption, R.color.text_primary, true));
                special.setContentDescription("Super global pronto. Golpe de "
                        + battle.activeFighter().spec.name + ": "
                        + battle.activeFighter().spec.specialName + ". Toque para liberar.");
                special.setTag("battle-super-button");
                special.setOnClickListener(view -> {
                    if (battleAnimating || !battle.canSpecial()) return;
                    battleAnimating = true;
                    battle.special();
                    playBattleSound(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
                    renderMagnetoBattle(battle, campaign);
                    animateBattleAction(battle.activeFighterId, null, true);
                    showBattleImpact("KRAKOOM", battle.lastBossDamage, battle.lastTeamDamage, true);
                    contentContainer.postDelayed(() -> finishBattleActionAnimation(battle, campaign),
                            animationDelay(2500));
                });
                special.setEnabled(!battleAnimating);
                screen.addView(special, new LinearLayout.LayoutParams(-1, -2));
            }
            if (!battle.replacementRequired) {
                LinearLayout choices = new LinearLayout(this);
                screen.addView(choices, new LinearLayout.LayoutParams(-1, -2));
                battleChoice(choices, "⚔\nINVESTIR\nDANO ALTO", LovableBattle.Choice.ATTACK, battle, campaign);
                battleChoice(choices, "⬡\nPROTEGER\nREDUZ IMPACTO", LovableBattle.Choice.DEFEND, battle, campaign);
                battleChoice(choices, "✦\nDESESTABILIZAR\n+ CARGA", LovableBattle.Choice.CONTROL, battle, campaign);
            }
        } else {
            TextView[] rewardControl = new TextView[1];
            TextView reward = action("COLETAR RECOMPENSA", () -> {
                if (battleClaiming || !battle.victory) return;
                battleClaiming = true;
                rewardControl[0].setEnabled(false);
                rewardControl[0].setText("REGISTRANDO RECOMPENSA...");
                forgeExecutor.execute(() -> {
                    try {
                        boolean granted = forgeRepository.completeMission(campaign.campaignId,
                                battle.mission.number);
                        runOnUiThread(() -> {
                            Runnable continueAfterStory = () -> {
                                battleClaiming = false;
                                showCampaignRewardScreen(campaign.campaignId,
                                        battle.mission.number, granted);
                            };
                            showStorySequence(CampaignStory.afterMission(battle.mission.number),
                                    campaign.teamIds, continueAfterStory);
                        });
                    } catch (RuntimeException error) {
                        runOnUiThread(() -> { battleClaiming = false;
                            rewardControl[0].setEnabled(true);
                            rewardControl[0].setText("COLETAR RECOMPENSA");
                            android.widget.Toast.makeText(this, error instanceof ForgeException
                                            ? "Libere espaço na Forja para receber os Fragmentos."
                                            : "Não foi possível concluir a missão",
                                    android.widget.Toast.LENGTH_LONG).show(); });
                    }
                });
            });
            rewardControl[0] = reward;
            reward.setEnabled(!battleClaiming);
            screen.addView(reward, new LinearLayout.LayoutParams(-1, -2));
        }
    }

    private void battleChoice(LinearLayout choices, String label, LovableBattle.Choice choice,
            LovableBattle battle, CampaignState campaign) {
        TextView button = text(label, R.style.TextAppearance_Ruptura_Caption,
                R.color.text_primary, true);
        button.setGravity(Gravity.CENTER);
        button.setMinHeight(dimension(R.dimen.target_min) + dimension(R.dimen.space_4));
        button.setBackground(background(R.color.surface_elevated, R.color.border_subtle, 0));
        button.setClickable(true);
        button.setFocusable(true);
        button.setEnabled(!battleAnimating);
        button.setContentDescription(label.replace('\n', ' '));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, -2, 1f);
        params.setMargins(dimension(R.dimen.space_1), 0, dimension(R.dimen.space_1), 0);
        choices.addView(button, params);
        button.setOnClickListener(view -> {
            if (battleAnimating || !battle.canChoose()) return;
            battleAnimating = true;
            battle.choose(choice);
            playBattleSound(choice == LovableBattle.Choice.ATTACK ? ToneGenerator.TONE_PROP_BEEP2
                    : choice == LovableBattle.Choice.DEFEND ? ToneGenerator.TONE_PROP_ACK
                    : ToneGenerator.TONE_DTMF_5);
            renderMagnetoBattle(battle, campaign);
            animateBattleAction(battle.activeFighterId, choice, false);
            String actionLabel = battle.lastTrap ? "ARMADILHA"
                    : choice == LovableBattle.Choice.ATTACK ? "GOLPE"
                    : choice == LovableBattle.Choice.DEFEND ? "GUARDA" : "RESSONÂNCIA";
            showBattleImpact(actionLabel + (battle.lastCounter ? " · BRECHA" : ""),
                    battle.lastBossDamage, battle.lastTeamDamage, false);
            contentContainer.postDelayed(() -> finishBattleActionAnimation(battle, campaign),
                    animationDelay(1800));
        });
    }

    private boolean shouldRerenderBattleAfterAction(LovableBattle battle) {
        return currentBattle == battle && !battleClaiming && !storyOpen
                && contentContainer.getChildCount() > 0
                && "campaign-battle-screen".equals(contentContainer.getChildAt(0).getTag());
    }

    private void finishBattleActionAnimation(LovableBattle battle, CampaignState campaign) {
        battleAnimating = false;
        if (shouldRerenderBattleAfterAction(battle)) renderMagnetoBattle(battle, campaign);
    }

    private void animateBattleAction(String fighterId, LovableBattle.Choice choice,
                                     boolean special) {
        View fighter = battleFighterViews.get(fighterId);
        if (fighter == null || animationDelay(1) == 0) return;
        if (special || choice == LovableBattle.Choice.ATTACK) {
            fighter.animate().translationY(-dimension(R.dimen.space_8))
                    .scaleX(1.06f).scaleY(1.06f).setDuration(180)
                    .withEndAction(() -> fighter.animate().translationY(0f)
                            .scaleX(1f).scaleY(1f).setDuration(260));
            if (battleBossView != null) {
                battleBossView.animate().scaleX(1.04f).scaleY(1.04f).setDuration(180)
                        .withEndAction(() -> battleBossView.animate().scaleX(1f).scaleY(1f)
                                .setDuration(240));
            }
        } else if (choice == LovableBattle.Choice.DEFEND && fighter instanceof FrameLayout) {
            FrameLayout frame = (FrameLayout) fighter;
            TextView shield = text("🛡", R.style.TextAppearance_Ruptura_Display,
                    R.color.accent_cyan, true);
            shield.setGravity(Gravity.CENTER);
            shield.setShadowLayer(dimension(R.dimen.space_2), 0, 0,
                    getColor(R.color.accent_cyan));
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-1, -1, Gravity.CENTER);
            frame.addView(shield, params);
            shield.setScaleX(.45f);
            shield.setScaleY(.45f);
            shield.setAlpha(.25f);
            shield.animate().scaleX(1.25f).scaleY(1.25f).alpha(.94f).setDuration(240)
                    .withEndAction(() -> shield.animate().alpha(0f).setDuration(650)
                            .withEndAction(() -> frame.removeView(shield)));
        } else if (choice == LovableBattle.Choice.CONTROL) {
            fighter.animate().rotation(3f).setDuration(100)
                    .withEndAction(() -> fighter.animate().rotation(-3f).setDuration(100)
                            .withEndAction(() -> fighter.animate().rotation(0f).setDuration(100)));
        }
    }

    private int animationDelay(int normal) {
        return android.provider.Settings.Global.getFloat(getContentResolver(),
                android.provider.Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f ? 0 : normal;
    }

    private void showBattleImpact(String label, int bossDamage, int teamDamage, boolean special) {
        if (animationDelay(1) == 0) return;
        FrameLayout flash = new FrameLayout(this);
        flash.setBackgroundColor(special ? 0x7748E0FF
                : label.startsWith("ARMADILHA") ? 0x44E53945 : 0x2EFFFFFF);
        flash.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        contentContainer.addView(flash, new FrameLayout.LayoutParams(-1, -1));
        if (special) {
            View ring = new View(this);
            GradientDrawable circle = new GradientDrawable();
            circle.setShape(GradientDrawable.OVAL);
            circle.setColor(0x00303060);
            circle.setStroke(dimension(R.dimen.space_1), getColor(R.color.accent_gold));
            ring.setBackground(circle);
            FrameLayout.LayoutParams ringParams = new FrameLayout.LayoutParams(
                    dimension(R.dimen.space_8) * 5, dimension(R.dimen.space_8) * 5, Gravity.CENTER);
            flash.addView(ring, ringParams);
            ring.setScaleX(.25f);
            ring.setScaleY(.25f);
            ring.animate().scaleX(2f).scaleY(2f).alpha(0f).setDuration(1200);
        }
        LovableBattle.Fighter active = currentBattle == null ? null : currentBattle.activeFighter();
        String damagedName = active == null ? "HERÓI" : active.spec.name.toUpperCase(java.util.Locale.ROOT);
        TextView impact = text(label + "\n-" + bossDamage + " CHEFE"
                        + (teamDamage > 0 ? " · -" + teamDamage + " HP " + damagedName : ""),
                R.style.TextAppearance_Ruptura_Title,
                special ? R.color.accent_gold : R.color.accent_deadpool, true);
        impact.setGravity(Gravity.CENTER);
        impact.setElevation(dimension(R.dimen.space_4));
        impact.setPadding(dimension(R.dimen.space_3), dimension(R.dimen.space_2),
                dimension(R.dimen.space_3), dimension(R.dimen.space_2));
        impact.setBackground(background(R.color.surface_elevated,
                special ? R.color.accent_gold : R.color.border_subtle,
                dimension(R.dimen.radius_card)));
        impact.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-1,
                dimension(R.dimen.space_8) * 2, Gravity.CENTER);
        flash.addView(impact, params);
        impact.animate().alpha(0f).translationY(-dimension(R.dimen.space_8))
                .setDuration(special ? 2200 : 1700)
                .withEndAction(() -> contentContainer.removeView(flash));
    }

    private void battleMeter(LinearLayout parent, String label, int value, int color) {
        battleMeter(parent, label, value, 100, color);
    }

    private void battleMeter(LinearLayout parent, String label, int value, int maximum, int color) {
        LinearLayout meter = new LinearLayout(this);
        meter.setOrientation(LinearLayout.VERTICAL);
        meter.setPadding(dimension(R.dimen.space_2), dimension(R.dimen.space_2),
                dimension(R.dimen.space_2), dimension(R.dimen.space_2));
        meter.setBackground(background(R.color.surface_elevated, R.color.border_subtle, 0));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, -2, 1f);
        params.setMargins(dimension(R.dimen.space_1), 0, dimension(R.dimen.space_1), 0);
        parent.addView(meter, params);
        meter.addView(text(label + "  " + value + "/" + maximum,
                R.style.TextAppearance_Ruptura_Caption,
                R.color.text_primary, true));
        addBattleBar(meter, Math.round(value * 100f / maximum), color);
    }

    private void addBattleBar(LinearLayout parent, int value, int color) {
        LinearLayout track = new LinearLayout(this);
        LinearLayout.LayoutParams trackParams = new LinearLayout.LayoutParams(0,
                dimension(R.dimen.space_1), 1f);
        if (parent.getOrientation() == LinearLayout.VERTICAL) trackParams =
                new LinearLayout.LayoutParams(-1, dimension(R.dimen.space_1));
        track.setBackgroundColor(getColor(R.color.surface_primary));
        parent.addView(track, trackParams);
        View fill = new View(this);
        fill.setBackgroundColor(getColor(color));
        track.addView(fill, new LinearLayout.LayoutParams(0, -1, value));
        track.addView(new View(this), new LinearLayout.LayoutParams(0, -1, Math.max(1, 100 - value)));
    }


    private TextView text(int stringRes, int styleRes, int colorRes, boolean bold) {
        return text(getString(stringRes), styleRes, colorRes, bold);
    }

    private TextView text(String value, int styleRes, int colorRes, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextAppearance(styleRes);
        view.setTextColor(getColor(colorRes));
        Typeface sora = getResources().getFont(R.font.sora_variable);
        view.setTypeface(sora, bold ? Typeface.BOLD : Typeface.NORMAL);
        view.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        return view;
    }

    private GradientDrawable background(int fill, int stroke, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(getColor(fill));
        drawable.setCornerRadius(radius == 0 ? dimension(R.dimen.space_3) : radius);
        if (fill != stroke) {
            drawable.setStroke(dimension(R.dimen.border_width), getColor(stroke));
        } else if (fill == R.color.accent_cyan) {
            drawable.setStroke(dimension(R.dimen.border_width), getColor(R.color.accent_cyan));
        }
        return drawable;
    }

    private int dimension(int resourceId) {
        return getResources().getDimensionPixelSize(resourceId);
    }

    @Override
    protected void onDestroy() {
        battleOpen = false;
        stopBattleMusic();
        if (battleTones != null) { battleTones.release(); battleTones = null; }
        super.onDestroy();
        if (forgeRepository != null) forgeRepository.close();
        if (portraitLoader != null) portraitLoader.close();
        forgeExecutor.shutdown();
        editorialExecutor.shutdown();
    }
}
