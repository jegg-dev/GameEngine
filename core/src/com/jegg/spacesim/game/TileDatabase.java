package com.jegg.spacesim.game;

import com.badlogic.gdx.graphics.Color;

public class TileDatabase {
    private static final Tile[] Tiles = new Tile[]{
            new Tile(Color.CLEAR, 0, false),
            new LootTile(Color.CYAN, 1, true),
            new LootTile(Color.DARK_GRAY, 1, true),
            new LootTile(Color.GRAY, 2, true),
            new LootTile(Color.PURPLE, 3, true),
            new LootTile(Color.PINK, 4, true),
            new LootTile(Color.LIME, 5, true),
            new SpawnerTile<>(Color.CLEAR, 0, false, TurretEnemy.class, true)
    };

    public static Tile Get(int index){
        return Tiles[index];
    }

}
