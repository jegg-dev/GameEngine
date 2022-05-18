package com.jegg.spacesim.core.ecs;

import com.badlogic.ashley.core.Entity;

public interface ISensorContactListener {
    void sensorContactEnter(Entity entity);
    void sensorContactExit(Entity entity);
}
