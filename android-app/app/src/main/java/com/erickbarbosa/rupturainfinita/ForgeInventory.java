package com.erickbarbosa.rupturainfinita;

import java.util.EnumMap;
import java.util.Map;

final class ForgeInventory {
    private final Map<InfinityStone, Map<ForgeStage, Integer>> counts;

    ForgeInventory(Map<InfinityStone, Map<ForgeStage, Integer>> source) {
        counts = new EnumMap<>(InfinityStone.class);
        for (InfinityStone stone : InfinityStone.values()) {
            Map<ForgeStage, Integer> stages = new EnumMap<>(ForgeStage.class);
            for (ForgeStage stage : ForgeStage.values()) {
                int count = source.containsKey(stone) && source.get(stone).containsKey(stage)
                        ? source.get(stone).get(stage) : 0;
                ForgePolicy.validateCount(count);
                stages.put(stage, count);
            }
            counts.put(stone, stages);
        }
    }

    int count(InfinityStone stone, ForgeStage stage) {
        return counts.get(stone).get(stage);
    }
}
