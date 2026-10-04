package com.erickbarbosa.rupturainfinita;

/** Telegraph-led local combat; team power and mission difficulty decide the result. */
final class LovableBattle {
    enum Choice { ATTACK, DEFEND, CONTROL }

    static final int MAX_ROUNDS = 6;
    private static final String[] INTENTS = {
            "Investida direta", "Defesa reforçada", "Fonte exposta",
            "Canalização", "Abertura instável", "Ofensiva final"
    };
    private static final Choice[] COUNTERS = {
            Choice.DEFEND, Choice.CONTROL, Choice.ATTACK,
            Choice.CONTROL, Choice.ATTACK, Choice.DEFEND
    };
    private final int teamPower;
    final BattleMission mission;
    final int maxBoss;
    int round;
    int boss;
    int team = 100;
    int charge = 0;
    boolean victory;
    boolean defeat;
    String feedback;
    int lastBossDamage;
    int lastTeamDamage;
    boolean lastCounter;

    LovableBattle(int teamPower) {
        this(teamPower, BattleMission.forMission("rupture", 1));
    }

    LovableBattle(int teamPower, BattleMission mission) {
        if (teamPower <= 0) throw new IllegalArgumentException("Team power must be positive");
        if (mission == null) throw new IllegalArgumentException("Mission is required");
        this.teamPower = teamPower;
        this.mission = mission;
        maxBoss = 88 + 12 * mission.difficulty;
        boss = maxBoss;
        feedback = mission.opponentName + " prepara o confronto. Leia o movimento e escolha a resposta.";
    }

    boolean canChoose() { return round < MAX_ROUNDS && !victory && !defeat; }
    boolean canSpecial() { return canChoose() && charge >= 60; }

    String intent() { return INTENTS[(round + mission.counterOffset) % MAX_ROUNDS]; }

    String warning() {
        switch (COUNTERS[(round + mission.counterOffset) % MAX_ROUNDS]) {
            case DEFEND: return mission.opponentName + " prepara uma investida. Proteja a equipe.";
            case CONTROL: return mission.opponentName + " canaliza uma defesa. Desestabilize-a.";
            default: return mission.opponentName + " expõe uma abertura. Ataque agora.";
        }
    }

    void choose(Choice choice) {
        if (choice == null || !canChoose()) throw new IllegalStateException("Battle choice unavailable");
        boolean counter = choice == COUNTERS[(round + mission.counterOffset) % MAX_ROUNDS];
        int rawDamage = choice == Choice.ATTACK ? (counter ? 28 : 10)
                : choice == Choice.CONTROL ? (counter ? 18 : 8) : 0;
        int threat = 9 + 3 * mission.difficulty;
        int rawTeamDamage = choice == Choice.DEFEND ? (counter ? Math.max(2, threat / 4)
                : Math.max(4, threat / 2)) : choice == Choice.ATTACK
                ? (counter ? threat + 2 : threat + 8)
                : (counter ? Math.max(3, threat / 2) : threat + 4);
        int gained = choice == Choice.DEFEND ? 8
                : choice == Choice.ATTACK ? (counter ? 20 : 12) : (counter ? 32 : 18);
        lastBossDamage = Math.round(rawDamage * powerMultiplier());
        lastTeamDamage = Math.max(1, Math.round(rawTeamDamage * 31f / teamPower));
        lastCounter = counter;
        boss = Math.max(0, boss - lastBossDamage);
        if (boss > 0) team = Math.max(0, team - lastTeamDamage);
        else lastTeamDamage = 0;
        charge = Math.min(100, charge + gained);
        round++;
        if (choice == Choice.DEFEND) feedback = "A equipe resistiu, mas precisa abrir caminho para vencer.";
        else if (counter) feedback = "Resposta certa: a equipe explorou a intenção anunciada!";
        else feedback = "O golpe teve pouco efeito e deixou a equipe exposta.";
        settle();
    }

    void special() {
        if (!canSpecial()) throw new IllegalStateException("Special unavailable");
        lastBossDamage = Math.max(1, Math.round((38 + (charge - 60) / 5f) * powerMultiplier()));
        boss = Math.max(0, boss - lastBossDamage);
        int threat = 9 + 3 * mission.difficulty;
        lastTeamDamage = boss > 0 ? Math.max(1, Math.round(threat * 31f / (2f * teamPower))) : 0;
        team = Math.max(0, team - lastTeamDamage);
        charge = 0;
        round++;
        feedback = boss == 0 ? "O especial coordenado encerrou o confronto!"
                : "O especial abriu uma brecha, mas " + mission.opponentName + " ainda resiste.";
        settle();
    }

    private float powerMultiplier() { return Math.max(0.65f, teamPower / 31f); }

    private void settle() {
        if (boss == 0 && team > 0) {
            victory = true;
            feedback = mission.opponentName + " caiu. A equipe venceu com suas decisões.";
        } else if (team == 0 || round >= MAX_ROUNDS) {
            defeat = true;
            feedback = "A equipe não superou " + mission.opponentName
                    + ". Ajuste o trio, variantes ou respostas e tente novamente.";
        }
    }
}
