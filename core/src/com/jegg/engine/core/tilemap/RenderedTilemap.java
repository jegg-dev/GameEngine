package com.jegg.engine.core.tilemap;

import com.badlogic.gdx.math.Vector2;

public abstract class RenderedTilemap extends PrimitiveTilemap {
    public static boolean ShowChunkOutlines;

    public int viewDist;

    public RenderedTilemap(int mapWidthInChunks, int chunkWidth, float tileWidth, int viewDist){
        super(mapWidthInChunks, chunkWidth, tileWidth);
        this.viewDist = viewDist;
    }

    public abstract void update(Vector2 chunkPosition);
}
