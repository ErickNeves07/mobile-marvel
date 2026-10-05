package com.erickbarbosa.rupturainfinita;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class DeadpoolOfflineReplyTest {
    private static final String CONTEXT = "Equipe salva em ordem: Homem-Aranha, Wolverine, Tocha Humana. "
            + "Missão selecionada/próxima: Titã em Colapso contra Thanos em Titã.";

    @Test public void legacyPayloadStaysUnderOldSchemaLimitAndKeepsCurrentTeam() {
        String payload = DeadpoolPrompt.legacy("O que acha do meu time? " + "x".repeat(250), CONTEXT);
        assertTrue(payload.length() <= 300);
        assertTrue(payload.contains("Homem-Aranha"));
        assertTrue(payload.contains("Wolverine"));
    }

    @Test public void offlineThanosOpinionUsesContextWithoutDefaultingToMagneto() {
        String reply = DeadpoolOfflineReply.respond("O que acha do Thanos?", CONTEXT, 0);
        assertTrue(reply.contains("Thanos"));
        assertFalse(reply.contains("Magneto"));
    }

    @Test public void offlineTeamReplyCanNameTheSavedHeroesAndVaries() {
        String first = DeadpoolOfflineReply.respond("Qual é meu time?", CONTEXT, 0);
        String second = DeadpoolOfflineReply.respond("Qual é meu time?", CONTEXT, 1);
        assertTrue(first.contains("Homem-Aranha"));
        assertTrue(first.contains("Wolverine"));
        assertFalse(first.equals(second));
    }

    @Test public void generalOfflineReplyStillAnswersWithHumor() {
        String reply = DeadpoolOfflineReply.respond("Qual sua opinião sobre filmes?", "", 2);
        assertFalse(reply.isEmpty());
        assertTrue(reply.contains("responder"));
    }
}
