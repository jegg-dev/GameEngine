package com.jegg.spacesim.core.ecs;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import com.jegg.spacesim.core.Game;

public class ParticleSystem implements Component {
    public Transform transform;

    public boolean useCircleEmission;
    public float circleEmissionRadius;
    public boolean useConeEmission;
    public float coneEmissionAngle;
    public float coneEmissionRotation;

    public float[] particleVerts;
    public float particleScale = 1.0f;
    public Color particleColor = Color.WHITE;
    public Vector2 particleVelocity;
    public float particleLifetime = 5;

    public boolean useCollisions;
    public float particleSpawnTime = 1;
    public int maxParticles = 100;
    public float systemPlayTime = 10;
    public boolean looping;
    protected boolean playing;

    private Array<Entity> particles = new Array<>();
    private float systemTimer;
    private float spawnTimer;
    private float lastParticleLifetime;

    public void play(){
        playing = true;
        systemTimer = systemPlayTime;
        spawnTimer = particleSpawnTime;
    }

    protected void update(float deltaTime){
        systemTimer -= deltaTime;
        spawnTimer -= deltaTime;
        if(systemTimer <= 0){
            if(looping){
                systemTimer = systemPlayTime;
            }
            else stop(); return;
        }



        if(!useCollisions) {
            for (Entity p : particles) {
                Transform t = ComponentMappers.transform.get(p);
                t.setPosition(t.getPosition().add(particleVelocity.x * deltaTime, particleVelocity.y * deltaTime, 0));
            }
        }

    }

    protected void render(ShapeRenderer sr){
        sr.setColor(particleColor);
        for(Entity p : particles){
            Transform t = ComponentMappers.transform.get(p);
            Polygon poly = new Polygon(particleVerts);
            poly.setPosition(t.getPosition().x, t.getPosition().y);
            poly.setRotation(t.getRotation());
            sr.polygon(poly.getTransformedVertices());
        }
    }

    public void stop(){
        playing = false;
        for(Entity p : particles){
            Game.DestroyEntity(p);
        }
        particles.clear();
    }

    private Entity createParticle(){
        Entity particle = Game.CreateWorldEntity(transform.getPosition(), transform.getRotation());
        if(useCollisions){
            Rigidbody rb = Game.CreateRigidbody(particleVerts, BodyDef.BodyType.DynamicBody, 1);
            rb.body.setLinearVelocity(particleVelocity);
            particle.add(rb);
        }
        return particle;
    }
}
