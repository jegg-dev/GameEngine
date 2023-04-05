package com.jegg.engine.physics;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

public class AABBHit {
    public Entity entity;
    public Body body;
    public Fixture fixture;

    public AABBHit(Entity entity, Body body, Fixture fixture){
        this.entity = entity;
        this.body = body;
        this.fixture = fixture;
    }
}
