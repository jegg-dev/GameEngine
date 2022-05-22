package com.jegg.spacesim.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class FPSCounter extends Label {

    public FPSCounter(CharSequence text, Skin skin) {
        super(text, skin);
    }

    @Override
    public void act(float deltaTime){
        setText(Gdx.graphics.getFramesPerSecond());
        super.act(deltaTime);
    }
}
