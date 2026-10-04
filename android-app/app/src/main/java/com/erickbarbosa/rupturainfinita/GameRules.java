package com.erickbarbosa.rupturainfinita;

import java.util.ArrayList;
import java.util.List;

final class GameRules {
    private GameRules() { }

    static String factionFeedback(GameCatalogCharacter target, GameCatalogCharacter guess) {
        if (target.groupId.equals(guess.groupId)) return "Mesmo grupo";
        return "Grupo diferente";
    }

    static String alphabeticalFeedback(GameCatalogCharacter target, GameCatalogCharacter guess) {
        int compare = guess.name.compareToIgnoreCase(target.name);
        if (compare == 0) return "Nome correto";
        return compare < 0 ? "Resposta depois na ordem alfabética" : "Resposta antes na ordem alfabética";
    }

    static List<GameCatalogCharacter> members(List<GameCatalogCharacter> roster, String groupId) {
        List<GameCatalogCharacter> result = new ArrayList<>();
        for (GameCatalogCharacter character : roster) if (groupId.equals(character.groupId)) result.add(character);
        return result;
    }

    static BattleResult battle(List<GameCatalogCharacter> roster, List<String> teamIds,
                               String groupId, int missionNumber) {
        if (roster == null || teamIds == null || groupId == null) throw new IllegalArgumentException("Battle input is required");
        if (teamIds.size() != 3 || teamIds.stream().distinct().count() != 3 || missionNumber < 1 || missionNumber > 3) {
            throw new IllegalArgumentException("Invalid team or mission");
        }
        int playerPower = teamPower(roster, teamIds, groupId);
        int enemyPower = missionNumber == 1 ? 17 : missionNumber == 2 ? 22 : 27;
        int turn = 0;
        while (playerPower > 0 && enemyPower > 0 && turn++ < 12) {
            enemyPower -= Math.max(1, playerPower / 8 + 2);
            if (enemyPower > 0) playerPower -= missionNumber + 2;
        }
        return new BattleResult(enemyPower <= 0, Math.max(0, playerPower), Math.max(0, enemyPower), turn);
    }

    static int teamPower(List<GameCatalogCharacter> roster, List<String> teamIds, String groupId) {
        if (roster == null || teamIds == null || groupId == null || teamIds.size() != 3
                || teamIds.stream().distinct().count() != 3)
            throw new IllegalArgumentException("Invalid faction team");
        int playerPower = 0;
        for (String id : teamIds) {
            GameCatalogCharacter character = null;
            for (GameCatalogCharacter candidate : roster) if (candidate.id.equals(id)) character = candidate;
            if (character == null || !groupId.equals(character.groupId)) throw new IllegalArgumentException("Invalid faction team");
            playerPower += authoredPower(id);
        }
        return playerPower;
    }

    private static int authoredPower(String id) {
        switch (id) {
            case "wolverine": case "hulk": case "thor": case "coisa": return 12;
            case "ciclope": case "mulher-invisivel": case "homem-de-ferro": case "jean-grey": return 10;
            case "professor-xavier": case "doutor-estranho": case "tocha-humana": case "senhor-fantastico": return 9;
            case "tempestade": case "noturno": case "fera": case "gambit":
            case "rocket-raccoon": return 8;
            default: return 8;
        }
    }
}

final class BattleResult {
    final boolean victory;
    final int remainingPlayerPower;
    final int remainingEnemyPower;
    final int turns;

    BattleResult(boolean victory, int remainingPlayerPower, int remainingEnemyPower, int turns) {
        this.victory = victory;
        this.remainingPlayerPower = remainingPlayerPower;
        this.remainingEnemyPower = remainingEnemyPower;
        this.turns = turns;
    }
}
