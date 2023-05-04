package com.jegg.game.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.jegg.engine.AssetDatabase;
import com.jegg.engine.Game;
import com.jegg.engine.ecs.IteratedEntity;
import com.jegg.engine.ecs.IteratedFlag;
import com.jegg.engine.ecs.StaticFlag;
import com.jegg.engine.ecs.Transform;
import com.jegg.engine.physics.Rigidbody;
import com.jegg.engine.rendering.PolygonRenderer;
import com.jegg.engine.rendering.SpriteRenderer;

public class Station extends IteratedEntity {
    public int numDocks = 4;
    public Array<Entity> docks = new Array<>();

    public Station(){
        Transform t = Game.CreateComponent(Transform.class);
        t.setPosition(new Vector3(0,0,0));
        t.scale.set(1, 1);
        add(t);

        SpriteRenderer sr = Game.CreateComponent(SpriteRenderer.class);
        sr.setTexture(AssetDatabase.GetTexture("octagon"), 256, 256);
        sr.setColor(Color.ORANGE);
        add(sr);

        float[] verts = new float[]{
                0.66f, 1,
                1, 0.66f,
                1, -0.66f,
                0.66f, -1,
                -0.66f, -1,
                -1, -0.66f,
                -1, 0.66f,
                -0.66f, 1
        };
        Polygon poly = new Polygon(verts);
        poly.setScale(4, 4);
        Rigidbody rb = Game.CreateRigidbody(poly.getTransformedVertices(), BodyDef.BodyType.StaticBody, 1);
        rb.body.setAngularDamping(5);
        rb.body.setUserData(this);
        add(rb);

        Vector3[] dockPositions = new Vector3[]{
                new Vector3(6,0,0),
                new Vector3(0, -6, 0),
                new Vector3(-6, 0, 0),
                new Vector3(0, 6, 0)
        };
        for(int i = 0; i < numDocks; i++){
            Entity dock = Game.CreateWorldEntity(t.getPosition().add(dockPositions[i]), 0);
            Transform dockT = dock.getComponent(Transform.class);
            PolygonRenderer polyR = Game.CreateComponent(PolygonRenderer.class);
            polyR.poly = new Polygon(PolygonRenderer.boxVerts);
            polyR.poly.setScale(2, 2);
            polyR.color = Color.WHITE;
            Fixture fix = rb.CreateFixture(polyR.poly.getTransformedVertices(), 1, 1, dockT.getPosition().x, dockT.getPosition().y, 0);
            fix.setSensor(true);
            fix.setUserData(dock);
            dock.add(polyR);
            docks.add(dock);
        }

        add(new StaticFlag());
        add(Game.CreateComponent(IteratedFlag.class));
        Game.AddEntity(this);
    }

    @Override
    public void start(float deltaTime) {

    }

    @Override
    public void update(float deltaTime) {

    }
}