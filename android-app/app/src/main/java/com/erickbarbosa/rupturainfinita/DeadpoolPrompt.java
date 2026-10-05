package com.erickbarbosa.rupturainfinita;

/** Bounded compatibility payload for an older Render deployment. */
final class DeadpoolPrompt {
    private DeadpoolPrompt() { }

    static String legacy(String question, String gameContext) {
        String cleanQuestion = question == null ? "" : question.trim();
        String cleanFacts = gameContext == null ? "" : gameContext.trim();
        int questionLimit = Math.min(cleanQuestion.length(), 125);
        String prompt = cleanQuestion.substring(0, questionLimit);
        String prefix = prompt.isEmpty() ? "" : prompt + " ";
        String factsLabel = "Estado do meu jogo: ";
        int factsLimit = Math.max(0, 300 - prefix.length() - factsLabel.length());
        return prefix + factsLabel + cleanFacts.substring(0, Math.min(cleanFacts.length(), factsLimit));
    }
}
