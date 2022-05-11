package com.jegg.spacesim.core.ecs;

import com.badlogic.gdx.utils.Array;

public class Chunk<T> {
    //public int originX, originY;
    private int width;
    protected final Array<T> tiles;

    public Chunk(int width) {
        this.width = width;
        //this.originX = width / 2;
        //this.originY = width / 2;
        tiles = new Array<>(width * width);
        tiles.setSize(width * width);
    }

    public T getTile(int x, int y) {
        return tiles.get(x + (y * width));
    }

    public void setTile(int x, int y, T value) {
        tiles.set(x + (y * width), value);
    }
}
