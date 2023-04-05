package com.jegg.engine.physics;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.jegg.engine.ecs.IteratedEntity;
import com.jegg.engine.ecs.IteratedFlag;
import com.jegg.engine.ecs.StaticFlag;
import com.jegg.engine.ecs.Transform;

public class PhysicsSystem extends IntervalIteratingSystem {

    public static final float TIME_STEP = 1/60f;

    protected static World world;
    private final Array<Entity> bodiesQueue;

    private final ComponentMapper<Rigidbody> rm = ComponentMapper.getFor(Rigidbody.class);
    private final ComponentMapper<Transform> tm = ComponentMapper.getFor(Transform.class);
    private final ComponentMapper<IteratedFlag> im = ComponentMapper.getFor(IteratedFlag.class);

    private static ContactSystem ContactSystem;

    public PhysicsSystem(World world, ContactSystem contactSystem){
        super(Family.all(Rigidbody.class, Transform.class).exclude(StaticFlag.class).get(), TIME_STEP);
        PhysicsSystem.world = world;
        this.bodiesQueue = new Array<>();
        PhysicsSystem.ContactSystem = contactSystem;
    }

    @Override
    protected void updateInterval(){
        super.updateInterval();

        world.step(TIME_STEP, 6, 2);
        for(Entity entity : bodiesQueue){
            if(entity instanceof IteratedEntity && im.has(entity)){
                ((IteratedEntity)entity).physicsUpdate(TIME_STEP);
            }
            Transform t = tm.get(entity);
            Rigidbody r = rm.get(entity);
            Vector2 position = r.body.getPosition();
            t.setPosition(new Vector3(position.x, position.y, t.getPosition().z));
            t.setRotation(r.body.getAngle() * MathUtils.radiansToDegrees);
        }

        ContactSystem.update();

        /*timer += Math.min(deltaTime, 0.25f);
        if(timer >= TIME_STEP) {
            int stepCount = MathUtils.floor(timer / TIME_STEP);
            timer -= TIME_STEP * stepCount;
            world.step(TIME_STEP * stepCount, 6, 2);
            for(Entity entity : bodiesQueue){
                if(entity instanceof IteratedEntity && im.has(entity)){
                    ((IteratedEntity)entity).fixedUpdate(TIME_STEP * stepCount);
                }
                Transform t = tm.get(entity);
                Rigidbody r = rm.get(entity);
                Vector2 position = r.body.getPosition();
                t.setPosition(new Vector3(position.x, position.y, t.getPosition().z));
                t.setRotation(r.body.getAngle() * MathUtils.radiansToDegrees);
            }
        }*/
        /*world.step(deltaTime, 6, 2);
        for(Entity entity : bodiesQueue){
            Transform t = tm.get(entity);
            Rigidbody r = rm.get(entity);
            Vector2 position = r.body.getPosition();
            t.setPosition(new Vector3(position.x, position.y, t.getPosition().z));
            t.setRotation(r.body.getAngle() * MathUtils.radiansToDegrees);
        }
        */

        bodiesQueue.clear();
    }

    @Override
    protected void processEntity(Entity entity){
        //Manual static check
        /*if(rm.get(entity).body.getType() != BodyDef.BodyType.StaticBody) {
            bodiesQueue.add(entity);
        }*/
        bodiesQueue.add(entity);
        /*
        Transform t = tm.get(entity);
        Rigidbody rb = rm.get(entity);
        rb.body.setTransform(t.getPosition().x, t.getPosition().y,t.getRotation() * MathUtils.degreesToRadians);
        */
    }
}
