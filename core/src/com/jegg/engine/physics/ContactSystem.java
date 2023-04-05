package com.jegg.engine.physics;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.Map;

public class ContactSystem {
    public HashMap<Entity, Array<Entity>> entryContacts = new HashMap<>();
    public HashMap<Entity, Array<Entity>> sensorEntryContacts = new HashMap<>();

    public HashMap<Entity, Array<Entity>> exitContacts = new HashMap<>();
    public HashMap<Entity, Array<Entity>> sensorExitContacts = new HashMap<>();

    public void update(){
        for(Map.Entry<Entity, Array<Entity>> e : entryContacts.entrySet()){
            for(Entity e2 : e.getValue()) {
                if (e.getKey() != null && e2 != null) {
                    if (e.getKey() instanceof IContactListener) {
                        ((IContactListener) e.getKey()).contactEnter(e2);
                    }
                    if (e2 instanceof IContactListener) {
                        ((IContactListener) e2).contactEnter(e.getKey());
                    }
                }
            }
        }

        for(Map.Entry<Entity, Array<Entity>> e : sensorEntryContacts.entrySet()){
            for(Entity e2 : e.getValue()) {
                if (e.getKey() != null && e2 != null) {
                    ((ISensorContactListener) e.getKey()).sensorContactEnter(e2);
                }
            }
        }

        for(Map.Entry<Entity, Array<Entity>> e : exitContacts.entrySet()){
            for(Entity e2 : e.getValue()) {
                if (e.getKey() != null && e2 != null) {
                    if (e.getKey() instanceof IContactListener) {
                        ((IContactListener) e.getKey()).contactExit(e2);
                    }
                    if (e2 instanceof IContactListener) {
                        ((IContactListener) e2).contactExit(e.getKey());
                    }
                }
            }
        }

        for(Map.Entry<Entity, Array<Entity>> e : sensorExitContacts.entrySet()){
            for(Entity e2 : e.getValue()) {
                if (e.getKey() != null && e2 != null) {
                    ((ISensorContactListener) e.getKey()).sensorContactExit(e2);
                }
            }
        }

        entryContacts.clear();
        sensorEntryContacts.clear();
        exitContacts.clear();
        sensorEntryContacts.clear();
    }

    public void contactEnter(Entity e1, Entity e2){
        if(entryContacts.get(e1) == null){
            Array<Entity> arr = new Array<>();
            arr.add(e2);
            entryContacts.put(e1, arr);
        }
        else{
            entryContacts.get(e1).add(e2);
        }
    }

    public void contactExit(Entity e1, Entity e2){
        if(exitContacts.get(e1) == null){
            Array<Entity> arr = new Array<>();
            arr.add(e2);
            exitContacts.put(e1, arr);
        }
        else{
            exitContacts.get(e1).add(e2);
        }
    }

    public void sensorContactEnter(Entity e1, Entity e2){
        if(sensorEntryContacts.get(e1) == null){
            Array<Entity> arr = new Array<>();
            arr.add(e2);
            sensorEntryContacts.put(e1, arr);
        }
        else{
            sensorEntryContacts.get(e1).add(e2);
        }
    }

    public void sensorContactExit(Entity e1, Entity e2){
        if(sensorExitContacts.get(e1) == null){
            Array<Entity> arr = new Array<>();
            arr.add(e2);
            sensorExitContacts.put(e1, arr);
        }
        else{
            sensorExitContacts.get(e1).add(e2);
        }
    }
}