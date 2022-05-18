package com.jegg.spacesim.game;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.jegg.spacesim.core.Game;
import com.jegg.spacesim.core.GameCamera;
import com.jegg.spacesim.core.PerlinNoise;
import com.jegg.spacesim.core.ecs.IteratedEntity;
import com.jegg.spacesim.core.ecs.IteratedFlag;
import com.jegg.spacesim.core.ecs.RenderSystem;

public class PerlinTest extends IteratedEntity {
    public float amplitude = 10.1f;
    public float biomeAmplitude = 100.1f;
    public float zLevel;

    public int[] tiles = new int[1000000];

    public PerlinTest(){
        add(Game.CreateComponent(IteratedFlag.class));
        Game.AddEntity(this);
    }

    @Override
    public void start(float deltaTime) {
        for(int x = -500; x < 500; x += 1.5f){
            for(int y = -500; y < 500; y += 1.5f){
                float z = PerlinNoise.At((float) x / amplitude, (float) y / amplitude, zLevel);
                float z2 = PerlinNoise.At((float) x / biomeAmplitude, (float) y / biomeAmplitude, zLevel + 1000);
                int tile = 0;
                if (z >= 0.8f) {
                    if(z2 >= 0.25f) {
                        tile = 4;
                    }
                    else{
                        tile = 5;
                    }
                } else if (z >= 0.7f) {
                    if(z2 >= 0.25f) {
                        tile = 2;
                    }
                    else{
                        tile = 6;
                    }
                } else if (z >= 0.0f) {
                    if(z2 >= 0.25f) {
                        tile = 3;
                    }
                    else{
                        tile = 4;
                    }
                }
                if(tile != 0) {
                    tiles[x + 500 + (y + 500 * 1000)] = tile;
                }
            }
        }
    }

    @Override
    public void update(float deltaTime) {
        ShapeRenderer sr = new ShapeRenderer();
        sr.setProjectionMatrix(GameCamera.GetMain().getCombined());
        sr.begin(ShapeRenderer.ShapeType.Line);
        for(int x = -500; x < 500; x += 1.5f){
            for(int y = -500; y < 500; y += 1.5f){
                sr.setColor(TileDatabase.Get(tiles[x + 500 + (y + 500 * 1000)]).color);
                sr.box(x, y, 0,1,1,0);
            }
        }
        sr.end();
    }
}
