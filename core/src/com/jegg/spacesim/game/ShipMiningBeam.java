package com.jegg.spacesim.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.jegg.spacesim.core.*;
import com.jegg.spacesim.core.ecs.Transform;
import com.jegg.spacesim.core.physics.Physics;
import com.jegg.spacesim.core.physics.RaycastHit;

public class ShipMiningBeam {
    public Ship ship;

    public float beamTime = 0.0f;
    public float beamTimer = 0;

    public int strength = 1;
    public float miningSpeed = 0.1f;
    public float miningProgress;
    public Body miningBody;

    public ShipMiningBeam(Ship ship){
        this.ship = ship;
    }

    public void use(float deltaTime){
        beamTimer -= deltaTime;
        if(beamTimer <= 0){
            Vector2 mousePos = new Vector2(GameCamera.GetMain().screenToWorld(Input.MousePos).x, GameCamera.GetMain().screenToWorld(Input.MousePos).y);
            Vector2 dir = mousePos.cpy().sub(ship.turret.getComponent(Transform.class).getPosition2());
            float dist = dir.len();
            dist = MathUtils.clamp(dist, 0.0f, 10.0f);
            RaycastHit[] hits = Physics.RaycastAll(ship.turret.getComponent(Transform.class).getPosition2(), dir.cpy(), dist);
            Body closestBody = null;
            float closestDist = Float.MAX_VALUE;
            for(RaycastHit hit : hits) {
                if(hit.point != null && !hit.fixture.isSensor()){
                    float dist2 = hit.point.cpy().sub(ship.turret.getComponent(Transform.class).getPosition2()).len();
                    if(dist2 < closestDist){
                        closestDist = dist2;
                        closestBody = hit.body;
                    }
                }
            }
            if (closestBody != null) {
                Game.lines.add(new DebugLine(ship.turret.getComponent(Transform.class).getPosition2(), ship.turret.getComponent(Transform.class).getPosition2().add(dir.nor().scl(closestDist)), 0.0f, Color.RED));
                if (closestBody.getUserData() instanceof TerrainController) {
                    TerrainController terrain = (TerrainController)closestBody.getUserData();
                    Vector3 pos = new Vector3(closestBody.getWorldCenter(), 0);
                    if(TileDatabase.Get(terrain.tilemap.getTile(pos)).hardness <= strength) {
                        if (miningBody == closestBody) {
                            miningProgress += deltaTime;
                            /*ShapeRenderer sr = new ShapeRenderer();
                            sr.setProjectionMatrix(GameCamera.GetMain().getCombined());
                            sr.begin(ShapeRenderer.ShapeType.Line);
                            sr.setColor(Color.RED);
                            float extent = miningProgress / miningSpeed * terrain.tilemap.getTileWidth() / 2;
                            sr.box(pos.x + (terrain.tilemap.getTileWidth() / 2) - extent, pos.y + (terrain.tilemap.getTileWidth() / 2) - extent, 0, extent * 2, extent * 2, 0);
                            sr.end();*/
                            if (miningProgress >= miningSpeed) {
                                terrain.removeTile(pos);
                                miningProgress = 0;
                            }
                        } else if (miningBody == null) {
                            miningBody = closestBody;
                        } else {
                            miningProgress = 0;
                            miningBody = null;
                        }
                    }
                }
            } else {
                Game.lines.add(new DebugLine(ship.turret.getComponent(Transform.class).getPosition2(), ship.turret.getComponent(Transform.class).getPosition2().add(dir.nor().scl(dist)), 0.0f, Color.RED));
                miningProgress = 0;
                miningBody = null;
            }
            beamTimer = beamTime;
        }
    }
}
