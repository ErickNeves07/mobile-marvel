package com.erickbarbosa.rupturainfinita;

import java.util.List;

/** Authored hero moves; never infer powers from Comic Vine or AI text. */
final class BattleSpecial {
    private BattleSpecial() { }

    static String nameForTeam(List<String> teamIds) {
        if (teamIds == null || teamIds.isEmpty()) throw new IllegalArgumentException("Team is required");
        if (teamIds.contains("homem-aranha") && teamIds.contains("wolverine")
                && teamIds.contains("tocha-humana")) return "Teias, Garras e Chamas";
        return nameForCharacter(teamIds.get(0));
    }

    static String nameForCharacter(String characterId) {
        if (characterId == null) throw new IllegalArgumentException("Character is required");
        switch (characterId) {
            case "homem-de-ferro": return "Salva de Repulsores";
            case "capitao-america": return "Investida do Escudo";
            case "thor": return "Tempestade de Mjolnir";
            case "hulk": return "Impacto Gama";
            case "feiticeira-escarlate": return "Onda do Caos";
            case "pantera-negra": return "Garras de Vibranium";
            case "homem-aranha": return "Rajada de Teias";
            case "doutor-estranho": return "Círculo Místico";
            case "wolverine": return "Fúria de Adamantium";
            case "ciclope": return "Raio Óptico";
            case "jean-grey": return "Explosão Telecinética";
            case "professor-xavier": return "Pulso Psíquico";
            case "senhor-fantastico": return "Golpe Elástico";
            case "mulher-invisivel": return "Explosão de Campo";
            case "tocha-humana": return "Nova de Fogo";
            case "coisa": return "Martelo Rochoso";
            case "rocket-raccoon": return "Barragem de Plasma";
            case "groot": return "Raízes do Cosmos";
            case "surfista-prateado": return "Onda Cósmica";
            case "loki": return "Ilusão de Asgard";
            case "deadpool": return "Caos de Espadas";
            default: throw new IllegalArgumentException("Unknown team leader");
        }
    }
}
