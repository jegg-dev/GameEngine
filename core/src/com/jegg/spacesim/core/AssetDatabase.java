package com.jegg.spacesim.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;

public class AssetDatabase {
    private static HashMap<String, Texture> textures = new HashMap<>();

    protected static void Load(){
        textures.put("triangle-256", new Texture(Gdx.files.internal("triangle-256px.png")));
        textures.put("circle-256", new Texture(Gdx.files.internal("circle-256px.png")));
        textures.put("square-16", new Texture(Gdx.files.internal("square-16px.png")));
        textures.put("ship", new Texture(Gdx.files.internal("ship.png")));
    }

    public static Texture GetTexture(String name){
        return textures.get(name);
    }
}
