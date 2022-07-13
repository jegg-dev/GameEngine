package com.jegg.engine.core;

import com.badlogic.gdx.Gdx;

import java.awt.*;

public class Settings {
    public static boolean UseVsync = true;

    public enum DisplayModeType{
        Fullscreen,
        Windowed,
        WindowedBorderless
    }
    private static DisplayModeType DisplayMode = DisplayModeType.Windowed;
    public static final Dimension BorderlessResolution = new Dimension(800,600);

    public static void SetDisplayMode(DisplayModeType type){
        DisplayMode = type;
        if(type == DisplayModeType.Fullscreen){
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }
        else if(type == DisplayModeType.Windowed){
            Gdx.graphics.setWindowedMode(800, 600);
            Gdx.graphics.setUndecorated(false);
        }
        else{
            Gdx.graphics.setWindowedMode(BorderlessResolution.width, BorderlessResolution.height);
            Gdx.graphics.setUndecorated(true);
        }
    }
}
