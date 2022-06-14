package com.jegg.spacesim.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.jegg.spacesim.core.Game;
import com.jegg.spacesim.core.ecs.*;
import com.jegg.spacesim.core.physics.Rigidbody;
import com.jegg.spacesim.core.rendering.PolygonRenderer;

public class Station extends IteratedEntity {

    public Station(){
        Transform t = Game.CreateComponent(Transform.class);
        PolygonRenderer poly = Game.CreateComponent(PolygonRenderer.class);
        poly.poly = new Polygon(PolygonRenderer.boxVerts);
        poly.poly.scale(3);
        poly.color = Color.WHITE;

        Rigidbody rb = Game.CreateRigidbody(poly.poly.getTransformedVertices(), BodyDef.BodyType.StaticBody, 1);
        rb.body.setAngularDamping(5);
        rb.body.setUserData(this);
        rb.body.getFixtureList().get(0).setSensor(true);
        add(t);
        add(rb);
        add(new StaticFlag());
        add(poly);
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