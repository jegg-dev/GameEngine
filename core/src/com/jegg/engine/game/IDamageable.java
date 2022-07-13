package com.jegg.engine.game;

import com.badlogic.ashley.core.Entity;

public interface IDamageable {
    //Should return true if target was killed
    boolean damage(int damage, Entity source);

    int getHealth();
    void setHealth(int health);
}
