package com.jegg.engine.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.jegg.engine.core.Game;
import com.jegg.engine.core.physics.Rigidbody;
import com.jegg.engine.core.ecs.Transform;
import com.jegg.engine.core.rendering.PolygonSpriteRenderer;

public class Asteroid extends Entity {
    public void Hit(){
        Transform t = getComponent(Transform.class);
        PolygonSpriteRenderer polyR = getComponent(PolygonSpriteRenderer.class);

        Polygon poly = new Polygon(polyR.getVertices());
        poly.setScale(1f / 128f, 1f / 128f);
        float[] polyVerts = poly.getTransformedVertices();
        if(polyVerts.length > 10) {
            int vertsAIndex = MathUtils.random(0, polyVerts.length / 2 - 1) * 2;
            int vertsBIndex = vertsAIndex + (polyVerts.length / 2);
            if (vertsBIndex > polyVerts.length - 2) {
                vertsBIndex = 0;
            }

            float[] vertsA = new float[polyVerts.length / 2 + 2];
            float[] vertsB = new float[polyVerts.length / 2 + 2];

            for (int i = 0; i < polyVerts.length / 2 + 2; i += 2) {
                vertsA[i] = polyVerts[vertsAIndex];
                vertsA[i + 1] = polyVerts[vertsAIndex + 1];
                vertsB[i] = polyVerts[vertsBIndex];
                vertsB[i + 1] = polyVerts[vertsBIndex + 1];
                vertsAIndex += 2;
                vertsBIndex += 2;
                if (vertsAIndex > polyVerts.length - 2) {
                    vertsAIndex = 0;
                }
                if (vertsBIndex > polyVerts.length - 2) {
                    vertsBIndex = 0;
                }
            }
            Spacemap.CreateAsteroid(t.getPosition(), vertsA, new Vector2(MathUtils.random(-1f, 1f), MathUtils.random(-1f, 1f)).nor().scl(0.2f));
            Spacemap.CreateAsteroid(t.getPosition(), vertsB, new Vector2(MathUtils.random(-1f, 1f), MathUtils.random(-1f, 1f)).nor().scl(0.2f));
            Game.DestroyEntity(this);
        }
        else{
            Loot loot = new Loot(Color.GRAY);
            Vector3 position = t.getPosition();
            float rotation = t.getRotation();
            loot.getComponent(Rigidbody.class).body.setTransform(position.x, position.y, rotation * MathUtils.degreesToRadians);
            loot.getComponent(Transform.class).setPosition(position);
            loot.getComponent(Transform.class).setRotation(rotation);
            Game.DestroyEntity(this);
        }
    }
}