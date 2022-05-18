package com.jegg.spacesim.core.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.jegg.spacesim.core.GameCamera;

public class PhysicsDebugSystem extends IteratingSystem {

    private Box2DDebugRenderer debugRenderer;
    private World world;
    private GameCamera camera;

    public PhysicsDebugSystem(World world, GameCamera camera){
        super(Family.all().get());
        debugRenderer = new Box2DDebugRenderer();
        this.world = world;
        this.camera = camera;
    }

    @Override
    public void update(float deltaTime){
        super.update(deltaTime);
        debugRenderer.render(world, camera.getCombined());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime){}
}
