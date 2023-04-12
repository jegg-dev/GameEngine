package com.jegg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.jegg.engine.Game;
import com.jegg.game.Inventory;

public class InventoryMenu extends Actor {
    public Group group;
    public Inventory inventory;

    private InventorySlot[] itemSlots;
    private Texture slotTexture;
    private Skin skin = new Skin(Gdx.files.internal("skins/flat/skin.json"));

    public InventoryMenu(Group group, Inventory inventory){
        this.group = group;
        this.inventory = inventory;
        slotTexture = new Texture(Gdx.files.internal("inventory-slot.png"));
    }

    @Override
    public void act(float deltaTime){
        if(itemSlots != null) {
            for (InventorySlot slot : itemSlots) {
                slot.remove();
            }
        }
        itemSlots = new InventorySlot[inventory.items.length];
        for(int i = 0; i < inventory.items.length; i++){
            InventorySlot slot = new InventorySlot();
            slot.add(new Image(slotTexture));
            if(inventory.items[i] != null) {
                Image img = new Image(inventory.items[i].GetItem().tx);
                slot.add(img);
                img.setPosition(slot.getX() + (img.getWidth() / 2), slot.getY() + (img.getHeight() / 2));
                Label label = new Label(String.valueOf(inventory.items[i].amount), skin);
                slot.add(label);
                label.setPosition(slot.getX() + 1, slot.getY());
            }
            group.addActor(slot);
            itemSlots[i] = slot;
        }
    }
}
