package com.jegg.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.jegg.engine.AssetDatabase;
import com.jegg.engine.Game;
import com.jegg.engine.ecs.DestroyedFlag;
import com.jegg.engine.ecs.IteratedFlag;
import com.jegg.engine.ecs.Transform;
import com.jegg.engine.particles.ParticleSystem;
import com.jegg.engine.physics.Rigidbody;
import com.jegg.engine.rendering.PolygonRenderer;
import com.jegg.engine.rendering.SpriteRenderer;
import com.jegg.game.world.TerrainController;

public class BombProjectile extends Projectile{
    public BombProjectile(Entity owner) {
        this.owner = owner;

        Transform t = Game.CreateComponent(Transform.class);
        add(t);

        SpriteRenderer sr = new SpriteRenderer();
        sr.setTexture(AssetDatabase.GetTexture("circle"), 256, 256);
        sr.setColor(Color.RED);
        t.scale.set(0.2f, 0.2f);
        add(sr);

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        CircleShape shape = new CircleShape();
        shape.setRadius(0.75f);
        Rigidbody rb = Game.CreateRigidbody(def, shape, 1);
        rb.body.setUserData(this);
        rb.body.getFixtureList().get(0).setSensor(true);
        rb.body.setBullet(true);
        add(rb);

        ParticleSystem trail = Game.CreateComponent(ParticleSystem.class);
        trail.transform = t;
        Polygon poly = new Polygon();
        poly.setVertices(PolygonRenderer.boxVerts);
        poly.setScale(0.3f, 0.3f);
        trail.particleVerts = poly.getTransformedVertices();
        trail.particleSprite = new Sprite(AssetDatabase.GetTexture("square"), 16, 16);
        trail.particleColor = Color.BLUE;
        trail.particleScale.set(0.04f, 0.04f);
        trail.particleLocalVelocity.set(0, 0);
        trail.emissionLocalOffset.set(0, 0);
        trail.useConeEmission = true;
        trail.coneEmissionRotation = 270;
        trail.coneEmissionAngle = 45;
        trail.coneEmissionLength = 0.5f;
        trail.maxParticles = 10000;
        trail.systemPlayTime = 10;
        trail.particleSpawnTime = 0.01f;
        trail.particleLifetime = 0.5f;
        trail.loop = true;
        trail.useCollisions = false;
        trail.decreaseScaleWithLifetime = true;
        trail.destroyParticlesOnStop = false;
        trail.play();
        add(trail);

        add(Game.CreateComponent(IteratedFlag.class));
        Game.AddEntity(this);
    }

    @Override
    public void sensorContactEnter(Entity entity){
        if(getComponent(DestroyedFlag.class) != null){
            return;
        }

        if(entity instanceof TerrainController){
            TerrainController tc = (TerrainController) entity;
            Vector2 pos = getTransform().getPosition2();
            for(int x = (int)pos.x - 10; x < (int)pos.x + 10; x += tc.tilemap.getTileWidth()){
                for(int y = (int)pos.y - 10; y < (int)pos.y + 10; y += tc.tilemap.getTileWidth()){
                    if(Vector2.dst(x, y, (int)pos.x, (int)pos.y) <= 10){
                        tc.removeTile(new Vector3(x, y, 0));
                    }
                }
            }
            Game.DestroyEntity(this);
        }
        else if(entity != owner){
            Game.DestroyEntity(this);
        }
    }
}