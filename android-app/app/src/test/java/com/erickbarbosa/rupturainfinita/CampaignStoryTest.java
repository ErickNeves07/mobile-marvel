package com.erickbarbosa.rupturainfinita;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class CampaignStoryTest {
    @Test public void chamberOpeningExplainsBothRolesAndKeepsThePlayerInControl() {
        CampaignStory.Scene scene = CampaignStory.chamberOpening();
        assertEquals(3, scene.lines.size());
        assertEquals("senhor-fantastico", scene.lines.get(0).speakerId);
        assertEquals("doutor-estranho", scene.lines.get(1).speakerId);
        assertTrue(scene.lines.get(0).text.contains("mapear a assinatura"));
        assertTrue(scene.lines.get(1).text.contains("selos"));
        assertTrue(scene.lines.get(2).text.contains("você escolhe"));
    }

    @Test public void allNinePostBattleScenesHaveDialogueAndOnlyLastIsFinale() {
        for (int chapter = 1; chapter <= 9; chapter++) {
            CampaignStory.Scene scene = CampaignStory.afterMission(chapter);
            assertFalse(scene.lines.isEmpty());
            assertEquals(chapter == 9, scene.finale);
        }
    }
}
