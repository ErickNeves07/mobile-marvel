package com.erickbarbosa.rupturainfinita;

/** Authored four-round Magneto scene from the published Lovable prototype. */
final class LovableBattle {
    enum Choice { ATTACK, DEFEND, CONTROL }

    static final String[] INTENTS = {
            "Estilhaços orbitais", "Escudo polarizado",
            "Sobrecarga do núcleo", "Colapso de Cerebro"
    };
    static final String[] WARNINGS = {
            "Magneto comprime metal sobre a equipe.",
            "O campo magnético vai bloquear ataques diretos.",
            "A armadura se abre enquanto ele canaliza energia.",
            "A ressonância alcança sua frequência crítica."
    };
    private static final Choice[] COUNTERS = {
            Choice.DEFEND, Choice.CONTROL, Choice.ATTACK, Choice.CONTROL
    };
    private final int teamPower;

    LovableBattle(int teamPower) {
        if (teamPower <= 0) throw new IllegalArgumentException("Team power must be positive");
        this.teamPower = teamPower;
    }

    int round;
    int boss = 100;
    int team = 100;
    int charge = 20;
    boolean victory;
    boolean defeat;
    String feedback = "Magneto prepara o campo magnético. Escolha a resposta da equipe.";
    int lastBossDamage;
    int lastTeamDamage;
    boolean lastCounter;

    boolean canChoose() { return round < INTENTS.length && !victory && !defeat; }
    boolean canSpecial() { return round == INTENTS.length && charge == 100 && !victory && !defeat; }

    void choose(Choice choice) {
        if (choice == null || !canChoose()) throw new IllegalStateException("Battle choice unavailable");
        boolean counter = choice == COUNTERS[round];
        int bossDamage = choice == Choice.ATTACK ? (counter ? 28 : 19)
                : choice == Choice.CONTROL ? (counter ? 22 : 13) : (counter ? 16 : 9);
        int teamDamage = choice == Choice.DEFEND ? (counter ? 2 : 6)
                : counter ? 5 : 14;
        int gained = choice == Choice.CONTROL ? (counter ? 34 : 23) : (counter ? 23 : 15);
        bossDamage = Math.max(1, Math.round(bossDamage * teamPower / 31f));
        teamDamage = Math.max(1, Math.round(teamDamage
                * (1f + Math.max(0, 31 - teamPower) / 2f)));
        boss = Math.max(8, boss - bossDamage);
        team = Math.max(0, team - teamDamage);
        charge = round == INTENTS.length - 1 ? 100 : Math.min(100, charge + gained);
        lastBossDamage = bossDamage;
        lastTeamDamage = teamDamage;
        lastCounter = counter;
        if (choice == Choice.ATTACK) feedback = counter
                ? "A equipe rompeu a canalização!" : "O ataque acertou, mas deixou a equipe exposta.";
        else if (choice == Choice.DEFEND) feedback = counter
                ? "A equipe ergueu a barreira no instante exato!" : "A formação resistiu, sem abrir vantagem.";
        else feedback = counter
                ? "A equipe desfez a frequência do campo!" : "A mente de Magneto ofereceu resistência.";
        round++;
        if (team == 0) {
            defeat = true;
            feedback = "A equipe caiu diante de Magneto. Revise suas escolhas e tente novamente.";
        }
    }

    void special() {
        if (!canSpecial()) throw new IllegalStateException("Special unavailable");
        lastBossDamage = boss;
        lastTeamDamage = 0;
        boss = 0;
        charge = 0;
        victory = true;
        feedback = "A equipe atravessou o campo e silenciou a ressonância.";
    }
}
