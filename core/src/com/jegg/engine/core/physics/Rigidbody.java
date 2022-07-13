package com.jegg.engine.core.physics;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.jegg.engine.core.rendering.PolygonRenderer;
import com.jegg.engine.core.ecs.StaticFlag;
import com.jegg.engine.core.ecs.Transform;

public class Rigidbody implements Component {

    public Body body;

    public static Entity BuildStaticBox(Engine engine, World world, Vector2 position, float width, float height){
        Entity box = engine.createEntity();
        PolygonRenderer poly = engine.createComponent(PolygonRenderer.class);
        poly.poly = new Polygon(PolygonRenderer.boxVerts);
        poly.poly.setScale(width, height);
        com.jegg.engine.core.ecs.Transform t = engine.createComponent(Transform.class);

        Rigidbody rb = engine.createComponent(Rigidbody.class);
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(def);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2,height / 2);
        body.createFixture(shape, 0);
        rb.body = body;

        box.add(rb);
        box.add(poly);
        box.add(t);
        box.add(new StaticFlag());
        return box;
    }
}
