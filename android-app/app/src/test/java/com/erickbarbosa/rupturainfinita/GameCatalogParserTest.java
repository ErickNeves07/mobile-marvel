package com.erickbarbosa.rupturainfinita;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class GameCatalogParserTest {
    @Test
    public void sharedCatalogKeepsExpectedCharacterAndVariantStructure() throws Exception {
        String catalog = new String(Files.readAllBytes(
                Path.of("../../shared/game_catalog.json")), StandardCharsets.UTF_8);
        assertEquals(21, occurrences(catalog, "\"group_id\""));
        assertEquals(105, occurrences(catalog, "\"tier_id\""));
        assertEquals(21, occurrences(catalog, "\"variants\""));
        assertEquals(21, GameCatalogParser.parse(catalog).size());
    }

    @Test
    public void rejectsBlankRequiredCharacterName() throws Exception {
        JSONObject root = validCatalog();
        root.getJSONArray("items").getJSONObject(0).put("name", "  ");
        assertInvalid(root.toString());
    }

    @Test
    public void rejectsDuplicateCharacterIds() throws Exception {
        JSONObject root = validCatalog();
        JSONArray items = root.getJSONArray("items");
        items.getJSONObject(1).put("id", items.getJSONObject(0).getString("id"));
        assertInvalid(root.toString());
    }

    @Test
    public void rejectsDuplicateVariantIdsAcrossCharacters() throws Exception {
        JSONObject root = validCatalog();
        JSONArray items = root.getJSONArray("items");
        String firstVariantId = items.getJSONObject(0).getJSONArray("variants")
                .getJSONObject(0).getString("id");
        items.getJSONObject(1).getJSONArray("variants").getJSONObject(0).put("id", firstVariantId);
        assertInvalid(root.toString());
    }

    @Test
    public void rejectsUnexpectedVariantTierOrder() throws Exception {
        JSONObject root = validCatalog();
        JSONArray variants = root.getJSONArray("items").getJSONObject(0).getJSONArray("variants");
        variants.getJSONObject(0).put("tier_id", "unknown");
        assertInvalid(root.toString());
    }

    private static JSONObject validCatalog() throws Exception {
        String catalog = new String(Files.readAllBytes(
                Path.of("../../shared/game_catalog.json")), StandardCharsets.UTF_8);
        return new JSONObject(catalog);
    }

    private static void assertInvalid(String json) throws Exception {
        try {
            GameCatalogParser.parse(json);
            fail("Expected malformed catalog rejection");
        } catch (JSONException expected) {
            // Expected structural rejection.
        }
    }

    private static int occurrences(String input, String token) {
        int count = 0, offset = 0;
        while ((offset = input.indexOf(token, offset)) >= 0) {
            count++;
            offset += token.length();
        }
        return count;
    }
}
