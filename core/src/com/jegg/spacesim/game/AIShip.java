package com.jegg.spacesim.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.jegg.spacesim.core.*;
import com.jegg.spacesim.core.ecs.*;
import com.jegg.spacesim.core.ecs.Transform;

import java.util.Arrays;
import java.util.HashMap;

public class AIShip extends IteratedEntity implements IDamageable {
    public int health = 100;
    public float thrustForce = 5;
    public float turnTorque = 6;

    public Ship targetShip;

    public float fireTimer = 1.0f;
    public float fireTime = 1.0f;

    public ProgressBar healthBar;

    public AIShip(Ship targetShip){
        this.targetShip = targetShip;

        Transform t = Game.CreateComponent(Transform.class);
        PolygonRenderer poly = Game.CreateComponent(PolygonRenderer.class);
        float[] verts = new float[]{
                -0.2f, -1,
                0.2f, -1,
                0.5f, -0.5f,
                0.5f, 0.5f,
                0, 1f,
                -0.5f, 0.5f,
                -0.5f, -0.5f
        };
        poly.poly = new Polygon(verts);
        poly.color = Color.ORANGE;

        Rigidbody rb = Game.CreateRigidbody(verts, BodyDef.BodyType.DynamicBody, 1);
        rb.body.setAngularDamping(5);
        rb.body.setUserData(this);
        rb.body.setBullet(true);
        add(t);
        add(rb);
        add(poly);
        add(Game.CreateComponent(IteratedFlag.class));
        Game.AddEntity(this);

        healthBar = new ProgressBar(0, 100, 1, false, new Skin(Gdx.files.internal("skins/flat/skin.json")));
        healthBar.setAnimateDuration(0.5f);
        healthBar.setAnimateInterpolation(Interpolation.fastSlow);
        healthBar.getStyle().background.setMinHeight(5);
        healthBar.getStyle().knobBefore.setMinHeight(5);
        healthBar.setHeight(5);
        healthBar.setWidth(50);
        Game.GetUIStage().addActor(healthBar);
    }

    @Override
    public void start(float deltaTime){}

    @Override
    public void update(float deltaTime){
        Transform t = getComponent(Transform.class);
        Rigidbody rb = getComponent(Rigidbody.class);

        Vector3 worldPos = t.getPosition().add(0, 1, 0);
        Vector2 screenPos = GameCamera.GetMain().worldToScreen(worldPos);
        healthBar.setPosition(screenPos.x - (healthBar.getWidth() / 2), screenPos.y);
        healthBar.setValue(health);

        fireTime -= deltaTime;

        Vector2 nose = t.getPosition2().add(t.up().scl(1));
        RaycastHit[] hits = Physics.RaycastAll(nose, targetShip.getTransform().getPosition2().sub(nose).nor(), 25);
        for(int i = 0; i < hits.length; i++){
            if(hits[i] != null && hits[i].body == rb.body){
                hits[i] = null;
            }
        }
        RaycastHit closest = Physics.GetClosest(hits, nose);
        if(closest != null && closest.entity == targetShip && fireTime <= 0){
            //Game.lines.add(new DebugLine(nose, nose.cpy().add(t.up().scl(10)), 0, Color.CYAN));
            Projectile p = new Projectile(this);
            p.getComponent(Rigidbody.class).body.setTransform(nose, MathUtils.atan2(t.up().y, t.up().x) - (MathUtils.PI / 2));
            p.getTransform().setPosition(new Vector3(nose, 0));
            p.getTransform().setRotation(MathUtils.atan2(t.up().y, t.up().x) * MathUtils.radiansToDegrees - 90);
            fireTime = fireTimer;
        }

        Game.lines.add(new DebugLine(t.getPosition2(), t.getPosition2().add(rb.body.getLinearVelocity().scl(0.1f)), 0, Color.CYAN));
    }

    @Override
    public void onDestroy(){
        healthBar.remove();
    }

    @Override
    public void fixedUpdate(float deltaTime){
        Transform t = getComponent(Transform.class);
        Rigidbody rb = getComponent(Rigidbody.class);

        Vector2 dir = targetShip.getTransform().getPosition2().sub(t.getPosition2());
        float dist = dir.len();
        dir.nor();

        Vector3 futurePos = targetShip.getTransform().getPosition();
        float time = targetShip.getTransform().getPosition().sub(new Vector3(t.getPosition2().add(t.up().scl(1)), 0)).len()/ Projectile.speed;
        futurePos.add(new Vector3(targetShip.getComponent(Rigidbody.class).body.getLinearVelocity().scl(time), 0));
        Vector3 futureDir = futurePos.sub(t.getPosition());
        float angle = MathUtils.atan2(futureDir.y, futureDir.x) - (MathUtils.PI / 2);
        rb.body.setTransform(rb.body.getPosition(), angle);

        HashMap<Float, Vector2> weights = new HashMap<>();
        Vector2 simPos = t.getPosition2().add(t.up().scl(1));
        for(float theta = 0; theta < 360.0f; theta += 10){
            Vector2 scanDir = new Vector2(MathUtils.cosDeg(theta), MathUtils.sinDeg(theta));
            float scanDist = 20.0f;
            RaycastHit[] hits = Physics.RaycastAll(simPos, scanDir, scanDist);
            RaycastHit closest = Physics.GetClosest(hits, simPos);
            float closestDist = scanDist;
            if(closest != null) {
                closestDist = closest.body.getPosition().sub(simPos).len();
            }
            float dot = Vector2.dot(dir.x, dir.y, scanDir.x, scanDir.y) / scanDist;
            float weight = dot * 2 + (closestDist / scanDist * 100);
            weights.put(weight, scanDir.nor());
        }
        /*for(int i = 0; i < weights.size(); i++){
            Game.lines.add(new DebugLine(simPos, simPos.cpy().add(weights.values().toArray(new Vector2[0])[i].scl(weights.keySet().toArray(new Float[0])[i]/10)), 0, Color.CYAN));
        }*/
        Float[] keys = weights.keySet().toArray(new Float[0]);
        Arrays.sort(keys);
        Vector2 bestDir2 = weights.get(keys[keys.length - 1]);

        //Vector2 thrust = bestDir.add(bestDir2).nor();
        Vector2 thrust = bestDir2.nor();
        //Game.lines.add(new DebugLine(t.getPosition2(), t.getPosition2().add(thrust), 0, Color.GREEN));
        Vector2 vel = rb.body.getLinearVelocity().nor();
        if(Vector2.dot(thrust.x, thrust.y, vel.x, vel.y) < 0.8f && rb.body.getLinearVelocity().len() > 2.0f){
            rb.body.setLinearDamping(4);
            rb.body.applyForceToCenter(thrust.scl(thrustForce), true);
        }
        else{
            rb.body.setLinearDamping(0);
            rb.body.applyForceToCenter(thrust.scl(thrustForce), true);
        }
        //rb.body.setTransform(rb.body.getPosition().add(thrust), angle);

    }

    @Override
    public boolean damage(int damage, Entity source){
        health -= damage;
        if(health <= 0){
            Game.DestroyEntity(this);
            return true;
        }
        return false;
    }

    @Override
    public int getHealth(){
        return health;
    }

    @Override
    public void setHealth(int health){
        this.health = health;
    }
}
