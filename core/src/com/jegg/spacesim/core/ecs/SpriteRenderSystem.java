package com.jegg.spacesim.core.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.jegg.spacesim.core.GameCamera;

import java.util.Comparator;

public class SpriteRenderSystem extends SortedIteratingSystem {

    public static final float PIXELS_PER_METER = 32.0f;
    public static final float METERS_PER_PIXEL = 1.0f / PIXELS_PER_METER;

    private static Vector2 meterDimensions = new Vector2();
    private static Vector2 pixelDimensions = new Vector2();
    public static Vector2 getScreenSizeInMeters(){
        return meterDimensions.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private SpriteBatch batch;
    private Array<Entity> renderQueue;
    private Comparator<Entity> comparator = new ZComparator();
    private OrthographicCamera camera;

    private ComponentMapper<Transform> transformM;
    private ComponentMapper<SpriteRenderer> spriteM;

    public SpriteRenderSystem(SpriteBatch batch, OrthographicCamera camera){
        super(Family.all(Transform.class, SpriteRenderer.class).exclude(InactiveFlag.class).get(), new ZComparator());

        transformM = ComponentMapper.getFor(Transform.class);
        spriteM = ComponentMapper.getFor(SpriteRenderer.class);

        renderQueue = new Array<>();
        this.batch = batch;
        this.camera = camera;
        //camera = new OrthographicCamera(Gdx.graphics.getWidth() / PIXELS_PER_METER, Gdx.graphics.getHeight() / PIXELS_PER_METER);
        //camera.position.set(FRUSTUM_WIDTH / 2f, FRUSTUM_HEIGHT / 2f, 0);
    }

    @Override
    public void update(float deltaTime){
        super.update(deltaTime);
        renderQueue.sort(comparator);
        camera.update();
        batch.setProjectionMatrix(GameCamera.GetMain().getCombined());

        for(Entity entity : renderQueue){
            SpriteRenderer sprite = spriteM.get(entity);
            sprite.render(transformM.get(entity), batch);
        }

        renderQueue.clear();
    }

    @Override
    public void processEntity(Entity entity, float deltaTime){
        renderQueue.add(entity);
    }
}
