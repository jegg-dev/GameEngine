package com.jegg.spacesim.core.ecs;

import com.badlogic.ashley.core.Entity;

import java.util.HashMap;
import java.util.Map;

public class ContactSystem {
    public HashMap<Entity, Entity> entryContacts = new HashMap<>();
    public HashMap<Entity, Entity> sensorEntryContacts = new HashMap<>();
    public HashMap<Entity, Entity> exitContacts = new HashMap<>();
    public HashMap<Entity, Entity> sensorExitContacts = new HashMap<>();

    public void update(){
        for(Map.Entry<Entity, Entity> e : entryContacts.entrySet()){
            if(e.getKey() != null && e.getValue() != null){
                if(e.getKey() instanceof IContactListener)
                    ((IContactListener)e.getKey()).contactEnter(e.getValue());
                if(e.getValue() instanceof IContactListener)
                    ((IContactListener)e.getValue()).contactEnter(e.getKey());
            }
        }

        for(Map.Entry<Entity, Entity> e : sensorEntryContacts.entrySet()){
            if(e.getKey() != null && e.getValue() != null){
                ((ISensorContactListener) e.getKey()).sensorContactEnter(e.getValue());
            }
        }

        for(Map.Entry<Entity, Entity> e : exitContacts.entrySet()){
            if(e.getKey() != null && e.getValue() != null){
                if(e.getKey() instanceof IContactListener)
                    ((IContactListener)e.getKey()).contactExit(e.getValue());
                if(e.getValue() instanceof IContactListener)
                    ((IContactListener)e.getValue()).contactExit(e.getKey());
            }
        }

        for(Map.Entry<Entity, Entity> e : sensorExitContacts.entrySet()){
            if(e.getKey() != null && e.getValue() != null){
                ((ISensorContactListener) e.getKey()).sensorContactExit(e.getValue());
            }
        }

        entryContacts.clear();
        sensorEntryContacts.clear();
        exitContacts.clear();
        sensorEntryContacts.clear();
    }
}