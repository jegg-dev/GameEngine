package com.jegg.spacesim.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class OreTile extends Tile{
    public int ore;
    public OreTile(Color color, int hardness, boolean useCollider, int ore) {
        super(color, hardness, useCollider);
        this.ore = ore;
    }

    @Override
    public void onDestroy(TerrainMap map, Vector3 worldPos){

    }
}