package com.jegg.engine.tilemap;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class PrimitiveTilemap implements Component {
    public PrimitiveChunk[] chunks;
    private int mapWidthInChunks, chunkWidth;
    private float tileWidth;

    public PrimitiveTilemap(int mapWidthInChunks, int chunkWidth, float tileWidth){
        this.mapWidthInChunks = mapWidthInChunks;
        this.chunkWidth = chunkWidth;
        this.tileWidth = tileWidth;
        chunks = new PrimitiveChunk[mapWidthInChunks * mapWidthInChunks];
        for(int i = 0; i < chunks.length; i++){
            chunks[i] = new PrimitiveChunk(this, chunkWidth, i);
        }
    }

    private PrimitiveChunk getChunk(int x, int y){
        return chunks[(x / chunkWidth + (mapWidthInChunks / 2)) + ((y / chunkWidth + (mapWidthInChunks / 2)) * mapWidthInChunks)];
    }

    //Placeholder for other tilemaps
    public void onSetTile(int x, int y, int oldValue, int newValue){}

    public int getChunkNum(int x, int y){
        Vector2 chunkPos = TileToChunkPosition(x, y);
        return ((int)chunkPos.x / chunkWidth + (mapWidthInChunks / 2)) + (((int)chunkPos.y / chunkWidth + (mapWidthInChunks / 2)) * mapWidthInChunks);
    }

    public int getChunkNumByChunkPosition(int x, int y){
        return (x / chunkWidth + (mapWidthInChunks / 2) + (y / chunkWidth + (mapWidthInChunks / 2)) * mapWidthInChunks);
    }

    public int getTile(int x, int y){
        Vector2 chunkPos = TileToChunkPosition(x, y);
        return getChunk((int)chunkPos.x, (int)chunkPos.y).getTile(x - (int)chunkPos.x, y - (int)chunkPos.y);
    }

    public int getTile(Vector3 worldPos){
        return getTile(MathUtils.floor(worldPos.x / tileWidth), MathUtils.floor(worldPos.y / tileWidth));
    }

    public void setTile(int x, int y, int value){
        Vector2 chunkPos = TileToChunkPosition(x, y);

        getChunk((int)chunkPos.x, (int)chunkPos.y).setTile(x - (int)chunkPos.x, y - (int)chunkPos.y, value);
    }

    public void setTile(Vector3 worldPos, int value){
        setTile(MathUtils.floor(worldPos.x / tileWidth), MathUtils.floor(worldPos.y / tileWidth), value);
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

    public Vector3 TileToWorldCenterPosition(int x, int y){
        return new Vector3((float)x * tileWidth + (tileWidth / 2), (float)y * tileWidth + (tileWidth / 2), 0);
    }

    public Vector2 TileToChunkPosition(Vector2 pos){
        return new Vector2(MathUtils.floor(pos.x / (chunkWidth / 2) * 0.5f), MathUtils.floor(pos.y / (chunkWidth / 2) * 0.5f)).scl(chunkWidth);
    }

    public Vector2 TileToChunkPosition(int x, int y){
        return new Vector2(MathUtils.floor((float)x / (chunkWidth / 2) * 0.5f), MathUtils.floor((float)y / (chunkWidth / 2) * 0.5f)).scl(chunkWidth);
    }

    public Vector2 ChunkNumToPosition(int num){
        int y = MathUtils.floor((float)num / mapWidthInChunks);
        int x = num - (y * mapWidthInChunks);
        return new Vector2(x, y).sub(mapWidthInChunks / 2, mapWidthInChunks / 2).scl(chunkWidth);
    }
}