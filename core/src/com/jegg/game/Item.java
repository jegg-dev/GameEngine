package com.jegg.game;

import com.badlogic.gdx.graphics.Texture;

public class Item {
    public String name;
    public Texture tx;
    public int maxStack = 1;
    public int value;

    public Item(String name, Texture tx, int maxStack, int value){
        this.name = name;
        this.tx = tx;
        this.maxStack = maxStack;
        this.value = value;
    }

    public boolean isStackable(){
        return maxStack > 1;
    }

    public ItemInstance CreateInstance(int amount){
        return new ItemInstance(ItemDatabase.GetID(this), amount);
    }
}
