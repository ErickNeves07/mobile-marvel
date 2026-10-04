package com.erickbarbosa.rupturainfinita;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public final class ForgeRepositoryTest {
    private Context context;
    private ForgeRepository repository;

    @Before public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.deleteDatabase("forge_inventory.db");
        repository = new ForgeRepository(context);
    }

    @After public void tearDown() {
        repository.close();
        context.deleteDatabase("forge_inventory.db");
    }

    @Test public void newInventoryStartsWithThreeFragmentsPerStoneAndMergePersists() {
        assertEquals(0, repository.load().count(InfinityStone.SPACE, ForgeStage.SHARD));
        assertEquals(0, repository.load().count(InfinityStone.MIND, ForgeStage.SHARD));
        for (InfinityStone stone : InfinityStone.values()) {
            assertEquals(3, repository.load().count(stone, ForgeStage.FRAGMENT));
        }
        assertTrue(repository.grantCompletionReward("daily-001", "DAILY_CHALLENGE", InfinityStone.SPACE));
        ForgeInventory afterMerge = repository.merge("merge-001", InfinityStone.SPACE, ForgeStage.SHARD);
        assertEquals(1, afterMerge.count(InfinityStone.SPACE, ForgeStage.SHARD));
        assertEquals(4, afterMerge.count(InfinityStone.SPACE, ForgeStage.FRAGMENT));
        repository.close();
        repository = new ForgeRepository(context);
        assertEquals(4, repository.load().count(InfinityStone.SPACE, ForgeStage.FRAGMENT));
    }

    @Test public void sameRewardEventIsAppliedOnlyOnce() {
        assertTrue(repository.grantCompletionReward("campaign-001", "CAMPAIGN", InfinityStone.MIND));
        assertFalse(repository.grantCompletionReward("campaign-001", "CAMPAIGN", InfinityStone.MIND));
        assertEquals(3, repository.load().count(InfinityStone.MIND, ForgeStage.SHARD));
    }

    @Test public void sameMergeOperationCannotDuplicateOutput() {
        repository.grantCompletionReward("daily-merge", "DAILY_CHALLENGE", InfinityStone.REALITY);
        repository.merge("operation-once", InfinityStone.REALITY, ForgeStage.SHARD);
        ForgeInventory retried = repository.merge("operation-once", InfinityStone.REALITY, ForgeStage.SHARD);
        assertEquals(1, retried.count(InfinityStone.REALITY, ForgeStage.SHARD));
        assertEquals(4, retried.count(InfinityStone.REALITY, ForgeStage.FRAGMENT));
    }

    @Test public void insufficientMergeDoesNotMutateOrConsumeOperationId() {
        try {
            repository.merge("retry-after-reward", InfinityStone.SPACE, ForgeStage.SHARD);
            fail("expected insufficient items error");
        } catch (ForgeException expected) {
            assertEquals(ForgeException.Reason.INSUFFICIENT_ITEMS, expected.reason);
        }
        assertEquals(0, repository.load().count(InfinityStone.SPACE, ForgeStage.SHARD));
        assertEquals(3, repository.load().count(InfinityStone.SPACE, ForgeStage.FRAGMENT));
        repository.grantCompletionReward("daily-after-insufficient", "DAILY_CHALLENGE", InfinityStone.SPACE);
        ForgeInventory merged = repository.merge("retry-after-reward", InfinityStone.SPACE, ForgeStage.SHARD);
        assertEquals(4, merged.count(InfinityStone.SPACE, ForgeStage.FRAGMENT));
    }

    @Test public void newMergesConsumeExactlyTwoAndOldBalancesRemain() {
        setCount(InfinityStone.MIND, ForgeStage.FRAGMENT, 3);
        ForgeInventory first = repository.merge("new-ratio-001", InfinityStone.MIND, ForgeStage.FRAGMENT);
        assertEquals(1, first.count(InfinityStone.MIND, ForgeStage.FRAGMENT));
        assertEquals(1, first.count(InfinityStone.MIND, ForgeStage.UNSTABLE_CORE));
        try {
            repository.merge("new-ratio-002", InfinityStone.MIND, ForgeStage.FRAGMENT);
            fail("one input must not merge");
        } catch (ForgeException expected) {
            assertEquals(ForgeException.Reason.INSUFFICIENT_ITEMS, expected.reason);
        }
        repository.close(); repository = new ForgeRepository(context);
        assertEquals(1, repository.load().count(InfinityStone.MIND, ForgeStage.FRAGMENT));
        assertEquals(1, repository.load().count(InfinityStone.MIND, ForgeStage.UNSTABLE_CORE));
        ForgeInventory retry = repository.merge("new-ratio-001", InfinityStone.MIND, ForgeStage.FRAGMENT);
        assertEquals(1, retry.count(InfinityStone.MIND, ForgeStage.FRAGMENT));
        assertEquals(1, retry.count(InfinityStone.MIND, ForgeStage.UNSTABLE_CORE));
    }

    @Test public void versionOneInventoryIsPreservedByMigration() {
        SQLiteOpenHelper legacy = new SQLiteOpenHelper(context, "forge_inventory.db", null, 1) {
            @Override public void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE inventory (stone TEXT NOT NULL, stage TEXT NOT NULL, "
                        + "count INTEGER NOT NULL CHECK(count BETWEEN 0 AND 999), "
                        + "PRIMARY KEY(stone, stage))");
                db.execSQL("CREATE TABLE applied_rewards (event_id TEXT PRIMARY KEY NOT NULL, "
                        + "source TEXT NOT NULL)");
                ContentValues row = new ContentValues();
                row.put("stone", InfinityStone.POWER.name());
                row.put("stage", ForgeStage.SHARD.name());
                row.put("count", 3);
                db.insertOrThrow("inventory", null, row);
            }

            @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                throw new IllegalStateException("Unexpected migration in fixture");
            }
        };
        legacy.getWritableDatabase();
        legacy.close();

        repository.close();
        repository = new ForgeRepository(context);
        assertEquals(3, repository.load().count(InfinityStone.POWER, ForgeStage.SHARD));
        try (android.database.Cursor cursor = repository.getReadableDatabase()
                .rawQuery("SELECT COUNT(*) FROM applied_merges", null)) {
            assertTrue(cursor.moveToFirst());
            assertEquals(0, cursor.getInt(0));
        }
    }

    @Test public void capFailureRollsBackRewardAndLeavesEventRetryable() {
        setCount(InfinityStone.SOUL, ForgeStage.SHARD, 997);
        try {
            repository.grantCompletionReward("daily-cap", "DAILY_CHALLENGE", InfinityStone.SOUL);
            fail("expected inventory cap error");
        } catch (ForgeException expected) {
            assertEquals(ForgeException.Reason.INVENTORY_FULL, expected.reason);
        }
        assertEquals(997, repository.load().count(InfinityStone.SOUL, ForgeStage.SHARD));
        setCount(InfinityStone.SOUL, ForgeStage.SHARD, 996);
        assertTrue(repository.grantCompletionReward("daily-cap", "DAILY_CHALLENGE", InfinityStone.SOUL));
        assertEquals(999, repository.load().count(InfinityStone.SOUL, ForgeStage.SHARD));
    }

    @Test public void gauntletRequiresAllStonesAndActivationDoesNotConsumeThem() {
        assertFalse(repository.activateGauntlet());
        assertFalse(repository.isGauntletActivated());
        for (InfinityStone stone : InfinityStone.values()) setCount(stone, ForgeStage.COMPLETE, 1);
        assertTrue(repository.activateGauntlet());
        assertTrue(repository.isGauntletActivated());
        for (InfinityStone stone : InfinityStone.values()) assertEquals(1,
                repository.load().count(stone, ForgeStage.COMPLETE));
        assertFalse(repository.activateGauntlet());
    }

    @Test public void variantUnlockIsSequentialIdempotentAndConsumesWholeGauntlet() throws Exception {
        java.util.List<GameCatalogCharacter> roster = GameCatalogParser.read(context.getAssets());
        for (InfinityStone stone : InfinityStone.values()) setCount(stone, ForgeStage.COMPLETE, 1);
        assertTrue(repository.activateGauntlet());
        GameCatalogCharacter character = null;
        for (GameCatalogCharacter candidate : roster) {
            if ("wolverine".equals(candidate.id)) character = candidate;
        }
        assertTrue(character != null);
        assertTrue(repository.unlockNextVariant(character.id, GameVariantTier.ASCENSION, roster));
        assertFalse(repository.unlockNextVariant(character.id, GameVariantTier.ASCENSION, roster));
        for (InfinityStone stone : InfinityStone.values()) {
            assertEquals(0, repository.load().count(stone, ForgeStage.COMPLETE));
            assertEquals(3, repository.load().count(stone, ForgeStage.FRAGMENT));
        }
        assertEquals(GameVariantTier.ASCENSION.name(), repository.loadEquippedTier(character.id));
        try {
            repository.unlockNextVariant(character.id, GameVariantTier.INFINITY, roster);
            fail("Expected next-tier validation");
        } catch (IllegalStateException expected) { }
        assertEquals(java.util.EnumSet.of(GameVariantTier.ORIGIN, GameVariantTier.ASCENSION),
                repository.loadOwnedTiers(character.id));
        repository.close(); repository = new ForgeRepository(context);
        assertTrue(repository.isGauntletActivated());
        assertEquals(GameVariantTier.ASCENSION.name(), repository.loadEquippedTier(character.id));
        assertEquals(java.util.EnumSet.of(GameVariantTier.ORIGIN, GameVariantTier.ASCENSION),
                repository.loadOwnedTiers(character.id));
    }

    @Test public void newCharacterCostsWholeGauntletAndCannotBeGrantedTwice() throws Exception {
        java.util.List<GameCatalogCharacter> roster = GameCatalogParser.read(context.getAssets());
        for (InfinityStone stone : InfinityStone.values()) setCount(stone, ForgeStage.COMPLETE, 1);
        assertTrue(repository.activateGauntlet());
        assertTrue(repository.unlockCharacter("homem-de-ferro", roster));
        assertTrue(repository.ownsCharacter("homem-de-ferro"));
        assertEquals(java.util.EnumSet.of(GameVariantTier.ORIGIN),
                repository.loadOwnedTiers("homem-de-ferro"));
        for (InfinityStone stone : InfinityStone.values()) {
            assertEquals(0, repository.load().count(stone, ForgeStage.COMPLETE));
        }
        assertFalse(repository.unlockCharacter("homem-de-ferro", roster));
        assertEquals(GameVariantTier.ORIGIN.name(), repository.loadEquippedTier("homem-de-ferro"));
    }

    @Test public void incompleteGauntletDoesNotConsumeAnyStone() throws Exception {
        java.util.List<GameCatalogCharacter> roster = GameCatalogParser.read(context.getAssets());
        for (InfinityStone stone : InfinityStone.values()) setCount(stone, ForgeStage.COMPLETE, 1);
        assertTrue(repository.activateGauntlet());
        setCount(InfinityStone.SOUL, ForgeStage.COMPLETE, 0);
        try {
            repository.unlockCharacter("homem-de-ferro", roster);
            fail("Expected missing Soul stone");
        } catch (ForgeException expected) {
            assertEquals(ForgeException.Reason.INSUFFICIENT_ITEMS, expected.reason);
        }
        assertFalse(repository.ownsCharacter("homem-de-ferro"));
        for (InfinityStone stone : InfinityStone.values()) {
            assertEquals(stone == InfinityStone.SOUL ? 0 : 1,
                    repository.load().count(stone, ForgeStage.COMPLETE));
        }
    }

    @Test public void starterRosterIsOwnedWithoutActivatingGauntletAndOthersStayLocked() throws Exception {
        assertEquals(new java.util.HashSet<>(java.util.Arrays.asList(
                        "homem-aranha", "wolverine", "tocha-humana")),
                repository.loadOwnedCharacterIds());
        assertEquals(java.util.EnumSet.of(GameVariantTier.ORIGIN),
                repository.loadOwnedTiers("wolverine"));
        assertFalse(repository.ownsCharacter("homem-de-ferro"));
        for (InfinityStone stone : InfinityStone.values()) setCount(stone, ForgeStage.COMPLETE, 1);
        assertTrue(repository.activateGauntlet());
        assertTrue(repository.loadOwnedTiers("homem-de-ferro").isEmpty());
        java.util.List<GameCatalogCharacter> roster = GameCatalogParser.read(context.getAssets());
        try {
            repository.unlockNextVariant("homem-de-ferro", GameVariantTier.ASCENSION, roster);
            fail("Locked character must not gain a variant");
        } catch (IllegalStateException expected) { }
    }

    @Test public void versionThreeMigrationPreservesProgressAndAddsLockedGauntlet() {
        SQLiteOpenHelper legacy = new SQLiteOpenHelper(context, "forge_inventory.db", null, 3) {
            @Override public void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE inventory (stone TEXT NOT NULL, stage TEXT NOT NULL, "
                        + "count INTEGER NOT NULL CHECK(count BETWEEN 0 AND 999), PRIMARY KEY(stone,stage))");
                db.execSQL("CREATE TABLE applied_rewards (event_id TEXT PRIMARY KEY NOT NULL, source TEXT NOT NULL)");
                db.execSQL("CREATE TABLE applied_merges (operation_id TEXT PRIMARY KEY NOT NULL)");
                db.execSQL("CREATE TABLE challenge_runs (challenge_date TEXT PRIMARY KEY NOT NULL, target_id TEXT NOT NULL, guesses TEXT NOT NULL, status TEXT NOT NULL)");
                db.execSQL("CREATE TABLE campaign_progress (campaign_id TEXT PRIMARY KEY NOT NULL, unlocked_mission INTEGER NOT NULL, team_ids TEXT NOT NULL)");
                db.execSQL("CREATE TABLE completed_missions (mission_id TEXT PRIMARY KEY NOT NULL)");
                ContentValues item = new ContentValues(); item.put("stone", InfinityStone.SOUL.name());
                item.put("stage", ForgeStage.COMPLETE.name()); item.put("count", 2);
                db.insertOrThrow("inventory", null, item);
                ContentValues campaign = new ContentValues(); campaign.put("campaign_id", "xmen");
                campaign.put("unlocked_mission", 2); campaign.put("team_ids", "wolverine,ciclope,jean-grey");
                db.insertOrThrow("campaign_progress", null, campaign);
            }

            @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                throw new IllegalStateException("Unexpected migration in fixture");
            }
        };
        legacy.getWritableDatabase(); legacy.close();

        repository.close(); repository = new ForgeRepository(context);
        assertEquals(2, repository.load().count(InfinityStone.SOUL, ForgeStage.COMPLETE));
        assertFalse(repository.isGauntletActivated());
        try (android.database.Cursor cursor = repository.getReadableDatabase().query("campaign_progress",
                new String[]{"unlocked_mission"}, "campaign_id=?", new String[]{"xmen"}, null, null, null)) {
            assertTrue(cursor.moveToFirst()); assertEquals(2, cursor.getInt(0));
        }
    }

    @Test public void versionFourMigrationKeepsOldMissionPaidWithoutBackfill() {
        java.util.List<String> team = java.util.Arrays.asList("wolverine", "ciclope", "jean-grey");
        repository.loadCampaign("xmen", team);
        setCount(InfinityStone.SPACE, ForgeStage.SHARD, 3);
        setCount(InfinityStone.MIND, ForgeStage.FRAGMENT, 0);
        SQLiteDatabase db = repository.getWritableDatabase();
        db.execSQL("UPDATE campaign_progress SET unlocked_mission=2 WHERE campaign_id='xmen'");
        db.execSQL("INSERT INTO completed_missions(mission_id) VALUES('xmen:1')");
        db.execSQL("INSERT INTO applied_rewards(event_id,source) VALUES('campaign:xmen:1','CAMPAIGN')");
        db.execSQL("DROP TABLE player_resources");
        db.setVersion(4);
        repository.close();
        repository = new ForgeRepository(context);

        assertEquals(3, repository.load().count(InfinityStone.SPACE, ForgeStage.SHARD));
        assertEquals(0, repository.load().count(InfinityStone.MIND, ForgeStage.FRAGMENT));
        assertEquals(0, repository.loadPlayerResources().credits);
        assertEquals(0, repository.loadPlayerResources().xp);
        assertFalse(repository.completeMission("xmen", 1));
        assertTrue(repository.completeMission("xmen", 2));
        assertEquals(3, repository.load().count(InfinityStone.SPACE, ForgeStage.SHARD));
        assertEquals(3, repository.load().count(InfinityStone.SPACE, ForgeStage.FRAGMENT));
        assertEquals(4, repository.load().count(InfinityStone.MIND, ForgeStage.FRAGMENT));
        assertEquals(3300, repository.loadPlayerResources().credits);
        assertEquals(920, repository.loadPlayerResources().xp);
    }

    @Test public void rewardReceiptFailureRollsBackEntireCampaignReward() {
        repository.loadCampaign("xmen", java.util.Arrays.asList("wolverine", "ciclope", "jean-grey"));
        repository.getWritableDatabase().execSQL("DROP TABLE reward_fragments");
        try {
            repository.completeMission("xmen", 1);
            fail("expected receipt storage error");
        } catch (android.database.sqlite.SQLiteException expected) { }
        assertEquals(1, repository.loadCampaign("xmen", java.util.Collections.emptyList()).unlockedMission);
        assertEquals(3, repository.load().count(InfinityStone.MIND, ForgeStage.FRAGMENT));
        assertEquals(0, repository.loadPlayerResources().credits);
        assertEquals(0, repository.loadPlayerResources().xp);
        repository.getWritableDatabase().execSQL("CREATE TABLE reward_fragments "
                + "(event_id TEXT NOT NULL, stone TEXT NOT NULL, count INTEGER NOT NULL, "
                + "PRIMARY KEY(event_id,stone))");
        assertTrue(repository.completeMission("xmen", 1));
        assertEquals(4, repository.load().count(InfinityStone.MIND, ForgeStage.FRAGMENT));
        assertEquals(3000, repository.loadPlayerResources().credits);
    }

    @Test public void fullOutputStageDoesNotConsumeMergeInput() {
        setCount(InfinityStone.TIME, ForgeStage.SHARD, 3);
        setCount(InfinityStone.TIME, ForgeStage.FRAGMENT, 999);
        try {
            repository.merge("capped-merge", InfinityStone.TIME, ForgeStage.SHARD);
            fail("expected inventory cap error");
        } catch (ForgeException expected) {
            assertEquals(ForgeException.Reason.INVENTORY_FULL, expected.reason);
        }
        ForgeInventory inventory = repository.load();
        assertEquals(3, inventory.count(InfinityStone.TIME, ForgeStage.SHARD));
        assertEquals(999, inventory.count(InfinityStone.TIME, ForgeStage.FRAGMENT));
    }

    private void setCount(InfinityStone stone, ForgeStage stage, int count) {
        ContentValues row = new ContentValues();
        row.put("count", count);
        SQLiteDatabase db = repository.getWritableDatabase();
        assertEquals(1, db.update("inventory", row, "stone=? AND stage=?",
                new String[]{stone.name(), stage.name()}));
    }
}
