package com.jegg.engine.ecs;

import com.badlogic.ashley.core.Entity;

public abstract class IteratedEntity extends Entity {

    protected boolean started = false;

    public abstract void start(float deltaTime);
    public abstract void update(float deltaTime);
    public void physicsUpdate(float deltaTime){}

    public void onDestroy(){}

    public Transform getTransform(){
        return ComponentMappers.transform.get(this);
    }
}
