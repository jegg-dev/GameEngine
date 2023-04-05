package com.jegg.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.jegg.engine.Game;
import com.jegg.engine.ecs.IteratedEntity;
import com.jegg.engine.ecs.IteratedFlag;
import com.jegg.engine.ecs.Transform;
import com.jegg.engine.physics.Rigidbody;
import com.jegg.engine.rendering.PolygonRenderer;

public class TurretEnemy extends IteratedEntity implements IDamageable {
    public int health = 100;
    public int directions;

    public float fireTime = 1.0f;
    public float fireTimer = 1.0f;

    public TurretEnemy(){
        directions = MathUtils.random(1, 4);

        Transform t = Game.CreateComponent(Transform.class);
        PolygonRenderer poly = Game.CreateComponent(PolygonRenderer.class);
        poly.poly = new Polygon(PolygonRenderer.boxVerts);
        poly.poly.setScale(0.5f,0.5f);
        poly.poly.rotate(45);
        poly.color = Color.RED;
        add(poly);
        add(t);

        Rigidbody rb = Game.CreateRigidbody(poly.poly.getTransformedVertices(), BodyDef.BodyType.StaticBody, 1);
        rb.body.setUserData(this);
        add(rb);

        add(Game.CreateComponent(IteratedFlag.class));
        Game.AddEntity(this);
    }

    @Override
    public void start(float deltaTime) {}

    @Override
    public void update(float deltaTime) {
        fireTime -= deltaTime;
        if(fireTime <= deltaTime){
            float angle = 2 * MathUtils.PI / directions;
            for(int i = 0; i < directions; i++) {
                Projectile p = new Projectile(this);
                p.getComponent(Rigidbody.class).body.setTransform(getTransform().getPosition2(), angle * i);
                p.getTransform().setPosition(getTransform().getPosition());
                p.getTransform().setRotation(angle * i * MathUtils.radiansToDegrees);
            }
            fireTime = fireTimer;
        }
    }

    @Override
    public boolean damage(int damage, Entity source) {
        health -= damage;
        if(health <= 0){
            Game.DestroyEntity(this);
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public int getHealth() {
        return 0;
    }

    @Override
    public void setHealth(int health){
        this.health = health;
    }
}
