package com.proj.actors;


import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ActorSystem {
    Map<String,ActorGroup> registry = new HashMap<>();
    int maxLoad = 20;
    Logger log = Logger.getLogger(ActorSystem.class);

    synchronized void recreate(Actor deadActor) throws InterruptedException {
        ActorGroup actorGroup = registry.get(deadActor.getActorName());
        CopyOnWriteArrayList<Actor> actors =  actorGroup.getActors();
        int indexOfDeadActor = actors.indexOf(deadActor);
        actorGroup.removeActor(deadActor);

        if(indexOfDeadActor >= actors.size() || indexOfDeadActor < 0){
            actors.add(new Actor(deadActor));
            actors.get(actors.size() - 1).start();
        } else {
            actors.add(indexOfDeadActor, new Actor(deadActor));
            actors.get(indexOfDeadActor).start();
        }

        log.info("Actor " + deadActor.getActorName() + indexOfDeadActor + " was recreated");
    }

    public void createActorGroup(String name, Handler handler){
        registry.put(name, new ActorGroup(name,this,handler));
    }

    public void sendMessage(String toActor, Object msg){
        if(msg == null) return;

        ActorGroup actorGroup = registry.get(toActor);
        CopyOnWriteArrayList<Actor> actors = actorGroup.getActors();

        if(actors.isEmpty()){
            actorGroup.addActor();
        }

        boolean messageIsSend = false;

        for(Actor actor : actors){
            // creating new actor on high load
            if(actor.getInbox().size() < maxLoad && !messageIsSend){
                actor.getInbox().add(msg);
                messageIsSend = true;
                continue;
            }
            // removing actors on low load
            if(messageIsSend && actor.getInbox().isEmpty()){
                actorGroup.removeActor(actor);
            }
        }

        if(!messageIsSend){
            actorGroup.addActor();

            // sending message to last created actor
            actors.get(actors.size() - 1)
                    .getInbox()
                    .add(msg);
        }

    }

    public void getLoad(){
        System.out.println("==========================");
        System.out.println("Load for each actor : ");
        registry.values().forEach( actorGroup ->
            {
                for(int i = 0 ; i < actorGroup.getActors().size(); i++){
                    Actor actor = actorGroup.getActors().get(i);
                    System.out.println(actor.getActorName() + "_" + i  + " - load is - " + actor.getInbox().size());
                }
            }
        );
        System.out.println("==========================");

    }

    public void start(){
        registry.values()
                .forEach( actorGroup ->
                        actorGroup.getActors()
                                .forEach(Actor::start)
                );
    }

    public void shutDown() throws InterruptedException {
        for (ActorGroup actorGroup : registry.values()) {
            for(Actor actor : actorGroup.getActors()){
                actor.stopThread();
//                actor.join();
            }
        }
    }
}
