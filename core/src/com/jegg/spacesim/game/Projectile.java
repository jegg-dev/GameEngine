package com.jegg.spacesim.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.jegg.spacesim.core.Game;
import com.jegg.spacesim.core.ecs.*;
import com.jegg.spacesim.core.ecs.Transform;

public class Projectile extends IteratedEntity implements ISensorContactListener {
    public Entity owner;
    public int damage = 10;
    public static float speed = 20.0f;
    private float timer = 8.0f;

    public Projectile(Entity owner){
        this.owner = owner;

        Transform t = Game.CreateComponent(Transform.class);

        CircleRenderer circle = Game.CreateComponent(CircleRenderer.class);
        circle.radius = 0.5f;
        circle.color = Color.RED;
        add(circle);
        add(t);

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        CircleShape shape = new CircleShape();
        shape.setRadius(0.25f);
        Rigidbody rb = Game.CreateRigidbody(def, shape, 1);
        rb.body.setUserData(this);
        rb.body.getFixtureList().get(0).setSensor(true);
        rb.body.setBullet(true);
        add(rb);

        add(Game.CreateComponent(IteratedFlag.class));
        Game.AddEntity(this);
    }

    @Override
    public void start(float deltaTime){
        Vector2 vel = getTransform().up();
        vel.scl(speed);
        getComponent(Rigidbody.class).body.setLinearVelocity(vel);
    }

    @Override
    public void update(float deltaTime){
        timer -= deltaTime;
        if(timer <= 0){
            Game.DestroyEntity(this);
        }
    }

    @Override
    public void sensorContactEnter(Entity entity){
        if(getComponent(DestroyedFlag.class) != null){
            return;
        }

        if(entity instanceof Asteroid){
            Game.DestroyEntity(this);
            ((Asteroid) entity).Hit();
        }
        else if(entity instanceof IDamageable && entity != owner){
            ((IDamageable) entity).damage(damage, owner);
            Game.DestroyEntity(this);
        }
        else if(!(entity instanceof Projectile) && entity != owner && entity != null){
            Game.DestroyEntity(this);
        }
    }

    @Override
    public void sensorContactExit(Entity entity){}
}
