package com.jegg.engine.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.jegg.engine.core.*;
import com.jegg.engine.core.physics.Rigidbody;
import com.jegg.engine.core.ecs.StaticFlag;
import com.jegg.engine.core.ecs.Transform;
import com.jegg.engine.core.rendering.PolygonSpriteRenderer;
import com.jegg.engine.core.tilemap.PrimitiveChunk;
import com.jegg.engine.core.tilemap.PrimitiveTilemap;
import com.jegg.engine.core.tilemap.Tilemap;

import java.util.ArrayList;

public class Spacemap extends PrimitiveTilemap implements IIteratedBehavior {
    private Tilemap<Entity> entityMap;

    public int viewDist = 1;

    public int lastChunkNum;
    public ArrayList<Integer> loadedChunkNums = new ArrayList<>();
    public ArrayList<Integer> activeChunkNums = new ArrayList<>();

    public static final float[] AsteroidVerts = new float[]{
            -0.5f,-1f,
            0.5f,-1f,
            1f,-0.5f,
            1f,0.5f,
            0.5f,1f,
            -0.5f,1f,
            -1f,0.5f,
            -1f,-0.5f
    };

    public Spacemap(int chunkDist, int chunkWidth, float tileWidth) {
        super(chunkDist, chunkWidth, tileWidth);
        entityMap = new Tilemap<>(chunkDist, chunkWidth, tileWidth);
        Game.AddScript(this);
    }

    public void update(float deltaTime){
        Vector3 camPos = GameCamera.GetMain().getPosition();
        Vector2 chunkPos = TileToChunkPosition(WorldToTilePosition(camPos));

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
                        boolean spawn = Math.abs(Random.Lehmer((x + x2 + (getMapWidthInChunks() * getChunkWidth() / 2)) + ((y + y2 + (getMapWidthInChunks() * getChunkWidth() / 2)) * getMapWidthInChunks() * getChunkWidth()))) % 256 < 15;
                        if(spawn) {
                            chunk.setTile(x2, y2, 1);
                            entityMap.setTile(x + x2, y + y2, spawnRandomAsteroid(TileToWorldCenterPosition(x + x2, y + y2)));
                        }
                    }
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
                    if(chunk.getTile(x2, y2) == 1){
                        entityMap.setTile(x + x2, y + y2, spawnRandomAsteroid(TileToWorldCenterPosition(x + x2, y + y2)));
                    }
                }
            }
        }
    }

    public void unloadChunk(int chunkNum){
        PrimitiveChunk chunk = chunks[chunkNum];
        for (int x = 0; x < getChunkWidth(); x++) {
            for (int y = 0; y < getChunkWidth(); y++) {
                Vector2 chunkPos = ChunkNumToPosition(chunkNum);
                if(chunk != null) {
                    Entity e = entityMap.getTile((int)chunkPos.x + x, (int)chunkPos.y + y);
                    if(e != null)
                        Game.DestroyEntity(e);
                }
            }
        }
    }

    public Asteroid spawnRandomAsteroid(Vector3 pos){
        float [] verts = AsteroidVerts.clone();
        for(int i = 0; i < verts.length; i++){
            verts[i] = verts[i] + MathUtils.random(-0.2f,0.2f);
        }
        return CreateAsteroid(pos, verts, true);
    }

    public static Asteroid CreateAsteroid(Vector3 pos, float[] verts, boolean staticFlag){
        Asteroid asteroid = Game.CreateEntity(Asteroid.class);
        Transform t = Game.CreateComponent(Transform.class);
        t.setPosition(pos);
        asteroid.add(t);

        Polygon poly = new Polygon(verts);
        poly.setScale(4, 4);

        Rigidbody rb;
        if(staticFlag) {
            rb = Game.CreateRigidbody(poly.getTransformedVertices(), BodyDef.BodyType.StaticBody, 0);
            asteroid.add(new StaticFlag());
        }
        else{
            rb = Game.CreateRigidbody(poly.getTransformedVertices(), BodyDef.BodyType.DynamicBody, 1);
        }

        rb.body.setUserData(asteroid);
        rb.body.setTransform(pos.x, pos.y, 0);
        asteroid.add(rb);

        poly.setScale(128f, 128f);
        PolygonSpriteRenderer sr = new PolygonSpriteRenderer();
        sr.setTexture(AssetDatabase.GetTexture("rock-tile"), poly.getTransformedVertices());
        sr.setColor(Color.DARK_GRAY);
        sr.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        asteroid.add(sr);
        Game.AddEntity(asteroid);
        return asteroid;
    }

    public static void CreateAsteroid(Vector3 pos, float[] verts, Vector2 vel){
        Asteroid asteroid = CreateAsteroid(pos, verts, false);
        asteroid.getComponent(Rigidbody.class).body.setLinearVelocity(vel);
    }
}
