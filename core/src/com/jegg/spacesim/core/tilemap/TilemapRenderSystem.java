package com.jegg.spacesim.core.tilemap;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.jegg.spacesim.core.AssetDatabase;
import com.jegg.spacesim.core.GameCamera;
import com.jegg.spacesim.core.Input;
import com.jegg.spacesim.core.ecs.InactiveFlag;
import com.jegg.spacesim.core.rendering.SpriteRenderer;
import com.jegg.spacesim.core.rendering.ZComparator;
import com.jegg.spacesim.game.TerrainMap;
import com.jegg.spacesim.game.TileDatabase;

import java.util.Comparator;

public class TilemapRenderSystem extends SortedIteratingSystem {

    private SpriteBatch batch;
    private Array<Entity> renderQueue;
    private Comparator<Entity> comparator;

    private ComponentMapper<TerrainMap> tilemapM;

    public TilemapRenderSystem(SpriteBatch batch){
        super(Family.all(TerrainMap.class).exclude(InactiveFlag.class).get(), new ZComparator());

        tilemapM = ComponentMapper.getFor(TerrainMap.class);

        renderQueue = new Array<>();
        this.batch = batch;

    }

    @Override
    public void update(float deltaTime){
        super.update(deltaTime);

        for(Entity entity : renderQueue){
            RenderedTilemap tm = tilemapM.get(entity);
            Vector2 pos = new Vector2(tm.TileToChunkPosition(tm.WorldToTilePosition(GameCamera.GetMain().getPosition())));
            tm.update(pos);

            int chunkWidth = tm.getChunkWidth();
            float tileWidth = tm.getTileWidth();
            int mapWidth = tm.getMapWidthInChunks();
            int worldStartX = (int)pos.x - (tm.viewDist * chunkWidth);
            int worldEndX = (int)pos.x + ((tm.viewDist + 1) * chunkWidth);
            int worldStartY = (int)pos.y - (tm.viewDist * chunkWidth);
            int worldEndY = (int)pos.y + ((tm.viewDist + 1) * chunkWidth);

            if(Input.getKey(Input.K)){
                ShapeRenderer sr = new ShapeRenderer();
                sr.setProjectionMatrix(GameCamera.GetMain().getCombined());
                sr.begin(ShapeRenderer.ShapeType.Line);
                sr.setColor(Color.RED);

                for(int x = worldStartX; x < worldEndX; x += chunkWidth){
                    sr.line(x, worldStartY, x, worldEndY);
                }
                for(int y = worldStartY; y < worldEndY; y += chunkWidth){
                    sr.line(worldStartX, y, worldEndX, y);
                }
                sr.end();
            }

            for(int x = worldStartX; x < worldEndX; x++){
                for(int y = worldStartY; y < worldEndY; y++){
                    if(x / chunkWidth > -mapWidth / 2 && x / chunkWidth < mapWidth / 2
                            && y / chunkWidth > -mapWidth / 2 && y / chunkWidth < mapWidth / 2) {
                        Sprite sprite = TileDatabase.Get(tm.getTile(x, y)).sprite;
                        if(sprite.getTexture() != null){
                            sprite.setOriginBasedPosition(x * tileWidth, y * tileWidth);
                            sprite.setScale(1f / sprite.getWidth() * tileWidth);
                            sprite.draw(batch);
                        }
                    }
                }
            }
        }

        renderQueue.clear();
    }

    @Override
    public void processEntity(Entity entity, float deltaTime){
        renderQueue.add(entity);
    }
}