package com.jegg.spacesim.core.physics;

import com.badlogic.ashley.core.Entity;

public interface ISensorContactListener {
    void sensorContactEnter(Entity entity);
    void sensorContactExit(Entity entity);
}
