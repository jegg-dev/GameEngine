package com.jegg.engine.ecs;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jegg.engine.physics.Physics;

public class GarbageSystem extends IteratingSystem {
    private final Engine engine;

    public GarbageSystem(Engine engine){
        super(Family.all(DestroyedFlag.class).get());
        this.engine = engine;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if(entity instanceof IteratedEntity){
            ((IteratedEntity)entity).onDestroy();
        }

        if(ComponentMappers.rigidbody.get(entity) != null){
			Physics.GetWorld().destroyBody(ComponentMappers.rigidbody.get(entity).body);
		}
        engine.removeEntity(entity);
    }
}
