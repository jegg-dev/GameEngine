package com.jegg.spacesim.core.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.jegg.spacesim.core.GameCamera;

import java.util.Comparator;

public class RenderSystem extends SortedIteratingSystem {

    public static final float PIXELS_PER_METER = 32.0f;
    public static final float METERS_PER_PIXEL = 1.0f / PIXELS_PER_METER;

    private static Vector2 meterDimensions = new Vector2();
    private static Vector2 pixelDimensions = new Vector2();
    public static Vector2 getScreenSizeInMeters(){
        return meterDimensions.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private SpriteBatch batch;
    private Array<Entity> renderQueue;
    private Comparator<Entity> comparator;
    private OrthographicCamera camera;

    private ComponentMapper<Transform> transformM;
    private ComponentMapper<TextureRenderer> textureM;

    public RenderSystem(SpriteBatch batch, OrthographicCamera camera){
        super(Family.all(Transform.class, TextureRenderer.class).exclude(InactiveFlag.class).get(), new ZComparator());

        transformM = ComponentMapper.getFor(Transform.class);
        textureM = ComponentMapper.getFor(TextureRenderer.class);

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

        batch.setProjectionMatrix(camera.combined);
        batch.enableBlending();
        batch.begin();

        for(Entity entity : renderQueue){
            TextureRenderer tex = textureM.get(entity);
            Transform t = transformM.get(entity);

            if(tex.region == null){
                continue;
            }

            float width = tex.region.getRegionWidth();
            float height = tex.region.getRegionHeight();
            float originX = width / 2f;
            float originY = height / 2f;

            batch.draw(tex.region, t.getPosition().x - originX, t.getPosition().y - originY,
                    originX, originY, width, height,
                    t.scale.x * PIXELS_PER_METER, t.scale.y * PIXELS_PER_METER, t.getRotation());
        }
        batch.end();
        renderQueue.clear();
    }

    @Override
    public void processEntity(Entity entity, float deltaTime){
        renderQueue.add(entity);
    }
}
