package com.erickbarbosa.rupturainfinita;

/** Small authored tuning applied uniformly to campaign bosses. */
final class BossBalance {
    private static final double HP_SCALE = 1.06d;
    private static final double DAMAGE_SCALE = 1.08d;

    private BossBalance() { }

    static int hitPoints(int base) {
        if (base < 1) throw new IllegalArgumentException("Boss HP must be positive");
        return (int) Math.ceil(base * HP_SCALE);
    }

    static int damage(int rawThreat, float mitigation) {
        if (rawThreat < 0 || mitigation < 0f) throw new IllegalArgumentException("Invalid boss damage");
        int mitigated = Math.round(rawThreat * mitigation);
        return Math.max(2, (int) Math.ceil(mitigated * DAMAGE_SCALE));
    }
}
