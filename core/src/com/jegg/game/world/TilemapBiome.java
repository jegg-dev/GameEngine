package com.jegg.game.world;

import com.badlogic.gdx.math.MathUtils;
import com.jegg.engine.Random;

public class TilemapBiome {
    public static final IndexLevelPair[] DefaultTileLevels = {
            new IndexLevelPair(2, 0.5f, 0.55f),
            new IndexLevelPair(3, 0.55f, 0.6f),
            new IndexLevelPair(4, 0.6f, 0.65f),
            new IndexLevelPair(5, 0.65f, 0.7f)
    };

    private Integer[] tileCache;

    public float amplitude = 25.0f;

    public TilemapBiome(IndexLevelPair[] tileLevels, float amplitude){
        tileCache = IndexLevelPair.FillRange(tileLevels, 2000);
        this.amplitude = amplitude;
    }

    public int GetTile(int x, int y){
        float z = Random.Perlin((float) (x) / amplitude, (float) (y) / amplitude, 0.0f);
        return tileCache[(int) (MathUtils.clamp(z, -1.0f, 1.0f) * 1000) + 999];
    }
}
