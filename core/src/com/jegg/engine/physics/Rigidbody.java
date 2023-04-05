package com.jegg.engine.physics;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.jegg.engine.Game;
import com.jegg.engine.rendering.PolygonRenderer;
import com.jegg.engine.ecs.StaticFlag;
import com.jegg.engine.ecs.Transform;
import com.jegg.engine.rendering.SpriteRenderSystem;
import com.jegg.engine.rendering.SpriteRenderer;

public class Rigidbody implements Component {

    public Body body;

    public Fixture CreateFixture(float[] verts, float width, float height, float x, float y, float rotation){
        FixtureDef fixtureDef = new FixtureDef();
        Polygon poly = new Polygon(verts);
        poly.setPosition(x, y);
        poly.setScale(width, height);
        poly.setRotation(rotation);
        PolygonShape shape = new PolygonShape();
        shape.set(poly.getTransformedVertices());
        fixtureDef.shape = shape;
        fixtureDef.density = 1;
        return body.createFixture(fixtureDef);
    }

    public Entity CreateSpriteFixture(Transform parent, Texture tx, float[] verts, float width, float height, float x, float y, float rotation){
        Entity entity = Game.CreateWorldEntity(new Vector3(x, y, 0), 0);
        FixtureDef fixtureDef = new FixtureDef();
        Polygon poly = new Polygon(verts);
        poly.setPosition(x, y);
        poly.setRotation(rotation);
        poly.setScale(width, height);
        PolygonShape shape = new PolygonShape();
        shape.set(poly.getTransformedVertices());
        fixtureDef.shape = shape;
        fixtureDef.density = 1;
        body.createFixture(fixtureDef);
        Transform t = entity.getComponent(Transform.class);
        t.setParent(parent);
        t.setLocalPosition(new Vector3(x, y, 0));
        t.setLocalRotation(rotation);

        SpriteRenderer sr = Game.CreateComponent(SpriteRenderer.class);
        sr.setTexture(tx, (int)SpriteRenderSystem.PIXELS_PER_METER, (int)SpriteRenderSystem.PIXELS_PER_METER);
        entity.add(sr);

        t.scale.set(width, height);

        return entity;
    }

    public static Entity BuildStaticBox(Engine engine, World world, Vector2 position, float width, float height){
        Entity box = engine.createEntity();
        PolygonRenderer poly = engine.createComponent(PolygonRenderer.class);
        poly.poly = new Polygon(PolygonRenderer.boxVerts);
        poly.poly.setScale(width, height);
        com.jegg.engine.ecs.Transform t = engine.createComponent(Transform.class);

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
