package com.jegg.engine.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import com.jegg.engine.core.Game;
import com.jegg.engine.core.ecs.ComponentMappers;

public class SpawnerTile<T extends Entity> extends Tile{
    public Class<T> entityType;
    public boolean useHealth;

    public SpawnerTile(Sprite sprite, Color color, int hardness, boolean useCollider, Class<T> entityType, boolean useHealth) {
        super(sprite, color, hardness, useCollider);
        this.entityType = entityType;
        this.useHealth = useHealth;
    }

    @Override
    public void onCreate(TerrainMap map, Vector3 worldPos){
        if(useHealth){
            map.healthMap.setTile(worldPos, -1);
        }
    }

    @Override
    public void onLoad(TerrainMap map, Vector3 worldPos){
        Entity spawned = Game.CreateEntity(entityType);
        map.entityMap.setTile(worldPos, spawned);
        if(ComponentMappers.rigidbody.has(spawned)){
            ComponentMappers.rigidbody.get(spawned).body.setTransform(worldPos.x, worldPos.y, 0);
        }
        else if(ComponentMappers.transform.has(spawned)) {
            ComponentMappers.transform.get(spawned).setPosition(worldPos);
        }
        if(useHealth) {
            if(map.healthMap.getTile(worldPos) == -1){
                map.healthMap.setTile(worldPos,((IDamageable) spawned).getHealth());
            }
            else {
                ((IDamageable) spawned).setHealth(map.healthMap.getTile(worldPos));
            }
        }
    }

    @Override
    public void onUnload(TerrainMap map, Vector3 worldPos){
        if(map.entityMap.getTile(worldPos) != null){
            if(useHealth){
                map.healthMap.setTile(worldPos, ((IDamageable)map.entityMap.getTile(worldPos)).getHealth());
            }
            Game.DestroyEntity(map.entityMap.getTile(worldPos));
            map.entityMap.setTile(worldPos, null);
        }
    }

    @Override
    public void onDestroy(TerrainMap map, Vector3 worldPos){
        if(map.entityMap.getTile(worldPos) != null){
            Game.DestroyEntity(map.entityMap.getTile(worldPos));
            map.entityMap.setTile(worldPos, null);
        }
    }
}
