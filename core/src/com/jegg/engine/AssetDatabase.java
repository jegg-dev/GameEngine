package com.jegg.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;

public class AssetDatabase {
    private static HashMap<String, Texture> textures = new HashMap<>();

    protected static void Load(){
        textures.put("triangle", new Texture(Gdx.files.internal("triangle-256px.png")));
        textures.put("circle", new Texture(Gdx.files.internal("circle-256px.png")));
        textures.put("square", new Texture(Gdx.files.internal("square-16px.png")));
        textures.put("ship", new Texture(Gdx.files.internal("ship.png")));
        textures.put("rock-tile", new Texture(Gdx.files.internal("rock-tile-32px.png")));
        textures.put("octagon", new Texture(Gdx.files.internal("octagon-256px.png")));
    }

    public static Texture GetTexture(String name){
        return textures.get(name);
    }
}
