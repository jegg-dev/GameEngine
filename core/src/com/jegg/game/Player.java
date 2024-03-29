package com.jegg.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.jegg.engine.DebugLine;
import com.jegg.engine.Game;
import com.jegg.engine.GameCamera;
import com.jegg.engine.Input;
import com.jegg.engine.ecs.InactiveFlag;
import com.jegg.engine.ecs.IteratedEntity;
import com.jegg.engine.ecs.IteratedFlag;
import com.jegg.engine.ecs.Transform;
import com.jegg.engine.physics.AABBHit;
import com.jegg.engine.physics.Physics;
import com.jegg.engine.physics.Rigidbody;
import com.jegg.engine.rendering.CircleRenderer;

public class Player extends IteratedEntity {
    public PlayerShip ship;

    public float thrustForce = 3.0f;

    public Player(){
        Transform t = Game.CreateComponent(Transform.class);
        CircleRenderer cr = new CircleRenderer();
        cr.radius = 0.25f;
        cr.color = Color.CYAN;

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        CircleShape shape = new CircleShape();
        shape.setRadius(0.5f);
        Rigidbody rb = Game.CreateRigidbody(def, shape, 1);
        rb.body.setUserData(this);
        add(rb);

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
            AABBHit[] hits = Physics.AABBAll(t.getPosition2(), 1f,1f);
            for(AABBHit hit : hits){
                if(hit.entity == ship){
                    add(new InactiveFlag());
                    ship.add(new IteratedFlag());
                    ship.getComponent(Rigidbody.class).body.setType(BodyDef.BodyType.DynamicBody);
                    rb.body.setActive(false);
                    break;
                }
            }
        }

        if(Input.getKeyUp(Input.F)){
            Vector3 mousePos = GameCamera.GetMain().screenToWorld(Input.MousePos);
            rb.body.setTransform(mousePos.x, mousePos.y, 0);
        }

        Game.lines.add(new DebugLine(t.getPosition2(), t.getPosition2().add(rb.body.getLinearVelocity().scl(0.1f)), 0, Color.GOLD));

        GameCamera.GetMain().setPosition(t.getPosition());
        GameCamera.GetMain().setZoom(8.0f);
    }

    @Override
    public void physicsUpdate(float deltaTime){
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
