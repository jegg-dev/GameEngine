package com.jegg.spacesim.core.ecs;

import com.badlogic.ashley.core.ComponentMapper;

public class ComponentMappers {
    public static ComponentMapper<Transform> transform;
    public static ComponentMapper<Rigidbody> rigidbody;

    static{
        transform = ComponentMapper.getFor(Transform.class);
        rigidbody = ComponentMapper.getFor(Rigidbody.class);
    }
}
