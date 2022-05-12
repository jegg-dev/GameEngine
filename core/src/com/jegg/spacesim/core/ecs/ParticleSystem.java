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
import com.jegg.spacesim.core.Game;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ParticleSystem implements Component {
    public Transform transform;

    public boolean useSquareEmission;
    public float squareEmissionWidth;

    public boolean useConeEmission;
    public float coneEmissionAngle;
    public float coneEmissionRotation;
    public float coneEmissionLength;

    public Vector2 emissionLocalOffset = new Vector2(0,0);

    public boolean useEllipseRender;
    public float[] particleVerts = PolygonRenderer.boxVerts;
    public Vector2 particleScale = new Vector2(1,1);
    public Color particleColor = Color.WHITE;
    public Vector2 particleLocalVelocity = new Vector2(0,0);
    public float particleLifetime = 5;

    public boolean useCollisions;
    public float particleSpawnTime = 1;
    public int maxParticles = 100;
    public float systemPlayTime = 10;
    public boolean looping;
    protected boolean playing;

    private HashMap<Entity, Vector3> particles = new HashMap<>();
    private float systemTimer;
    private float spawnTimer;

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

        for(Map.Entry<Entity, Vector3> e : particles.entrySet()){
            e.setValue(e.getValue().add(0,0,deltaTime));
        }

        HashMap<Entity, Vector3> tempMap = (HashMap<Entity, Vector3>)particles.clone();
        for (Map.Entry<Entity, Vector3> e : tempMap.entrySet()) {
            if (e.getValue().z >= particleLifetime) {
                particles.remove(e.getKey());
                Game.DestroyEntity(e.getKey());
            }
        }
        if(!useCollisions) {
            for (Map.Entry<Entity, Vector3> e : tempMap.entrySet()) {
                Transform t = ComponentMappers.transform.get(e.getKey());
                t.setPosition(t.getPosition().add(e.getValue().x * deltaTime, e.getValue().y * deltaTime, 0));
            }
            /*for (Entity p : particles.keySet()) {
                Transform t = ComponentMappers.transform.get(p);
                t.setPosition(t.getPosition().add(particleVelocity.x * deltaTime, particleVelocity.y * deltaTime, 0));
            }*/
        }

        if(spawnTimer <= 0 && particles.size() < maxParticles){
            spawnTimer = particleSpawnTime;
            Entity p = createParticle();
            Vector3 data = new Vector3(0,0,0);
            float rot = transform.getRotation();
            data.x = MathUtils.cosDeg(rot) * particleLocalVelocity.x - MathUtils.sinDeg(rot) * particleLocalVelocity.y;
            data.y = MathUtils.sinDeg(rot) * particleLocalVelocity.x + MathUtils.cosDeg(rot) * particleLocalVelocity.y;
            if(useCollisions){
                ComponentMappers.rigidbody.get(p).body.setLinearVelocity(data.x, data.y);
            }
            particles.put(p, data);
        }

    }

    protected void render(ShapeRenderer sr){
        sr.setColor(particleColor);
        if(useEllipseRender){
            for(Entity p : particles.keySet()){
                Transform t = ComponentMappers.transform.get(p);
                sr.ellipse(t.getPosition().x, t.getPosition().y, particleScale.x, particleScale.y);
            }
        }
        else {
            Polygon poly = new Polygon(particleVerts);
            poly.setScale(particleScale.x, particleScale.y);
            for (Entity p : particles.keySet()) {
                Transform t = ComponentMappers.transform.get(p);
                poly.setPosition(t.getPosition().x, t.getPosition().y);
                poly.setRotation(t.getRotation());
                sr.polygon(poly.getTransformedVertices());
            }
        }
    }

    public void stop(){
        playing = false;
        for(Entity p : particles.keySet()){
            Game.DestroyEntity(p);
        }
        particles.clear();
    }

    private Entity createParticle(){
        Entity particle = Game.CreateWorldEntity(transform.getPosition(), transform.getRotation());

        float rot = transform.getRotation();
        Vector3 offset = new Vector3(MathUtils.cosDeg(rot) * emissionLocalOffset.x - MathUtils.sinDeg(rot) * emissionLocalOffset.y,
                MathUtils.sinDeg(rot) * emissionLocalOffset.x + MathUtils.cosDeg(rot) * emissionLocalOffset.y, 0);
        offset.add(transform.getPosition());

        Vector3 pos;
        if(useSquareEmission){
            pos = new Vector3(MathUtils.random(-squareEmissionWidth, squareEmissionWidth),
                    MathUtils.random(-squareEmissionWidth, squareEmissionWidth), 0).add(offset);
        }
        else if(useConeEmission){
            float length = MathUtils.random(0, coneEmissionLength);
            float angle = MathUtils.random(coneEmissionRotation - (coneEmissionAngle / 2.0f), coneEmissionRotation + (coneEmissionAngle / 2.0f));
            pos = new Vector3(MathUtils.cosDeg(angle + rot) * length,
                    MathUtils.sinDeg(angle + rot) * length, 0).add(offset);
        }
        else{
            pos = offset;
        }

        if(useCollisions){
            Polygon poly = new Polygon(particleVerts);
            poly.setScale(particleScale.x, particleScale.y);
            Rigidbody rb = Game.CreateRigidbody(poly.getTransformedVertices(), BodyDef.BodyType.DynamicBody, 0.01f);
            //rb.body.setLinearVelocity(particleVelocity);
            rb.body.setTransform(pos.x, pos.y, transform.getRotation() * MathUtils.degreesToRadians);
            particle.add(rb);
        }

        ComponentMappers.transform.get(particle).setPosition(pos);
        return particle;
    }
}
