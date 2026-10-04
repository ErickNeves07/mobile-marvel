package com.erickbarbosa.rupturainfinita;

/** Persisted game currencies, separate from editorial character data. */
final class PlayerResources {
    final long credits;
    final long xp;

    PlayerResources(long credits, long xp) {
        this.credits = credits;
        this.xp = xp;
    }
}
