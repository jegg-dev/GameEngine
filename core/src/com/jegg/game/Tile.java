package com.jegg.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;

public class Tile {
    public Sprite sprite;
    public int hardness;
    public boolean useCollider;

    public Tile(Sprite sprite, Color color, int hardness, boolean useCollider){
        this.sprite = sprite;
        this.sprite.setOrigin(0, 0);
        sprite.setScale(1f / sprite.getWidth());
        sprite.setColor(color);
        this.hardness = hardness;
        this.useCollider = useCollider;
    }

    //Called when tile is first added to tilemap
    public void onCreate(TerrainMap map, Vector3 worldPos){}
    //Called when tile begins rendering
    public void onLoad(TerrainMap map, Vector3 worldPos){}
    //Called when tile is no longer being rendered
    public void onUnload(TerrainMap map, Vector3 worldPos){}
    //Called when a tile is removed from tilemap
    public void onDestroy(TerrainMap map, Vector3 worldPos){}
}
