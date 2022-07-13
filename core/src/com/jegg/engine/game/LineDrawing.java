package com.jegg.engine.game;

import com.badlogic.gdx.graphics.Color;
import com.jegg.engine.core.Game;
import com.jegg.engine.core.GameCamera;
import com.jegg.engine.core.Input;
import com.jegg.engine.core.ecs.IteratedEntity;
import com.jegg.engine.core.ecs.IteratedFlag;
import com.jegg.engine.core.ecs.Transform;
import com.jegg.engine.core.rendering.LineRenderer;

public class LineDrawing extends IteratedEntity {
    public LineRenderer lr;

    public LineDrawing(){
        add(Game.CreateComponent(IteratedFlag.class));
        add(Game.CreateComponent(Transform.class));
        Game.AddEntity(this);
    }

    @Override
    public void start(float deltaTime) {
        lr = new LineRenderer();
        lr.color = Color.BLUE;
        add(lr);
    }

    @Override
    public void update(float deltaTime) {
        if(Input.PointerDragging && (lr.points.size == 0 || lr.points.get(lr.points.size - 1).dst(GameCamera.GetMain().screenToWorld2(Input.MousePos)) > 0.1f)){
            lr.points.add(GameCamera.GetMain().screenToWorld2(Input.MousePos.cpy()));
        }
    }
}
