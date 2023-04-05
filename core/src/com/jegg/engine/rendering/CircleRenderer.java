package com.jegg.engine.rendering;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;

public class CircleRenderer implements Component {
    public float radius = 1;
    public int segments = 20;
    public Color color = Color.WHITE;
}
