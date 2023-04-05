package com.jegg.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import com.jegg.engine.physics.Rigidbody;
import com.jegg.engine.ecs.Transform;

public class LootTile extends Tile{
    public LootTile(Sprite sprite, Color color, int hardness, boolean useCollider){
        super(sprite, color, hardness, useCollider);
    }

    @Override
    public void onDestroy(TerrainMap map, Vector3 worldPos){
        Loot loot = new Loot(sprite.getColor());
        loot.getComponent(Transform.class).setPosition(worldPos);
        loot.getComponent(Rigidbody.class).body.setTransform(worldPos.x, worldPos.y, 0);
    }
}
