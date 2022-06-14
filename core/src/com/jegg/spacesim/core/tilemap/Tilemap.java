package com.jegg.spacesim.core.tilemap;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class Tilemap<T> implements Component {
    public Array<Chunk<T>> chunks;
    private int mapWidthInChunks, chunkWidth;
    private float tileWidth;

    public Tilemap(int chunkDist, int chunkWidth, float tileWidth){
        this.mapWidthInChunks = chunkDist;
        this.chunkWidth = chunkWidth;
        this.tileWidth = tileWidth;
        chunks = new Array<>(chunkDist * chunkDist);
        chunks.setSize(chunkDist * chunkDist);
    }

    private Chunk<T> getChunk(int x, int y){
        int chunkNum = (x / chunkWidth + (mapWidthInChunks / 2)) + ((y / chunkWidth + (mapWidthInChunks / 2)) * mapWidthInChunks);

        if(chunks.get(chunkNum) == null){
            chunks.set(chunkNum, new Chunk<T>(chunkWidth));
        }
        return chunks.get(chunkNum);
    }

    public T getTile(int x, int y){
        Vector2 chunkPos = TileToChunkPosition(x, y);
        Chunk<T> chunk = getChunk((int)chunkPos.x, (int)chunkPos.y);
        return chunk.getTile(x - (int)chunkPos.x, y - (int)chunkPos.y);
    }

    public T getTile(Vector3 worldPos){
        Vector2 tilePos = WorldToTilePosition(worldPos);
        return getTile((int)tilePos.x, (int)tilePos.y);
    }

    public void setTile(int x, int y, T value){
        Vector2 chunkPos = TileToChunkPosition(x, y);
        Chunk<T> chunk = getChunk((int)chunkPos.x, (int)chunkPos.y);
        chunk.setTile(x - (int)chunkPos.x, y - (int)chunkPos.y, value);
    }

    public void setTile(Vector3 worldPos, T value){
        Vector2 tilePos = WorldToTilePosition(worldPos);
        setTile((int)tilePos.x, (int)tilePos.y, value);
    }

    //Placeholder for abstraction
    public void setTile(int x, int y){
    }

    public int getChunkWidth(){
        return chunkWidth;
    }

    public int getMapWidthInChunks(){
        return mapWidthInChunks;
    }

    public float getTileWidth(){
        return tileWidth;
    }

    public Vector2 WorldToTilePosition(Vector3 pos){
        return new Vector2(MathUtils.floor(pos.x / tileWidth), MathUtils.floor(pos.y / tileWidth));
    }

    public Vector2 TileToChunkPosition(Vector2 pos){
        return new Vector2(CustomRound(pos.x / (chunkWidth / 2) * 0.5f), CustomRound(pos.y / (chunkWidth / 2) * 0.5f)).scl(chunkWidth);
    }

    public Vector2 TileToChunkPosition(int x, int y){
        return new Vector2(CustomRound((float)x / (chunkWidth / 2) * 0.5f), CustomRound((float)y / (chunkWidth / 2) * 0.5f)).scl(chunkWidth);
    }

    public static int CustomRound(float value){
        /*float v = value - MathUtils.floor(value);
        if (v >= 0.5f)
        {
            return MathUtils.ceil(value);
        }
        else if (v > 0)
        {
            return MathUtils.floor(value);
        }
        else if(v < -0.5f)
        {
            return MathUtils.floor(value);
        }
        else
        {
            return MathUtils.ceil(value);
        }*/
        return MathUtils.floor(value);
    }
}