package com.erickbarbosa.rupturainfinita;

/** Stone names for the local informational Forge preview; this type has no inventory state. */
enum InfinityStone {
    SPACE(R.string.stone_space),
    MIND(R.string.stone_mind),
    REALITY(R.string.stone_reality),
    POWER(R.string.stone_power),
    TIME(R.string.stone_time),
    SOUL(R.string.stone_soul);

    final int labelRes;

    InfinityStone(int labelRes) {
        this.labelRes = labelRes;
    }
}
