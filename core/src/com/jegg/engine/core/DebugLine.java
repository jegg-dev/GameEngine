package com.jegg.engine.core;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class DebugLine {
    public float timer = 1f;
    public Vector2 start, end;
    public Color color;

    public DebugLine(Vector2 start, Vector2 end, float time, Color color){
        this.start = start;
        this.end = end;
        this.timer = time;
        this.color = color;
    }
}
