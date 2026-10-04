package com.erickbarbosa.rupturainfinita;

import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.content.res.AssetManager;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.OnBackPressedCallback;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        portraitLoader = new EditorialPortraitLoader(backendClient());
        for (String id : new String[]{"homem-aranha", "wolverine", "tocha-humana",
                "doutor-estranho", "senhor-fantastico"}) portraitLoader.prefetch(id);
        transientBack = new OnBackPressedCallback(false) {
            @Override public void handleOnBackPressed() { renderShell(); }
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
        transientScreen = false;
        if (transientBack != null) transientBack.setEnabled(false);
        navigationBar.setVisibility(View.VISIBLE);
        renderNavigation();
        renderDestination(selectedDestination);
    }

    private void renderIntro() {
        navigationBar.setVisibility(View.GONE);
        contentContainer.removeAllViews();
        LinearLayout intro = new LinearLayout(this);
        intro.setOrientation(LinearLayout.VERTICAL);
        intro.setGravity(Gravity.CENTER_HORIZONTAL);
        intro.setPadding(dimension(R.dimen.space_6), dimension(R.dimen.space_6),
                dimension(R.dimen.space_6), dimension(R.dimen.space_8));
        intro.setBackground(new IntroRiftDrawable(this));

        TextView skip = text(R.string.intro_skip, R.style.TextAppearance_Ruptura_Label,
                R.color.text_secondary, true);
        skip.setAllCaps(true);
        skip.setLetterSpacing(0.28f);
        skip.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        skip.setMinimumHeight(dimension(R.dimen.target_min));
        skip.setClickable(true);
        skip.setFocusable(true);
        skip.setOnClickListener(view -> enterNexus());
        intro.addView(skip, new LinearLayout.LayoutParams(-1, -2));

        LinearLayout titleBlock = new LinearLayout(this);
        titleBlock.setOrientation(LinearLayout.VERTICAL);
        titleBlock.setGravity(Gravity.CENTER);
        intro.addView(titleBlock, new LinearLayout.LayoutParams(-1, 0, 1f));
        TextView phase = text(R.string.intro_phase, R.style.TextAppearance_Ruptura_Label,
                R.color.text_secondary, true);
        phase.setAllCaps(true);
        phase.setLetterSpacing(0.22f);
        phase.setGravity(Gravity.CENTER);
        titleBlock.addView(phase);
        for (int label : new int[]{R.string.intro_marvel, R.string.intro_ruptura, R.string.intro_infinita}) {
            TextView line = text(label, R.style.TextAppearance_Ruptura_Display,
                    label == R.string.intro_ruptura ? R.color.accent_cyan : R.color.text_primary, true);
            line.setTextSize(46f);
            line.setAllCaps(true);
            line.setGravity(Gravity.CENTER);
            titleBlock.addView(line);
        }
        TextView tagline = text(R.string.intro_tagline, R.style.TextAppearance_Ruptura_Body,
                R.color.text_secondary, false);
        tagline.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams taglineParams = new LinearLayout.LayoutParams(-1, -2);
        taglineParams.topMargin = dimension(R.dimen.space_4);
        titleBlock.addView(tagline, taglineParams);

        LinearLayout gems = new LinearLayout(this);
        gems.setGravity(Gravity.CENTER);
        for (int color : new int[]{R.color.stone_space, R.color.stone_mind,
                R.color.stone_reality, R.color.stone_power, R.color.stone_time, R.color.stone_soul}) {
            View gem = new View(this);
            gem.setBackgroundColor(getColor(color));
            gem.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
            LinearLayout.LayoutParams gemParams = new LinearLayout.LayoutParams(0,
                    dimension(R.dimen.space_2), 1f);
            gemParams.setMargins(dimension(R.dimen.space_1), 0,
                    dimension(R.dimen.space_1), 0);
            gems.addView(gem, gemParams);
        }
        intro.addView(gems, new LinearLayout.LayoutParams(-1, -2));
        TextView enter = action(getString(R.string.intro_enter), this::enterNexus);
        LinearLayout.LayoutParams enterParams = new LinearLayout.LayoutParams(-1, -2);
        enterParams.topMargin = dimension(R.dimen.space_4);
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
        renderShell();
    }

    private void renderNavigation() {
        navigationBar.removeAllViews();
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
            item.setBackgroundColor(getColor(R.color.canvas));

            ImageView icon = new ImageView(this);
            icon.setImageResource(destination.iconRes);
            icon.setColorFilter(getColor(selected ? selectedColor : R.color.text_secondary));
            icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            icon.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);

            TextView label = new TextView(this);
            label.setText(destination.labelRes);
            label.setTextAppearance(R.style.TextAppearance_Ruptura_Caption);
            label.setTextColor(getColor(selected ? selectedColor : R.color.text_secondary));
            label.setTypeface(getResources().getFont(R.font.barlow_condensed_bold));
            label.setGravity(Gravity.CENTER);
            label.setMaxLines(2);
            label.setAllCaps(true);
            label.setLetterSpacing(0.14f);
            label.setIncludeFontPadding(false);

            item.addView(icon, new LinearLayout.LayoutParams(-1, dimension(R.dimen.nav_icon_height)));
            item.addView(label, new LinearLayout.LayoutParams(-1, -2));
            View indicator = new View(this);
            indicator.setBackgroundColor(selected ? getColor(selectedColor) : getColor(R.color.canvas));
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
            renderNexusGauntletPreview(nexusContent);
            renderNexusResources(nexusContent);
            renderNexusShortcuts(nexusContent);
            renderDailyChallenge(nexusContent);
        }
        if (destination == AppDestination.FORGE) {
            renderForgeInventory(page);
            renderGauntlet(page);
        }
        if (destination == AppDestination.COLLECTION) {
            renderGameCatalog(page);
            renderEditorialSearch(page);
        }
        if (destination == AppDestination.DEADPOOL) {
            renderDeadpool(page);
        }

        scroll.addView(page);
        contentContainer.addView(scroll, new FrameLayout.LayoutParams(-1, -1));
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
                R.color.text_secondary, true);
        kicker.setAllCaps(true);
        kicker.setLetterSpacing(0.22f);
        labels.addView(kicker);
        int titleRes = destination == AppDestination.FORGE ? R.string.forge_page_title
                : destination == AppDestination.CAMPAIGNS ? R.string.campaigns_page_title
                : R.string.nav_collection;
        TextView title = text(titleRes, R.style.TextAppearance_Ruptura_Display,
                R.color.text_primary, true);
        title.setAllCaps(true);
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
        hero.setPadding(dimension(R.dimen.space_4), dimension(R.dimen.space_4),
                dimension(R.dimen.space_4), dimension(R.dimen.space_4));
        hero.setBackground(new NexusPortalDrawable(getResources().getDisplayMetrics().density));
        TextView kicker = text(R.string.nexus_kicker, R.style.TextAppearance_Ruptura_Label,
                R.color.text_secondary, true);
        kicker.setAllCaps(true);
        kicker.setLetterSpacing(0.22f);
        hero.addView(kicker);
        TextView title = text(R.string.nexus_page_title, R.style.TextAppearance_Ruptura_Display,
                R.color.text_primary, true);
        title.setAllCaps(true);
        hero.addView(title);
        View spacer = new View(this);
        hero.addView(spacer, new LinearLayout.LayoutParams(1, 0, 1f));
        LinearLayout people = new LinearLayout(this);
        people.setGravity(Gravity.BOTTOM);
        addNexusCompanion(people, "senhor-fantastico", "Reed Richards",
                R.string.nexus_reed);
        addNexusCompanion(people, "doutor-estranho", "Doutor Estranho",
                R.string.nexus_strange);
        hero.addView(people);
        page.addView(hero, new LinearLayout.LayoutParams(-1, dimension(R.dimen.nexus_hero_height)));
    }

    private void addNexusCompanion(LinearLayout people, String gameId, String name,
                                   int labelRes) {
        LinearLayout companion = new LinearLayout(this);
        companion.setOrientation(LinearLayout.VERTICAL);
        companion.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams companionParams = new LinearLayout.LayoutParams(0, -2, 1f);
        companionParams.leftMargin = dimension(R.dimen.space_1);
        companionParams.rightMargin = dimension(R.dimen.space_1);
        people.addView(companion, companionParams);
        ImageView portrait = new ImageView(this);
        portrait.setScaleType(ImageView.ScaleType.FIT_CENTER);
        portrait.setBackground(background(R.color.surface_primary, R.color.border_subtle,
                dimension(R.dimen.radius_card)));
        companion.addView(portrait, new LinearLayout.LayoutParams(-1,
                dimension(R.dimen.space_8) * 3));
        TextView label = text(labelRes, R.style.TextAppearance_Ruptura_Caption,
                R.color.text_primary, true);
        label.setGravity(Gravity.CENTER);
        companion.addView(label);
        TextView credit = text(R.string.editorial_portrait_loading,
                R.style.TextAppearance_Ruptura_Caption, R.color.text_secondary, false);
        credit.setGravity(Gravity.CENTER);
        credit.setMinHeight(dimension(R.dimen.target_min));
        companion.addView(credit);
        portraitLoader.load(gameId, name, portrait, credit);
    }

    private void renderNexusResources(LinearLayout page) {
        LinearLayout panel = card();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.topMargin = dimension(R.dimen.space_3);
        page.addView(panel, params);
        panel.addView(text("RECURSOS DA EQUIPE", R.style.TextAppearance_Ruptura_Label,
                R.color.accent_gold, true));
        TextView balances = text("Carregando créditos e XP…",
                R.style.TextAppearance_Ruptura_Body, R.color.text_primary, false);
        balances.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);
        panel.addView(balances);
        forgeExecutor.execute(() -> {
            PlayerResources resources = forgeRepository.loadPlayerResources();
            runOnUiThread(() -> {
                if (balances.getParent() == panel) balances.setText(getString(
                        R.string.player_resources, formatAmount(resources.credits),
                        formatAmount(resources.xp)));
            });
        });
    }

    private String formatAmount(long amount) {
        return String.format(java.util.Locale.forLanguageTag("pt-BR"), "%,d", amount);
    }

    private void renderNexusGauntletPreview(LinearLayout page) {
        LinearLayout preview = card();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.topMargin = dimension(R.dimen.space_4);
        page.addView(preview, params);
        TextView label = text(R.string.gauntlet_heading, R.style.TextAppearance_Ruptura_Label,
                R.color.accent_gold, true);
        label.setAllCaps(true);
        label.setLetterSpacing(.16f);
        preview.addView(label);
        TextView count = text(R.string.gauntlet_loading, R.style.TextAppearance_Ruptura_Title,
                R.color.text_primary, true);
        preview.addView(count);
        GauntletConstellationView constellation = new GauntletConstellationView(this);
        LinearLayout.LayoutParams visualParams = new LinearLayout.LayoutParams(-1,
                dimension(R.dimen.space_8) * 5);
        visualParams.topMargin = dimension(R.dimen.space_3);
        preview.addView(constellation, visualParams);
        preview.setClickable(true);
        preview.setFocusable(true);
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
                count.setText(getString(R.string.nexus_gauntlet_count, total));
                constellation.setCompleted(state);
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

    private void renderNexusShortcuts(LinearLayout page) {
        TextView heading = text(R.string.nexus_shortcuts_heading,
                R.style.TextAppearance_Ruptura_Title, R.color.text_primary, true);
        LinearLayout.LayoutParams headingParams = new LinearLayout.LayoutParams(-1, -2);
        headingParams.topMargin = dimension(R.dimen.space_8);
        page.addView(heading, headingParams);

        for (AppDestination destination : AppDestination.nexusShortcuts()) {
            LinearLayout shortcut = new LinearLayout(this);
            shortcut.setOrientation(LinearLayout.VERTICAL);
            shortcut.setPadding(dimension(R.dimen.space_4), dimension(R.dimen.space_4),
                    dimension(R.dimen.space_4), dimension(R.dimen.space_4));
            shortcut.setMinimumHeight(dimension(R.dimen.target_min));
            shortcut.setFocusable(true);
            shortcut.setClickable(true);
            shortcut.setBackground(background(R.color.surface_elevated, R.color.border_subtle,
                    dimension(R.dimen.radius_card)));
            shortcut.setContentDescription(getString(R.string.nexus_shortcut_action,
                    getString(destination.labelRes)));
            shortcut.setOnClickListener(view -> selectDestination(destination));

            LinearLayout.LayoutParams shortcutParams = new LinearLayout.LayoutParams(-1, -2);
            shortcutParams.topMargin = dimension(R.dimen.space_4);
            page.addView(shortcut, shortcutParams);

            shortcut.addView(text(destination.labelRes,
                    R.style.TextAppearance_Ruptura_Title, R.color.text_primary, true));
            TextView description = text(destination.descriptionRes,
                    R.style.TextAppearance_Ruptura_Body, R.color.text_secondary, false);
            LinearLayout.LayoutParams descriptionParams = new LinearLayout.LayoutParams(-1, -2);
            descriptionParams.topMargin = dimension(R.dimen.space_2);
            shortcut.addView(description, descriptionParams);
        }
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
        LinearLayout mergePanel = new LinearLayout(this);
        mergePanel.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams mergeParams = new LinearLayout.LayoutParams(-1, -2);
        mergeParams.topMargin = dimension(R.dimen.space_4);
        inventoryPanel.addView(mergePanel, mergeParams);
        TextView toggle = text(R.string.forge_open_details,
                R.style.TextAppearance_Ruptura_Label, R.color.accent_cyan, true);
        toggle.setMinimumHeight(dimension(R.dimen.target_min));
        toggle.setGravity(Gravity.CENTER_VERTICAL);
        toggle.setClickable(true);
        toggle.setFocusable(true);
        inventoryPanel.addView(toggle);
        LinearLayout details = new LinearLayout(this);
        details.setOrientation(LinearLayout.VERTICAL);
        details.setVisibility(View.GONE);
        page.addView(details, new LinearLayout.LayoutParams(-1, -2));
        toggle.setOnClickListener(view -> {
            boolean expand = details.getVisibility() != View.VISIBLE;
            details.setVisibility(expand ? View.VISIBLE : View.GONE);
            toggle.setText(expand ? R.string.forge_close_details : R.string.forge_open_details);
        });

        TextView heading = text(R.string.forge_stones_heading,
                R.style.TextAppearance_Ruptura_Title, R.color.text_primary, true);
        LinearLayout.LayoutParams headingParams = new LinearLayout.LayoutParams(-1, -2);
        headingParams.topMargin = dimension(R.dimen.space_8);
        details.addView(heading, headingParams);

        LinearLayout stages = new LinearLayout(this);
        stages.setOrientation(LinearLayout.VERTICAL);
        stages.setPadding(dimension(R.dimen.space_4), dimension(R.dimen.space_4),
                dimension(R.dimen.space_4), dimension(R.dimen.space_4));
        stages.setBackground(background(R.color.surface_elevated, R.color.border_subtle,
                dimension(R.dimen.radius_card)));
        LinearLayout.LayoutParams stagesParams = new LinearLayout.LayoutParams(-1, -2);
        stagesParams.topMargin = dimension(R.dimen.space_4);
        details.addView(stages, stagesParams);
        stages.addView(text(R.string.forge_stage_label,
                R.style.TextAppearance_Ruptura_Label, R.color.accent_gold, true));
        TextView sequence = text(R.string.forge_stage_sequence,
                R.style.TextAppearance_Ruptura_Body, R.color.text_primary, false);
        LinearLayout.LayoutParams sequenceParams = new LinearLayout.LayoutParams(-1, -2);
        sequenceParams.topMargin = dimension(R.dimen.space_2);
        stages.addView(sequence, sequenceParams);

        for (InfinityStone stone : InfinityStone.values()) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dimension(R.dimen.space_4), dimension(R.dimen.space_4),
                    dimension(R.dimen.space_4), dimension(R.dimen.space_4));
            card.setBackground(background(R.color.surface_elevated, R.color.border_subtle,
                    dimension(R.dimen.radius_card)));
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(-1, -2);
            cardParams.topMargin = dimension(R.dimen.space_4);
            details.addView(card, cardParams);

            card.addView(text(stone.labelRes, R.style.TextAppearance_Ruptura_Title,
                    R.color.text_primary, true));
            card.setTag(stone);
            renderForgeStoneCounts(card, stone, null);
        }
        refreshForgeInventory(empty, details, inventoryGrid, mergePanel);
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

    private void refreshForgeInventory(TextView empty, LinearLayout details,
                                       LinearLayout inventoryGrid, LinearLayout mergePanel) {
        forgeExecutor.execute(() -> {
            try {
                ForgeInventory inventory = forgeRepository.load();
                runOnUiThread(() -> {
                    boolean hasItems = false;
                    inventoryGrid.removeAllViews();
                    mergePanel.removeAllViews();
                    mergePanel.addView(text("FUSÕES DISPONÍVEIS · 2:1",
                            R.style.TextAppearance_Ruptura_Label, R.color.accent_gold, true));
                    int mergeCount = 0;
                    LinearLayout gridRow = null;
                    int shown = 0;
                    int[] colors = {R.color.stone_space, R.color.stone_mind,
                            R.color.stone_reality, R.color.stone_power,
                            R.color.stone_time, R.color.stone_soul};
                    for (int i = 0; i < details.getChildCount(); i++) {
                        View child = details.getChildAt(i);
                        Object tag = child.getTag();
                        if (tag instanceof InfinityStone) {
                            InfinityStone stone = (InfinityStone) tag;
                            renderForgeStoneCounts((LinearLayout) child, stone, inventory);
                            for (ForgeStage stage : ForgeStage.values()) {
                                int count = inventory.count(stone, stage);
                                if (stage != ForgeStage.COMPLETE
                                        && count >= ForgePolicy.INPUT_COUNT) {
                                    ForgeStage output = stage.next();
                                    TextView merge = action(getString(stone.labelRes) + " · "
                                            + ForgePolicy.INPUT_COUNT + " "
                                            + getString(stageLabel(stage)) + " → 1 "
                                            + getString(stageSingleLabel(output)),
                                            () -> confirmMerge(stone, stage, output));
                                    LinearLayout.LayoutParams mergeActionParams =
                                            new LinearLayout.LayoutParams(-1, -2);
                                    mergeActionParams.topMargin = dimension(R.dimen.space_3);
                                    mergePanel.addView(merge, mergeActionParams);
                                    mergeCount++;
                                }
                                if (count <= 0) continue;
                                hasItems = true;
                                if (shown % 4 == 0) {
                                    gridRow = new LinearLayout(this);
                                    LinearLayout.LayoutParams rowParams =
                                            new LinearLayout.LayoutParams(-1, -2);
                                    rowParams.topMargin = dimension(R.dimen.space_2);
                                    inventoryGrid.addView(gridRow, rowParams);
                                }
                                LinearLayout item = new LinearLayout(this);
                                item.setOrientation(LinearLayout.VERTICAL);
                                item.setGravity(Gravity.CENTER);
                                item.setMinimumHeight(dimension(R.dimen.target_min));
                                item.setBackground(background(R.color.surface_primary,
                                        R.color.border_subtle, 0));
                                TextView icon = text("⬡", R.style.TextAppearance_Ruptura_Title,
                                        colors[stone.ordinal()], true);
                                icon.setGravity(Gravity.CENTER);
                                item.addView(icon);
                                TextView title = text(getString(stageSingleLabel(stage)),
                                        R.style.TextAppearance_Ruptura_Caption,
                                        R.color.text_secondary, false);
                                title.setGravity(Gravity.CENTER);
                                item.addView(title);
                                TextView amount = text("×" + count,
                                        R.style.TextAppearance_Ruptura_Caption,
                                        R.color.text_primary, true);
                                amount.setGravity(Gravity.CENTER);
                                item.addView(amount);
                                item.setContentDescription(getString(stone.labelRes) + ", "
                                        + getString(stageSingleLabel(stage)) + ": " + count);
                                LinearLayout.LayoutParams itemParams =
                                        new LinearLayout.LayoutParams(0, -2, 1f);
                                itemParams.setMargins(dimension(R.dimen.space_1), 0,
                                        dimension(R.dimen.space_1), 0);
                                gridRow.addView(item, itemParams);
                                shown++;
                            }
                        }
                    }
                    if (mergeCount == 0) {
                        TextView needPair = text("Junte 2 peças da mesma Joia e do mesmo estágio para fundir.",
                                R.style.TextAppearance_Ruptura_Body,
                                R.color.text_secondary, false);
                        LinearLayout.LayoutParams needPairParams =
                                new LinearLayout.LayoutParams(-1, -2);
                        needPairParams.topMargin = dimension(R.dimen.space_2);
                        mergePanel.addView(needPair, needPairParams);
                    }
                    empty.setVisibility(hasItems ? View.GONE : View.VISIBLE);
                });
            } catch (RuntimeException exception) {
                runOnUiThread(() -> empty.setText(R.string.forge_inventory_load_error));
            }
        });
    }

    private void renderForgeStoneCounts(LinearLayout card, InfinityStone stone, ForgeInventory inventory) {
        while (card.getChildCount() > 1) card.removeViewAt(card.getChildCount() - 1);
        for (ForgeStage stage : ForgeStage.values()) {
            int count = inventory == null ? 0 : inventory.count(stone, stage);
            TextView row = text(getString(R.string.forge_count_line, getString(stageLabel(stage)), count),
                    R.style.TextAppearance_Ruptura_Body, R.color.text_secondary, false);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(-1, -2);
            rowParams.topMargin = dimension(R.dimen.space_2);
            card.addView(row, rowParams);
            if (stage == ForgeStage.COMPLETE || count < ForgePolicy.INPUT_COUNT) continue;
            ForgeStage output = stage.next();
            TextView action = text(getString(R.string.forge_merge_action,
                            getString(stageLabel(stage))),
                    R.style.TextAppearance_Ruptura_Label, R.color.accent_cyan, true);
            action.setFocusable(true);
            action.setClickable(true);
            action.setContentDescription(getString(R.string.forge_merge_action,
                    getString(stageLabel(stage))));
            action.setPadding(0, dimension(R.dimen.space_2), 0, dimension(R.dimen.space_2));
            action.setOnClickListener(view -> confirmMerge(stone, stage, output));
            card.addView(action, new LinearLayout.LayoutParams(-1, -2));
        }
    }

    private int stageLabel(ForgeStage stage) {
        switch (stage) {
            case SHARD: return R.string.forge_stage_shard;
            case FRAGMENT: return R.string.forge_stage_fragment;
            case UNSTABLE_CORE: return R.string.forge_stage_core;
            default: return R.string.forge_stage_complete;
        }
    }

    private int stageSingleLabel(ForgeStage stage) {
        switch (stage) {
            case SHARD: return R.string.forge_stage_shard_single;
            case FRAGMENT: return R.string.forge_stage_fragment_single;
            case UNSTABLE_CORE: return R.string.forge_stage_core_single;
            default: return R.string.forge_stage_complete_single;
        }
    }

    private void confirmMerge(InfinityStone stone, ForgeStage input, ForgeStage output) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.forge_merge_confirm_title)
                .setMessage(getString(R.string.forge_merge_confirm_message,
                        getString(stageLabel(input)), getString(stageSingleLabel(output))))
                .setNegativeButton(R.string.forge_action_cancel, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.forge_action_confirm, (dialog, which) -> {
                    String operationId = java.util.UUID.randomUUID().toString();
                    forgeExecutor.execute(() -> {
                        try {
                            forgeRepository.merge(operationId, stone, input);
                            runOnUiThread(() -> {
                                if (selectedDestination == AppDestination.FORGE) renderShell();
                                android.widget.Toast.makeText(this, R.string.forge_merge_success,
                                        android.widget.Toast.LENGTH_SHORT).show();
                            });
                        } catch (ForgeException exception) {
                            runOnUiThread(() -> android.widget.Toast.makeText(this,
                                    exception.reason == ForgeException.Reason.INSUFFICIENT_ITEMS
                                            ? R.string.forge_insufficient_items
                                            : R.string.forge_inventory_full_error,
                                    android.widget.Toast.LENGTH_LONG).show());
                        }
                    });
                }).show();
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
        Runnable update = () -> {
            gallery.removeAllViews();
            String query = search.getText().toString().trim().toLowerCase(java.util.Locale.ROOT);
            int shown = 0;
            LinearLayout pair = null;
            for (GameCatalogCharacter character : characters) {
                    if (selectedGroup[0] != null
                            && !selectedGroup[0].equals(character.groupId)) continue;
                    java.util.Set<GameVariantTier> owned = ownedByCharacter.get(character.id);
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
        filterFilters(filters, selectedGroup, update);
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
        portraitLoader.load(character.id, character.name, portrait, source);
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
        portraitLoader.load(character.id, character.name, cover, source);

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
        LinearLayout stats = card();
        LinearLayout.LayoutParams statsParams = new LinearLayout.LayoutParams(-1, -2);
        statsParams.topMargin = dimension(R.dimen.space_4);
        page.addView(stats, statsParams);
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
        renderEditorialFacts(page, character.id);
        for (GameCatalogVariant variant : character.variants) {
            boolean isOwned = owned.contains(variant.tier);
            LinearLayout variantCard = card();
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(-1, -2);
            cardParams.topMargin = dimension(R.dimen.space_3);
            page.addView(variantCard, cardParams);
            LinearLayout row = new LinearLayout(this);
            row.setGravity(Gravity.CENTER_VERTICAL);
            variantCard.addView(row);
            variantCard.setClickable(true);
            variantCard.setFocusable(true);
            variantCard.setOnClickListener(view -> showCharacterVariants(character, roster, variant.tier));
            ImageView thumbnail = new ImageView(this);
            thumbnail.setScaleType(ImageView.ScaleType.FIT_CENTER);
            thumbnail.setBackgroundColor(getColor(R.color.surface_primary));
            row.addView(thumbnail, new LinearLayout.LayoutParams(
                    dimension(R.dimen.target_min), dimension(R.dimen.target_min)));
            portraitLoader.load(character.id, character.name, thumbnail, null);
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
                variantCard.addView(action(getString(R.string.variant_unlock_action,
                        getString(variant.tier.requiredStone.labelRes)), () -> forgeExecutor.execute(() -> {
                    try {
                        forgeRepository.unlockNextVariant(character.id, variant.tier, roster);
                        runOnUiThread(() -> showCharacterVariants(character, roster, variant.tier));
                    } catch (RuntimeException error) {
                        runOnUiThread(() -> android.widget.Toast.makeText(this,
                                error instanceof ForgeException ? R.string.variant_stone_required
                                        : R.string.variant_gauntlet_required,
                                android.widget.Toast.LENGTH_LONG).show());
                    }
                })));
            }
            if (active && isOwned && !variant.tier.name().equals(equippedTier)) {
                variantCard.addView(action(getString(R.string.variant_equip_action),
                        () -> forgeExecutor.execute(() -> {
                    forgeRepository.equipVariant(character.id, variant.tier, roster);
                    runOnUiThread(() -> showCharacterVariants(character, roster, variant.tier));
                })));
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
        facts.addView(text("ARQUIVO EDITORIAL · COMIC VINE",
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
                String siteUrl = metadata.getString("site_url");
                String finalSummary = summary;
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    while (facts.getChildCount() > 1) facts.removeViewAt(facts.getChildCount() - 1);
                    if (!realName.isEmpty()) addEditorialLine(facts, "IDENTIDADE", realName);
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

    private void filterFilters(LinearLayout filters, String[] selectedGroup, Runnable update) {
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
                    filterFilters(filters, selectedGroup, update);
                    update.run();
                });
            }
        }
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
        String[] contextId = {"nexus"};
        String[] cardPrompt = {""};
        Runnable[] sendRequest = {null};
        TextView bubble = text(R.string.deadpool_bubble, R.style.TextAppearance_Ruptura_Body,
                R.color.deadpool_ink, false);
        bubble.setPadding(dimension(R.dimen.space_3), dimension(R.dimen.space_3),
                dimension(R.dimen.space_3), dimension(R.dimen.space_3));
        bubble.setBackground(background(R.color.deadpool_white, R.color.deadpool_ink, 0));
        bubble.setElevation(dimension(R.dimen.space_1));
        LinearLayout.LayoutParams bubbleParams = new LinearLayout.LayoutParams(-1, -2);
        bubbleParams.bottomMargin = dimension(R.dimen.space_4);
        page.addView(bubble, bubbleParams);

        LinearLayout firstRow = new LinearLayout(this);
        firstRow.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout secondRow = new LinearLayout(this);
        secondRow.setOrientation(LinearLayout.HORIZONTAL);
        addComicCard(firstRow, R.string.deadpool_briefing_label, R.string.deadpool_briefing_detail,
                true, () -> {
                    contextId[0] = "xmen";
                    cardPrompt[0] = "Faça um briefing curto para enfrentar Magneto.";
                    if (sendRequest[0] != null) sendRequest[0].run();
                });
        addComicCard(firstRow, R.string.deadpool_team_label, R.string.deadpool_team_detail,
                true, () -> {
                    contextId[0] = "xmen";
                    CampaignState saved = forgeRepository.loadCampaign("xmen", java.util.Arrays.asList(
                            "homem-aranha", "wolverine", "tocha-humana"));
                    List<GameCatalogCharacter> roster = loadRoster();
                    StringBuilder names = new StringBuilder();
                    for (String id : saved.teamIds) {
                        GameCatalogCharacter character = findCharacter(roster, id);
                        if (character != null) {
                            if (names.length() > 0) names.append(", ");
                            names.append(character.name);
                        }
                    }
                    cardPrompt[0] = "Comente meu trio salvo para Magneto: " + names + ".";
                    if (sendRequest[0] != null) sendRequest[0].run();
                });
        addComicCard(secondRow, R.string.deadpool_daily_label, R.string.deadpool_daily_detail,
                true, this::showDailyChallengeScreen);
        addComicCard(secondRow, R.string.deadpool_strange_label, R.string.deadpool_strange_detail,
                true, () -> {
                    contextId[0] = "forge";
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
        prompt.setBackground(background(R.color.deadpool_white, R.color.deadpool_ink, 0));
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
            requestDeadpoolLine(contextId[0], message, answer, ask);
        };
        ask.setOnClickListener(view -> sendRequest[0].run());
        LinearLayout.LayoutParams askParams = new LinearLayout.LayoutParams(-1, -2);
        askParams.topMargin = dimension(R.dimen.space_3);
        page.addView(ask, askParams);
    }

    private void requestDeadpoolLine(String contextId, String prompt, TextView answer,
                                     TextView ask) {
        if (!ask.isEnabled()) return;
        ask.setEnabled(false);
        answer.setText(R.string.deadpool_loading);
        editorialExecutor.execute(() -> {
            try {
                org.json.JSONObject request = new org.json.JSONObject();
                request.put("context_id", contextId);
                request.put("prompt", prompt);
                org.json.JSONObject response = backendClient().post("/v1/ai/deadpool-line", request);
                String line = response.getString("text");
                boolean fallback = response.optBoolean("fallback", true);
                runOnUiThread(() -> {
                    answer.setText(fallback ? line + "\n\n" + getString(R.string.deadpool_offline)
                            : line);
                    ask.setEnabled(true);
                });
            } catch (Exception error) {
                runOnUiThread(() -> {
                    answer.setText(R.string.deadpool_error);
                    ask.setEnabled(true);
                });
            }
        });
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
                        + "Diferente da manopla clássica, esta não foi feita para usar as Joias, "
                        + "e sim para impedir que elas voltem a se procurar.",
                R.style.TextAppearance_Ruptura_Body, R.color.text_secondary, false);
        LinearLayout.LayoutParams loreParams = new LinearLayout.LayoutParams(-1, -2);
        loreParams.topMargin = dimension(R.dimen.space_3);
        resonance.addView(lore, loreParams);

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
        TextView action = action(active ? "ABRIR CÂMARA DE VARIANTES"
                        : count == stones.length ? "ATIVAR MANOPLA DE CONTENÇÃO"
                        : "REÚNA AS SEIS JOIAS COMPLETAS", () -> {
            if (active) {
                selectedDestination = AppDestination.COLLECTION;
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
        action.setEnabled(active || count == stones.length);
        LinearLayout.LayoutParams actionParams = new LinearLayout.LayoutParams(-1, -2);
        actionParams.topMargin = dimension(R.dimen.space_3);
        state.addView(action, actionParams);
    }

    private int completeCount(ForgeInventory inventory) {
        int count = 0;
        for (InfinityStone stone : InfinityStone.values())
            if (inventory.count(stone, ForgeStage.COMPLETE) > 0) count++;
        return count;
    }

    private void selectDestination(AppDestination destination) {
        if (selectedDestination != destination) {
            selectedDestination = destination;
            renderShell();
        }
    }

    private List<GameCatalogCharacter> loadRoster() {
        try { return GameCatalogParser.read(getAssets()); }
        catch (IOException | JSONException exception) { throw new IllegalStateException(exception); }
    }

    private LinearLayout card() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dimension(R.dimen.space_4), dimension(R.dimen.space_4),
                dimension(R.dimen.space_4), dimension(R.dimen.space_4));
        card.setBackground(new AngularPanelDrawable(getColor(R.color.surface_elevated),
                getColor(R.color.surface_primary), getColor(R.color.border_subtle),
                dimension(R.dimen.angular_cut), false));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.topMargin = dimension(R.dimen.space_4);
        return card;
    }

    private ImageView editorialImage(LinearLayout parent, int height) {
        ImageView image = new ImageView(this);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        image.setBackground(new AngularPanelDrawable(getColor(R.color.surface_primary),
                getColor(R.color.surface_elevated), getColor(R.color.border_subtle),
                dimension(R.dimen.angular_cut), false));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, height);
        params.bottomMargin = dimension(R.dimen.space_2);
        parent.addView(image, params);
        return image;
    }

    private TextView action(String label, Runnable task) {
        TextView button = text(label, R.style.TextAppearance_Ruptura_Label, R.color.canvas, true);
        button.setFocusable(true);
        button.setClickable(true);
        button.setMinimumHeight(dimension(R.dimen.space_8) * 2);
        button.setPadding(dimension(R.dimen.space_4), dimension(R.dimen.space_3),
                dimension(R.dimen.space_4), dimension(R.dimen.space_3));
        button.setGravity(Gravity.CENTER);
        button.setAllCaps(true);
        button.setLetterSpacing(0.16f);
        button.setOnClickListener(view -> task.run());
        button.setBackground(new AngularPanelDrawable(getColor(R.color.accent_cyan),
                getColor(R.color.accent_quartet), getColor(R.color.accent_cyan),
                dimension(R.dimen.angular_cut), false));
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
        challengeCard.addView(text("Descubra o herói em até seis tentativas e ganhe 1 Fragmento da Joia do dia.",
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
        mystery.setBackground(new AngularPanelDrawable(getColor(R.color.surface_primary),
                getColor(R.color.surface_elevated), getColor(R.color.border_subtle),
                dimension(R.dimen.angular_cut), false));
        panel.addView(mystery, new LinearLayout.LayoutParams(-1, -2));
        if (!"ACTIVE".equals(state.status)) {
            GameCatalogCharacter revealed = findCharacter(roster, state.targetId);
            ImageView portrait = editorialImage(mystery, dimension(R.dimen.space_8) * 4);
            portraitLoader.load(revealed.id, revealed.name, portrait, null);
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
        page.addView(text("Cada capítulo é uma batalha. Escolha três personagens da coleção antes de lutar.",
                R.style.TextAppearance_Ruptura_Body, R.color.text_secondary, false));
        int chapter = 1;
        for (BattleMission mission : BattleMission.ALL) {
            addBattleMissionCard(page, roster, mission, chapter++);
        }
    }

    private void addBattleMissionCard(LinearLayout page, List<GameCatalogCharacter> roster,
                                      BattleMission mission, int chapter) {
        LinearLayout missionCard = card();
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(-1, -2);
        cardParams.topMargin = dimension(R.dimen.space_4);
        page.addView(missionCard, cardParams);
        missionCard.addView(text(String.format(java.util.Locale.ROOT, "CAPÍTULO %02d", chapter),
                R.style.TextAppearance_Ruptura_Caption, R.color.accent_cyan, true));
        missionCard.addView(text(mission.title, R.style.TextAppearance_Ruptura_Title,
                R.color.text_primary, true));
        missionCard.addView(text(mission.location + "  ·  " + mission.opponentName,
                R.style.TextAppearance_Ruptura_Caption, R.color.text_secondary, false));
        ImageView enemy = editorialImage(missionCard, dimension(R.dimen.space_8) * 4);
        TextView source = text(R.string.editorial_portrait_loading,
                R.style.TextAppearance_Ruptura_Caption, R.color.text_secondary, false);
        missionCard.addView(source);
        portraitLoader.loadOpponent(mission.opponentId, mission.opponentName, enemy, source);
        CampaignReward reward = CampaignReward.forMission(mission.campaignId, mission.number);
        missionCard.addView(text("PRIMEIRA VITÓRIA  ·  4 " + fragmentName(reward.stone)
                        + "  ·  " + reward.credits + " CR  ·  " + reward.xp + " XP",
                R.style.TextAppearance_Ruptura_Caption, R.color.accent_gold, true));
        TextView status = text("Consultando progresso…", R.style.TextAppearance_Ruptura_Caption,
                R.color.text_secondary, false);
        missionCard.addView(status);
        forgeExecutor.execute(() -> {
            CampaignState state = forgeRepository.loadCampaign(mission.campaignId,
                    java.util.Arrays.asList("homem-aranha", "wolverine", "tocha-humana"));
            boolean completed = forgeRepository.hasCompletedMission(mission.campaignId, mission.number);
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) return;
                boolean available = mission.number <= state.unlockedMission;
                status.setText(completed ? "CONCLUÍDA · recompensa já recebida"
                        : available ? "DISPONÍVEL" : "BLOQUEADA · vença o capítulo anterior");
                TextView enter = action(completed ? "REJOGAR BATALHA" : "ESCOLHER EQUIPE E BATALHAR",
                        () -> showTeamChooser(roster, state, mission.number));
                enter.setEnabled(available);
                missionCard.addView(enter);
            });
        });
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
        portraitLoader.load(left.id, left.name, heroImages[0], heroSources[0]);
        portraitLoader.load(right.id, right.name, heroImages[1], heroSources[1]);
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

    private void showCampaignRewardScreen(String campaignId, int mission) {
        CampaignReward reward = CampaignReward.forMission(campaignId, mission);
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
        LinearLayout.LayoutParams firstRow = new LinearLayout.LayoutParams(-1, -2);
        firstRow.topMargin = dimension(R.dimen.space_6);
        page.addView(rewardRow("⬡", fragmentName(reward.stone),
                "x" + CampaignReward.FRAGMENTS, stoneColor(reward.stone)), firstRow);
        LinearLayout.LayoutParams nextRow = new LinearLayout.LayoutParams(-1, -2);
        nextRow.topMargin = dimension(R.dimen.space_3);
        page.addView(rewardRow("◇", "Créditos", "+" + formatAmount(reward.credits),
                R.color.accent_gold), nextRow);
        LinearLayout.LayoutParams xpRow = new LinearLayout.LayoutParams(-1, -2);
        xpRow.topMargin = dimension(R.dimen.space_3);
        page.addView(rewardRow("◇", "Experiência", "+" + formatAmount(reward.xp) + " XP",
                R.color.accent_cyan), xpRow);

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
        TextView slots = text("SELECIONADOS  " + selected.size() + "/3",
                R.style.TextAppearance_Ruptura_Label, R.color.accent_cyan, true);
        LinearLayout.LayoutParams slotsParams = new LinearLayout.LayoutParams(-1, -2);
        slotsParams.topMargin = dimension(R.dimen.space_4);
        page.addView(slots, slotsParams);
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
            portraitLoader.load(character.id, character.name, image, null);
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
                    int power = GameRules.equippedTeamPower(roster, saved.teamIds, forgeRepository);
                    runOnUiThread(() -> showMagnetoBattle(new LovableBattle(power,
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
        transientScreen = true;
        transientBack.setEnabled(true);
        battleAnimating = false;
        battleClaiming = false;
        renderMagnetoBattle(battle, campaign);
    }

    private void renderMagnetoBattle(LovableBattle battle, CampaignState campaign) {
        navigationBar.setVisibility(View.GONE);
        contentContainer.removeAllViews();
        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
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
        titles.addView(text("ROUND " + Math.min(battle.round + 1, 4) + "/4  ·  "
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
                battle.boss, R.color.accent_deadpool);
        battleMeter(health, "EQUIPE", battle.team, R.color.accent_latveria);

        ScrollView central = new ScrollView(this);
        central.setFillViewport(true);
        screen.addView(central, new LinearLayout.LayoutParams(-1, 0, 1f));
        LinearLayout scene = new LinearLayout(this);
        scene.setOrientation(LinearLayout.VERTICAL);
        central.addView(scene);
        LinearLayout boss = card();
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
        for (String id : campaign.teamIds) {
            GameCatalogCharacter selected = findCharacter(battleRoster, id);
            String name = selected == null ? id : selected.name;
            LinearLayout member = card();
            member.setPadding(dimension(R.dimen.space_1), dimension(R.dimen.space_1),
                    dimension(R.dimen.space_1), dimension(R.dimen.space_1));
            ImageView portrait = editorialImage(member, dimension(R.dimen.space_8) * 2);
            portraitLoader.load(id, name, portrait, null);
            TextView nameLabel = text(name, R.style.TextAppearance_Ruptura_Caption,
                    R.color.accent_cyan, true);
            nameLabel.setGravity(Gravity.CENTER);
            member.addView(nameLabel);
            member.addView(text(getString(tierById(forgeRepository.loadEquippedTier(id)).labelRes),
                    R.style.TextAppearance_Ruptura_Caption, R.color.text_secondary, false));
            LinearLayout.LayoutParams memberParams = new LinearLayout.LayoutParams(0, -2, 1f);
            memberParams.setMargins(dimension(R.dimen.space_1), dimension(R.dimen.space_2),
                    dimension(R.dimen.space_1), 0);
            team.addView(member, memberParams);
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
                R.color.text_secondary, false);
        feedback.setGravity(Gravity.CENTER);
        feedback.setMinHeight(dimension(R.dimen.space_8) * 2);
        feedback.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);
        scene.addView(feedback, new LinearLayout.LayoutParams(-1, -2));

        if (battle.defeat) {
            screen.addView(action("TENTAR NOVAMENTE", () -> showMagnetoBattle(
                    new LovableBattle(GameRules.equippedTeamPower(loadRoster(),
                            campaign.teamIds, forgeRepository), battle.mission),
                    campaign)), new LinearLayout.LayoutParams(-1, -2));
        } else if (!battle.victory) {
            LinearLayout charge = new LinearLayout(this);
            charge.setGravity(Gravity.CENTER_VERTICAL);
            screen.addView(charge, new LinearLayout.LayoutParams(-1, -2));
            charge.addView(text("ϟ", R.style.TextAppearance_Ruptura_Title,
                    R.color.stone_mind, true));
            addBattleBar(charge, battle.charge, R.color.stone_mind);
            charge.addView(text(battle.charge + "%", R.style.TextAppearance_Ruptura_Caption,
                    R.color.stone_mind, true));
            if (battle.canSpecial()) {
                TextView special = action("ESPECIAL · LANÇA PSÍQUICA", () -> {
                    if (battleAnimating || !battle.canSpecial()) return;
                    battleAnimating = true;
                    battle.special();
                    renderMagnetoBattle(battle, campaign);
                    showBattleImpact("KRAKOOM", battle.lastBossDamage, 0, true);
                    contentContainer.postDelayed(() -> { battleAnimating = false;
                        renderMagnetoBattle(battle, campaign); }, animationDelay(1200));
                });
                special.setEnabled(!battleAnimating);
                screen.addView(special, new LinearLayout.LayoutParams(-1, -2));
            } else {
                LinearLayout choices = new LinearLayout(this);
                screen.addView(choices, new LinearLayout.LayoutParams(-1, -2));
                battleChoice(choices, "⚔\nINVESTIR\nDANO ALTO", LovableBattle.Choice.ATTACK, battle, campaign);
                battleChoice(choices, "⬡\nPROTEGER\nREDUZ IMPACTO", LovableBattle.Choice.DEFEND, battle, campaign);
                battleChoice(choices, "✦\nDESESTABILIZAR\n+ CARGA", LovableBattle.Choice.CONTROL, battle, campaign);
            }
        } else {
            TextView reward = action("COLETAR RECOMPENSA", () -> {
                if (battleClaiming || !battle.victory) return;
                battleClaiming = true;
                forgeExecutor.execute(() -> {
                    try {
                        boolean granted = forgeRepository.completeMission(campaign.campaignId,
                                battle.mission.number);
                        runOnUiThread(() -> {
                            if (granted) showCampaignRewardScreen(campaign.campaignId,
                                    battle.mission.number);
                            else {
                                renderShell();
                                android.widget.Toast.makeText(this,
                                        "Missão já concluída; recompensa não duplicada",
                                        android.widget.Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (RuntimeException error) {
                        runOnUiThread(() -> { battleClaiming = false;
                            android.widget.Toast.makeText(this, error instanceof ForgeException
                                            ? "Libere espaço na Forja para receber os Fragmentos."
                                            : "Não foi possível concluir a missão",
                                    android.widget.Toast.LENGTH_LONG).show(); });
                    }
                });
            });
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
            renderMagnetoBattle(battle, campaign);
            showBattleImpact(battle.lastCounter ? "CONTRA-ATAQUE" : "IMPACTO",
                    battle.lastBossDamage, battle.lastTeamDamage, false);
            contentContainer.postDelayed(() -> { battleAnimating = false;
                renderMagnetoBattle(battle, campaign); }, animationDelay(850));
        });
    }

    private int animationDelay(int normal) {
        return android.provider.Settings.Global.getFloat(getContentResolver(),
                android.provider.Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f ? 0 : normal;
    }

    private void showBattleImpact(String label, int bossDamage, int teamDamage, boolean special) {
        if (animationDelay(1) == 0) return;
        FrameLayout flash = new FrameLayout(this);
        flash.setBackgroundColor(special ? 0x5548E0FF : 0x33FFFFFF);
        flash.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        contentContainer.addView(flash, new FrameLayout.LayoutParams(-1, -1));
        if (special) {
            View ring = new View(this);
            GradientDrawable circle = new GradientDrawable();
            circle.setShape(GradientDrawable.OVAL);
            circle.setColor(0x00303060);
            circle.setStroke(dimension(R.dimen.space_1), getColor(R.color.stone_mind));
            ring.setBackground(circle);
            FrameLayout.LayoutParams ringParams = new FrameLayout.LayoutParams(
                    dimension(R.dimen.space_8) * 5, dimension(R.dimen.space_8) * 5, Gravity.CENTER);
            flash.addView(ring, ringParams);
            ring.setScaleX(.25f);
            ring.setScaleY(.25f);
            ring.animate().scaleX(2f).scaleY(2f).alpha(0f).setDuration(1200);
        }
        TextView impact = text(label + "\n-" + bossDamage + " CHEFE"
                        + (teamDamage > 0 ? " · -" + teamDamage + " EQUIPE" : ""),
                R.style.TextAppearance_Ruptura_Title,
                special ? R.color.stone_mind : R.color.accent_deadpool, true);
        impact.setGravity(Gravity.CENTER);
        impact.setElevation(dimension(R.dimen.space_4));
        impact.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-1,
                dimension(R.dimen.space_8) * 2, Gravity.CENTER);
        flash.addView(impact, params);
        impact.animate().alpha(0f).translationY(-dimension(R.dimen.space_8))
                .setDuration(special ? 1200 : 850).withEndAction(() -> contentContainer.removeView(flash));
    }

    private void battleMeter(LinearLayout parent, String label, int value, int color) {
        LinearLayout meter = new LinearLayout(this);
        meter.setOrientation(LinearLayout.VERTICAL);
        meter.setPadding(dimension(R.dimen.space_2), dimension(R.dimen.space_2),
                dimension(R.dimen.space_2), dimension(R.dimen.space_2));
        meter.setBackground(background(R.color.surface_elevated, R.color.border_subtle, 0));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, -2, 1f);
        params.setMargins(dimension(R.dimen.space_1), 0, dimension(R.dimen.space_1), 0);
        parent.addView(meter, params);
        meter.addView(text(label + "  " + value + "%", R.style.TextAppearance_Ruptura_Caption,
                R.color.text_primary, true));
        addBattleBar(meter, value, color);
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
        if (styleRes == R.style.TextAppearance_Ruptura_Display) {
            view.setTypeface(getResources().getFont(R.font.barlow_condensed_extrabold));
        } else if (styleRes == R.style.TextAppearance_Ruptura_Title
                || styleRes == R.style.TextAppearance_Ruptura_Label
                || styleRes == R.style.TextAppearance_Ruptura_Caption) {
            view.setTypeface(getResources().getFont(bold
                    ? R.font.barlow_condensed_bold : R.font.barlow_condensed_regular));
        } else {
            Typeface sora = getResources().getFont(R.font.sora_variable);
            view.setTypeface(sora, bold ? Typeface.BOLD : Typeface.NORMAL);
        }
        view.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        return view;
    }

    private GradientDrawable background(int fill, int stroke, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(getColor(fill));
        drawable.setCornerRadius(radius);
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
        super.onDestroy();
        if (forgeRepository != null) forgeRepository.close();
        if (portraitLoader != null) portraitLoader.close();
        forgeExecutor.shutdown();
        editorialExecutor.shutdown();
    }
}
