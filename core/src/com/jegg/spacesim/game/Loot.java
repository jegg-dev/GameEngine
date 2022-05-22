package com.jegg.spacesim.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.jegg.spacesim.core.Game;
import com.jegg.spacesim.core.ecs.*;

public class Loot extends IteratedEntity implements ISensorContactListener {
    private float timer = 5f;
    private final float chaseSpeed = 25.0f;
    private Ship targetShip;

    public Loot(Color color){
        Transform t = Game.CreateComponent(Transform.class);
        PolygonRenderer poly = Game.CreateComponent(PolygonRenderer.class);
        float[] verts = new float[]{
                -0.25f,-0.25f,
                0.25f,-0.25f,
                0,0.25f
        };
        poly.poly = new Polygon(verts);
        poly.color = color;
        add(poly);
        add(t);

        CircleShape circle = new CircleShape();
        circle.setRadius(15.0f);
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        Rigidbody rb = Game.CreateRigidbody(bodyDef, circle, 0);
        rb.body.getFixtureList().get(0).setSensor(true);
        rb.body.setUserData(this);
        add(rb);

        add(Game.CreateComponent(IteratedFlag.class));
        Game.AddEntity(this);
    }

    @Override
    public void start(float deltaTime) {
        Rigidbody rb = getComponent(Rigidbody.class);
        rb.body.setLinearVelocity(new Vector2(MathUtils.random(-1f, 1f), MathUtils.random(-1f,1f)).nor().scl(0.5f));
        rb.body.setAngularVelocity(90 * MathUtils.degreesToRadians);
    }

    @Override
    public void update(float deltaTime) {
        timer -= deltaTime;
        if(timer <= 0){
            Game.DestroyEntity(this);
        }
        if(targetShip != null){
            Transform t = getTransform();
            Transform shipT = targetShip.getTransform();
            if(Vector2.dst(t.getPosition2().x, t.getPosition2().y, shipT.getPosition2().x, shipT.getPosition2().y) < 0.5f){
                Game.DestroyEntity(this);
            }
            else {
                getComponent(Rigidbody.class).body.setLinearVelocity(shipT.getPosition2().sub(t.getPosition2()).nor().scl(chaseSpeed));
            }
        }
    }

    @Override
    public void sensorContactEnter(Entity entity) {
        if(entity instanceof Ship){
            targetShip = (Ship) entity;
        }
    }

    @Override
    public void sensorContactExit(Entity entity) {
        if(entity == targetShip){
            targetShip = null;
        }
    }
}
