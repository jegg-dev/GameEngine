package com.jegg.spacesim.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class GameCamera {
    protected static GameCamera Main;

    public static final float SCALE_FACTOR = 10.0f;

    protected OrthographicCamera orthoCam;

    protected GameCamera(OrthographicCamera cam){
        orthoCam = cam;
    }

    public Vector3 getPosition(){
        return orthoCam.position;
    }

    public void setPosition(Vector3 position){
        orthoCam.position.set(position);
    }

    public void setPosition(float x, float y, float z){
        orthoCam.position.set(x, y, z);
    }

    public float getZoom(){
        return 1 / (orthoCam.zoom / SCALE_FACTOR);
    }

    public void setZoom(float zoom){
        orthoCam.zoom = (1 / zoom) * SCALE_FACTOR;
    }

    public float getWidth(){
        return orthoCam.viewportWidth;
    }

    public float getHeight(){
        return orthoCam.viewportHeight;
    }

    public Matrix4 getCombined(){
        return orthoCam.combined;
    }

    public boolean boundsInFrustum(Vector3 center, Vector3 dimensions){
        return orthoCam.frustum.boundsInFrustum(center, dimensions);
    }

    public Vector3 screenToWorld(Vector2 screenPos){
        return orthoCam.unproject(new Vector3(screenPos.x, screenPos.y, 0));
    }

    public Vector3 screenToWorld(Vector3 screenPos){
        return orthoCam.unproject(screenPos);
    }

    public Vector2 worldToScreen(Vector3 worldPos){
        Vector3 screenPos = orthoCam.project(worldPos).scl(1f / (float)Gdx.graphics.getWidth(), 1f / (float)Gdx.graphics.getHeight(), 1).scl(800, 600, 1);
        return new Vector2(screenPos.x, screenPos.y);
    }

    public static GameCamera GetMain(){
        return Main;
    }
}
