package com.jegg.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.jegg.engine.Game;
import com.jegg.engine.GameCamera;
import com.jegg.engine.Input;
import com.jegg.engine.ecs.IteratedEntity;
import com.jegg.engine.ecs.IteratedFlag;
import com.jegg.engine.ecs.Transform;
import com.jegg.engine.rendering.LineRenderer;
import com.jegg.engine.rendering.PolygonRenderer;

public class RigidbodyDraw extends IteratedEntity {
    public LineRenderer lr;

    public RigidbodyDraw(){
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
        if(Input.getKey(Input.Mouse0) && (lr.points.size == 0 || lr.points.get(lr.points.size - 1).dst(GameCamera.GetMain().screenToWorld2(Input.MousePos)) > 1f)){
            lr.points.add(GameCamera.GetMain().screenToWorld2(Input.MousePos.cpy()));
        }
        else if(Input.getKeyUp(Input.Mouse0) && lr.points.size >= 3 && lr.points.size <= 8){
            Entity e = Game.CreateWorldEntity(new Vector3(Input.MousePos.x, Input.MousePos.y, 0), 0);

            float[] verts = new float[lr.points.size * 2];
            for(int i = 0; i < verts.length; i++){
                if(i % 2 == 0) {
                    verts[i] = lr.points.get(i / 2).x;
                }
                else {
                    verts[i] = lr.points.get(i / 2).y;
                }
            }

            PolygonShape polygonShape = new PolygonShape();
            polygonShape.set(verts);
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            e.add(Game.CreateRigidbody(bodyDef, polygonShape, 1f));

            PolygonRenderer polyr = new PolygonRenderer();
            polyr.poly = new Polygon(verts);
            polyr.color = Color.WHITE;
            e.add(polyr);

            lr.points.clear();
        }
        else if(Input.getKeyUp(Input.Mouse0)){
            lr.points.clear();
        }
    }
}
