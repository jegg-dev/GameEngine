package com.jegg.engine.physics;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

public class RaycastHit {
    public Entity entity;
    public Body body;
    public Fixture fixture;
    public Vector2 point;

    public RaycastHit(Entity entity, Body body, Fixture fixture, Vector2 point){
        this.entity = entity;
        this.body = body;
        this.fixture = fixture;
        this.point = point;
    }
}