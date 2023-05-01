package com.jegg.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.jegg.engine.Input;
import com.jegg.engine.ecs.IteratedEntity;

import java.lang.reflect.InvocationTargetException;

public class Console extends TextField {
    public Console(String text, Skin skin) {
        super(text, skin);
    }

    @Override
    public void act(float deltaTime){
        if(Input.getKeyDown(Input.Enter)){
            parseInput(getText());
        }
    }

    private void parseInput(String input){
        if(input.isEmpty()) return;
        String[] words = input.split(" ");
        switch(words[0]){
            case "spawn":
                if(words.length == 2){
                    try {
                        Class c = Class.forName(words[1]);
                        if(c == IteratedEntity.class){
                            IteratedEntity e = (IteratedEntity)c.getDeclaredConstructor().newInstance();
                        }
                    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException |
                             IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
                break;
            default:
                break;
        }
    }
}
