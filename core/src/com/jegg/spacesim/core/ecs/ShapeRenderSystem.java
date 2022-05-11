package com.jegg.spacesim.core.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.Array;

import java.util.Comparator;

public class ShapeRenderSystem extends SortedIteratingSystem {

    private ShapeRenderer renderer;
    private Array<Entity> renderQueue;
    private Comparator<Entity> comparator;

    private ComponentMapper<Transform> transformM;
    private ComponentMapper<PolygonRenderer> polygonM;
    private ComponentMapper<CircleRenderer> circleM;

    public ShapeRenderSystem(ShapeRenderer renderer){
        super(Family.all(Transform.class).one(Transform.class, CircleRenderer.class).exclude(InactiveFlag.class).get(), new ZComparator());

        transformM = ComponentMapper.getFor(Transform.class);
        polygonM = ComponentMapper.getFor(PolygonRenderer.class);
        circleM = ComponentMapper.getFor(CircleRenderer.class);

        renderQueue = new Array<>();
        this.renderer = renderer;
    }

    @Override
    public void update(float deltaTime){
        super.update(deltaTime);
        //renderQueue.sort(comparator);

        renderer.setProjectionMatrix(RenderSystem.getCamera().combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);

        for(Entity entity : renderQueue){
            if(polygonM.has(entity)) {
                PolygonRenderer polyr = polygonM.get(entity);
                Transform t = transformM.get(entity);

                renderer.setColor(polyr.color);
                Polygon poly = new Polygon(polyr.poly.getTransformedVertices());
                poly.setPosition(t.getPosition().x, t.getPosition().y);
                poly.setRotation(t.getRotation());
                renderer.polygon(poly.getTransformedVertices());
            }
            else{
                CircleRenderer circle = circleM.get(entity);
                Transform t = transformM.get(entity);

                renderer.setColor(circle.color);
                renderer.circle(t.getPosition().x, t.getPosition().y, circle.radius, circle.segments);
            }
        }
        renderer.end();
        renderQueue.clear();
    }

    @Override
    public void processEntity(Entity entity, float deltaTime){
        renderQueue.add(entity);
    }
}
