package com.jegg.spacesim.core.physics;

import com.badlogic.ashley.core.Entity;

public interface IContactListener {
    void contactEnter(Entity entity);
    void contactExit(Entity entity);
}