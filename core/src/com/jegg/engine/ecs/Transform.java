package com.jegg.engine.ecs;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.badlogic.gdx.utils.Array;
import com.jegg.engine.physics.Physics;
import com.jegg.engine.physics.Rigidbody;

import java.util.Objects;

public class Transform implements Component {
    public String name;
    private Transform parent;
    private Rigidbody rigidbody;
    private Joint parentJoint;
    private final Array<Transform> children = new Array<>();
    private final Vector3 localPosition = new Vector3();
    private final Vector3 position = new Vector3();
    public final Vector2 scale = new Vector2(1, 1);
    private float rotation;
    private float localRotation;

    public void connectRigidbody(Entity parentEntity, Rigidbody rigidbody){
        if(parentEntity.getComponent(Rigidbody.class) == rigidbody){
            this.rigidbody = rigidbody;
        }
    }

    public void chainParentRigidbody(){
        if(parent != null && parent.rigidbody != null) {
            WeldJointDef joint = new WeldJointDef();
            joint.bodyA = parent.rigidbody.body;
            joint.bodyB = rigidbody.body;
            joint.collideConnected = false;
            joint.type = JointDef.JointType.WeldJoint;
            parentJoint = Physics.GetWorld().createJoint(joint);
        }
    }

    public void setParent(Transform transform){
        if(parent != null){
            parent.children.removeValue(this, true);
            if(parentJoint != null){
                Physics.GetWorld().destroyJoint(parentJoint);
                parentJoint = null;
            }
        }
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
    }

    public Vector3 getPosition(){
        if(parent != null){
            //return parent.getPosition().add(localPosition);
            float angle = (float)Math.atan2(localPosition.y, localPosition.x);
            angle *= MathUtils.radiansToDegrees;
            angle += parent.rotation;
            float dist = localPosition.len();
            Vector3 pos = new Vector3(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle), 0);
            return pos.scl(dist).add(parent.position);
        }
        else return position.cpy();
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
        if(parent != null){
            return parent.rotation + localRotation;
        }
        else return rotation;
    }

    public void setRotation(float degrees){
        rotation = degrees;
        if(parent != null) {
            localRotation = rotation - parent.rotation;
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
