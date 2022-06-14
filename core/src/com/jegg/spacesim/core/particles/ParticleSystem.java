package com.jegg.spacesim.core.particles;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.jegg.spacesim.core.Game;
import com.jegg.spacesim.core.ecs.ComponentMappers;
import com.jegg.spacesim.core.rendering.PolygonRenderer;
import com.jegg.spacesim.core.ecs.Transform;
import com.jegg.spacesim.core.physics.Physics;
import com.jegg.spacesim.core.physics.Rigidbody;

import java.util.HashMap;
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

    public Sprite particleSprite;
    public float[] particleVerts = PolygonRenderer.boxVerts;
    public Vector2 particleScale = new Vector2(1,1);
    public Color particleColor = Color.WHITE;
    public Vector2 particleLocalVelocity = new Vector2(0,0);
    public boolean decreaseScaleWithLifetime;
    public float particleLifetime = 5;

    public boolean useCollisions;
    public float particleSpawnTime = 1;
    public int maxParticles = 100;
    public float systemPlayTime = 10;
    public boolean loop;
    public boolean destroyParticlesOnStop = true;
    protected boolean playing;
    protected boolean decaying;

    private HashMap<Entity, Vector3> particles = new HashMap<>();
    private float systemTimer;
    private float spawnTimer;

    public void play(){
        playing = true;
        decaying = false;
        systemTimer = systemPlayTime;
        spawnTimer = particleSpawnTime;
    }

    public boolean isPlaying() { return playing; }

    protected void update(float deltaTime){
        systemTimer -= deltaTime;
        spawnTimer -= deltaTime;
        if(systemTimer <= 0){
            if(loop){
                systemTimer = systemPlayTime;
            }
            else if(destroyParticlesOnStop){
                stop();
                return;
            }
            else if(particles.size() > 0){
                playing = false;
                decaying = true;
            }
            else{
                decaying = false;
                return;
            }
        }

        for(Map.Entry<Entity, Vector3> e : particles.entrySet()){
            e.setValue(e.getValue().add(0,0,deltaTime));
        }

        HashMap<Entity, Vector3> tempMap = new HashMap<>(particles);
        for (Map.Entry<Entity, Vector3> e : tempMap.entrySet()) {
            if (e.getValue().z >= particleLifetime) {
                particles.remove(e.getKey());
                Game.DestroyEntity(e.getKey());
            }
        }

        if(!useCollisions) {
            for (Map.Entry<Entity, Vector3> e : particles.entrySet()) {
                Transform t = ComponentMappers.transform.get(e.getKey());
                t.setPosition(t.getPosition().add(e.getValue().x * deltaTime, e.getValue().y * deltaTime, 0));
                if(decreaseScaleWithLifetime)
                    t.scale.set(particleScale.x * ((1 - (e.getValue().z / particleLifetime))), particleScale.y * ((1 - (e.getValue().z / particleLifetime))));
            }
        }
        else if(decreaseScaleWithLifetime){
            for (Map.Entry<Entity, Vector3> e : particles.entrySet()) {
                Transform t = ComponentMappers.transform.get(e.getKey());
                t.scale.set(particleScale.x * ((1 - (e.getValue().z / particleLifetime))), particleScale.y * ((1 - (e.getValue().z / particleLifetime))));
                //ComponentMappers.rigidbody.get(e.getKey()).body.getFixtureList().get(0).getShape().setRadius(t.scale.x);
            }
        }

        if(!decaying && spawnTimer <= 0 && particles.size() < maxParticles){
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

    protected void render(SpriteBatch batch){
        particleSprite.setColor(particleColor);
        particleSprite.setOrigin(particleSprite.getWidth() / 2, particleSprite.getHeight() / 2);
        if(!decreaseScaleWithLifetime){
            particleSprite.setScale(particleScale.x, particleScale.y);
        }
        for (Entity p : particles.keySet()) {
            Transform t = ComponentMappers.transform.get(p);
            if(decreaseScaleWithLifetime) {
                particleSprite.setScale(t.scale.x, t.scale.y);
            }
            particleSprite.setOriginBasedPosition(t.getPosition2().x, t.getPosition2().y);
            particleSprite.setRotation(t.getRotation());
            particleSprite.draw(batch);
        }
    }

    public void stop(){
        playing = false;
        if(destroyParticlesOnStop) {
            for (Entity p : particles.keySet()) {
                Game.DestroyEntity(p);
            }
            particles.clear();
        } else if(particles.size() > 0) decaying = true;
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
            //poly.setScale(particleScale.x, particleScale.y);
            Rigidbody rb = Game.CreateRigidbody(poly.getTransformedVertices(), BodyDef.BodyType.DynamicBody, 0.01f);
            rb.body.setTransform(pos.x, pos.y, transform.getRotation() * MathUtils.degreesToRadians);
            Filter filter = new Filter();
            filter.categoryBits = Physics.CATEGORY_PARTICLE;
            filter.maskBits = Physics.MASK_PARTICLE;
            rb.body.getFixtureList().get(0).setFilterData(filter);
            particle.add(rb);
        }

        ComponentMappers.transform.get(particle).setPosition(pos);
        ComponentMappers.transform.get(particle).scale.set(particleScale);
        return particle;
    }
}
