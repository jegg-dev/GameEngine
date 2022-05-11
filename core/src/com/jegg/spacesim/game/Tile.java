package com.jegg.spacesim.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class Tile {
    public Color color;
    public int hardness = 1;
    public boolean useCollider;

    public Tile(Color color, int hardness, boolean useCollider){
        this.color = color;
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
