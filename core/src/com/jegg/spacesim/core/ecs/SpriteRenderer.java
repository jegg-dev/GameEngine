package com.jegg.spacesim.core.ecs;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SpriteRenderer implements Component {
    private final Sprite sprite = new Sprite();

    public void setTexture(Texture tx, int width, int height){
        sprite.set(new Sprite(tx, width, height));
        sprite.setOrigin(width / 2f, height / 2f);
        sprite.setAlpha(0.0f);
    }

    public void setWrap(Texture.TextureWrap wrapU, Texture.TextureWrap wrapV){
        sprite.getTexture().setWrap(wrapU, wrapV);
    }

    public void scroll(float x, float y){
        sprite.scroll(x, y);
    }

    public void setColor(Color color){
        sprite.setColor(color);
    }

    public void setFlip(boolean x, boolean y){
        sprite.setFlip(x, y);
    }

    protected void render(Transform transform, SpriteBatch batch){
        sprite.setOriginBasedPosition(transform.getPosition().x, transform.getPosition().y);
        sprite.setRotation(transform.getRotation());
        sprite.setScale(transform.scale.x * SpriteRenderSystem.METERS_PER_PIXEL,
                transform.scale.y * SpriteRenderSystem.METERS_PER_PIXEL);

        sprite.draw(batch);
    }
}
