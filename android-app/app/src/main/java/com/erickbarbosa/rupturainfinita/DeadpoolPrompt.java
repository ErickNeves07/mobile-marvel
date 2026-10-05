package com.erickbarbosa.rupturainfinita;

/** Bounded compatibility payload for an older Render deployment. */
final class DeadpoolPrompt {
    private DeadpoolPrompt() { }

    static String legacy(String question, String gameContext) {
        String cleanQuestion = question == null ? "" : question.trim();
        String cleanFacts = compactContext(gameContext);
        String factsLabel = "Estado do meu jogo: ";
        int questionLimit = Math.min(cleanQuestion.length(), 150);
        int remainingFacts = Math.max(0, 300 - questionLimit - factsLabel.length()
                - (questionLimit == 0 ? 0 : 1));
        String questionPart = cleanQuestion.substring(0, questionLimit);
        String factsPart = cleanFacts.substring(0, Math.min(cleanFacts.length(), remainingFacts));
        return questionPart + (questionPart.isEmpty() ? "" : " ") + factsLabel + factsPart;
    }

    private static String compactContext(String context) {
        if (context == null || context.trim().isEmpty()) return "estado não disponível";
        StringBuilder compact = new StringBuilder();
        append(compact, "equipe", extract(context, "Equipe salva em ordem: ", "."));
        append(compact, "batalha", extract(context, "Batalha atual: ", "."));
        append(compact, "missão", extract(context, "Missão selecionada/próxima: ", "."));
        append(compact, "recursos", extract(context, "Recursos: ", "."));
        if (compact.length() == 0) {
            compact.append(context.trim(), 0, Math.min(context.trim().length(), 120));
        }
        return compact.toString();
    }

    private static void append(StringBuilder target, String label, String value) {
        if (value.isEmpty()) return;
        if (target.length() > 0) target.append("; ");
        target.append(label).append(':').append(value);
    }

    private static String extract(String source, String prefix, String endMarker) {
        int start = source.indexOf(prefix);
        if (start < 0) return "";
        start += prefix.length();
        int end = source.indexOf(endMarker, start);
        if (end < 0) end = Math.min(source.length(), start + 90);
        return source.substring(start, Math.min(end, start + 90)).trim();
    }
}
