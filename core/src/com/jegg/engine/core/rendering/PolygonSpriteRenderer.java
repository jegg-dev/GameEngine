package com.jegg.engine.core.rendering;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.jegg.engine.core.ecs.Transform;

public class PolygonSpriteRenderer implements Component {
    private PolygonSprite sprite;

    public float[] getVertices(){
        return sprite.getRegion().getVertices();
    }

    public void setTexture(Texture tx, float[] vertices){
        sprite = new PolygonSprite(new PolygonRegion(
                new TextureRegion(tx),
                vertices,
                new EarClippingTriangulator().computeTriangles(vertices).toArray()
        ));
        sprite.setOrigin(0, 0);
    }

    public void setWrap(Texture.TextureWrap wrapU, Texture.TextureWrap wrapV){
        sprite.getRegion().getRegion().getTexture().setWrap(wrapU, wrapV);
    }

    public void setColor(Color color){
        sprite.setColor(color);
    }

    public void setFlip(boolean x, boolean y){
        sprite.getRegion().getRegion().flip(x, y);
    }

    protected void render(Transform transform, PolygonSpriteBatch batch){
        sprite.setPosition(transform.getPosition().x, transform.getPosition().y);
        sprite.setRotation(transform.getRotation());
        sprite.setScale(transform.scale.x * PolygonSpriteRenderSystem.METERS_PER_PIXEL,
                transform.scale.y * PolygonSpriteRenderSystem.METERS_PER_PIXEL);

        sprite.draw(batch);
    }
}
