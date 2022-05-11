package com.jegg.spacesim.core.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.jegg.spacesim.core.ecs.*;

public class IteratingEntitySystem extends IteratingSystem {

    public IteratingEntitySystem(){
        super(Family.all(IteratedFlag.class).exclude(InactiveFlag.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        IteratedEntity ie = (IteratedEntity)entity;
        if(!ie.started){
            ie.start(deltaTime);
            ie.started = true;
        }
        ie.update(deltaTime);
    }
}
