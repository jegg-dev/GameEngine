package com.jegg.spacesim.core.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.jegg.spacesim.core.GameCamera;
import com.jegg.spacesim.game.TerrainMap;
import com.jegg.spacesim.game.TileDatabase;

import java.util.Comparator;

public class TilemapRenderSystem extends SortedIteratingSystem {

    private ShapeRenderer renderer;
    private Array<Entity> renderQueue;
    private Comparator<Entity> comparator;

    private ComponentMapper<TerrainMap> tilemapM;

    public TilemapRenderSystem(ShapeRenderer renderer){
        super(Family.all(TerrainMap.class).exclude(InactiveFlag.class).get(), new ZComparator());

        tilemapM = ComponentMapper.getFor(TerrainMap.class);

        renderQueue = new Array<>();
        this.renderer = renderer;
    }

    @Override
    public void update(float deltaTime){
        super.update(deltaTime);
        //renderQueue.sort(comparator);

        //renderer.setProjectionMatrix(GameCamera.GetMain().getCombined());
        //renderer.begin(ShapeRenderer.ShapeType.Line);
        for(Entity entity : renderQueue){
            RenderedTilemap tm = tilemapM.get(entity);
            Vector2 pos = new Vector2(tm.TileToChunkPosition(tm.WorldToTilePosition(GameCamera.GetMain().getPosition())));
            tm.update(pos);
            for(int x = (int)pos.x - (tm.viewDist * tm.getChunkWidth()); x < pos.x + ((tm.viewDist + 1) * tm.getChunkWidth()); x++){
                for(int y = (int)pos.y - (tm.viewDist * tm.getChunkWidth()); y < pos.y + ((tm.viewDist + 1) * tm.getChunkWidth()); y++){
                    Color color = TileDatabase.Get(tm.getTile(x, y)).color;
                    if(color != Color.CLEAR){
                        renderer.setColor(color);
                        renderer.box(x * tm.getTileWidth(), y * tm.getTileWidth(), 0, tm.getTileWidth(), tm.getTileWidth(), 0);
                    }
                }
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
