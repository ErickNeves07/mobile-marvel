package com.erickbarbosa.rupturainfinita;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Authored turn-based combat with three persistent fighters and a shared super meter. */
final class LovableBattle {
    enum Choice { ATTACK, DEFEND, CONTROL }

    /** Kept for source compatibility with old callers; it defines the telegraph cycle, not a cap. */
    @Deprecated static final int MAX_ROUNDS = 6;
    private static final int PATTERN_LENGTH = 6;
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

    static final class FighterSpec {
        final String id;
        final String name;
        final String tierName;
        final int[] stats;
        final String specialName;

        FighterSpec(String id, String name, String tierName, int[] stats, String specialName) {
            if (id == null || id.trim().isEmpty() || stats == null || stats.length != 4) {
                throw new IllegalArgumentException("Fighter identity and four game stats are required");
            }
            for (int stat : stats) if (stat <= 0) throw new IllegalArgumentException("Fighter stats must be positive");
            this.id = id;
            this.name = name == null || name.trim().isEmpty() ? id : name;
            this.tierName = tierName == null ? "ORIGEM" : tierName;
            this.stats = Arrays.copyOf(stats, stats.length);
            this.specialName = specialName == null ? BattleSpecial.nameForCharacter(id) : specialName;
        }

        int maxHealth() { return Math.max(60, Math.round(stats[0] / 9f)); }
        int attack() { return stats[1]; }
        int defense() { return stats[2]; }
        int speed() { return stats[3]; }
        int totalPower() { return VariantStats.total(stats); }
    }

    static final class Fighter {
        final FighterSpec spec;
        final int maxHealth;
        int health;

        Fighter(FighterSpec spec) {
            this.spec = spec;
            maxHealth = spec.maxHealth();
            health = maxHealth;
        }

        boolean alive() { return health > 0; }
    }

    static final class Summary {
        final int turns;
        final int damageDealt;
        final int damageReceived;
        final int attacks;
        final int defenses;
        final int controls;
        final int specials;
        final int counters;
        final List<String> knockedOut;
        final List<String> survivors;
        final List<String> specialNames;

        Summary(int turns, int damageDealt, int damageReceived, int attacks, int defenses,
                int controls, int specials, int counters, List<String> knockedOut,
                List<String> survivors, List<String> specialNames) {
            this.turns = turns;
            this.damageDealt = damageDealt;
            this.damageReceived = damageReceived;
            this.attacks = attacks;
            this.defenses = defenses;
            this.controls = controls;
            this.specials = specials;
            this.counters = counters;
            this.knockedOut = Collections.unmodifiableList(new ArrayList<>(knockedOut));
            this.survivors = Collections.unmodifiableList(new ArrayList<>(survivors));
            this.specialNames = Collections.unmodifiableList(new ArrayList<>(specialNames));
        }

        String toDisplayText() {
            StringBuilder result = new StringBuilder()
                    .append("Ações: ").append(turns)
                    .append(" · Dano no chefe: ").append(damageDealt)
                    .append(" · Dano recebido: ").append(damageReceived)
                    .append("\nAtaques ").append(attacks).append(" · Defesas ").append(defenses)
                    .append(" · Desestabilizações ").append(controls)
                    .append(" · Especiais ").append(specials)
                    .append(" · Contra-ataques certos ").append(counters);
            if (!knockedOut.isEmpty()) result.append("\nCaíram: ").append(join(knockedOut));
            if (!survivors.isEmpty()) result.append("\nEm pé: ").append(join(survivors));
            result.append("\nSuper usado: ").append(specialNames.isEmpty()
                    ? "nenhum" : join(specialNames));
            return result.toString();
        }

        private static String join(List<String> values) {
            StringBuilder joined = new StringBuilder();
            for (String value : values) {
                if (joined.length() > 0) joined.append(", ");
                joined.append(value);
            }
            return joined.toString();
        }
    }

    /** Legacy scalar constructor retained for existing debug/sample entry points. */
    LovableBattle(int teamPower) {
        this(teamPower, BattleMission.forMission("rupture", 1));
    }

    /** Legacy scalar constructor retained for existing debug/sample entry points. */
    LovableBattle(int teamPower, BattleMission mission) {
        this(legacyTeam(teamPower), mission);
    }

    private static List<FighterSpec> legacyTeam(int teamPower) {
        if (teamPower <= 0) throw new IllegalArgumentException("Team power must be positive");
        List<FighterSpec> specs = new ArrayList<>();
        String[] ids = {"homem-aranha", "wolverine", "tocha-humana"};
        String[] names = {"Homem-Aranha", "Wolverine", "Tocha Humana"};
        for (int i = 0; i < ids.length; i++) specs.add(new FighterSpec(ids[i], names[i], "ORIGEM",
                VariantStats.forVariant(ids[i], GameVariantTier.ORIGIN), null));
        return specs;
    }

    final BattleMission mission;
    final int teamPower;
    final int maxBoss;
    final List<Fighter> fighters = new ArrayList<>();
    int round;
    int boss;
    int team = 100; // Compatibility summary percentage for older view/test callers.
    int charge;
    boolean victory;
    boolean defeat;
    boolean replacementRequired;
    String feedback;
    String activeFighterId;
    int lastBossDamage;
    int lastTeamDamage;
    boolean lastCounter;
    boolean lastTrap;
    int attacks;
    int defenses;
    int controls;
    int specials;
    int counters;
    int totalDamageDealt;
    int totalDamageReceived;
    final List<String> usedSpecials = new ArrayList<>();

    LovableBattle(List<FighterSpec> team, BattleMission mission) {
        if (team == null || team.size() != 3 || mission == null)
            throw new IllegalArgumentException("A battle requires three fighters and a mission");
        java.util.HashSet<String> ids = new java.util.HashSet<>();
        int totalAttack = 0;
        int summedPower = 0;
        for (FighterSpec spec : team) {
            if (spec == null || !ids.add(spec.id)) throw new IllegalArgumentException("Fighters must be distinct");
            fighters.add(new Fighter(spec));
            totalAttack += spec.attack();
            summedPower += spec.totalPower();
        }
        this.mission = mission;
        teamPower = Math.max(1, Math.round(summedPower / 200f));
        int averageAttack = Math.round(totalAttack / 3f);
        maxBoss = BossBalance.hitPoints(150 + 26 * mission.difficulty
                + Math.round(averageAttack * .55f));
        boss = maxBoss;
        activeFighterId = fighters.get(0).spec.id;
        feedback = mission.opponentName + " prepara o confronto. Leia o movimento e escolha a resposta.";
        updateTeamPercent();
    }

    List<Fighter> fighters() { return Collections.unmodifiableList(fighters); }
    Fighter fighter(String id) {
        for (Fighter fighter : fighters) if (fighter.spec.id.equals(id)) return fighter;
        return null;
    }
    Fighter activeFighter() { return fighter(activeFighterId); }
    int livingFighters() {
        int count = 0;
        for (Fighter fighter : fighters) if (fighter.alive()) count++;
        return count;
    }

    boolean canChoose() { return !victory && !defeat && !replacementRequired && activeFighter() != null
            && activeFighter().alive(); }
    boolean canSpecial() { return canChoose() && charge >= 60; }

    void selectFighter(String id) {
        if (victory || defeat) throw new IllegalStateException("Battle is finished");
        Fighter selected = fighter(id);
        if (selected == null || !selected.alive()) throw new IllegalArgumentException("Fighter is unavailable");
        activeFighterId = id;
        replacementRequired = false;
        feedback = "";
    }

    String intent() { return INTENTS[patternIndex()]; }

    String warning() { return WARNINGS[mission.number - 1][patternIndex()]; }

    private int patternIndex() { return (round + mission.counterOffset) % PATTERN_LENGTH; }

    void choose(Choice choice) {
        if (choice == null || !canChoose()) throw new IllegalStateException("Battle choice unavailable");
        Fighter active = activeFighter();
        int pattern = patternIndex();
        boolean counter = choice == COUNTERS[pattern];
        if (counter) counters++;
        lastTrap = pattern == 4 && !counter;
        float attackScale = counter ? 2.15f : 1f;
        int baseAttack = Math.max(6, Math.round((active.spec.attack() * .8f
                + active.spec.speed() * .2f) / 34f));
        if (choice == Choice.ATTACK) {
            lastBossDamage = Math.max(1, Math.round(baseAttack * attackScale));
            attacks++;
        } else if (choice == Choice.CONTROL) {
            lastBossDamage = Math.max(1, Math.round(baseAttack * (counter ? 1.25f : .62f)));
            controls++;
        } else {
            lastBossDamage = 0;
            defenses++;
        }
        resolvePlayerAction(choice, counter);
    }

    void special() {
        if (!canSpecial()) throw new IllegalStateException("Special unavailable");
        Fighter active = activeFighter();
        String specialLabel = active.spec.name + " — " + active.spec.specialName;
        if (!usedSpecials.contains(specialLabel)) usedSpecials.add(specialLabel);
        lastBossDamage = Math.max(1, Math.round(37 + active.spec.attack() / 20f
                + (charge - 60) / 5f));
        specials++;
        resolvePlayerAction(null, false);
        charge = 0;
    }

    private void resolvePlayerAction(Choice choice, boolean counter) {
        Fighter active = activeFighter();
        lastCounter = counter;
        boss = Math.max(0, boss - lastBossDamage);
        totalDamageDealt += lastBossDamage;
        if (boss > 0) {
            int rawThreat = Math.max(3, 8 + Math.round(mission.difficulty * 1.55f)
                    - active.spec.defense() / 52);
            float mitigation = choice == Choice.DEFEND
                    ? (counter ? .38f : .58f)
                    : counter ? .82f : 1.18f;
            if (choice == null) mitigation = .92f;
            lastTeamDamage = BossBalance.damage(rawThreat, mitigation);
            active.health = Math.max(0, active.health - lastTeamDamage);
            totalDamageReceived += lastTeamDamage;
        } else {
            lastTeamDamage = 0;
        }
        if (choice == Choice.DEFEND) charge = Math.min(100, charge + 8);
        else if (choice == Choice.ATTACK) charge = Math.min(100, charge + (counter ? 20 : 12));
        else if (choice == Choice.CONTROL) charge = Math.min(100, charge + (counter ? 32 : 18));
        round++;
        if (active.health == 0) replacementRequired = livingFighters() > 0;

        if (lastTrap) feedback = "Era uma armadilha: " + mission.opponentName
                + " cercou o herói ativo; o impacto veio pelos flancos.";
        else if (choice == Choice.DEFEND && counter)
            feedback = active.spec.name + " ergueu a defesa no momento certo e conteve a investida.";
        else if (choice == Choice.DEFEND)
            feedback = active.spec.name + " suportou o impacto atrás do escudo. A abertura não se confirmou.";
        else if (counter)
            feedback = active.spec.name + (choice == Choice.ATTACK
                    ? " atravessou a abertura e atingiu " : " interrompeu o movimento de ")
                    + mission.opponentName + ".";
        else if (choice == Choice.CONTROL)
            feedback = active.spec.name + " desestabilizou o campo, mas " + mission.opponentName
                    + " ainda encontrou espaço para responder.";
        else if (choice == Choice.ATTACK)
            feedback = active.spec.name + " investiu contra a guarda; " + mission.opponentName
                    + " respondeu com força.";
        else feedback = active.spec.name + " usou " + active.spec.specialName + ".";
        settle();
        updateTeamPercent();
    }

    private void settle() {
        if (boss == 0) {
            victory = true;
            replacementRequired = false;
            feedback = mission.opponentName + " caiu. A equipe venceu após " + round + " ações.";
        } else if (livingFighters() == 0) {
            defeat = true;
            replacementRequired = false;
            feedback = "Os três heróis caíram. " + mission.opponentName
                    + " manteve o controle do campo. Ajuste a estratégia e tente novamente.";
        }
    }

    private void updateTeamPercent() {
        int current = 0;
        int maximum = 0;
        for (Fighter fighter : fighters) {
            current += fighter.health;
            maximum += fighter.maxHealth;
        }
        team = maximum == 0 ? 0 : Math.round(current * 100f / maximum);
    }

    Summary summary() {
        List<String> knockedOut = new ArrayList<>();
        List<String> survivors = new ArrayList<>();
        for (Fighter fighter : fighters) {
            (fighter.alive() ? survivors : knockedOut).add(fighter.spec.name);
        }
        return new Summary(round, totalDamageDealt, totalDamageReceived, attacks, defenses,
                controls, specials, counters, knockedOut, survivors, usedSpecials);
    }
}
