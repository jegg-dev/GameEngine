package com.jegg.game;

import com.badlogic.gdx.math.MathUtils;

public class Inventory {
    public ItemInstance[] items;

    public Inventory(int size){
        items = new ItemInstance[size];
    }

    public boolean contains(Item item){
        int id = ItemDatabase.GetID(item);
        for(ItemInstance i : items){
            if(i.id == id) return true;
        }
        return false;
    }

    //First searches for existing stacks of the same items and tries to fill them, then creates a new stack if needed
    public int addItem(ItemInstance item){
        int amountLeft = item.amount;
        for(int i = 0; i < items.length && amountLeft > 0; i++){
            if(items[i] != null && items[i].id == item.id && items[i].amount < item.GetItem().maxStack){
                int amountToAdd = MathUtils.clamp(amountLeft, 0, item.GetItem().maxStack - items[i].amount);
                items[i].amount += amountToAdd;
                amountLeft -= amountToAdd;
            }
        }
        if(amountLeft > 0) {
            for (int i = 0; i < items.length && amountLeft > 0; i++) {
                if(items[i] == null){
                    int amountToAdd = MathUtils.clamp(amountLeft, 0, item.GetItem().maxStack);
                    items[i] = new ItemInstance(item.id, amountToAdd);
                    amountLeft -= amountToAdd;
                }
            }
        }

        return amountLeft;
    }

    //Returns the amount actually removed from the inventory if enough of the item wasn't present
    public int removeItem(Item item, int amount){
        int id = ItemDatabase.GetID(item);
        for(int i = 0; i < items.length && amount > 0; i++){
            if(items[i] != null && items[i].id == id){
                int removed = MathUtils.clamp(amount, 0, items[i].amount);
                amount -= removed;
            }
        }
        return amount;
    }
}
