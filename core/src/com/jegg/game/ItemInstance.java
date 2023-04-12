package com.jegg.game;

import com.jegg.game.Item;
import com.jegg.game.ItemDatabase;

public class ItemInstance {
    public int id;
    public int amount;

    public ItemInstance(int id, int amount){
        this.id = id;
        this.amount = amount;
    }

    public Item GetItem(){
        return ItemDatabase.GetItem(id);
    }
}
