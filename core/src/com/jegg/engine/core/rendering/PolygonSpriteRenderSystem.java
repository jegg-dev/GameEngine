package com.jegg.engine.core.rendering;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.jegg.engine.core.GameCamera;
import com.jegg.engine.core.ecs.InactiveFlag;
import com.jegg.engine.core.ecs.Transform;

import java.util.Comparator;

public class PolygonSpriteRenderSystem extends SortedIteratingSystem {

    public static final float PIXELS_PER_METER = 32.0f;
    public static final float METERS_PER_PIXEL = 1.0f / PIXELS_PER_METER;

    private static Vector2 meterDimensions = new Vector2();
    private static Vector2 pixelDimensions = new Vector2();
    public static Vector2 getScreenSizeInMeters(){
        return meterDimensions.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private SpriteBatch batch;
    private PolygonSpriteBatch polyBatch;
    private Array<Entity> renderQueue;
    private Comparator<Entity> comparator = new ZComparator();
    private OrthographicCamera camera;

    private ComponentMapper<Transform> transformM;
    private ComponentMapper<PolygonSpriteRenderer> spriteM;

    public PolygonSpriteRenderSystem(SpriteBatch batch, PolygonSpriteBatch polyBatch, OrthographicCamera camera){
        super(Family.all(Transform.class, PolygonSpriteRenderer.class).exclude(InactiveFlag.class).get(), new ZComparator());

        transformM = ComponentMapper.getFor(Transform.class);
        spriteM = ComponentMapper.getFor(PolygonSpriteRenderer.class);

        renderQueue = new Array<>();
        this.batch = batch;
        this.polyBatch = polyBatch;
        this.camera = camera;
    }

    @Override
    public void update(float deltaTime){
        super.update(deltaTime);
        renderQueue.sort(comparator);
        camera.update();

        batch.end();
        polyBatch.setProjectionMatrix(GameCamera.GetMain().getCombined());
        polyBatch.begin();

        for(Entity entity : renderQueue){
            PolygonSpriteRenderer sprite = spriteM.get(entity);
            sprite.render(transformM.get(entity), polyBatch);
        }

        renderQueue.clear();

        polyBatch.end();
        batch.begin();
    }

    @Override
    public void processEntity(Entity entity, float deltaTime){
        renderQueue.add(entity);
    }
}
