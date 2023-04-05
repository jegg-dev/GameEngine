package com.jegg.engine.rendering;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Polygon;

public class PolygonRenderer implements Component {
    public Polygon poly;
    public Color color = Color.WHITE;

    public static final float[] boxVerts = new float[]{
            -0.5f, -0.5f,
            0.5f, -0.5f,
            0.5f, 0.5f,
            -0.5f, 0.5f
    };
}
