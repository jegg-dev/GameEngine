package com.jegg.spacesim.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.jegg.spacesim.core.Input;
import com.jegg.spacesim.core.PerlinNoise;
import com.jegg.spacesim.core.physics.Physics;
import com.jegg.spacesim.core.tilemap.*;

import java.util.ArrayList;

public class TerrainMap extends RenderedTilemap {
    public static boolean ShowChunkOutlines;

    public CollisionTilemap collider;
    public PrimitiveTilemap healthMap;
    public Tilemap<Entity> entityMap;

    public TiledMap renderMap;

    public int lastChunkNum;
    public ArrayList<Integer> loadedChunkNums = new ArrayList<>();
    public ArrayList<Integer> activeChunkNums = new ArrayList<>();

    public float zLevel;
    public float amplitude = 15.0f;
    public float enemyAmplitude = 1.2f;
    public float biomeAmplitude = 100.0f;

    public TerrainMap(int mapWidthInChunks, int chunkWidth, float tileWidth, int viewDist) {
        super(mapWidthInChunks, chunkWidth, tileWidth, viewDist);
        collider = new CollisionTilemap(mapWidthInChunks, chunkWidth, tileWidth);
        healthMap = new PrimitiveTilemap(mapWidthInChunks, chunkWidth, tileWidth);
        entityMap = new Tilemap<>(mapWidthInChunks, chunkWidth, tileWidth);
        lastChunkNum = Integer.MAX_VALUE;
    }

    @Override
    public void update(Vector2 chunkPos) {
        if(Input.getKeyUp(Input.K)){
            zLevel += 0.25f;
            for(int i = 0; i < getMapWidthInChunks() * getMapWidthInChunks(); i++){
                chunks[i] = null;
            }
            loadedChunkNums.clear();
            for(int num : activeChunkNums){
                unloadChunk(num);
            }
            activeChunkNums.clear();
            lastChunkNum = Integer.MAX_VALUE;
        }
        else if(Input.getKeyUp(Input.G)){
            ShowChunkOutlines = !ShowChunkOutlines;
        }
        if(getChunkNum((int)chunkPos.x, (int)chunkPos.y) == lastChunkNum){
            return;
        }
        else{
            lastChunkNum = getChunkNum((int)chunkPos.x, (int)chunkPos.y);
        }

        ArrayList<Integer> tempChunks = new ArrayList<>();
        for (int x = (int)chunkPos.x - (viewDist * getChunkWidth()); x < chunkPos.x + ((viewDist + 1) * getChunkWidth()); x += getChunkWidth()) {
            for (int y = (int)chunkPos.y - (viewDist * getChunkWidth()); y < chunkPos.y + ((viewDist + 1) * getChunkWidth()); y += getChunkWidth()) {
                if(x / getChunkWidth() > -getMapWidthInChunks() / 2 && x / getChunkWidth() < getMapWidthInChunks() / 2
                        && y / getChunkWidth() > -getMapWidthInChunks() / 2 && y / getChunkWidth() < getMapWidthInChunks() / 2) {
                    loadChunk(x, y, tempChunks);
                }
            }
        }
        for(int num : activeChunkNums){
            if(!tempChunks.contains(num)){
                unloadChunk(num);
            }
        }
        activeChunkNums = tempChunks;
    }

    public void loadChunk(int x, int y, ArrayList<Integer> tempChunks){
        int chunkNum = getChunkNum(x, y);
        tempChunks.add(chunkNum);
        if(!loadedChunkNums.contains(chunkNum)) {
            loadedChunkNums.add(chunkNum);
            PrimitiveChunk chunk = chunks[chunkNum];
            if (chunk == null) {
                chunk = new PrimitiveChunk(this, getChunkWidth(), chunkNum);
                chunks[chunkNum] = chunk;
            }
            for (int x2 = 0; x2 < getChunkWidth(); x2++) {
                for (int y2 = 0; y2 < getChunkWidth(); y2++) {
                    if (chunk.getTile(x2, y2) == 0) {
                        float z = PerlinNoise.At((float) (x + x2) / amplitude, (float) (y + y2) / amplitude, zLevel);
                        //float z2 = PerlinNoise.At((float) (x + x2) / enemyAmplitude, (float) (y + y2) / enemyAmplitude, zLevel + 1000);
                        int tile;
                        /*if((z >= -1 || z <= 0.2f) && z2 > 0.85f){
                            tile = 7;
                        }*/
                        switch ((int) (z * 10)) {
                            case 3: tile = 2; break;
                            case 5: tile = 3; break;
                            case 7: tile = 4; break;
                            case 9: case 10: tile = 5; break;
                            default: tile = 0; break;
                        }
                        if(tile != 0) {
                            chunk.setTile(x2, y2, tile);
                            if(TileDatabase.Get(tile).useCollider) {
                                collider.setTile(x2 + x, y2 + y);
                            }

                            //sr.setColor(TileDatabase.Get(tile).color);
                            //sr.box(x * getTileWidth(), y * getTileWidth(), 0, getTileWidth(), getTileWidth(), 0);
                        }
                    }
                    TileDatabase.Get(chunk.getTile(x2, y2)).onLoad(this, TileToWorldCenterPosition(x + x2, y + y2));
                }
            }
        }
        else if(!activeChunkNums.contains(chunkNum)){
            PrimitiveChunk chunk = chunks[chunkNum];
            if (chunk == null) {
                chunk = new PrimitiveChunk(this, getChunkWidth(), chunkNum);
                chunks[chunkNum] = chunk;
            }
            for (int x2 = 0; x2 < getChunkWidth(); x2++) {
                for (int y2 = 0; y2 < getChunkWidth(); y2++) {
                    TileDatabase.Get(chunk.getTile(x2, y2)).onLoad(this, TileToWorldCenterPosition(x + x2, y + y2));
                    if (TileDatabase.Get(chunk.getTile(x2, y2)).useCollider) {
                        collider.setTile(x2 + x, y2 + y);
                    }
                }
            }
        }
    }

    public void unloadChunk(int chunkNum){
        Chunk<Body> colliderChunk = collider.chunks.get(chunkNum);
        PrimitiveChunk chunk = chunks[chunkNum];
        for (int x = 0; x < getChunkWidth(); x++) {
            for (int y = 0; y < getChunkWidth(); y++) {
                Vector2 chunkPos = ChunkNumToPosition(chunkNum);
                if(chunk != null) {
                    TileDatabase.Get(chunk.getTile(x, y)).onUnload(this, TileToWorldCenterPosition(x + (int) chunkPos.x, y + (int) chunkPos.y));
                }
                if(colliderChunk != null) {
                    Body body = colliderChunk.getTile(x, y);
                    if (body != null) {
                        Physics.GetWorld().destroyBody(body);
                    }
                }
            }
        }
        collider.chunks.set(chunkNum, null);
    }

    @Override
    public void onSetTile(int x, int y, int oldValue, int newValue){
        TileDatabase.Get(oldValue).onDestroy(this, TileToWorldCenterPosition(x, y));
        TileDatabase.Get(newValue).onCreate(this, TileToWorldCenterPosition(x, y));
    }
}
