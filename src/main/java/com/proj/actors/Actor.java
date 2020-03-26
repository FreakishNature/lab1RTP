package com.proj.actors;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Actor extends Thread{
    static Logger log = Logger.getLogger(Actor.class);
    private volatile ConcurrentLinkedQueue<Object> inbox;
    private String actorName;
    private Handler handler;
    private volatile boolean isRunning = true;

    String getActorName() {
        return actorName;
    }

    void stopThread(){
        isRunning = false;
    }

    Actor(String name, ActorSystem system, Handler handler){
        handler.system = system;
        this.actorName = name;
        this.handler = handler;
        this.inbox = new ConcurrentLinkedQueue<>();
    }

    Actor(Actor actor){
        this.inbox = actor.inbox;
        this.handler = actor.handler;
        this.actorName = actor.actorName;
    }


    public Queue<Object> getInbox() {
        return inbox;
    }

    @Override
    public void run(){

        while (isRunning){
            try{
                handler.receive(!inbox.isEmpty() ? inbox.remove() : null);
            } catch (IOException ex){
                log.warn(ex.getMessage());
                try {
                    handler.system.recreate(this);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
