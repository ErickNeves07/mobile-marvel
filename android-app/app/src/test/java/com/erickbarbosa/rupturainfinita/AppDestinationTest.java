package com.erickbarbosa.rupturainfinita;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class AppDestinationTest {
    @Test
    public void destinationsFollowApprovedNavigationOrder() {
        assertArrayEquals(new AppDestination[] {
                AppDestination.NEXUS,
                AppDestination.CAMPAIGNS,
                AppDestination.FORGE,
                AppDestination.COLLECTION,
                AppDestination.DEADPOOL
        }, AppDestination.values());
    }

    @Test
    public void forgeIsTheInitialDestination() {
        assertEquals(AppDestination.FORGE, AppDestination.initial());
    }

    @Test
    public void nexusShortcutsPointToEveryOtherDestinationInOrder() {
        assertArrayEquals(new AppDestination[] {
                AppDestination.CAMPAIGNS,
                AppDestination.FORGE,
                AppDestination.COLLECTION,
                AppDestination.DEADPOOL
        }, AppDestination.nexusShortcuts());
    }

    @Test
    public void everyDestinationHasLocalContentAndAnIcon() {
        for (AppDestination destination : AppDestination.values()) {
            assertTrue(destination.name(), destination.descriptionRes > 0);
            assertTrue(destination.name(), destination.iconRes > 0);
        }
    }

}
