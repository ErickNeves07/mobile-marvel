package com.erickbarbosa.rupturainfinita;

final class ForgePolicy {
    static final int MAX_COUNT = 999;
    static final int INPUT_COUNT = 2;
    static final int OUTPUT_COUNT = 1;
    static final int REWARD_SHARDS = 3;

    private ForgePolicy() { }

    static ForgeStage outputFor(ForgeStage input) {
        if (input == null || input == ForgeStage.COMPLETE) {
            throw new IllegalArgumentException("No merge is defined for this stage");
        }
        return input.next();
    }

    static void validateCount(int count) {
        if (count < 0 || count > MAX_COUNT) {
            throw new IllegalArgumentException("Inventory count must be between 0 and 999");
        }
    }
}
