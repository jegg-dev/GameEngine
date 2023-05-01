package com.jegg.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.jegg.engine.*;
import com.jegg.engine.ecs.*;
import com.jegg.engine.ecs.Transform;
import com.jegg.engine.particles.ParticleSystem;
import com.jegg.engine.physics.AABBHit;
import com.jegg.engine.physics.Physics;
import com.jegg.engine.physics.RaycastHit;
import com.jegg.engine.physics.Rigidbody;
import com.jegg.engine.rendering.PolygonRenderer;
import com.jegg.engine.rendering.SpriteRenderer;

public class PlayerShip extends IteratedEntity implements IDamageable {
    public Player player;
    public int health = 100;
    public Inventory inventory = new Inventory(4);

    public float zoom = 8.0f;

    public ProgressBar healthBar;
    public Label healthLabel;
    public Slider zoomSlider;
    public Slider chunkSlider;

    public TerrainController tc;

    public float thrustForce = 60;
    public float turnTorque = 75;

    public Entity stars;

    public ParticleSystem trail;

    public Entity turret;

    public float fireTimer = 0.15f;
    public float fireTime = 0.15f;

    public ShipMiningBeam miningBeam = new ShipMiningBeam(this);

    public PlayerShip(){
        Transform t = Game.CreateComponent(Transform.class);
        t.setPosition(new Vector3(0,0,1));
        //PolygonRenderer poly = Game.CreateComponent(PolygonRenderer.class);
        /*float[] verts = new float[]{
                -0.2f, -1,
                0.2f, -1,
                0.5f, -0.5f,
                0.5f, 0.5f,
                0, 1f,
                -0.5f, 0.5f,
                -0.5f, -0.5f
        };*/
        float[] verts = new float[]{
                0, 1,
                1f, -1,
                -1f, -1
        };
        Polygon polyVerts = new Polygon(verts);
        polyVerts.setScale(1.0f, 1.0f);
        verts = polyVerts.getTransformedVertices();

        SpriteRenderer sr = Game.CreateComponent(SpriteRenderer.class);
        sr.setTexture(AssetDatabase.GetTexture("ship2"), 256, 256);
        sr.setColor(Color.GRAY);
        t.scale.set(0.25f, 0.25f);
        add(sr);

        trail = Game.CreateComponent(ParticleSystem.class);
        trail.transform = t;
        trail.particleVerts = PolygonRenderer.boxVerts;
        trail.particleSprite = new Sprite(AssetDatabase.GetTexture("square"), 16, 16);
        trail.particleColor = Color.PURPLE;
        trail.particleScale.set(0.04f, 0.04f);
        trail.particleLocalVelocity.set(0, -5f);
        trail.emissionLocalOffset.set(0, -1f);
        trail.useConeEmission = true;
        trail.coneEmissionRotation = 270;
        trail.coneEmissionAngle = 45;
        trail.coneEmissionLength = 0.5f;
        trail.maxParticles = 10000;
        trail.systemPlayTime = 10;
        trail.particleSpawnTime = 0.01f;
        trail.particleLifetime = 1f;
        trail.loop = true;
        trail.useCollisions = true;
        trail.decreaseScaleWithLifetime = true;
        trail.destroyParticlesOnStop = false;
        add(trail);

        Polygon scaledVerts = new Polygon(verts);
        Rigidbody rb = Game.CreateRigidbody(scaledVerts.getTransformedVertices(), BodyDef.BodyType.DynamicBody, 1);
        rb.body.setUserData(this);
        rb.body.setBullet(true);
        //rb.body.setLinearDamping(2f);
        rb.body.setAngularDamping(10);
        rb.body.getFixtureList().get(0).getFilterData().maskBits = Physics.MASK_PARTICLE;
        add(t);
        add(rb);
        add(Game.CreateComponent(IteratedFlag.class));
        Game.AddEntity(this);

        turret = Game.CreateWorldEntity(new Vector3(0,0,0),0);
        //SpriteRenderer sr2 = Game.CreateComponent(SpriteRenderer.class);
        //sr2.setTexture(AssetDatabase.GetTexture("circle"), 256, 256);
        //sr2.setColor(Color.GRAY);
        turret.getComponent(Transform.class).scale.set(0.05f, 0.05f);
        //turret.add(sr2);

        /*Entity cargo = rb.CreateSpriteFixture(t, AssetDatabase.GetTexture("square"), PolygonRenderer.boxVerts, 0.5f, 1.5f, 1.25f, 0, 0);
        cargo.getComponent(SpriteRenderer.class).setColor(Color.DARK_GRAY);

        Entity cargo2 = rb.CreateSpriteFixture(t, AssetDatabase.GetTexture("square"), PolygonRenderer.boxVerts, 0.5f, 1.5f, -1.25f, 0, 0);
        cargo2.getComponent(SpriteRenderer.class).setColor(Color.DARK_GRAY);*/

        stars = Game.CreateWorldEntity(new Vector3(0, 0, 0), 0);
        SpriteRenderer sr3 = Game.CreateComponent(SpriteRenderer.class);
        Texture tx = new Texture(Gdx.files.internal("stars-new.png"));
        sr3.setTexture(tx, 4096, 4096);
        sr3.setColor(Color.WHITE);
        sr3.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        stars.add(sr3);

        Skin skin = new Skin(Gdx.files.internal("skins/flat/skin.json"));
        /*TextButton tb = new TextButton("Zoom", skin);
        tb.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent e, float x, float y){
                if(zoom == 6.0f) zoom = 2.0f;
                else zoom = 6.0f;
            }
        });
        tb.setPosition(0,0);
        tb.setSize(50,50);
        Game.GetUIStage().addActor(tb);*/
        zoomSlider = new Slider(1, 10, 1, false, skin);
        //zoomSlider.setSize(50f, 200f);
        zoomSlider.setPosition(10, 10);
        zoomSlider.setValue(zoom);
        Game.GetUIStage().addActor(zoomSlider);

        chunkSlider = new Slider(1, 30, 1, false, skin);
        chunkSlider.setPosition(10, 30);
        chunkSlider.setValue(4);
        Game.GetUIStage().addActor(chunkSlider);
    }

    @Override
    public void start(float deltaTime){}

    @Override
    public void update(float deltaTime){
        Transform t = getComponent(Transform.class);
        Rigidbody rb = getComponent(Rigidbody.class);

        stars.getComponent(SpriteRenderer.class).scroll(rb.body.getLinearVelocity().x * deltaTime / 1000, -rb.body.getLinearVelocity().y * deltaTime / 1000);
        //stars.getComponent(Transform.class).setPosition(t.getPosition());
        stars.getComponent(Transform.class).setPosition(new Vector3(t.getPosition().x, t.getPosition().y, -1));

        healthBar.setValue(health);
        healthLabel.setText(health + "/100");

        /*if(rb.body.getLinearVelocity().len() > 15.0f){
            rb.body.setLinearVelocity(rb.body.getLinearVelocity().nor().scl(15.0f));
        }*/

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
            Projectile p = new BombProjectile(this);
            p.getComponent(Rigidbody.class).body.setTransform(turret.getComponent(Transform.class).getPosition2(), MathUtils.atan2(dir.y, dir.x) + MathUtils.HALF_PI);
            p.getTransform().setPosition(turret.getComponent(Transform.class).getPosition());
            p.getTransform().setRotation(MathUtils.atan2(dir.y, dir.x) * MathUtils.radiansToDegrees + 90);
            p.getComponent(SpriteRenderer.class).setColor(Color.BLUE);
            fireTime = fireTimer;
        }

        if(Input.getKeyUp(Input.E)){
            AABBHit[] hits2 = Physics.AABBAll(t.getPosition2(), 1f,1f);
            for(AABBHit hit : hits2){
                if(hit.entity instanceof Station){
                    rb.body.setTransform(((Entity)hit.fixture.getUserData()).getComponent(Transform.class).getPosition2(), 0);
                    rb.body.setLinearVelocity(0,0);
                    rb.body.setAngularVelocity(0);
                    /*player.remove(InactiveFlag.class);
                    player.getComponent(Rigidbody.class).body.setTransform(getTransform().getPosition2(), 0);
                    player.getComponent(Rigidbody.class).body.setActive(true);
                    remove(IteratedFlag.class);
                    rb.body.setType(BodyDef.BodyType.StaticBody);*/
                    break;
                }
            }
        }

        Game.lines.add(new DebugLine(t.getPosition2(), t.getPosition2().add(rb.body.getLinearVelocity().scl(0.2f)), 0, Color.CYAN));

        if(tc != null)
            tc.tilemap.viewDist = (int)chunkSlider.getValue();

        zoom = zoomSlider.getValue();
        GameCamera.GetMain().setPosition(t.getPosition());
        GameCamera.GetMain().setZoom(zoom);
        t.setPosition(new Vector3(t.getPosition().x, t.getPosition().y, 1));
    }

    @Override
    public void physicsUpdate(float deltaTime){
        Transform t = getComponent(Transform.class);
        Rigidbody rb = getComponent(Rigidbody.class);

        //Vector2 gravForce = t.getPosition2().scl(-1.0f).nor().scl(2.0f);
        //rb.body.applyForceToCenter(gravForce, true);

        if(Input.getKey(Input.Space)){
            rb.body.setLinearDamping(2.5f);
        }
        else{
            rb.body.setLinearDamping(0);
        }


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

        if(rb.body.getLinearVelocity().len() > 60.0f){
            trail.particleColor = Color.WHITE;
            rb.body.getFixtureList().get(0).getFilterData().maskBits = ~Physics.MASK_NORMAL;
            if(!trail.isPlaying()) trail.play();
        }
        else{
            trail.particleColor = Color.PURPLE;
            rb.body.getFixtureList().get(0).getFilterData().maskBits = Physics.MASK_PARTICLE;
        }
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