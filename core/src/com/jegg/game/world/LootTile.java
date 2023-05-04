package com.jegg.game.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import com.jegg.engine.physics.Rigidbody;
import com.jegg.engine.ecs.Transform;
import com.jegg.game.ItemInstance;
import com.jegg.game.Loot;

public class LootTile extends Tile{
    public ItemInstance lootItem;

    public LootTile(Sprite sprite, Color color, int hardness, boolean useCollider, ItemInstance lootItem){
        super(sprite, color, hardness, useCollider);
        this.lootItem = lootItem;
    }

    @Override
    public void onDestroy(TerrainMap map, Vector3 worldPos){
        Loot loot = new Loot(lootItem, sprite.getColor());
        loot.getComponent(Transform.class).setPosition(worldPos);
        loot.getComponent(Rigidbody.class).body.setTransform(worldPos.x, worldPos.y, 0);
    }
}
