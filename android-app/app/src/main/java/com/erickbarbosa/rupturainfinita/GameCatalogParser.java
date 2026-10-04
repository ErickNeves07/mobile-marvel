package com.erickbarbosa.rupturainfinita;

import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class GameCatalogParser {
    private static final String ASSET_NAME = "game_catalog.json";
    private static final int EXPECTED_CHARACTERS = 21;

    private GameCatalogParser() { }

    static List<GameCatalogCharacter> read(AssetManager assets) throws IOException, JSONException {
        try (InputStream input = assets.open(ASSET_NAME);
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
            return parse(new String(output.toByteArray(), StandardCharsets.UTF_8));
        }
    }

    static List<GameCatalogCharacter> parse(String json) throws JSONException {
        if (json == null || json.trim().isEmpty()) {
            throw new JSONException("Game catalog must not be blank");
        }
        JSONObject root = new JSONObject(json);
        JSONArray items = root.getJSONArray("items");
        if (items.length() != EXPECTED_CHARACTERS) {
            throw new JSONException("Game catalog must contain 21 characters");
        }

        List<GameCatalogCharacter> characters = new ArrayList<>();
        Set<String> characterIds = new HashSet<>();
        Set<String> variantIds = new HashSet<>();
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            String id = requireNonBlank(item, "id", "character");
            if (!characterIds.add(id)) {
                throw new JSONException("Duplicate game character id: " + id);
            }

            requireNonBlank(item, "name", id);
            requireNonBlank(item, "group_id", id);
            JSONArray variants = item.getJSONArray("variants");
            if (variants.length() != GameVariantTier.values().length) {
                throw new JSONException("Each game character must have five variants: " + id);
            }

            List<GameCatalogVariant> parsedVariants = new ArrayList<>();
            for (int variantIndex = 0; variantIndex < variants.length(); variantIndex++) {
                JSONObject variant = variants.getJSONObject(variantIndex);
                GameVariantTier tier = GameVariantTier.values()[variantIndex];
                String variantId = requireNonBlank(variant, "id", id);
                if (!variantIds.add(variantId)) {
                    throw new JSONException("Duplicate game variant id: " + variantId);
                }
                String tierId = requireNonBlank(variant, "tier_id", id);
                if (!tier.id.equals(tierId)) {
                    throw new JSONException("Unexpected variant order for game character: " + id);
                }
                parsedVariants.add(new GameCatalogVariant(
                        tier, requireNonBlank(variant, "name", variantId)));
            }

            characters.add(new GameCatalogCharacter(
                    id,
                    item.getString("name").trim(),
                    item.getString("group_id").trim(),
                    parsedVariants));
        }
        return Collections.unmodifiableList(characters);
    }

    private static String requireNonBlank(JSONObject object, String key, String owner)
            throws JSONException {
        String value = object.getString(key);
        if (value.trim().isEmpty()) {
            throw new JSONException("Blank " + key + " in game catalog entry: " + owner);
        }
        return value.trim();
    }
}

final class GameCatalogCharacter {
    final String id;
    final String name;
    final String groupId;
    final List<GameCatalogVariant> variants;

    GameCatalogCharacter(String id, String name, String groupId, List<GameCatalogVariant> variants) {
        this.id = id;
        this.name = name;
        this.groupId = groupId;
        this.variants = Collections.unmodifiableList(new ArrayList<>(variants));
    }
}

final class GameCatalogVariant {
    final GameVariantTier tier;
    final String name;

    GameCatalogVariant(GameVariantTier tier, String name) {
        this.tier = tier;
        this.name = name;
    }
}
