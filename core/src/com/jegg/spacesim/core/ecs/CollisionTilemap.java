package com.jegg.spacesim.core.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class CollisionTilemap extends Tilemap<Body> {
    public Polygon tileShape;
    public Entity collisionEntity;

    public CollisionTilemap(int mapWidthInChunks, int chunkWidth, float tileWidth){
        super(mapWidthInChunks, chunkWidth, tileWidth);
        tileShape = new Polygon();
        tileShape.setVertices(new float[]{
            0, 0,
            tileWidth, 0,
            tileWidth, tileWidth,
            0, tileWidth
        });
    }

    @Override
    public void setTile(int x, int y){
        if(getTile(x, y) == null){
            BodyDef def = new BodyDef();
            def.type = BodyDef.BodyType.StaticBody;
            Body body = PhysicsSystem.world.createBody(def);
            PolygonShape shape = new PolygonShape();
            shape.set(tileShape.getVertices());
            body.createFixture(shape,0);
            body.setTransform(x * getTileWidth(), y * getTileWidth(), 0);
            body.setUserData(collisionEntity);
            super.setTile(x, y, body);
        }
    }

    @Override
    public void setTile(int x, int y, Body body){
        Body tileBody = getTile(x, y);
        if(body == null && tileBody != null){
            PhysicsSystem.world.destroyBody(tileBody);
            super.setTile(x, y, null);
        }
        else super.setTile(x, y, body);
    }
}
