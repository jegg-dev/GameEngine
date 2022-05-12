package com.jegg.spacesim.core.ecs;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import com.jegg.spacesim.core.Game;

public class ParticleSystem implements Component {
    public Transform transform;

    public boolean useSquareEmission;
    public float squareEmissionWidth;

    public boolean useConeEmission;
    public float coneEmissionAngle;
    public float coneEmissionRotation;
    public float coneEmissionLength;

    public float[] particleVerts;
    public Vector2 particleScale = new Vector2(1,1);
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

        if(spawnTimer <= 0){
            spawnTimer = particleSpawnTime;
            Entity p = createParticle();
            particles.add(p);
            Vector3 pos;
            if(useSquareEmission){
                pos = new Vector3(MathUtils.random(-squareEmissionWidth, squareEmissionWidth),
                        MathUtils.random(-squareEmissionWidth, squareEmissionWidth), 0);
            }
            else if(useConeEmission){
                float length = MathUtils.random(0, coneEmissionLength);
                float angle = MathUtils.random(coneEmissionRotation - (coneEmissionAngle / 2.0f), coneEmissionRotation + (coneEmissionAngle / 2.0f));
                pos = new Vector3(MathUtils.cosDeg(angle) * length, MathUtils.sinDeg(angle) * length, 0);
            }
            else{
                pos = transform.getPosition();
            }
            ComponentMappers.transform.get(p).setPosition(pos);
        }

    }

    protected void render(ShapeRenderer sr){
        sr.setColor(particleColor);
        Polygon poly = new Polygon(particleVerts);
        poly.setScale(particleScale.x, particleScale.y);

        for(Entity p : particles){
            Transform t = ComponentMappers.transform.get(p);
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
