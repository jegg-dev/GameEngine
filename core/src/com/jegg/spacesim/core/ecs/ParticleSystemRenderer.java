package com.jegg.spacesim.core.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.jegg.spacesim.core.Game;
import com.jegg.spacesim.core.GameCamera;

import java.util.Comparator;

public class ParticleSystemRenderer extends SortedIteratingSystem {

    private ShapeRenderer renderer;
    private Array<Entity> renderQueue;
    private Comparator<Entity> comparator;

    private ComponentMapper<ParticleSystem> particlesM;

    public ParticleSystemRenderer(ShapeRenderer renderer){
        super(Family.all(ParticleSystem.class).exclude(InactiveFlag.class).get(), new ZComparator());

        particlesM = ComponentMapper.getFor(ParticleSystem.class);

        renderQueue = new Array<>();
        this.renderer = renderer;
    }

    @Override
    public void update(float deltaTime){
        super.update(deltaTime);
        //renderQueue.sort(comparator);

        //renderer.begin(ShapeRenderer.ShapeType.Line);
        for(Entity entity : renderQueue){
            ParticleSystem ps = particlesM.get(entity);

            if(ps.playing || ps.decaying){
                if(!Game.debugging)
                    ps.update(deltaTime);

                ps.render(renderer);
            }
        }
        //renderer.end();
        renderQueue.clear();
    }

    @Override
    public void processEntity(Entity entity, float deltaTime){
        renderQueue.add(entity);
    }
}
