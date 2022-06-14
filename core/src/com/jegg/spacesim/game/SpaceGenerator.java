package com.jegg.spacesim.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.jegg.spacesim.core.AssetDatabase;
import com.jegg.spacesim.core.Game;
import com.jegg.spacesim.core.PerlinNoise;
import com.jegg.spacesim.core.rendering.CircleRenderer;
import com.jegg.spacesim.core.rendering.PolygonRenderer;
import com.jegg.spacesim.core.physics.Rigidbody;
import com.jegg.spacesim.core.ecs.StaticFlag;
import com.jegg.spacesim.core.ecs.Transform;
import com.jegg.spacesim.core.rendering.SpriteRenderer;

public class SpaceGenerator {
    public final int objectsPerAngle = 2;
    public final int angleStep = 1;
    public final int maxRadius = 1000;
    public final int minRadius = 50;
    public final int radiusStep = 2;

    public final int width = 5000;
    public final int height = 5000;
    public final float step = 5.5f;

    public static final float[] AsteroidVerts = new float[]{
            -0.5f,-1f,
            0.5f,-1f,
            1f,-0.5f,
            1f,0.5f,
            0.5f,1f,
            -0.5f,1f,
            -1f,0.5f,
            -1f,-0.5f
    };

    public void generate(){
        /*for(int angle = 0; angle < 360; angle += angleStep){
            for(int r = minRadius; r < maxRadius; r += radiusStep){
                Vector3 pos = new Vector3(r * MathUtils.cosDeg(angle), r * MathUtils.sinDeg(angle), 0);
                if(PerlinNoise.At(pos.x, pos.y, 0.5f) > 0.5f)
                    SpawnRandomAsteroid(pos);
            }
        }*/
        /*for(float x = -(float)width / 2; x < (float)width / 2; x += step){
            for(float y = -(float)height / 2; y < (float)height / 2; y += step){
                Vector3 pos = new Vector3(x, y, 0);
                if(PerlinNoise.At(pos.x, pos.y, 0.1f) > 0.5f) {
                    SpawnRandomAsteroid(pos);
                }
            }
        }*/
        Entity planet = Game.CreateWorldEntity(new Vector3(0,0,0), 0);
        SpriteRenderer sr = new SpriteRenderer();
        sr.setTexture(AssetDatabase.GetTexture("circle-256"), 256, 256);
        sr.setColor(Color.WHITE);
        planet.add(sr);
        planet.getComponent(Transform.class).scale.set(50, 50);
    }

    public void SpawnRandomAsteroid(Vector3 pos){
        Asteroid asteroid = Game.CreateEntity(Asteroid.class);
        Transform t = Game.CreateComponent(Transform.class);
        t.setPosition(pos);
        asteroid.add(t);

        float [] verts = AsteroidVerts.clone();
        for(int i = 0; i < verts.length; i++){
            verts[i] = verts[i] + MathUtils.random(-0.2f,0.2f);
        }
        Rigidbody rb = Game.CreateRigidbody(verts, BodyDef.BodyType.StaticBody, 0);
        rb.body.setUserData(asteroid);
        rb.body.setTransform(pos.x, pos.y, 0);
        asteroid.add(rb);
        asteroid.add(new StaticFlag());
        PolygonRenderer poly = Game.CreateComponent(PolygonRenderer.class);
        poly.poly = new Polygon(verts);
        poly.color = Color.GRAY;
        asteroid.add(poly);
        Game.AddEntity(asteroid);
    }

    public static Asteroid CreateAsteroid(Vector3 pos, float[] verts, boolean staticFlag){
        Asteroid asteroid = Game.CreateEntity(Asteroid.class);
        Transform t = Game.CreateComponent(Transform.class);
        t.setPosition(pos);
        asteroid.add(t);

        Rigidbody rb;
        if(staticFlag) {
            rb = Game.CreateRigidbody(verts, BodyDef.BodyType.StaticBody, 0);
            asteroid.add(new StaticFlag());
        }
        else{
            rb = Game.CreateRigidbody(verts, BodyDef.BodyType.DynamicBody, 1);
        }

        rb.body.setUserData(asteroid);
        rb.body.setTransform(pos.x, pos.y, 0);
        asteroid.add(rb);

        PolygonRenderer poly = Game.CreateComponent(PolygonRenderer.class);
        poly.poly = new Polygon(verts);
        poly.color = Color.GRAY;
        asteroid.add(poly);
        Game.AddEntity(asteroid);
        return asteroid;
    }

    public static void CreateAsteroid(Vector3 pos, float[] verts, Vector2 vel){
        Asteroid asteroid = CreateAsteroid(pos, verts, false);
        asteroid.getComponent(Rigidbody.class).body.setLinearVelocity(vel);
    }
}
