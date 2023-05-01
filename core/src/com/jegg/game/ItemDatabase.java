package com.jegg.game;

import com.jegg.engine.AssetDatabase;
import java.util.HashMap;

public class ItemDatabase {
    private static final HashMap<Item, Integer> ItemMap;
    private static final Item[] Items = {
            new Item("Rock", AssetDatabase.GetTexture("square"), 100, 0),
            new Item("Pink Ore", AssetDatabase.GetTexture("square"), 100, 0)
    };

    static{
        ItemMap = new HashMap<>();
        for(int i = 0; i < Items.length; i++){
            ItemMap.put(Items[i], i);
        }
    }

    public static Item GetItem(int id){
        return Items[id];
    }

    public static int GetID(Item item){
        return ItemMap.get(item);
    }
}
