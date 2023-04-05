package com.jegg.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;

public class OreTile extends Tile{
    public int ore;
    public OreTile(Sprite sprite, Color color, int hardness, boolean useCollider, int ore) {
        super(sprite, color, hardness, useCollider);
        this.ore = ore;
    }

    @Override
    public void onDestroy(TerrainMap map, Vector3 worldPos){

    }
}
