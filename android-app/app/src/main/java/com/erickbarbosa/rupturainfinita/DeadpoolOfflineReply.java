package com.erickbarbosa.rupturainfinita;

import java.text.Normalizer;
import java.util.Locale;

/** Short authored replies used when the narrative service is unavailable. */
final class DeadpoolOfflineReply {
    private static final String[] GENERAL = {
            "Eu tinha uma resposta brilhante. Ela foi capturada por direitos autorais e um guaxinim.",
            "Minha opiniao profissional: dramatico, caro e provavelmente sem manual. Entao gostei.",
            "Posso responder. So preciso que alguem avise meu roteirista, que saiu para comprar chimichangas.",
            "Isso merece uma resposta inteligente. Vou esperar ela chegar e improvisar enquanto isso."
    };

    private DeadpoolOfflineReply() { }

    static String respond(String question, String gameContext, int turn) {
        String q = normalize(question);
        String context = gameContext == null ? "" : gameContext;
        if (containsAny(q, "thanos", "tita")) {
            return pick(new String[]{
                    "Thanos? Um problema de escala cosmica com confianca de quem nunca leu os termos de uso.",
                    "Thanos e o tipo de sujeito que transforma uma conversa dificil em evento de campanha.",
                    "Minha opiniao sobre Thanos: otima presenca de palco, pessimo historico de vizinhanca."
            }, turn);
        }
        if (containsAny(q, "equipe", "time atual", "meu time", "herois")) {
            String team = between(context, "Equipe salva em ordem: ", ".");
            if (team.isEmpty()) team = between(context, "Equipe atual: ", ".");
            if (!team.isEmpty()) return "Seu time salvo: " + team + ". " + pick(new String[]{
                    "Eu aprovo; entrada dramatica recomendada.",
                    "Boa escalacao. Vou fingir que fui eu quem montou.",
                    "Tem potencial; meu advogado quer os creditos."
            }, turn);
            return "Ainda nao achei um trio salvo no meu roteiro. Monte a equipe e eu faco a critica gratuita.";
        }
        if (containsAny(q, "campanha", "batalha", "missao", "progresso")) {
            String mission = between(context, "Missao selecionada/proxima: ", ".");
            if (mission.isEmpty()) mission = between(context, "Batalha atual: ", ".");
            if (!mission.isEmpty()) return "No seu roteiro consta: " + mission
                    + ". Eu chamaria isso de planejamento; meu terapeuta chama de repeticao.";
            return "Seu proximo capitulo ainda esta fora do meu roteiro local. A IA volta quando o backend responder.";
        }
        if (containsAny(q, "piada", "engrac", "humor")) {
            return pick(new String[]{
                    "Fui comprar uma Manopla usada. O vendedor disse que vinha com seis parcelas.",
                    "O multiverso tem infinitas versoes minhas. Em nenhuma eu terminei a papelada.",
                    "Meu plano de contingencia tem um plano de contingencia. Os dois sou eu improvisando."
            }, turn);
        }
        return pick(GENERAL, turn);
    }

    private static String between(String source, String start, String end) {
        int from = source.indexOf(start);
        if (from < 0) return "";
        from += start.length();
        int to = source.indexOf(end, from);
        if (to < 0) to = Math.min(source.length(), from + 100);
        return source.substring(from, Math.min(to, from + 100)).trim();
    }

    private static boolean containsAny(String text, String... needles) {
        for (String needle : needles) if (text.contains(normalize(needle))) return true;
        return false;
    }

    private static String normalize(String value) {
        if (value == null) return "";
        return Normalizer.normalize(value.toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
    }

    private static String pick(String[] choices, int turn) {
        return choices[Math.floorMod(turn, choices.length)];
    }
}
