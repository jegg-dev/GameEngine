package com.jegg.spacesim.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.jegg.spacesim.core.Game;
import com.jegg.spacesim.core.ecs.*;

public class Loot extends IteratedEntity implements CollisionSensor {
    private float timer = 5f;

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

        Rigidbody rb = Game.CreateRigidbody(poly.poly.getTransformedVertices(), BodyDef.BodyType.DynamicBody, 1);
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
    }

    @Override
    public void collisionEnter(Fixture fixture, Entity collisionEntity, Contact contact) {
        if(collisionEntity instanceof Ship){
            Game.DestroyEntity(this);
        }
    }

    @Override
    public void collisionExit(Fixture fixture, Entity collisionEntity, Contact contact) {

    }
}
