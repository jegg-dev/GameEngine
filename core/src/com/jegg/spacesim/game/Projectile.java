package com.jegg.spacesim.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.jegg.spacesim.core.Game;
import com.jegg.spacesim.core.Physics;
import com.jegg.spacesim.core.ecs.*;
import com.jegg.spacesim.core.ecs.Transform;

import java.util.Arrays;

public class Projectile extends IteratedEntity implements CollisionSensor {
    public Entity owner;
    public int damage = 10;
    public static float speed = 10.0f;
    private float timer = 8.0f;

    public Projectile(Entity owner){
        this.owner = owner;

        Transform t = Game.CreateComponent(Transform.class);
        //PolygonRenderer poly = Game.CreateComponent(PolygonRenderer.class);
        //poly.poly = new Polygon(PolygonRenderer.boxVerts);
        //poly.poly.setScale(0.1f,0.5f);
        //poly.color = Color.RED;
        //add(poly);
        CircleRenderer circle = Game.CreateComponent(CircleRenderer.class);
        circle.radius = 0.25f;
        circle.color = Color.RED;
        add(circle);
        add(t);

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        Body body = Physics.CreateBody(def);
        CircleShape shape = new CircleShape();
        shape.setRadius(0.25f);
        body.createFixture(shape,1);
        body.setUserData(this);
        body.getFixtureList().get(0).setSensor(true);
        body.setBullet(true);
        Rigidbody rb = new Rigidbody();
        rb.body = body;
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
    public void collisionEnter(Fixture fixture, Entity collisionEntity, Contact contact){
        /*if(collisionEntity != null && collisionEntity.getComponent(AsteroidComponent.class) != null){
            Loot loot = new Loot(Color.GREEN);
            Vector3 position = collisionEntity.getComponent(Transform.class).getPosition();
            float rotation = collisionEntity.getComponent(Transform.class).getRotation() * MathUtils.degreesToRadians;
            loot.getComponent(Rigidbody.class).body.setTransform(position.x, position.y, rotation);
            Game.DestroyEntity(collisionEntity);
            Game.DestroyEntity(this);
        }
        else if(collisionEntity instanceof TerrainController){
            TerrainController t = (TerrainController) collisionEntity;
            Game.DestroyEntity(this);
            t.removeTile(new Vector3(fixture.getBody().getWorldCenter(), 0));
        }*/
        if(collisionEntity instanceof Asteroid){
            Game.DestroyEntity(this);
            ((Asteroid)collisionEntity).Hit();
        }
        else if(collisionEntity instanceof IDamageable && collisionEntity != owner){
            ((IDamageable)collisionEntity).damage(damage, owner);
            Game.DestroyEntity(this);
        }
        else if(!(collisionEntity instanceof Projectile) && collisionEntity != owner && collisionEntity != null){
            Game.DestroyEntity(this);
        }
    }

    @Override
    public void collisionExit(Fixture fixture, Entity collisionEntity, Contact contact){

    }
}
