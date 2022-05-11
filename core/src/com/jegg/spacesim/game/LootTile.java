package com.jegg.spacesim.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.jegg.spacesim.core.ecs.Rigidbody;

public class LootTile extends Tile{
    public LootTile(Color color, int hardness, boolean useCollider){
        super(color, hardness, useCollider);
    }
    @Override
    public void onDestroy(TerrainMap map, Vector3 worldPos){
        Loot loot = new Loot(color);
        loot.getComponent(Rigidbody.class).body.setTransform(worldPos.x, worldPos.y, 0);
    }
}
