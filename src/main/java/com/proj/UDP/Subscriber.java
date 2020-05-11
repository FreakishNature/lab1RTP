package com.proj.UDP;

import com.proj.handlers.MessageBroker;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.Map;

public abstract class Subscriber {
    Map<String, Subscription> subscriptions = new HashMap<>();

    protected void subscribe(String topic,Processor processor){
        Subscription subscription = new Subscription(topic,processor);
        subscription.start();
        subscriptions.put(topic, subscription);
    }

    protected void unsubscribe(String topic){
        subscriptions.get(topic).unsubscribe();
    }


}
