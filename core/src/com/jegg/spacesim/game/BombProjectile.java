package com.jegg.spacesim.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.jegg.spacesim.core.Game;
import com.jegg.spacesim.core.ecs.*;

public class BombProjectile extends Projectile{
    public BombProjectile(Entity owner) {
        super(owner);
        getComponent(Transform.class).scale.set(0.2f, 0.2f);
    }

    @Override
    public void sensorContactEnter(Entity entity){
        if(getComponent(DestroyedFlag.class) != null){
            return;
        }

        if(entity instanceof TerrainController){
            TerrainController tc = (TerrainController) entity;
            Vector2 pos = getTransform().getPosition2();
            for(int x = (int)pos.x - 10; x < pos.x + 10; x++){
                for(int y = (int)pos.y - 10; y < pos.y + 10; y++){
                    if(Vector2.dst(x, y, pos.x, pos.y) <= 10){
                        tc.removeTile(new Vector3(x, y, 0));
                    }
                }
            }
            Game.DestroyEntity(this);
        }
        else if(entity != owner){
            Game.DestroyEntity(this);
        }
    }
}