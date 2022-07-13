package com.jegg.engine.core.physics;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class RigidbodyContactListener implements ContactListener {

    public ContactSystem contactSystem;

    public RigidbodyContactListener(ContactSystem contactSystem){
        this.contactSystem = contactSystem;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if(fa.isSensor() && !fb.isSensor() && fa.getBody().getUserData() instanceof ISensorContactListener){
            contactSystem.sensorContactEnter((Entity)fa.getBody().getUserData(), (Entity)fb.getBody().getUserData());
            //contactSystem.sensorEntryContacts.put((Entity)fa.getBody().getUserData(), (Entity)fb.getBody().getUserData());
        }
        else if(fb.isSensor() && !fa.isSensor() && fb.getBody().getUserData() instanceof ISensorContactListener){
            contactSystem.sensorContactEnter((Entity)fb.getBody().getUserData(), (Entity)fa.getBody().getUserData());
            //contactSystem.sensorEntryContacts.put((Entity)fb.getBody().getUserData(), (Entity)fa.getBody().getUserData());
        }
        else if(!fa.isSensor() && !fb.isSensor() && fa.getBody().getUserData() instanceof IContactListener){
            contactSystem.contactEnter((Entity)fa.getBody().getUserData(), (Entity)fb.getBody().getUserData());
            //contactSystem.entryContacts.put((Entity)fa.getBody().getUserData(), (Entity)fb.getBody().getUserData());
        }
        else if(!fa.isSensor() && !fb.isSensor() && fb.getBody().getUserData() instanceof IContactListener){
            contactSystem.contactEnter((Entity)fb.getBody().getUserData(), (Entity)fa.getBody().getUserData());
            //contactSystem.entryContacts.put((Entity)fb.getBody().getUserData(), (Entity)fa.getBody().getUserData());
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if(fa.isSensor() && !fb.isSensor() && fa.getBody().getUserData() instanceof ISensorContactListener){
            contactSystem.sensorContactExit((Entity)fa.getBody().getUserData(), (Entity)fb.getBody().getUserData());
            //contactSystem.sensorExitContacts.put((Entity)fa.getBody().getUserData(), (Entity)fb.getBody().getUserData());
        }
        else if(fb.isSensor() && !fa.isSensor() && fb.getBody().getUserData() instanceof ISensorContactListener){
            contactSystem.sensorContactExit((Entity)fb.getBody().getUserData(), (Entity)fa.getBody().getUserData());
            //contactSystem.sensorExitContacts.put((Entity)fb.getBody().getUserData(), (Entity)fa.getBody().getUserData());
        }
        else if(!fa.isSensor() && !fb.isSensor() && fa.getBody().getUserData() instanceof IContactListener){
            contactSystem.contactExit((Entity)fa.getBody().getUserData(), (Entity)fb.getBody().getUserData());
            //contactSystem.exitContacts.put((Entity)fa.getBody().getUserData(), (Entity)fb.getBody().getUserData());
        }
        else if(!fa.isSensor() && !fb.isSensor() && fb.getBody().getUserData() instanceof IContactListener){
            contactSystem.contactExit((Entity)fb.getBody().getUserData(), (Entity)fa.getBody().getUserData());
            //contactSystem.exitContacts.put((Entity)fb.getBody().getUserData(), (Entity)fa.getBody().getUserData());
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}
}