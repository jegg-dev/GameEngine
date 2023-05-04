package com.jegg.engine.tilemap;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.jegg.engine.GameCamera;
import com.jegg.engine.ecs.InactiveFlag;
import com.jegg.engine.rendering.ZComparator;
import com.jegg.game.world.TerrainMap;
import com.jegg.game.world.TileDatabase;

import java.util.ArrayList;
import java.util.Comparator;

public class TilemapRenderSystem extends SortedIteratingSystem {

    private SpriteBatch batch;
    private Array<Entity> renderQueue;
    private ArrayList<Entity> renderQueueList;
    private Comparator<Entity> comparator;

    private ComponentMapper<TerrainMap> tilemapM;

    public TilemapRenderSystem(SpriteBatch batch){
        super(Family.all(TerrainMap.class).exclude(InactiveFlag.class).get(), new ZComparator());

        tilemapM = ComponentMapper.getFor(TerrainMap.class);

        renderQueue = new Array<>();
        renderQueueList = new ArrayList<>();
        this.batch = batch;

    }

    @Override
    public void update(float deltaTime){
        super.update(deltaTime);

        for(Entity entity : renderQueueList){
            RenderedTilemap tm = tilemapM.get(entity);
            Vector2 pos = new Vector2(tm.TileToChunkPosition(tm.WorldToTilePosition(GameCamera.GetMain().getPosition())));
            tm.update(pos);

            int chunkWidth = tm.getChunkWidth();
            float tileWidth = tm.getTileWidth();
            int mapWidth = tm.getMapWidthInChunks();
            int worldStartX = (int)pos.x - (tm.viewDist * chunkWidth);
            worldStartX = MathUtils.clamp(worldStartX, -mapWidth * chunkWidth, mapWidth * chunkWidth);
            int worldEndX = (int)pos.x + ((tm.viewDist + 1) * chunkWidth);
            worldEndX = MathUtils.clamp(worldEndX, -mapWidth * chunkWidth, mapWidth * chunkWidth);
            int worldStartY = (int)pos.y - (tm.viewDist * chunkWidth);
            worldStartY = MathUtils.clamp(worldStartY, -mapWidth * chunkWidth, mapWidth * chunkWidth);
            int worldEndY = (int)pos.y + ((tm.viewDist + 1) * chunkWidth);
            worldEndY = MathUtils.clamp(worldEndY, -mapWidth * chunkWidth, mapWidth * chunkWidth);

            if(RenderedTilemap.ShowChunkOutlines){
                batch.end();
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
                batch.begin();
            }

            /*for(int x = worldStartX; x < worldEndX; x++){
                for(int y = worldStartY; y < worldEndY; y++){
                    Sprite sprite = TileDatabase.Get(tm.getTile(x, y)).sprite;
                    if(sprite.getTexture() != null){
                        sprite.setOriginBasedPosition(x * tileWidth, y * tileWidth);
                        sprite.setScale(1f / sprite.getWidth() * tileWidth);
                        sprite.draw(batch);
                    }
                }
            }*/
            for(int chunkX = worldStartX; chunkX < worldEndX; chunkX += chunkWidth){
                for(int chunkY = worldStartY; chunkY < worldEndY; chunkY += chunkWidth){
                    PrimitiveChunk chunk = tm.chunks[tm.getChunkNumByChunkPosition(chunkX, chunkY)];
                    for(int x = 0; x < chunkWidth; x++){
                        for(int y = 0; y < chunkWidth; y++){
                            Sprite sprite = TileDatabase.Get(chunk.getTile(x, y)).sprite;
                            if(sprite.getTexture() != null){
                                TextureRegion region = new TextureRegion(sprite.getTexture());
                                sprite.setOriginBasedPosition((chunkX + x) * tileWidth, (chunkY + y) * tileWidth);
                                sprite.setScale(1f / sprite.getWidth() * tileWidth);
                                sprite.draw(batch);
                            }
                        }
                    }
                }
            }
        }

        //renderQueue.clear();
        renderQueueList.clear();
    }

    @Override
    public void processEntity(Entity entity, float deltaTime){
        //renderQueue.add(entity);
        renderQueueList.add(entity);
    }
}