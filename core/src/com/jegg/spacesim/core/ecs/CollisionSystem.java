package com.jegg.spacesim.core.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;

import java.util.HashMap;

public class CollisionSystem {

    public enum ContactType{
        Enter,
        Exit
    }

    public HashMap<Contact, ContactType> contacts = new HashMap<>();

    public void update(){
        for(Contact c : contacts.keySet()){
            if(c.getFixtureA() != null && c.getFixtureB() != null) {
                Body bodyA = c.getFixtureA().getBody();
                Body bodyB = c.getFixtureB().getBody();

                if (contacts.get(c) == ContactType.Enter) {
                    if (bodyA.getUserData() instanceof CollisionSensor) {
                        ((CollisionSensor) bodyA.getUserData()).collisionEnter(c.getFixtureB(), (Entity) bodyB.getUserData(), c);
                    }
                    if (bodyB.getUserData() instanceof CollisionSensor) {
                        ((CollisionSensor) bodyB.getUserData()).collisionEnter(c.getFixtureA(), (Entity) bodyA.getUserData(), c);
                    }
                } else {
                    if (bodyA.getUserData() instanceof CollisionSensor) {
                        ((CollisionSensor) bodyA.getUserData()).collisionExit(c.getFixtureB(), (Entity) c.getFixtureB().getBody().getUserData(), c);
                    }
                    if (bodyB.getUserData() instanceof CollisionSensor) {
                        ((CollisionSensor) bodyB.getUserData()).collisionExit(c.getFixtureA(), (Entity) bodyA.getUserData(), c);
                    }
                }
            }
        }
        contacts.clear();
    }

}
