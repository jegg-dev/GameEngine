package com.jegg.spacesim.core.particles;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.jegg.spacesim.core.Game;
import com.jegg.spacesim.core.ecs.InactiveFlag;
import com.jegg.spacesim.core.rendering.ZComparator;

import java.util.Comparator;

public class ParticleSystemRenderer extends SortedIteratingSystem {

    private SpriteBatch batch;
    private Array<Entity> renderQueue;
    private Comparator<Entity> comparator;

    private ComponentMapper<ParticleSystem> particlesM;

    public ParticleSystemRenderer(SpriteBatch batch){
        super(Family.all(ParticleSystem.class).exclude(InactiveFlag.class).get(), new ZComparator());

        particlesM = ComponentMapper.getFor(ParticleSystem.class);

        renderQueue = new Array<>();
        this.batch = batch;
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

                ps.render(batch);
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
