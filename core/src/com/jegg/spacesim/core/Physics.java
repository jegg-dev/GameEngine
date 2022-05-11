package com.jegg.spacesim.core;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

public class Physics {
    protected static World world;

    public static RaycastHit[] RaycastAll(Vector2 position, Vector2 direction, float distance){
        final Array<RaycastHit> hits = new Array<>();
        Vector2 position2 = position.cpy().add(direction.nor().scl(distance));
        world.rayCast(new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                hits.add(new RaycastHit((Entity)fixture.getBody().getUserData(), fixture.getBody(), fixture, point.cpy()));
                return 1;
            }
        }, position, position2);
        return hits.toArray(RaycastHit.class);
    }

    public static RaycastHit Raycast(Vector2 position, Vector2 direction, float distance){
        final RaycastHit hit = new RaycastHit(null, null, null, null);
        Vector2 position2 = position.cpy().add(direction.nor().scl(distance));
        world.rayCast(new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                hit.entity = (Entity)fixture.getBody().getUserData();
                hit.body = fixture.getBody();
                hit.fixture = fixture;
                hit.point = point.cpy();
                return 1;
            }
        }, position, position2);
        return hit;
    }

    public static Entity[] AABBAll(Vector2 position, float width, float height){
        final Array<Entity> entities = new Array<>();
        world.QueryAABB(new QueryCallback() {
            @Override
            public boolean reportFixture(Fixture fixture) {
                entities.add((Entity)fixture.getBody().getUserData());
                return true;
            }
        }, position.x - (width / 2f), position.y - (height / 2f), position.x + (width / 2f), position.y + (height / 2f));
        return entities.toArray(Entity.class);
    }

    public static Entity AABB(Vector2 position, float width, float height){
        final Entity[] entity = new Entity[1];
        world.QueryAABB(new QueryCallback() {
            @Override
            public boolean reportFixture(Fixture fixture) {
                entity[0] = (Entity)fixture.getBody().getUserData();
                return false;
            }
        }, position.x - (width / 2f), position.y - (height / 2f), position.x + (width / 2f), position.y + (height / 2f));
        return entity[0];
    }

    public static RaycastHit GetClosest(RaycastHit[] hits, Vector2 pos){
        RaycastHit closest = null;
        float closestDist = Float.MAX_VALUE;
        for(RaycastHit hit : hits) {
            if(hit != null && hit.point != null && !hit.fixture.isSensor()){
                float dist = hit.point.cpy().sub(pos).len();
                if(dist < closestDist){
                    closestDist = dist;
                    closest = hit;
                }
            }
        }
        return closest;
    }

    public static World GetWorld(){
        return world;
    }
    public static Body CreateBody(BodyDef def){
        return world.createBody(def);
    }
}
