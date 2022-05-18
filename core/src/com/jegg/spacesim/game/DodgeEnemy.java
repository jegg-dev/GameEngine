package com.jegg.spacesim.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.jegg.spacesim.core.Game;
import com.jegg.spacesim.core.ecs.*;
import com.jegg.spacesim.core.ecs.Transform;

public class DodgeEnemy extends IteratedEntity implements ISensorContactListener {
    public float speed = 10.0f;
    public float radius = 5.0f;
    public boolean triggered;
    public Ship hitShip;

    public DodgeEnemy(){
        Transform t = Game.CreateComponent(Transform.class);
        PolygonRenderer poly = Game.CreateComponent(PolygonRenderer.class);
        poly.poly = new Polygon(PolygonRenderer.boxVerts);
        poly.poly.setScale(0.5f,0.5f);
        poly.color = Color.RED;
        add(poly);
        add(t);

        Rigidbody rb = Game.CreateRigidbody(poly.poly.getTransformedVertices(), BodyDef.BodyType.DynamicBody, 1);
        PolygonShape shape = new PolygonShape();
        Polygon polyShape = new Polygon(PolygonRenderer.boxVerts);
        polyShape.scale(radius);
        shape.set(polyShape.getTransformedVertices());
        rb.body.createFixture(shape,0);
        rb.body.getFixtureList().get(1).setSensor(true);
        rb.body.setUserData(this);
        add(rb);

        add(Game.CreateComponent(IteratedFlag.class));
        Game.AddEntity(this);
    }

    @Override
    public void start(float deltaTime){
    }

    @Override
    public void update(float deltaTime){
        Rigidbody rb = getComponent(Rigidbody.class);
        if(triggered){
            Vector3 dir = getTransform().getPosition().sub(hitShip.getTransform().getPosition());
            float dist = dir.len();
            float scaledSpeed = (-(float)Math.pow(dist / radius, 10) + 1) * speed;
            scaledSpeed = MathUtils.clamp(scaledSpeed, 0, speed);
            dir.nor().scl(scaledSpeed);
            rb.body.setLinearVelocity(dir.x, dir.y);
            rb.body.setAngularVelocity(0);
        }
        else{
            rb.body.setLinearVelocity(0, 0);
            rb.body.setAngularVelocity(0);
        }
    }

    @Override
    public void sensorContactEnter(Entity entity){
        if(entity instanceof Ship){
            hitShip = (Ship) entity;
            triggered = true;
        }
    }

    @Override
    public void sensorContactExit(Entity entity){
        hitShip = null;
        triggered = false;
    }
}
