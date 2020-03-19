package actors;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ActorSystem {
    Map<String,ActorGroup> registry = new HashMap<>();
    int maxLoad = 5;

    void recreate(Actor deadActor) throws InterruptedException {
        CopyOnWriteArrayList<Actor> actorGroup = registry.get(deadActor.getActorName()).getActors();
        int indexOfDeadActor = actorGroup.indexOf(deadActor);

        deadActor.stopThread();

        actorGroup.set(indexOfDeadActor, new Actor(deadActor));
        actorGroup.get(indexOfDeadActor).start();
        System.out.println("Actor " + deadActor.getActorName() + indexOfDeadActor + " was recreated");
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
