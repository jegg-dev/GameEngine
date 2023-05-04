package com.jegg.game.world;

import java.util.ArrayList;

public class IndexLevelPair {
    public int index;
    public float minLevel, maxLevel;

    public IndexLevelPair(int index, float minLevel, float maxLevel){
        this.index = index;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }

    public static Integer[] FillRange(IndexLevelPair[] pairs, int rangeSize) {
        ArrayList<Integer> list = new ArrayList<>();
        float halfRange = (float)rangeSize / 2;
        for (int i = 0; i < rangeSize; i++) {
            for (IndexLevelPair p : pairs) {
                if ((i - halfRange) / halfRange >= p.minLevel && (i - halfRange) / halfRange < p.maxLevel) {
                    list.add(p.index);
                    break;
                }
            }
            if (list.size() != i + 1)
                list.add(0);
        }

        return list.toArray(new Integer[0]);
    }
}
