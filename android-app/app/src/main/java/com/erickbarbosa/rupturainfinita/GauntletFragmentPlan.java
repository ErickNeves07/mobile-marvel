package com.erickbarbosa.rupturainfinita;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/** Fragments needed to make one complete stone with the player's existing 2:1 materials. */
final class GauntletFragmentPlan {
    private GauntletFragmentPlan() { }

    static int missing(int shards, int fragments, int nuclei, int complete) {
        if (shards < 0 || fragments < 0 || nuclei < 0 || complete < 0) {
            throw new IllegalArgumentException("Inventory cannot be negative");
        }
        if (complete > 0) return 0;
        long availableFragmentUnits = fragments + 2L * nuclei + shards / 2L;
        return (int) Math.max(0L, 4L - availableFragmentUnits);
    }

    static Map<InfinityStone, Integer> forInventory(ForgeInventory inventory) {
        EnumMap<InfinityStone, Integer> result = new EnumMap<>(InfinityStone.class);
        for (InfinityStone stone : InfinityStone.values()) {
            result.put(stone, missing(inventory.count(stone, ForgeStage.SHARD),
                    inventory.count(stone, ForgeStage.FRAGMENT),
                    inventory.count(stone, ForgeStage.UNSTABLE_CORE),
                    inventory.count(stone, ForgeStage.COMPLETE)));
        }
        return Collections.unmodifiableMap(result);
    }
}
