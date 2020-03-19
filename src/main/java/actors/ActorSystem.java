package actors;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ActorSystem {
    private ConcurrentMap<String,Handler> registryHandlers = new ConcurrentHashMap<>();
    private ConcurrentMap<String, ArrayList<Actor>> registryActors = new ConcurrentHashMap<>();

    void recreate(Actor deadActor) throws InterruptedException {
        List<Actor> actorGroup = registryActors.get(deadActor.getActorName());
        int indexOfDeadActor = actorGroup.indexOf(deadActor);

        deadActor.stopThread();

        actorGroup.set(indexOfDeadActor, new Actor(deadActor));
        actorGroup.get(indexOfDeadActor).start();
        System.out.println("Actor " + deadActor.getActorName() + indexOfDeadActor + " was recreated");
    }

    public void createActor(String name,Handler handler){
        registry.put(name,new Actor(name,this, handler));
    }

//    public Actor getActor(String name){ return registry.get(name); }

    public void sendMessage(String toActor,Object msg){
        getActor(toActor).getInbox().add(msg);
    }

    public void getLoad(){
        System.out.println("==========================");
        System.out.println("Load for each actor : ");
        registry.values().forEach( a ->
            System.out.println(a.getActorName() + " - load is - " + a.getInbox().size())
        );
        System.out.println("==========================");

    }

    public void start(){
        registry.values()
                .forEach(Actor::start);
    }

    public void shutDown() throws InterruptedException {
        for(Actor actor : registry.values()){
            actor.stopThread();
            actor.join();
        }
    }
}
