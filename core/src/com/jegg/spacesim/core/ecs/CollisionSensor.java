package com.jegg.spacesim.core.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

public interface CollisionSensor {
    void collisionEnter(Fixture fixture, Entity collisionEntity, Contact contact);
    void collisionExit(Fixture fixture, Entity collisionEntity, Contact contact);
}
