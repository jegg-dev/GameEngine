package com.jegg.game;

import com.badlogic.gdx.math.Vector3;
import com.jegg.engine.Game;
import com.jegg.engine.GameCamera;
import com.jegg.engine.Input;
import com.jegg.engine.ecs.IteratedEntity;
import com.jegg.engine.ecs.IteratedFlag;

public class TerrainController extends IteratedEntity {

    public TerrainMap tilemap;

    public TerrainController(){
        tilemap = new TerrainMap(500,16, 1f, 4);
        add(tilemap);
        tilemap.collider.collisionEntity = this;
        add(tilemap.collider);
        add(Game.CreateComponent(IteratedFlag.class));
        Game.AddEntity(this);
    }

    @Override
    public void start(float deltaTime) {
    }

    @Override
    public void update(float deltaTime) {
        if(Input.getKey(Input.LeftControl) && Input.getKeyUp(Input.Mouse0)){
            Vector3 worldPos = GameCamera.GetMain().screenToWorld(Input.MousePos);
            addTile(worldPos, 1);
        }
        else if(Input.getKey(Input.LeftControl) && Input.getKeyUp(Input.Mouse1)){
            Vector3 worldPos = GameCamera.GetMain().screenToWorld(Input.MousePos);
            removeTile(worldPos);
        }
    }

    public void addTile(Vector3 pos, int tileIndex){
        tilemap.setTile(pos, tileIndex);
        tilemap.collider.setTile((int)(pos.x / tilemap.getTileWidth()), (int)(pos.y / tilemap.getTileWidth()));
    }

    public void removeTile(Vector3 pos){
        tilemap.setTile(pos, 0);
        tilemap.collider.setTile(pos, null);
    }
}
