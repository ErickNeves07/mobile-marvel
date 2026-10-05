package com.erickbarbosa.rupturainfinita;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class DeadpoolPromptTest {
    @Test public void legacyRequestCarriesQuestionAndBoundedGameFacts() {
        String request = DeadpoolPrompt.legacy("O que acha do Thanos?",
                "Missão Titã em Colapso contra Thanos. Equipe: Homem-Aranha, Wolverine e Tocha Humana."
                        + "x".repeat(1_000));
        assertTrue(request.length() <= 300);
        assertTrue(request.contains("O que acha do Thanos?"));
        assertTrue(request.contains("Titã em Colapso"));
        assertTrue(request.contains("Homem-Aranha"));
        assertFalse(request.contains("magneto"));
    }
}
