package com.erickbarbosa.rupturainfinita;

enum ForgeStage {
    SHARD,
    FRAGMENT,
    UNSTABLE_CORE,
    COMPLETE;

    ForgeStage next() {
        switch (this) {
            case SHARD: return FRAGMENT;
            case FRAGMENT: return UNSTABLE_CORE;
            case UNSTABLE_CORE: return COMPLETE;
            default: throw new IllegalStateException("Complete is terminal");
        }
    }
}
