package com.jegg.game;

import com.badlogic.gdx.graphics.Color;
import com.jegg.engine.Game;
import com.jegg.engine.GameCamera;
import com.jegg.engine.Input;
import com.jegg.engine.ecs.IteratedEntity;
import com.jegg.engine.ecs.IteratedFlag;
import com.jegg.engine.ecs.Transform;
import com.jegg.engine.rendering.LineRenderer;

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
