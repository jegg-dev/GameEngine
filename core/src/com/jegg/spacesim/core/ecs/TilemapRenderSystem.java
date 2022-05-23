package com.jegg.spacesim.core.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.jegg.spacesim.core.GameCamera;
import com.jegg.spacesim.game.TerrainMap;
import com.jegg.spacesim.game.TileDatabase;

import java.util.Comparator;

public class TilemapRenderSystem extends SortedIteratingSystem {

    private ShapeRenderer renderer;
    private SpriteBatch batch;
    private Array<Entity> renderQueue;
    private Comparator<Entity> comparator;

    private ComponentMapper<TerrainMap> tilemapM;

    private Sprite tileSprite;

    public TilemapRenderSystem(ShapeRenderer renderer, SpriteBatch batch){
        super(Family.all(TerrainMap.class).exclude(InactiveFlag.class).get(), new ZComparator());

        tilemapM = ComponentMapper.getFor(TerrainMap.class);

        renderQueue = new Array<>();
        this.renderer = renderer;
        this.batch = batch;

        tileSprite = new Sprite(new Texture(Gdx.files.internal("tile-white.png")), 16, 16);
        tileSprite.setScale(0.0625f, 0.0625f);
        tileSprite.setOrigin(0,0);
    }

    @Override
    public void update(float deltaTime){
        super.update(deltaTime);

        batch.setProjectionMatrix(GameCamera.GetMain().getCombined());
        batch.begin();
        batch.disableBlending();

        for(Entity entity : renderQueue){
            RenderedTilemap tm = tilemapM.get(entity);
            Vector2 pos = new Vector2(tm.TileToChunkPosition(tm.WorldToTilePosition(GameCamera.GetMain().getPosition())));
            tm.update(pos);

            if(tm instanceof TerrainMap && TerrainMap.ShowChunkOutlines) {
                renderer.setColor(Color.RED);
                for (int x = (int) pos.x - (tm.viewDist * tm.getChunkWidth()); x < pos.x + ((tm.viewDist + 1) * tm.getChunkWidth()); x += tm.getChunkWidth()) {
                    for (int y = (int) pos.y - (tm.viewDist * tm.getChunkWidth()); y < pos.y + ((tm.viewDist + 1) * tm.getChunkWidth()); y += tm.getChunkWidth()) {
                        Vector2 newChunkPos = tm.TileToChunkPosition(new Vector2(x, y));
                        newChunkPos.scl(tm.getTileWidth());
                        renderer.polygon(new float[]{
                                newChunkPos.x, newChunkPos.y,
                                newChunkPos.x + (tm.getChunkWidth() * tm.getTileWidth()), newChunkPos.y,
                                newChunkPos.x + (tm.getChunkWidth() * tm.getTileWidth()), newChunkPos.y + (tm.getChunkWidth() * tm.getTileWidth()),
                                newChunkPos.x, newChunkPos.y + (tm.getChunkWidth() * tm.getTileWidth())
                        });
                    }
                }
            }

            int chunkWidth = tm.getChunkWidth();
            float tileWidth = tm.getTileWidth();
            int mapWidth = tm.getMapWidthInChunks();
            int worldStartX = (int)pos.x - (tm.viewDist * chunkWidth);
            int worldEndX = (int)pos.x + ((tm.viewDist + 1) * chunkWidth);
            int worldStartY = (int)pos.y - (tm.viewDist * chunkWidth);
            int worldEndY = (int)pos.y + ((tm.viewDist + 1) * chunkWidth);
            ///Pixmap map = new Pixmap(worldEndX - worldStartX, worldEndY - worldStartY, Pixmap.Format.Alpha);
            //map.fill();

            for(int x = worldStartX; x < worldEndX; x++){
                for(int y = worldStartY; y < worldEndY; y++){
                    if(x / chunkWidth > -mapWidth / 2 && x / chunkWidth < mapWidth / 2
                            && y / chunkWidth > -mapWidth / 2 && y / chunkWidth < mapWidth / 2) {
                        Color color = TileDatabase.Get(tm.getTile(x, y)).color;
                        if(color != Color.CLEAR){
                            //map.setColor(color);
                            //map.fillRectangle(x, y, 16, 16);
                            //renderer.setColor(color);
                            tileSprite.setColor(color);
                            tileSprite.setOriginBasedPosition(x * tileWidth, y * tileWidth);
                            tileSprite.draw(batch);
                            //renderer.rect(x * tileWidth, y * tileWidth, tileWidth, tileWidth);
                        }
                    }
                }
            }
            //Texture tx = new Texture(map);
            //batch.draw(tx, pos.x, pos.y);
            //Sprite sprite = new Sprite(tx);
            //sprite.setPosition(pos.x, pos.y);
            //sprite.setOrigin(pos.x, pos.y);
            //sprite.setOriginBasedPosition(pos.x, pos.y);
            //sprite.draw(batch);
            //tx.dispose();
        }
        renderQueue.clear();
        batch.end();
    }

    @Override
    public void processEntity(Entity entity, float deltaTime){
        renderQueue.add(entity);
    }
}