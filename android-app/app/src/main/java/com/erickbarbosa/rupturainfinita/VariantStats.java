package com.erickbarbosa.rupturainfinita;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Game-authored values mirrored from the published characters-BL6XEGoz.js. */
final class VariantStats {
    private static final Map<String, int[]> BASE;
    static {
        Map<String, int[]> values = new LinkedHashMap<>();
        values.put("homem-de-ferro", new int[]{820, 310, 240, 190});
        values.put("capitao-america", new int[]{980, 250, 330, 200});
        values.put("thor", new int[]{1040, 360, 290, 210});
        values.put("hulk", new int[]{1320, 400, 250, 140});
        values.put("feiticeira-escarlate", new int[]{760, 390, 200, 230});
        values.put("pantera-negra", new int[]{880, 300, 280, 320});
        values.put("homem-aranha", new int[]{720, 280, 210, 380});
        values.put("doutor-estranho", new int[]{800, 340, 240, 220});
        values.put("wolverine", new int[]{1010, 370, 260, 260});
        values.put("ciclope", new int[]{840, 330, 230, 240});
        values.put("jean-grey", new int[]{790, 410, 200, 250});
        values.put("professor-xavier", new int[]{680, 180, 300, 150});
        values.put("senhor-fantastico", new int[]{760, 240, 300, 200});
        values.put("mulher-invisivel", new int[]{820, 260, 350, 210});
        values.put("tocha-humana", new int[]{700, 350, 180, 340});
        values.put("coisa", new int[]{1240, 320, 360, 130});
        values.put("rocket-raccoon", new int[]{640, 320, 190, 300});
        values.put("groot", new int[]{1180, 270, 340, 150});
        values.put("surfista-prateado", new int[]{900, 420, 260, 360});
        values.put("loki", new int[]{740, 300, 220, 280});
        values.put("deadpool", new int[]{950, 380, 210, 290});
        BASE = Collections.unmodifiableMap(values);
    }

    private VariantStats() { }

    static int count() { return BASE.size(); }

    static int[] forVariant(String id, GameVariantTier tier) {
        int[] base = BASE.get(id);
        if (base == null || tier == null) throw new IllegalArgumentException("Unknown variant");
        double factor;
        switch (tier) {
            case ORIGIN: factor = 1.0; break;
            case ASCENSION: factor = 1.08; break;
            case LEGENDARY: factor = 1.17; break;
            case MULTIVERSAL: factor = 1.28; break;
            case INFINITY: factor = 1.4; break;
            default: throw new IllegalArgumentException("Unknown tier");
        }
        int[] scaled = new int[base.length];
        for (int i = 0; i < base.length; i++) scaled[i] = (int) Math.floor(base[i] * factor + 0.5);
        return scaled;
    }

    static int total(int[] stats) {
        int result = 0;
        for (int value : stats) result += value;
        return result;
    }
}
