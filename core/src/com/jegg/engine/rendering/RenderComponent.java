package com.jegg.engine.rendering;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class RenderComponent implements Component {
    public abstract void render(Batch batch, ShapeRenderer shapeRend);
}
