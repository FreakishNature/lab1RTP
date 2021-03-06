package com.proj.actors;

import java.util.concurrent.CopyOnWriteArrayList;

public class ActorGroup {
    Handler handler;
    CopyOnWriteArrayList<Actor> actors = new CopyOnWriteArrayList<>();
    String groupName;
    ActorSystem system;

    public ActorGroup(String groupName, ActorSystem system, Handler handler) {
        this.handler = handler;
        this.groupName = groupName;
        this.system = system;
    }

    public void addActor(){
        Actor createdActor = new Actor(groupName,system,handler);
        createdActor.start();
        actors.add(createdActor);
    }

    public synchronized void removeActor(Actor removingActor){
        actors.remove(removingActor);
        removingActor.stopThread();
    }

    public CopyOnWriteArrayList<Actor> getActors() {
        return actors;
    }
}
