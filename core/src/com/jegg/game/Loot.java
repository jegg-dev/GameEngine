package com.jegg.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.jegg.engine.AssetDatabase;
import com.jegg.engine.Game;
import com.jegg.engine.ecs.IteratedEntity;
import com.jegg.engine.ecs.IteratedFlag;
import com.jegg.engine.ecs.Transform;
import com.jegg.engine.physics.ISensorContactListener;
import com.jegg.engine.physics.Rigidbody;
import com.jegg.engine.rendering.SpriteRenderer;

public class Loot extends IteratedEntity implements ISensorContactListener {
    public ItemInstance item;
    private float timer = 5f;
    private final float chaseSpeed = 25.0f;
    private PlayerShip targetShip;

    public Loot(ItemInstance item, Color color){
        this.item = item;

        Transform t = Game.CreateComponent(Transform.class);
        add(t);

        SpriteRenderer sr = Game.CreateComponent(SpriteRenderer.class);
        sr.setTexture(AssetDatabase.GetTexture("triangle"), 256, 256);
        sr.setColor(color);
        t.scale.set(0.08f, 0.08f);
        add(sr);

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
                targetShip.inventory.addItem(item);
                Game.DestroyEntity(this);
            }
            else {
                getComponent(Rigidbody.class).body.setLinearVelocity(shipT.getPosition2().sub(t.getPosition2()).nor().scl(chaseSpeed));
            }
        }
    }

    @Override
    public void sensorContactEnter(Entity entity) {
        if(entity instanceof PlayerShip){
            targetShip = (PlayerShip) entity;
        }
    }

    @Override
    public void sensorContactExit(Entity entity) {
        if(entity == targetShip){
            targetShip = null;
        }
    }
}
