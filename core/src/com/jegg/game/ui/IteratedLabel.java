package com.jegg.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class IteratedLabel extends Label {
    public LabelTextLoader textLoader;

    public IteratedLabel(Skin skin, LabelTextLoader textLoader) {
        super(textLoader.GetText(), skin);
        this.textLoader = textLoader;
    }


    @Override
    public void act(float deltaTime){
        setText(textLoader.GetText());
        super.act(deltaTime);
    }
}
