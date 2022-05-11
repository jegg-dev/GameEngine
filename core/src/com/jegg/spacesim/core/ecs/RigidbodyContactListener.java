package com.jegg.spacesim.core.ecs;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import java.util.Arrays;

public class RigidbodyContactListener implements ContactListener {

    public CollisionSystem collisionSystem;

    public RigidbodyContactListener(CollisionSystem collisionSystem){
        this.collisionSystem = collisionSystem;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        /*if(fa.getBody().getUserData() instanceof CollisionSensor && fb.getBody().getUserData() instanceof Entity){
            ((CollisionSensor)fa.getBody().getUserData()).collisionEnter((Entity)fb.getBody().getUserData());
        }
        if(fb.getBody().getUserData() instanceof CollisionSensor && fb.getBody().getUserData() instanceof Entity){
            ((CollisionSensor)fb.getBody().getUserData()).collisionEnter((Entity)fa.getBody().getUserData());
        }*/
        if(fa.getBody().getUserData() instanceof CollisionSensor){
            collisionSystem.contacts.put(contact, CollisionSystem.ContactType.Enter);
        }
        else if(fb.getBody().getUserData() instanceof CollisionSensor){
            collisionSystem.contacts.put(contact, CollisionSystem.ContactType.Enter);
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if(fa.getBody().getUserData() instanceof CollisionSensor){
            collisionSystem.contacts.put(contact, CollisionSystem.ContactType.Exit);
        }
        else if(fb.getBody().getUserData() instanceof CollisionSensor){
            collisionSystem.contacts.put(contact, CollisionSystem.ContactType.Exit);
        }
    }
    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }
}