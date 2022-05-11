package com.jegg.spacesim.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.jegg.spacesim.core.DebugLine;
import com.jegg.spacesim.core.Game;
import com.jegg.spacesim.core.Physics;
import com.jegg.spacesim.core.ecs.*;

public class Player extends IteratedEntity {
    public Ship ship;

    public float thrustForce = 3.0f;

    public Player(){
        Transform t = Game.CreateComponent(Transform.class);
        CircleRenderer cr = new CircleRenderer();
        cr.radius = 0.25f;
        cr.color = Color.CYAN;

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        Body body = Physics.CreateBody(def);
        CircleShape circle = new CircleShape();
        circle.setRadius(0.25f);
        body.createFixture(circle,1);
        body.setTransform(0, 0, 0);
        body.setUserData(this);
        Rigidbody rb = new Rigidbody();
        rb.body = body;

        add(t);
        add(rb);
        add(cr);
        add(Game.CreateComponent(IteratedFlag.class));
        Game.AddEntity(this);
    }

    @Override
    public void start(float deltaTime) {}

    @Override
    public void update(float deltaTime) {
        Rigidbody rb = getComponent(Rigidbody.class);
        Transform t = getComponent(Transform.class);

        if(Input.getKeyUp(Input.E)){
            Entity[] entities = Physics.AABBAll(t.getPosition2(), 1f,1f);
            for(Entity e : entities){
                if(e == ship){
                    add(new InactiveFlag());
                    ship.add(new IteratedFlag());
                    ship.getComponent(Rigidbody.class).body.setType(BodyDef.BodyType.DynamicBody);
                    rb.body.setActive(false);
                    break;
                }
            }
        }

        if(Input.getKeyUp(Input.F)){
            Vector3 mousePos = Game.ScreenToWorld(Input.mousePos);
            rb.body.setTransform(mousePos.x, mousePos.y, 0);
        }

        Game.lines.add(new DebugLine(t.getPosition2(), t.getPosition2().add(rb.body.getLinearVelocity().scl(0.1f)), 0, Color.GOLD));

        RenderSystem.getCamera().position.set(t.getPosition());
        RenderSystem.getCamera().zoom = 1.0f;
        Game.WriteUI("" + getTransform().getPosition2(), 0, Game.getUICamera().viewportHeight - 20);
    }

    @Override
    public void fixedUpdate(float deltaTime){
        Transform t = getComponent(Transform.class);
        Rigidbody rb = getComponent(Rigidbody.class);

        if(Input.getKey(Input.Space)){
            rb.body.setLinearDamping(2.5f);
        }
        else{
            rb.body.setLinearDamping(0);
        }

        Vector2 thrust = new Vector2();
        thrust.y = Input.getKey(Input.W) ? 1 : Input.getKey(Input.S) ? -1 : 0;
        thrust.x = Input.getKey(Input.D) ? 1 : Input.getKey(Input.A) ? -1 : 0;
        thrust.nor().scl(thrustForce);
        rb.body.applyForceToCenter(thrust, true);
        float mag = rb.body.getLinearVelocity().len();
        mag = MathUtils.clamp(mag, 0.0f, 2.0f);
        rb.body.setLinearVelocity(rb.body.getLinearVelocity().nor().scl(mag));
    }
}
