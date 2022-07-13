package com.jegg.engine.core.tilemap;

import com.badlogic.gdx.math.Vector2;

public class PrimitiveChunk {
    public PrimitiveTilemap tilemap;
    public int num;
    private int width;
    private int[] tiles;

    public PrimitiveChunk(PrimitiveTilemap tilemap, int width, int num) {
        this.tilemap = tilemap;
        this.width = width;
        this.num = num;
        tiles = new int[width * width];
    }

    public int getTile(int x, int y) {
        return tiles[x + (y * width)];
    }

    public int getTile(int index){
        return tiles[index];
    }

    public void setTile(int x, int y, int value) {
        Vector2 pos = tilemap.ChunkNumToPosition(num);
        tilemap.onSetTile((int)pos.x + x, (int)pos.y + y, tiles[x + (y * width)], value);
        tiles[x + (y * width)] = value;
    }
}
