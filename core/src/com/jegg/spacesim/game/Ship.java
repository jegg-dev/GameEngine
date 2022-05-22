package com.jegg.spacesim.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.jegg.spacesim.core.*;
import com.jegg.spacesim.core.ecs.*;
import com.jegg.spacesim.core.ecs.Transform;
import com.jegg.spacesim.core.Input;

public class Ship extends IteratedEntity implements IDamageable {
    public Player player;
    public int health = 100;

    public ProgressBar healthBar;
    public Label healthLabel;

    public float thrustForce = 20;
    public float turnTorque = 14;

    public Entity stars;

    public ParticleSystem trail;

    public Entity turret;

    public float fireTimer = 0.15f;
    public float fireTime = 0.15f;

    public ShipMiningBeam miningBeam = new ShipMiningBeam(this);

    public Ship(){
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
        poly.color = Color.GOLD;

        trail = Game.CreateComponent(ParticleSystem.class);
        trail.transform = t;
        trail.particleColor = Color.PURPLE;
        trail.particleScale.set(0.25f, 0.25f);
        trail.particleLocalVelocity.set(0, -1.5f);
        trail.emissionLocalOffset.set(0, -1f);
        trail.useConeEmission = true;
        trail.coneEmissionRotation = 270;
        trail.coneEmissionAngle = 45;
        trail.coneEmissionLength = 1;
        trail.maxParticles = 10000;
        trail.systemPlayTime = 10;
        trail.particleSpawnTime = 0.025f;
        trail.particleLifetime = 5f;
        trail.loop = true;
        trail.useCollisions = false;
        trail.destroyParticlesOnStop = false;
        add(trail);

        Rigidbody rb = Game.CreateRigidbody(verts, BodyDef.BodyType.DynamicBody, 1);
        rb.body.setUserData(this);
        rb.body.setBullet(true);
        rb.body.setLinearDamping(1f);
        rb.body.setAngularDamping(10);
        add(t);
        add(rb);
        add(poly);
        add(Game.CreateComponent(IteratedFlag.class));
        Game.AddEntity(this);

        turret = Game.CreateWorldEntity(new Vector3(0,0,0),0);
        CircleRenderer circle = new CircleRenderer();
        circle.radius = 0.15f;
        circle.color = Color.GOLD;
        turret.add(circle);

        stars = Game.CreateWorldEntity(new Vector3(0, 0, 0), 0);
        SpriteRenderer sr = Game.CreateComponent(SpriteRenderer.class);
        Texture tx = new Texture(Gdx.files.internal("stars-new.png"));
        sr.setTexture(tx, 4096, 4096);
        sr.setColor(Color.WHITE);
        sr.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        stars.add(sr);
    }

    @Override
    public void start(float deltaTime){}

    @Override
    public void update(float deltaTime){
        Transform t = getComponent(Transform.class);
        Rigidbody rb = getComponent(Rigidbody.class);

        stars.getComponent(SpriteRenderer.class).scroll(rb.body.getLinearVelocity().x * deltaTime / 1000, -rb.body.getLinearVelocity().y * deltaTime / 1000);
        stars.getComponent(Transform.class).setPosition(t.getPosition());

        healthBar.setValue(health);
        healthLabel.setText(health + "/100");

        if(rb.body.getLinearVelocity().len() > 15.0f){
            rb.body.setLinearVelocity(rb.body.getLinearVelocity().nor().scl(15.0f));
        }

        fireTime -= deltaTime;
        Vector2 mousePos = new Vector2(GameCamera.GetMain().screenToWorld(Input.MousePos).x, GameCamera.GetMain().screenToWorld(Input.MousePos).y);
        Vector2 shipDir = getTransform().getPosition2().sub(mousePos);
        RaycastHit[] hits = Physics.RaycastAll(mousePos, shipDir, shipDir.len() + 0.1f);
        boolean moved = false;
        if (hits != null) {
            for (RaycastHit hit : hits) {
                if (hit != null && hit.entity == this) {
                    turret.getComponent(Transform.class).setPosition(new Vector3(hit.point, 0));
                    moved = true;
                }
            }
        }
        if (!moved) {
            turret.getComponent(Transform.class).setPosition(getTransform().getPosition());
        }

        if(Input.getKey(Input.Mouse0)){
            miningBeam.use(deltaTime);
        }
        else if(Input.getKey(Input.Mouse1) && fireTime <= 0){
            Vector2 dir = turret.getComponent(Transform.class).getPosition2().sub(mousePos);
            BombProjectile p = new BombProjectile(this);
            p.getComponent(Rigidbody.class).body.setTransform(turret.getComponent(Transform.class).getPosition2(), MathUtils.atan2(dir.y, dir.x) + MathUtils.HALF_PI);
            p.getTransform().setPosition(turret.getComponent(Transform.class).getPosition());
            p.getTransform().setRotation(MathUtils.atan2(dir.y, dir.x) * MathUtils.radiansToDegrees + 90);
            p.getComponent(CircleRenderer.class).color = Color.BLUE;
            fireTime = fireTimer;
        }

        if(Input.getKeyUp(Input.E)){
            Entity[] entities = Physics.AABBAll(t.getPosition2(), 1f,1f);
            for(Entity e : entities){
                if(e instanceof Station){
                    rb.body.setTransform(((Station)e).getTransform().getPosition2(), 0);
                    rb.body.setLinearVelocity(0,0);
                    rb.body.setAngularVelocity(0);
                    player.remove(InactiveFlag.class);
                    player.getComponent(Rigidbody.class).body.setTransform(getTransform().getPosition2(), 0);
                    player.getComponent(Rigidbody.class).body.setActive(true);
                    remove(IteratedFlag.class);
                    rb.body.setType(BodyDef.BodyType.StaticBody);
                    break;
                }
            }
        }

        //Game.lines.add(new DebugLine(t.getPosition2(), t.getPosition2().add(rb.body.getLinearVelocity().scl(0.1f)), 0, Color.CYAN));

        GameCamera.GetMain().setPosition(t.getPosition());
        GameCamera.GetMain().setZoom(8f);
    }

    @Override
    public void fixedUpdate(float deltaTime){
        Transform t = getComponent(Transform.class);
        Rigidbody rb = getComponent(Rigidbody.class);

        /*if(Input.getKey(Input.Space)){
            rb.body.setLinearDamping(2.5f);
        }
        else{
            rb.body.setLinearDamping(0);
        }*/


        Vector2 thrust = t.up().scl(thrustForce);
        thrust.scl(Input.getKey(Input.W) ? 1 : Input.getKey(Input.S) ? -1 : 0);

        if(thrust.len() > 0.0f && !trail.isPlaying()){
            trail.play();
        }
        else if(thrust.len() == 0.0f && trail.isPlaying()){
            trail.stop();
        }

        rb.body.applyForceToCenter(thrust, true);

        rb.body.applyTorque(Input.getKey(Input.D) ? -turnTorque : Input.getKey(Input.A) ? turnTorque : 0, true);

        /*
        Vector2 thrust;
        float x = touchpad.getKnobPercentX();
        float y = touchpad.getKnobPercentY();
        if(x != 0 || y != 0) {
            float angle = MathUtils.atan2(-x, y);
            t.setRotation(angle * MathUtils.radiansToDegrees);
            rb.body.setTransform(rb.body.getPosition().x, rb.body.getPosition().y, angle);
            thrust = t.up().scl(thrustForce);
        }
        else{
            thrust = new Vector2(0,0);
        }

        if(thrust.len() > 0.0f && !trail.isPlaying()){
            trail.play();
        }
        else if(thrust.len() == 0.0f && trail.isPlaying()){
            trail.stop();
        }

        rb.body.applyForceToCenter(thrust, true);
        */
    }

    @Override
    public boolean damage(int damage, Entity source){
        health -= damage;
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