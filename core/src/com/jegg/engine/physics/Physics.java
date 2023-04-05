package com.jegg.engine.physics;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

public class Physics {
    private static World world;

    public static final short CATEGORY_NORMAL = 0x0001;
    public static final short CATEGORY_PARTICLE = 0x0002;
    public static final short MASK_NORMAL = -1;
    public static final short MASK_PARTICLE = ~CATEGORY_PARTICLE;

    public static void CreateWorld(){
        if(world == null){
            world = new World(new Vector2(0, 0), true);
        }
    }

    public static RaycastHit[] RaycastAll(Vector2 position, Vector2 direction, float distance){
        if(distance == 0.0f || (direction.x == 0.0f && direction.y == 0.0f)) return null;
        if(Float.isNaN(position.x) || Float.isNaN(position.y)) return null;

        final Array<RaycastHit> hits = new Array<>();
        Vector2 position2 = position.cpy().add(direction.nor().scl(distance));
        world.rayCast(new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                hits.add(new RaycastHit((Entity) fixture.getBody().getUserData(), fixture.getBody(), fixture, point.cpy()));
                return 1;
            }
        }, position, position2);
        return hits.toArray(RaycastHit.class);
    }

    public static RaycastHit Raycast(Vector2 position, Vector2 direction, float distance){
        if(distance == 0.0f || (direction.x == 0.0f && direction.y == 0.0f)) return null;
        if(Float.isNaN(position.x) || Float.isNaN(position.y)) return null;

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

    public static AABBHit[] AABBAll(Vector2 position, float width, float height){
        if(width == 0.0f || height == 0.0f) return null;
        if(Float.isNaN(position.x) || Float.isNaN(position.y)) return null;

        final Array<AABBHit> hits = new Array<>();
        world.QueryAABB(new QueryCallback() {
            @Override
            public boolean reportFixture(Fixture fixture) {
                hits.add(new AABBHit((Entity)fixture.getBody().getUserData(), fixture.getBody(), fixture));
                return true;
            }
        }, position.x - (width / 2f), position.y - (height / 2f), position.x + (width / 2f), position.y + (height / 2f));
        return hits.toArray(AABBHit.class);
    }

    public static AABBHit AABB(Vector2 position, float width, float height){
        if(width == 0.0f || height == 0.0f) return null;
        if(Float.isNaN(position.x) || Float.isNaN(position.y)) return null;

        final AABBHit[] hits = new AABBHit[1];
        world.QueryAABB(new QueryCallback() {
            @Override
            public boolean reportFixture(Fixture fixture) {
                hits[0] = new AABBHit((Entity)fixture.getBody().getUserData(), fixture.getBody(), fixture);
                return false;
            }
        }, position.x - (width / 2f), position.y - (height / 2f), position.x + (width / 2f), position.y + (height / 2f));
        return hits[0];
    }

    public static RaycastHit GetClosest(RaycastHit[] hits, Vector2 position){
        if(Float.isNaN(position.x) || Float.isNaN(position.y)) return null;

        RaycastHit closest = null;
        float closestDist = Float.MAX_VALUE;
        for(RaycastHit hit : hits) {
            if(hit != null && hit.point != null && !hit.fixture.isSensor()){
                float dist = hit.point.cpy().sub(position).len();
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
}
