package com.jegg.game.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.jegg.engine.AssetDatabase;
import com.jegg.game.ItemInstance;
import com.jegg.game.TurretEnemy;

public class TileDatabase {
    private static final Tile[] Tiles = new Tile[]{
            new Tile(new Sprite(), Color.CLEAR,0, false),
            new Tile(new Sprite(AssetDatabase.GetTexture("rock-tile"), 32, 32), Color.CYAN, 1, true),
            new LootTile(new Sprite(AssetDatabase.GetTexture("rock-tile"), 32, 32), Color.DARK_GRAY, 1, true, new ItemInstance(0, 1)),
            new LootTile(new Sprite(AssetDatabase.GetTexture("rock-tile"), 32, 32), Color.GRAY, 2, true, new ItemInstance(0, 2)),
            new LootTile(new Sprite(AssetDatabase.GetTexture("square"), 16, 16), Color.PURPLE, 3, true, new ItemInstance(1, 1)),
            new LootTile(new Sprite(AssetDatabase.GetTexture("square"), 16, 16), Color.PINK, 4, true, new ItemInstance(1, 1)),
            new LootTile(new Sprite(AssetDatabase.GetTexture("square"), 16, 16), Color.LIME, 5, true, new ItemInstance(1, 1)),
            new SpawnerTile<>(new Sprite(AssetDatabase.GetTexture("square"), 16, 16), Color.CLEAR, 0, false, TurretEnemy.class, true)
    };

    public static Tile Get(int index){
        return Tiles[index];
    }

}
