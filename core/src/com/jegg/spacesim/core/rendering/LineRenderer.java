package com.jegg.spacesim.core.rendering;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class LineRenderer implements Component {
    public Color color = Color.WHITE;
    public float width = 1.0f;
    public boolean useLocalSpace;
    public Array<Vector2> points = new Array<>();
}
