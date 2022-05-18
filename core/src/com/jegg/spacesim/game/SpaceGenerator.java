package com.jegg.spacesim.game;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.jegg.spacesim.core.Game;
import com.jegg.spacesim.core.ecs.PolygonRenderer;
import com.jegg.spacesim.core.ecs.Rigidbody;
import com.jegg.spacesim.core.ecs.StaticFlag;
import com.jegg.spacesim.core.ecs.Transform;

public class SpaceGenerator {
    public final float objectsPerAngle = 2;
    public final float angleSteps = 1;
    public final float maxRadius = 1000;
    public final float minRadius = 10;

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
        for(float angle = 0; angle < 360 - angleSteps; angle += angleSteps){
            for(int i = 0; i < objectsPerAngle; i++){
                float r = MathUtils.random(minRadius, maxRadius);
                Vector3 pos = new Vector3(r * MathUtils.cosDeg(angle), r * MathUtils.sinDeg(angle), 0);
                SpawnRandomAsteroid(pos);
            }
        }
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
