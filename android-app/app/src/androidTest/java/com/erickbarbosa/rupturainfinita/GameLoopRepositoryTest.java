package com.erickbarbosa.rupturainfinita;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public final class GameLoopRepositoryTest {
    private Context context;
    private ForgeRepository repository;
    private List<GameCatalogCharacter> roster;

    @Before public void setUp() throws Exception {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.deleteDatabase("forge_inventory.db");
        repository = new ForgeRepository(context);
        roster = GameCatalogParser.read(context.getAssets());
    }

    @After public void tearDown() {
        repository.close();
        context.deleteDatabase("forge_inventory.db");
    }

    @Test public void dailyWinGrantsMissingFragmentsForAllSixAndPersistsReceipt() {
        LocalDate date = LocalDate.of(2026, 9, 28);
        ChallengeState state = repository.loadOrCreateChallenge(roster, date);
        ChallengeState won = repository.submitGuess(state, state.targetId, roster);

        assertEquals("WON", won.status);
        assertEquals(1, won.guesses.size());
        assertEquals(0, totalCount(ForgeStage.SHARD));
        assertEquals(24, totalCount(ForgeStage.FRAGMENT));
        for (InfinityStone stone : InfinityStone.values()) {
            assertEquals(4, repository.load().count(stone, ForgeStage.FRAGMENT));
            assertEquals(Integer.valueOf(1), repository.loadRewardFragments("daily:" + date).get(stone));
        }

        ChallengeState repeated = repository.submitGuess(won, state.targetId, roster);
        assertEquals("WON", repeated.status);
        assertEquals(24, totalCount(ForgeStage.FRAGMENT));

        repository.close();
        repository = new ForgeRepository(context);
        ChallengeState restored = repository.loadOrCreateChallenge(roster, date);
        assertEquals("WON", restored.status);
        assertEquals(state.targetId, restored.targetId);
        assertEquals(won.guesses, restored.guesses);
        assertEquals(24, totalCount(ForgeStage.FRAGMENT));
        assertEquals(6, repository.loadRewardFragments("daily:" + date).size());
    }

    @Test public void dailyLossAfterSixGuessesPersistsWithoutReward() {
        LocalDate date = LocalDate.of(2026, 9, 29);
        ChallengeState state = repository.loadOrCreateChallenge(roster, date);
        List<String> wrongGuesses = new ArrayList<>();
        for (GameCatalogCharacter character : roster) {
            if (!character.id.equals(state.targetId) && wrongGuesses.size() < 6) {
                wrongGuesses.add(character.id);
            }
        }

        ChallengeState current = state;
        for (String guess : wrongGuesses) current = repository.submitGuess(current, guess, roster);

        assertEquals(6, current.guesses.size());
        assertEquals("LOST", current.status);
        assertEquals(18, totalCount(ForgeStage.FRAGMENT));
        repository.close();
        repository = new ForgeRepository(context);
        ChallengeState restored = repository.loadOrCreateChallenge(roster, date);
        assertEquals("LOST", restored.status);
        assertEquals(current.guesses, restored.guesses);
        assertEquals(18, totalCount(ForgeStage.FRAGMENT));
    }

    @Test public void alreadySufficientInventoryGrantsZeroWithoutOverflow() {
        LocalDate date = LocalDate.of(2026, 10, 4);
        ChallengeState state = repository.loadOrCreateChallenge(roster, date);
        repository.getWritableDatabase().execSQL("UPDATE inventory SET count=999 WHERE stage='FRAGMENT'");
        ChallengeState won = repository.submitGuess(state, state.targetId, roster);
        assertEquals("WON", won.status);
        assertEquals(999 * InfinityStone.values().length, totalCount(ForgeStage.FRAGMENT));
        for (InfinityStone stone : InfinityStone.values()) {
            assertEquals(Integer.valueOf(0), repository.loadRewardFragments("daily:" + date).get(stone));
        }
    }

    @Test public void dailyRewardCountsExistingNucleiShardsAndCompleteStones() {
        repository.getWritableDatabase().execSQL("UPDATE inventory SET count=1 "
                + "WHERE stone='SPACE' AND stage='COMPLETE'");
        repository.getWritableDatabase().execSQL("UPDATE inventory SET count=2 "
                + "WHERE stone='MIND' AND stage='SHARD'");
        repository.getWritableDatabase().execSQL("UPDATE inventory SET count=0 "
                + "WHERE stone='REALITY' AND stage='FRAGMENT'");
        LocalDate date = LocalDate.of(2026, 10, 5);
        ChallengeState state = repository.loadOrCreateChallenge(roster, date);
        repository.submitGuess(state, state.targetId, roster);
        java.util.Map<InfinityStone, Integer> receipt = repository.loadRewardFragments("daily:" + date);
        assertEquals(Integer.valueOf(0), receipt.get(InfinityStone.SPACE));
        assertEquals(Integer.valueOf(0), receipt.get(InfinityStone.MIND));
        assertEquals(Integer.valueOf(4), receipt.get(InfinityStone.REALITY));
        assertEquals(Integer.valueOf(1), receipt.get(InfinityStone.POWER));
    }

    @Test public void campaignRequiresSequentialMissionsAndPersistsIdempotentRewardsAndTeam() {
        List<String> defaultTeam = Arrays.asList("homem-aranha", "wolverine", "tocha-humana");
        CampaignState initial = repository.loadCampaign("xmen", defaultTeam);
        assertEquals(1, initial.unlockedMission);

        List<String> selectedTeam = Arrays.asList("wolverine", "homem-aranha", "tocha-humana");
        CampaignState saved = repository.saveTeam("xmen", selectedTeam, roster);
        assertEquals(selectedTeam, saved.teamIds);
        try {
            repository.completeMission("xmen", 2);
            fail("Expected the second mission to remain locked");
        } catch (IllegalStateException expected) { }
        assertEquals(0, totalCount(ForgeStage.SHARD));

        assertTrue(repository.completeMission("xmen", 1));
        assertFalse(repository.completeMission("xmen", 1));
        assertEquals(2, repository.loadCampaign("xmen", defaultTeam).unlockedMission);
        assertEquals(0, totalCount(ForgeStage.SHARD));
        assertEquals(4, repository.load().count(InfinityStone.MIND, ForgeStage.FRAGMENT));
        assertEquals(3000, repository.loadPlayerResources().credits);
        assertEquals(840, repository.loadPlayerResources().xp);

        assertTrue(repository.completeMission("xmen", 2));
        assertEquals(3, repository.loadCampaign("xmen", defaultTeam).unlockedMission);
        assertTrue(repository.completeMission("xmen", 3));
        assertFalse(repository.completeMission("xmen", 3));
        assertEquals(24, totalCount(ForgeStage.FRAGMENT));
        assertEquals(4, repository.load().count(InfinityStone.SPACE, ForgeStage.FRAGMENT));
        assertEquals(4, repository.load().count(InfinityStone.TIME, ForgeStage.FRAGMENT));
        assertEquals(10100, repository.loadPlayerResources().credits);
        assertEquals(2860, repository.loadPlayerResources().xp);

        repository.close();
        repository = new ForgeRepository(context);
        CampaignState restored = repository.loadCampaign("xmen", defaultTeam);
        assertEquals(3, restored.unlockedMission);
        assertEquals(selectedTeam, restored.teamIds);
        assertEquals(24, totalCount(ForgeStage.FRAGMENT));
        assertEquals(10100, repository.loadPlayerResources().credits);
        assertEquals(2860, repository.loadPlayerResources().xp);
    }

    @Test public void nineChapterMapUnlocksInOrderAndNeverDuplicatesRewards() {
        List<String> team = Arrays.asList("homem-aranha", "wolverine", "tocha-humana");
        CampaignState initial = repository.loadCampaign("rupture", team);
        assertEquals(1, initial.unlockedMission);
        for (int number = 1; number <= 9; number++) {
            if (number == 9) {
                try {
                    repository.completeMission("rupture", 9);
                    fail("Titan should require a complete Gauntlet");
                } catch (IllegalStateException expected) { }
                for (InfinityStone stone : InfinityStone.values()) {
                    repository.getWritableDatabase().execSQL("UPDATE inventory SET count=1 "
                            + "WHERE stone='" + stone.name() + "' AND stage='COMPLETE'");
                }
            }
            assertTrue(repository.completeMission("rupture", number));
            assertFalse(repository.completeMission("rupture", number));
            assertEquals(Math.min(9, number + 1),
                    repository.loadCampaign("rupture", team).unlockedMission);
            assertEquals(6, repository.loadRewardFragments("campaign:rupture:" + number).size());
        }
        assertEquals(36600, repository.loadPlayerResources().credits);
        assertEquals(11070, repository.loadPlayerResources().xp);
        assertEquals(24, totalCount(ForgeStage.FRAGMENT));
    }

    @Test public void battleTeamUsesOwnedCrossFactionCharactersAndEquippedVariants() {
        List<String> starters = Arrays.asList("homem-aranha", "wolverine", "tocha-humana");
        CampaignState saved = repository.saveTeam("xmen", starters, roster);
        assertEquals(starters, saved.teamIds);
        int originPower = GameRules.equippedTeamPower(roster, starters, repository);
        try {
            repository.saveTeam("xmen", Arrays.asList("wolverine", "jean-grey", "tocha-humana"), roster);
            fail("Unowned Jean Grey must not join the team");
        } catch (IllegalArgumentException expected) { }
        for (InfinityStone stone : InfinityStone.values()) {
            repository.getWritableDatabase().execSQL("UPDATE inventory SET count=1 "
                    + "WHERE stone='" + stone.name() + "' AND stage='COMPLETE'");
        }
        assertTrue(repository.activateGauntlet());
        assertTrue(repository.unlockNextVariant("wolverine", GameVariantTier.ASCENSION, roster));
        assertTrue(GameRules.equippedTeamPower(roster, starters, repository) > originPower);
    }

    @Test public void fantasticFourUsesItsOwnThreeRewardsOnceEach() {
        List<String> team = Arrays.asList("senhor-fantastico", "mulher-invisivel", "tocha-humana");
        repository.loadCampaign("fantastic-four", team);
        assertTrue(repository.completeMission("fantastic-four", 1));
        assertTrue(repository.completeMission("fantastic-four", 2));
        assertTrue(repository.completeMission("fantastic-four", 3));
        assertFalse(repository.completeMission("fantastic-four", 3));
        ForgeInventory inventory = repository.load();
        assertEquals(4, inventory.count(InfinityStone.POWER, ForgeStage.FRAGMENT));
        assertEquals(4, inventory.count(InfinityStone.REALITY, ForgeStage.FRAGMENT));
        assertEquals(4, inventory.count(InfinityStone.SOUL, ForgeStage.FRAGMENT));
        assertEquals(24, totalCount(ForgeStage.FRAGMENT));
        assertEquals(10600, repository.loadPlayerResources().credits);
        assertEquals(3060, repository.loadPlayerResources().xp);
    }

    private int totalCount(ForgeStage stage) {
        int total = 0;
        for (InfinityStone stone : InfinityStone.values()) total += repository.load().count(stone, stage);
        return total;
    }
}
