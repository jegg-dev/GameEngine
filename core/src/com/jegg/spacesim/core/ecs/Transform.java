package com.jegg.spacesim.core.ecs;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import java.util.Objects;

public class Transform implements Component {
    public String name;
    private Transform parent;
    private final Array<Transform> children = new Array<>();
    private final Vector3 localPosition = new Vector3();
    private final Vector3 position = new Vector3();
    public final Vector2 scale = new Vector2();
    private float rotation;
    private float localRotation;

    public void setParent(Transform transform){
        if(!children.contains(transform, true)) {
            parent = transform;
            localPosition.set(position.cpy().sub(parent.position));
            parent.addChild(this);
        }
    }

    public Transform getParent(){
        return parent;
    }

    public void setPosition(Vector3 position){
        this.position.set(position);
        if(parent != null) {
            localPosition.set(position.cpy().sub(parent.position));
        }
        if(children.size > 0) {
            for (Transform child : children) {
                child.setPosition(position.cpy().add(child.localPosition));
            }
        }
    }

    public Vector3 getPosition(){
        if(parent != null){

        }
        return position.cpy();
    }

    public Vector2 getPosition2(){
        return new Vector2(position.x, position.y);
    }

    public void setLocalPosition(Vector3 position){
        if(parent != null) {
            setPosition(parent.position.cpy().add(position));
        }
        else{
            localPosition.set(position);
        }
    }

    public Vector3 getLocalPosition(){
        return localPosition.cpy();
    }

    public float getRotation(){
        return rotation;
    }

    public void setRotation(float degrees){
        float delta = degrees - rotation;
        rotation = degrees;
        if(parent != null) {
            localRotation = rotation - parent.rotation;
        }
        if(children.size > 0){
            for(Transform child : children){
                float angle = (float)Math.atan2(child.localPosition.y, child.localPosition.x);
                angle *= MathUtils.radiansToDegrees;
                angle += delta;
                float dist = child.localPosition.len();
                Vector3 pos = new Vector3(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle), 0);
                pos.scl(dist);
                child.setLocalPosition(pos);
                child.setRotation(rotation + child.localRotation);
            }
        }
    }

    public float getLocalRotation(){
        return localRotation;
    }

    public void setLocalRotation(float degrees){
        setRotation(degrees + parent.rotation);
    }

    public void addChild(Transform transform){
        if(parent != transform && !children.contains(transform, true)){
            children.add(transform);
        }
    }

    public Transform getChild(int i){
        return children.get(i);
    }

    public Transform getChild(String name){
        for(Transform child : children){
            if(Objects.equals(child.name, name)){
                return child;
            }
        }
        return null;
    }

    public Vector2 up(){
        return new Vector2(MathUtils.cosDeg(rotation + 90),MathUtils.sinDeg(rotation + 90));
    }

    public Vector2 right(){
        return new Vector2(MathUtils.cosDeg(rotation), MathUtils.sinDeg(rotation));
    }
}
