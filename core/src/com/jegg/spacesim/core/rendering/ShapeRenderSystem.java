package com.jegg.spacesim.core.rendering;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.jegg.spacesim.core.GameCamera;
import com.jegg.spacesim.core.ecs.InactiveFlag;
import com.jegg.spacesim.core.ecs.Transform;

import java.util.Comparator;

public class ShapeRenderSystem extends SortedIteratingSystem {

    private final ShapeRenderer renderer;
    private final SpriteBatch batch;
    private final Array<Entity> renderQueue;
    private Comparator<Entity> comparator;

    private final ComponentMapper<Transform> transformM;
    private final ComponentMapper<PolygonRenderer> polygonM;
    private final ComponentMapper<CircleRenderer> circleM;
    private final ComponentMapper<LineRenderer> lineM;

    public ShapeRenderSystem(ShapeRenderer renderer, SpriteBatch batch){
        super(Family.all(Transform.class).one(PolygonRenderer.class, CircleRenderer.class, LineRenderer.class).exclude(InactiveFlag.class).get(), new ZComparator());
        transformM = ComponentMapper.getFor(Transform.class);
        polygonM = ComponentMapper.getFor(PolygonRenderer.class);
        circleM = ComponentMapper.getFor(CircleRenderer.class);
        lineM = ComponentMapper.getFor(LineRenderer.class);

        renderQueue = new Array<>();
        this.renderer = renderer;
        this.batch = batch;
    }

    @Override
    public void update(float deltaTime){
        super.update(deltaTime);
        //renderQueue.sort(comparator);

        batch.end();
        renderer.setProjectionMatrix(GameCamera.GetMain().getCombined());
        renderer.begin(ShapeRenderer.ShapeType.Line);

        for(Entity entity : renderQueue){
            if(polygonM.has(entity)) {
                Transform t = transformM.get(entity);
                PolygonRenderer polyr = polygonM.get(entity);
                /*Rectangle bounds = polyr.poly.getBoundingRectangle();
                Vector3 dim = new Vector3(bounds.width, bounds.height, 1);
                Vector3 pos = GameCamera.GetMain().screenToWorld(t.getPosition());
                if(GameCamera.GetMain().boundsInFrustum(pos, dim)){
                    continue;
                }*/
                renderer.setColor(polyr.color);
                Polygon poly = new Polygon(polyr.poly.getTransformedVertices());
                poly.setPosition(t.getPosition().x, t.getPosition().y);
                poly.setRotation(t.getRotation());
                renderer.polygon(poly.getTransformedVertices());
            }
            else if(circleM.has(entity)){
                Transform t = transformM.get(entity);
                CircleRenderer circle = circleM.get(entity);
                /*if(GameCamera.GetMain().boundsInFrustum(t.getPosition(), new Vector3(circle.radius * 2, circle.radius * 2, 1))){
                    continue;
                }*/

                renderer.setColor(circle.color);
                renderer.circle(t.getPosition().x, t.getPosition().y, circle.radius, circle.segments);
            }
            else{
                Transform t = transformM.get(entity);
                LineRenderer line = lineM.get(entity);

                if(line.points.size > 1) {
                    renderer.setColor(line.color);

                    if (line.useLocalSpace) {
                        Vector3 pos = t.getPosition();
                        for (int i = 0; i < line.points.size; i += 2) {
                            renderer.rectLine(line.points.get(i).cpy().add(pos.x, pos.y),
                                    line.points.get(i + 1).cpy().add(pos.x, pos.y), line.width);
                        }
                    } else {
                        for (int i = 0; i < line.points.size - 1; i += 2) {
                            renderer.rectLine(line.points.get(i), line.points.get(i + 1), line.width);
                        }
                    }
                }
            }
        }

        renderQueue.clear();
        renderer.end();
        batch.begin();
    }

    @Override
    public void processEntity(Entity entity, float deltaTime){
        renderQueue.add(entity);
    }
}
