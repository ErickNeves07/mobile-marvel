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
    private static final String[][] WARNINGS = {
            {"Capangas avançam em bloco; a equipe sente o peso da investida.",
                    "Seguranças fecham o acesso ao Rei do Crime; a formação pode ser desfeita.",
                    "Uma passagem se abre entre os capangas por um instante.",
                    "O Rei do Crime coordena uma nova onda pelo rádio.",
                    "O corredor parece livre, mas passos ecoam dos dois lados.",
                    "Um reforço pesado investe contra a linha da equipe."},
            {"Drones de Ultron mergulham em direção à equipe.",
                    "Um escudo de drones se fecha ao redor do núcleo de comando.",
                    "Uma placa do núcleo se desloca e deixa um ponto vulnerável.",
                    "Ultron sincroniza suas unidades para uma descarga.",
                    "Luzes do complexo apagam por um segundo; drones mudam de posição.",
                    "Uma máquina de assalto desce do teto sobre a equipe."},
            {"Unidades invasoras atravessam as defesas de Wakanda.",
                    "Uma barreira tecnológica cobre a ameaça no centro do campo.",
                    "O emissor da barreira pulsa sem proteção por um instante.",
                    "A ameaça tecnológica concentra energia nos dispositivos próximos.",
                    "Sinais falsos surgem nas laterais da arena.",
                    "Uma onda de choque percorre o solo até a equipe."},
            {"Sombras da Dimensão Espelhada avançam em linha reta.",
                    "Dormammu dobra o espaço ao redor de si, dificultando a aproximação.",
                    "Um reflexo falha e revela uma passagem breve.",
                    "Dormammu reúne energia nas superfícies espelhadas.",
                    "Reflexos da equipe aparecem onde não deveriam estar.",
                    "O chão espelhado se rompe sob a formação."},
            {"As forças de Ronan avançam pelo convés de Knowhere.",
                    "Ronan se protege atrás de uma guarda compacta.",
                    "A guarda se desloca e deixa um flanco aberto.",
                    "Ronan concentra poder antes de atingir o convés.",
                    "Um corredor vazio parece oferecer passagem fácil.",
                    "Destroços caem do alto na direção da equipe."},
            {"Magneto lança destroços em direção à equipe.",
                    "Placas de metal se erguem ao redor de Magneto.",
                    "Uma lacuna surge entre as placas suspensas.",
                    "Magneto reúne os destroços para um golpe maior.",
                    "O metal se afasta de repente, deixando os flancos abertos.",
                    "Uma massa de metal desaba sobre a formação."},
            {"Criaturas da Zona Negativa avançam sob o comando de Annihilus.",
                    "Annihilus se abriga atrás de uma nuvem de criaturas.",
                    "A nuvem se dispersa e expõe seu centro por um instante.",
                    "Annihilus concentra a ofensiva em um único ponto.",
                    "O ar da Zona Negativa se aquieta de forma suspeita.",
                    "Uma onda de criaturas mergulha sobre a equipe."},
            {"Autômatos de Latveria investem contra a equipe.",
                    "Doutor Destino fecha a guarda atrás de seus escudos.",
                    "Uma falha breve aparece entre os escudos.",
                    "Doutor Destino canaliza energia para seus autômatos.",
                    "O salão fica silencioso enquanto os autômatos se afastam.",
                    "Uma descarga percorre o piso da cidadela."},
            {"Thanos avança pela superfície instável de Titã.",
                    "Uma guarda de energia protege Thanos da equipe.",
                    "A energia oscila e deixa uma abertura passageira.",
                    "Thanos concentra uma nova descarga.",
                    "O campo fica imóvel por um instante longo demais.",
                    "Fragmentos de Titã caem na direção da equipe."}
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
    boolean lastTrap;

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
        return WARNINGS[mission.number - 1][(round + mission.counterOffset) % MAX_ROUNDS];
    }

    void choose(Choice choice) {
        if (choice == null || !canChoose()) throw new IllegalStateException("Battle choice unavailable");
        int pattern = (round + mission.counterOffset) % MAX_ROUNDS;
        boolean counter = choice == COUNTERS[pattern];
        lastTrap = pattern == 4 && !counter;
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
        if (lastTrap) feedback = mission.opponentName.equals("Ultron")
                ? "Era uma armadilha: unidades de Ultron cercaram a equipe. Os flancos ficaram expostos."
                : "Era uma armadilha: o cerco veio pelos flancos e a equipe sofreu o impacto.";
        else if (choice == Choice.DEFEND && counter)
            feedback = "A defesa conteve a investida e reduziu o dano. O inimigo recuou por um instante.";
        else if (choice == Choice.DEFEND)
            feedback = "A defesa reduziu o impacto, mas n\u00e3o abriu uma brecha. Observe o pr\u00f3ximo movimento.";
        else if (counter)
            feedback = choice == Choice.ATTACK
                    ? "O golpe atravessou a abertura e atingiu o chefe. A vantagem pode durar pouco."
                    : "O controle interrompeu a canaliza\u00e7\u00e3o e atingiu o chefe. A vantagem pode durar pouco.";
        else
            feedback = choice == Choice.ATTACK
                    ? "O ataque encontrou a barreira e a equipe sofreu dano. Repare no movimento anunciado."
                    : "O controle n\u00e3o interrompeu o ataque; a equipe sentiu o impacto. Repare na inten\u00e7\u00e3o do chefe.";
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
