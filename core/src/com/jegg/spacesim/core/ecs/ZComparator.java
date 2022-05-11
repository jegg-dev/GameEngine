package com.jegg.spacesim.core.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

import java.util.Comparator;

public class ZComparator implements Comparator<Entity> {
    private ComponentMapper<Transform> tm;

    public ZComparator(){
        tm = ComponentMapper.getFor(Transform.class);
    }

    @Override
    public int compare(Entity entityA, Entity entityB){
        float az = tm.get(entityA).getPosition().z;
        float bz = tm.get(entityB).getPosition().z;
        int res = 0;
        if(az > bz){
            res = 1;
        }
        else if(az < bz){
            res = -1;
        }
        return res;
    }
}
