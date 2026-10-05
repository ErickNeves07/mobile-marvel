package com.erickbarbosa.rupturainfinita;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.EnumMap;
import java.util.Map;
import java.time.LocalDate;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Local transactional inventory. Completion events are supplied only by real game flows. */
final class ForgeRepository extends SQLiteOpenHelper {
    private static final String DB_NAME = "forge_inventory.db";
    private static final int DB_VERSION = 8;
    private static final String[] STARTER_IDS = {"homem-aranha", "wolverine", "tocha-humana"};
    private static final String COUNTS = "inventory";
    private static final String REWARDS = "applied_rewards";

    ForgeRepository(Context context) {
        super(context.getApplicationContext(), DB_NAME, null, DB_VERSION);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE inventory (stone TEXT NOT NULL, stage TEXT NOT NULL, "
                + "count INTEGER NOT NULL CHECK(count BETWEEN 0 AND 999), "
                + "PRIMARY KEY(stone, stage))");
        db.execSQL("CREATE TABLE applied_rewards (event_id TEXT PRIMARY KEY NOT NULL, "
                + "source TEXT NOT NULL)");
        db.execSQL("CREATE TABLE applied_merges (operation_id TEXT PRIMARY KEY NOT NULL)");
        createGameTables(db);
        createVariantTables(db);
        createResourceTable(db);
        createCharacterOwnership(db);
        createRewardReceiptTable(db);
        createShopPurchaseTable(db);
        db.beginTransaction();
        try {
            for (InfinityStone stone : InfinityStone.values()) {
                for (ForgeStage stage : ForgeStage.values()) {
                    ContentValues row = new ContentValues();
                    row.put("stone", stone.name());
                    row.put("stage", stage.name());
                    row.put("count", stage == ForgeStage.FRAGMENT ? 3 : 0);
                    db.insertOrThrow(COUNTS, null, row);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2 && newVersion >= 2) {
            db.execSQL("CREATE TABLE IF NOT EXISTS applied_merges (operation_id TEXT PRIMARY KEY NOT NULL)");
            oldVersion = 2;
        }
        if (oldVersion < 3 && newVersion >= 3) {
            createGameTables(db);
            oldVersion = 3;
        }
        if (oldVersion < 4 && newVersion >= 4) {
            createVariantTables(db);
            oldVersion = 4;
        }
        if (oldVersion < 5 && newVersion >= 5) {
            createResourceTable(db);
        }
        if (oldVersion < 6 && newVersion >= 6) {
            createCharacterOwnership(db);
            db.execSQL("INSERT OR IGNORE INTO character_ownership(character_id) "
                    + "SELECT DISTINCT character_id FROM variant_ownership");
        }
        if (oldVersion < 7 && newVersion >= 7) createRewardReceiptTable(db);
        if (oldVersion < 8 && newVersion >= 8) createShopPurchaseTable(db);
        if (newVersion != DB_VERSION) throw new IllegalStateException("No Forge database migration is defined");
    }

    private void createGameTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS challenge_runs (challenge_date TEXT PRIMARY KEY NOT NULL, "
                + "target_id TEXT NOT NULL, guesses TEXT NOT NULL, status TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS campaign_progress (campaign_id TEXT PRIMARY KEY NOT NULL, "
                + "unlocked_mission INTEGER NOT NULL, team_ids TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS completed_missions (mission_id TEXT PRIMARY KEY NOT NULL)");
    }

    private void createVariantTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS gauntlet_state (singleton_id INTEGER PRIMARY KEY CHECK(singleton_id=1), activated INTEGER NOT NULL CHECK(activated IN (0,1)))");
        db.execSQL("INSERT OR IGNORE INTO gauntlet_state(singleton_id,activated) VALUES(1,0)");
        db.execSQL("CREATE TABLE IF NOT EXISTS variant_ownership (character_id TEXT NOT NULL, tier_id TEXT NOT NULL, "
                + "event_id TEXT NOT NULL UNIQUE, PRIMARY KEY(character_id,tier_id))");
        db.execSQL("CREATE TABLE IF NOT EXISTS equipped_variants (character_id TEXT PRIMARY KEY NOT NULL, tier_id TEXT NOT NULL)");
    }

    private void createResourceTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS player_resources (singleton_id INTEGER PRIMARY KEY "
                + "CHECK(singleton_id=1), credits INTEGER NOT NULL CHECK(credits>=0), "
                + "xp INTEGER NOT NULL CHECK(xp>=0))");
        db.execSQL("INSERT OR IGNORE INTO player_resources(singleton_id,credits,xp) VALUES(1,0,0)");
    }

    private void createShopPurchaseTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS shop_purchases (operation_id TEXT PRIMARY KEY NOT NULL, "
                + "stone TEXT NOT NULL, amount INTEGER NOT NULL CHECK(amount=1), "
                + "credits_spent INTEGER NOT NULL CHECK(credits_spent>0))");
    }

    private void createRewardReceiptTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS reward_fragments (event_id TEXT NOT NULL, "
                + "stone TEXT NOT NULL, count INTEGER NOT NULL CHECK(count BETWEEN 0 AND 999), "
                + "PRIMARY KEY(event_id,stone))");
    }

    private void createCharacterOwnership(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS character_ownership (character_id TEXT PRIMARY KEY NOT NULL)");
        for (String id : STARTER_IDS) {
            ContentValues character = new ContentValues();
            character.put("character_id", id);
            db.insertWithOnConflict("character_ownership", null, character, SQLiteDatabase.CONFLICT_IGNORE);
            ContentValues origin = new ContentValues();
            origin.put("character_id", id);
            origin.put("tier_id", GameVariantTier.ORIGIN.name());
            origin.put("event_id", "starter:" + id);
            db.insertWithOnConflict("variant_ownership", null, origin, SQLiteDatabase.CONFLICT_IGNORE);
            ContentValues equipped = new ContentValues();
            equipped.put("character_id", id);
            equipped.put("tier_id", GameVariantTier.ORIGIN.name());
            db.insertWithOnConflict("equipped_variants", null, equipped, SQLiteDatabase.CONFLICT_IGNORE);
        }
    }

    java.util.Set<String> loadOwnedCharacterIds() {
        java.util.Set<String> ids = new java.util.LinkedHashSet<>();
        try (Cursor cursor = getReadableDatabase().query("character_ownership",
                new String[]{"character_id"}, null, null, null, null, "character_id")) {
            while (cursor.moveToNext()) ids.add(cursor.getString(0));
        }
        return java.util.Collections.unmodifiableSet(ids);
    }

    boolean ownsCharacter(String characterId) {
        try (Cursor cursor = getReadableDatabase().query("character_ownership",
                new String[]{"character_id"}, "character_id=?", new String[]{characterId},
                null, null, null)) {
            return cursor.moveToFirst();
        }
    }

    PlayerResources loadPlayerResources() {
        return readPlayerResources(getReadableDatabase());
    }

    boolean purchaseFragment(String operationId, InfinityStone stone) {
        if (operationId == null || operationId.trim().isEmpty() || operationId.length() > 128
                || stone == null) throw new IllegalArgumentException("Invalid shop purchase");
        FragmentShopOffer offer = FragmentShopOffer.forStone(stone);
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            try (Cursor cursor = db.query("shop_purchases", new String[]{"operation_id"},
                    "operation_id=?", new String[]{operationId}, null, null, null)) {
                if (cursor.moveToFirst()) {
                    db.setTransactionSuccessful();
                    return false;
                }
            }
            PlayerResources current = readPlayerResources(db);
            if (!offer.isUnlocked(current.xp)) {
                throw new ForgeException(ForgeException.Reason.INSUFFICIENT_XP);
            }
            if (current.credits < offer.priceCredits) {
                throw new ForgeException(ForgeException.Reason.INSUFFICIENT_CREDITS);
            }
            int currentFragments = readCount(db, stone, ForgeStage.FRAGMENT);
            if (currentFragments >= ForgePolicy.MAX_COUNT) {
                throw new ForgeException(ForgeException.Reason.INVENTORY_FULL);
            }
            ContentValues purchase = new ContentValues();
            purchase.put("operation_id", operationId);
            purchase.put("stone", stone.name());
            purchase.put("amount", 1);
            purchase.put("credits_spent", offer.priceCredits);
            db.insertOrThrow("shop_purchases", null, purchase);
            writeCount(db, stone, ForgeStage.FRAGMENT, currentFragments + 1);
            ContentValues balance = new ContentValues();
            balance.put("credits", current.credits - offer.priceCredits);
            if (db.update("player_resources", balance, "singleton_id=1", null) != 1) {
                throw new IllegalStateException("Player resources are missing");
            }
            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
    }

    private PlayerResources readPlayerResources(SQLiteDatabase db) {
        try (Cursor cursor = db.query("player_resources", new String[]{"credits", "xp"},
                "singleton_id=1", null, null, null, null)) {
            if (!cursor.moveToFirst()) throw new IllegalStateException("Player resources are missing");
            return new PlayerResources(cursor.getLong(0), cursor.getLong(1));
        }
    }

    boolean isGauntletActivated() {
        SQLiteDatabase db = getWritableDatabase();
        try (Cursor cursor = db.query("gauntlet_state", new String[]{"activated"}, "singleton_id=1", null, null, null, null)) {
            return cursor.moveToFirst() && cursor.getInt(0) == 1;
        }
    }

    boolean hasCompleteGauntlet() {
        SQLiteDatabase db = getReadableDatabase();
        for (InfinityStone stone : InfinityStone.values()) {
            if (readCount(db, stone, ForgeStage.COMPLETE) < 1) return false;
        }
        return true;
    }

    boolean activateGauntlet() {
        SQLiteDatabase db = getWritableDatabase();
        createVariantTables(db);
        db.beginTransaction();
        try {
            if (isGauntletActivated()) { db.setTransactionSuccessful(); return false; }
            for (InfinityStone stone : InfinityStone.values()) {
                if (readCount(db, stone, ForgeStage.COMPLETE) < 1) return false;
            }
            ContentValues activated = new ContentValues(); activated.put("activated", 1);
            db.update("gauntlet_state", activated, "singleton_id=1", null);
            db.setTransactionSuccessful();
            return true;
        } finally { db.endTransaction(); }
    }

    java.util.Set<GameVariantTier> loadOwnedTiers(String characterId) {
        return loadOwnedTiers(characterId, isGauntletActivated());
    }

    java.util.Set<GameVariantTier> loadOwnedTiers(String characterId, boolean gauntletActive) {
        java.util.EnumSet<GameVariantTier> owned = java.util.EnumSet.noneOf(GameVariantTier.class);
        try (Cursor cursor = getReadableDatabase().query("variant_ownership", new String[]{"tier_id"},
                "character_id=?", new String[]{characterId}, null, null, null)) {
            while (cursor.moveToNext()) owned.add(GameVariantTier.valueOf(cursor.getString(0)));
        }
        if (owned.isEmpty() && gauntletActive && ownsCharacter(characterId)) {
            SQLiteDatabase db = getWritableDatabase();
            db.beginTransaction();
            try {
                ContentValues origin = new ContentValues();
                origin.put("character_id", characterId);
                origin.put("tier_id", GameVariantTier.ORIGIN.name());
                origin.put("event_id", "variant:" + characterId + ":origin");
                db.insertWithOnConflict("variant_ownership", null, origin, SQLiteDatabase.CONFLICT_IGNORE);
                ContentValues equipped = new ContentValues();
                equipped.put("character_id", characterId);
                equipped.put("tier_id", GameVariantTier.ORIGIN.name());
                db.insertWithOnConflict("equipped_variants", null, equipped, SQLiteDatabase.CONFLICT_IGNORE);
                db.setTransactionSuccessful();
            } finally { db.endTransaction(); }
            owned.add(GameVariantTier.ORIGIN);
        }
        return java.util.Collections.unmodifiableSet(owned);
    }

    String loadEquippedTier(String characterId) {
        try (Cursor cursor = getReadableDatabase().query("equipped_variants", new String[]{"tier_id"},
                "character_id=?", new String[]{characterId}, null, null, null)) {
            if (cursor.moveToFirst()) return cursor.getString(0);
        }
        return GameVariantTier.ORIGIN.name();
    }

    boolean unlockNextVariant(String characterId, GameVariantTier tier, List<GameCatalogCharacter> roster) {
        if (findCharacter(roster, characterId) == null || tier == null) throw new IllegalArgumentException("Unknown variant target");
        if (!ownsCharacter(characterId)) throw new IllegalStateException("Character is not owned");
        SQLiteDatabase db = getWritableDatabase();
        createVariantTables(db);
        db.beginTransaction();
        try {
            if (!isGauntletActivated()) throw new IllegalStateException("Gauntlet is not active");
            java.util.Set<GameVariantTier> owned = loadOwnedTiers(characterId, true);
            if (owned.contains(tier)) { db.setTransactionSuccessful(); return false; }
            if (VariantProgression.next(owned) != tier) throw new IllegalStateException("Variant tier is not next");
            requireCompleteGauntlet(db);
            ContentValues row = new ContentValues();
            row.put("character_id", characterId); row.put("tier_id", tier.name());
            row.put("event_id", "variant:" + characterId + ":" + tier.id);
            db.insertOrThrow("variant_ownership", null, row);
            ContentValues equipped = new ContentValues(); equipped.put("character_id", characterId); equipped.put("tier_id", tier.name());
            db.insertWithOnConflict("equipped_variants", null, equipped, SQLiteDatabase.CONFLICT_REPLACE);
            consumeCompleteGauntlet(db);
            db.setTransactionSuccessful();
            return true;
        } finally { db.endTransaction(); }
    }

    boolean unlockCharacter(String characterId, List<GameCatalogCharacter> roster) {
        if (findCharacter(roster, characterId) == null) {
            throw new IllegalArgumentException("Unknown character target");
        }
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            if (!isGauntletActivated()) throw new IllegalStateException("Gauntlet is not active");
            if (ownsCharacter(characterId)) { db.setTransactionSuccessful(); return false; }
            requireCompleteGauntlet(db);
            ContentValues character = new ContentValues();
            character.put("character_id", characterId);
            db.insertOrThrow("character_ownership", null, character);
            ContentValues origin = new ContentValues();
            origin.put("character_id", characterId);
            origin.put("tier_id", GameVariantTier.ORIGIN.name());
            origin.put("event_id", "gauntlet:character:" + characterId);
            db.insertOrThrow("variant_ownership", null, origin);
            ContentValues equipped = new ContentValues();
            equipped.put("character_id", characterId);
            equipped.put("tier_id", GameVariantTier.ORIGIN.name());
            db.insertOrThrow("equipped_variants", null, equipped);
            consumeCompleteGauntlet(db);
            db.setTransactionSuccessful();
            return true;
        } finally { db.endTransaction(); }
    }

    private void requireCompleteGauntlet(SQLiteDatabase db) {
        for (InfinityStone stone : InfinityStone.values()) {
            if (readCount(db, stone, ForgeStage.COMPLETE) < 1) {
                throw new ForgeException(ForgeException.Reason.INSUFFICIENT_ITEMS);
            }
        }
    }

    private void consumeCompleteGauntlet(SQLiteDatabase db) {
        requireCompleteGauntlet(db);
        for (InfinityStone stone : InfinityStone.values()) {
            writeCount(db, stone, ForgeStage.COMPLETE,
                    readCount(db, stone, ForgeStage.COMPLETE) - 1);
        }
    }

    void equipVariant(String characterId, GameVariantTier tier, List<GameCatalogCharacter> roster) {
        if (findCharacter(roster, characterId) == null || tier == null) throw new IllegalArgumentException("Unknown variant target");
        if (!ownsCharacter(characterId)) throw new IllegalStateException("Character is not owned");
        SQLiteDatabase db = getWritableDatabase();
        createVariantTables(db);
        db.beginTransaction();
        try {
            if (!isGauntletActivated()) throw new IllegalStateException("Gauntlet is not active");
            if (!loadOwnedTiers(characterId, true).contains(tier)) {
                throw new IllegalStateException("Variant is not owned");
            }
            ContentValues row = new ContentValues(); row.put("character_id", characterId); row.put("tier_id", tier.name());
            db.insertWithOnConflict("equipped_variants", null, row, SQLiteDatabase.CONFLICT_REPLACE);
            db.setTransactionSuccessful();
        } finally { db.endTransaction(); }
    }

    ChallengeState loadOrCreateChallenge(List<GameCatalogCharacter> roster, LocalDate date) {
        SQLiteDatabase db = getWritableDatabase();
        String day = date.toString();
        try (Cursor cursor = db.query("challenge_runs", new String[]{"target_id", "guesses", "status"},
                "challenge_date=?", new String[]{day}, null, null, null)) {
            if (cursor.moveToFirst()) return new ChallengeState(day, cursor.getString(0),
                    decodeList(cursor.getString(1)), cursor.getString(2));
        }
        String target = deterministicIndex(day + ":target", roster.size(), roster).id;
        ContentValues row = new ContentValues();
        row.put("challenge_date", day);
        row.put("target_id", target);
        row.put("guesses", "");
        row.put("status", "ACTIVE");
        db.insertOrThrow("challenge_runs", null, row);
        return new ChallengeState(day, target, Collections.emptyList(), "ACTIVE");
    }

    ChallengeState loadExistingChallenge(LocalDate date) {
        if (date == null) throw new IllegalArgumentException("Challenge date is required");
        String day = date.toString();
        try (Cursor cursor = getReadableDatabase().query("challenge_runs",
                new String[]{"target_id", "guesses", "status"}, "challenge_date=?",
                new String[]{day}, null, null, null)) {
            if (!cursor.moveToFirst()) return null;
            return new ChallengeState(day, cursor.getString(0),
                    decodeList(cursor.getString(1)), cursor.getString(2));
        }
    }

    ChallengeState submitGuess(ChallengeState state, String guessId, List<GameCatalogCharacter> roster) {
        GameCatalogCharacter guess = findCharacter(roster, guessId);
        if (guess == null) throw new IllegalArgumentException("Unknown character guess");
        if (state == null) throw new IllegalArgumentException("Challenge state is required");
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ChallengeState current = loadOrCreateChallenge(roster, LocalDate.parse(state.date));
            if (!"ACTIVE".equals(current.status) || current.guesses.contains(guessId)) return current;
            List<String> guesses = new ArrayList<>(current.guesses);
            guesses.add(guessId);
            boolean won = guessId.equals(current.targetId);
            String status = won ? "WON" : guesses.size() >= 6 ? "LOST" : "ACTIVE";
            ContentValues row = new ContentValues();
            row.put("guesses", encodeList(guesses));
            row.put("status", status);
            db.update("challenge_runs", row, "challenge_date=?", new String[]{current.date});
            if (won) grantMissingGauntletFragments(db, "daily:" + current.date,
                    "DAILY_CHALLENGE");
            db.setTransactionSuccessful();
            return new ChallengeState(current.date, current.targetId, guesses, status);
        } finally { db.endTransaction(); }
    }

    CampaignState loadCampaign(String campaignId, List<String> defaultTeam) {
        if (defaultTeam == null) {
            throw new IllegalArgumentException("Unknown campaign");
        }
        CampaignReward.missionCount(campaignId);
        SQLiteDatabase db = getWritableDatabase();
        try (Cursor cursor = db.query("campaign_progress", new String[]{"unlocked_mission", "team_ids"},
                "campaign_id=?", new String[]{campaignId}, null, null, null)) {
            if (cursor.moveToFirst()) return new CampaignState(campaignId, cursor.getInt(0),
                    decodeList(cursor.getString(1)));
        }
        ContentValues row = new ContentValues();
        row.put("campaign_id", campaignId);
        row.put("unlocked_mission", 1);
        row.put("team_ids", encodeList(defaultTeam));
        db.insertOrThrow("campaign_progress", null, row);
        return new CampaignState(campaignId, 1, defaultTeam);
    }

    CampaignState saveTeam(String campaignId, List<String> team, List<GameCatalogCharacter> roster) {
        if (team.size() != 3 || team.stream().distinct().count() != 3) {
            throw new IllegalArgumentException("A campaign team must contain three unique characters");
        }
        for (String id : team) {
            GameCatalogCharacter character = findCharacter(roster, id);
            if (character == null || !ownsCharacter(id)) {
                throw new IllegalArgumentException("Campaign team has an unowned member");
            }
        }
        SQLiteDatabase db = getWritableDatabase();
        CampaignState state = loadCampaign(campaignId, team);
        ContentValues row = new ContentValues();
        row.put("campaign_id", campaignId);
        row.put("unlocked_mission", state.unlockedMission);
        row.put("team_ids", encodeList(team));
        db.insertWithOnConflict("campaign_progress", null, row, SQLiteDatabase.CONFLICT_REPLACE);
        return new CampaignState(campaignId, state.unlockedMission, team);
    }

    boolean completeMission(String campaignId, int missionNumber) {
        CampaignReward reward = CampaignReward.forMission(campaignId, missionNumber);
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            CampaignState state = loadCampaign(campaignId, Collections.emptyList());
            if (missionNumber > state.unlockedMission) throw new IllegalStateException("Mission is locked");
            String missionId = campaignId + ":" + missionNumber;
            if (isMissionComplete(db, missionId)) {
                db.setTransactionSuccessful();
                return false;
            }
            if ("rupture".equals(campaignId) && missionNumber == 9 && !hasCompleteGauntlet()) {
                throw new IllegalStateException("Final chapter requires the complete Gauntlet");
            }
            PlayerResources resources = readPlayerResources(db);
            long credits = Math.addExact(resources.credits, reward.credits);
            long xp = Math.addExact(resources.xp, reward.xp);
            ContentValues complete = new ContentValues();
            complete.put("mission_id", missionId);
            boolean first = db.insertWithOnConflict("completed_missions", null, complete,
                    SQLiteDatabase.CONFLICT_IGNORE) != -1;
            if (first) {
                grantMissingGauntletFragments(db, "campaign:" + missionId, "CAMPAIGN");
                ContentValues balance = new ContentValues();
                balance.put("credits", credits);
                balance.put("xp", xp);
                if (db.update("player_resources", balance, "singleton_id=1", null) != 1) {
                    throw new IllegalStateException("Player resources are missing");
                }
                ContentValues progress = new ContentValues();
                progress.put("campaign_id", campaignId);
                progress.put("unlocked_mission", Math.max(state.unlockedMission,
                        Math.min(CampaignReward.missionCount(campaignId), missionNumber + 1)));
                progress.put("team_ids", encodeList(state.teamIds));
                db.insertWithOnConflict("campaign_progress", null, progress, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
            return first;
        } finally { db.endTransaction(); }
    }

    private boolean isMissionComplete(SQLiteDatabase db, String missionId) {
        try (Cursor cursor = db.query("completed_missions", new String[]{"mission_id"},
                "mission_id=?", new String[]{missionId}, null, null, null)) {
            return cursor.moveToFirst();
        }
    }

    boolean hasCompletedMission(String campaignId, int missionNumber) {
        CampaignReward.forMission(campaignId, missionNumber);
        return isMissionComplete(getReadableDatabase(), campaignId + ":" + missionNumber);
    }

    private static InfinityStone stoneFor(String value) {
        return InfinityStone.values()[deterministicIndex(value, InfinityStone.values().length)];
    }

    private static <T> T deterministicIndex(String value, int count, List<T> values) {
        if (count < 1) throw new IllegalArgumentException("Cannot select from an empty collection");
        return values.get(deterministicIndex(value, count));
    }

    private static int deterministicIndex(String value, int count) {
        if (count < 1) throw new IllegalArgumentException("Cannot select from an empty collection");
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            int index = 0;
            for (int i = 0; i < 4; i++) index = (index << 8) | (digest[i] & 255);
            return (index & Integer.MAX_VALUE) % count;
        } catch (Exception exception) { throw new IllegalStateException(exception); }
    }

    private static GameCatalogCharacter findCharacter(List<GameCatalogCharacter> roster, String id) {
        for (GameCatalogCharacter character : roster) if (character.id.equals(id)) return character;
        return null;
    }

    private static String encodeList(List<String> values) { return String.join(",", values); }
    private static List<String> decodeList(String value) {
        if (value == null || value.isEmpty()) return Collections.emptyList();
        return new ArrayList<>(java.util.Arrays.asList(value.split(",", -1)));
    }

    ForgeInventory load() {
        Map<InfinityStone, Map<ForgeStage, Integer>> values = new EnumMap<>(InfinityStone.class);
        try (Cursor cursor = getReadableDatabase().query(COUNTS,
                new String[]{"stone", "stage", "count"}, null, null, null, null, null)) {
            while (cursor.moveToNext()) {
                InfinityStone stone = InfinityStone.valueOf(cursor.getString(0));
                ForgeStage stage = ForgeStage.valueOf(cursor.getString(1));
                values.computeIfAbsent(stone, ignored -> new EnumMap<>(ForgeStage.class))
                        .put(stage, cursor.getInt(2));
            }
        }
        return new ForgeInventory(values);
    }

    ForgeInventory merge(String operationId, InfinityStone stone, ForgeStage inputStage) {
        if (operationId == null || operationId.trim().isEmpty() || stone == null) {
            throw new IllegalArgumentException("A merge operation ID and stone are required");
        }
        ForgeStage outputStage = ForgePolicy.outputFor(inputStage);
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues operation = new ContentValues();
            operation.put("operation_id", operationId);
            boolean firstAttempt = db.insertWithOnConflict(
                    "applied_merges", null, operation, SQLiteDatabase.CONFLICT_IGNORE) != -1;
            if (firstAttempt) {
                int available = readCount(db, stone, inputStage);
                int output = readCount(db, stone, outputStage);
                if (available < ForgePolicy.INPUT_COUNT) {
                    throw new ForgeException(ForgeException.Reason.INSUFFICIENT_ITEMS);
                }
                if (output >= ForgePolicy.MAX_COUNT) {
                    throw new ForgeException(ForgeException.Reason.INVENTORY_FULL);
                }
                writeCount(db, stone, inputStage, available - ForgePolicy.INPUT_COUNT);
                writeCount(db, stone, outputStage, output + ForgePolicy.OUTPUT_COUNT);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return load();
    }

    boolean grantCompletionReward(String eventId, String source, InfinityStone stone) {
        if (eventId == null || eventId.trim().isEmpty() || stone == null
                || !("DAILY_CHALLENGE".equals(source) || "CAMPAIGN".equals(source))) {
            throw new IllegalArgumentException("Invalid completion reward event");
        }
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues event = new ContentValues();
            event.put("event_id", eventId);
            event.put("source", source);
            if (db.insertWithOnConflict(REWARDS, null, event, SQLiteDatabase.CONFLICT_IGNORE) == -1) {
                db.setTransactionSuccessful();
                return false;
            }
            int current = readCount(db, stone, ForgeStage.SHARD);
            if (current + ForgePolicy.REWARD_SHARDS > ForgePolicy.MAX_COUNT) {
                throw new ForgeException(ForgeException.Reason.INVENTORY_FULL);
            }
            writeCount(db, stone, ForgeStage.SHARD, current + ForgePolicy.REWARD_SHARDS);
            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
    }

    Map<InfinityStone, Integer> fragmentsNeededForGauntlet() {
        return GauntletFragmentPlan.forInventory(load());
    }

    Map<InfinityStone, Integer> loadRewardFragments(String eventId) {
        EnumMap<InfinityStone, Integer> result = new EnumMap<>(InfinityStone.class);
        try (Cursor cursor = getReadableDatabase().query("reward_fragments",
                new String[]{"stone", "count"}, "event_id=?", new String[]{eventId},
                null, null, null)) {
            while (cursor.moveToNext()) {
                result.put(InfinityStone.valueOf(cursor.getString(0)), cursor.getInt(1));
            }
        }
        return Collections.unmodifiableMap(result);
    }

    private void grantMissingGauntletFragments(SQLiteDatabase db, String eventId, String source) {
        ContentValues event = new ContentValues();
        event.put("event_id", eventId);
        event.put("source", source);
        if (db.insertWithOnConflict(REWARDS, null, event, SQLiteDatabase.CONFLICT_IGNORE) == -1) {
            return;
        }
        for (InfinityStone stone : InfinityStone.values()) {
            int current = readCount(db, stone, ForgeStage.FRAGMENT);
            int needed = GauntletFragmentPlan.missing(
                    readCount(db, stone, ForgeStage.SHARD), current,
                    readCount(db, stone, ForgeStage.UNSTABLE_CORE),
                    readCount(db, stone, ForgeStage.COMPLETE));
            if (current + needed > ForgePolicy.MAX_COUNT) {
                throw new ForgeException(ForgeException.Reason.INVENTORY_FULL);
            }
            if (needed > 0) writeCount(db, stone, ForgeStage.FRAGMENT, current + needed);
            ContentValues receipt = new ContentValues();
            receipt.put("event_id", eventId);
            receipt.put("stone", stone.name());
            receipt.put("count", needed);
            db.insertOrThrow("reward_fragments", null, receipt);
        }
    }

    private int readCount(SQLiteDatabase db, InfinityStone stone, ForgeStage stage) {
        try (Cursor cursor = db.query(COUNTS, new String[]{"count"}, "stone=? AND stage=?",
                new String[]{stone.name(), stage.name()}, null, null, null)) {
            if (!cursor.moveToFirst()) throw new IllegalStateException("Inventory row is missing");
            return cursor.getInt(0);
        }
    }

    private void writeCount(SQLiteDatabase db, InfinityStone stone, ForgeStage stage, int count) {
        ForgePolicy.validateCount(count);
        ContentValues row = new ContentValues();
        row.put("count", count);
        if (db.update(COUNTS, row, "stone=? AND stage=?",
                new String[]{stone.name(), stage.name()}) != 1) {
            throw new IllegalStateException("Inventory row is missing");
        }
    }
}
