package com.proj.UDP;

import com.proj.handlers.MessageBroker;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Subscription extends Thread {
    String topic;
    volatile boolean subscribed = true;
    Processor process;

    public void unsubscribe() {
        subscribed = false;
    }

    public Subscription(String topic, Processor process) {
        this.topic = topic;
        this.process = process;
    }


    @Override
    public void run() {
        try {
            receiveUDPMessage(topic);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void receiveUDPMessage(String topic) throws IOException, ClassNotFoundException {
        String ip = MessageBroker.topics.get(topic).host;
        int port = MessageBroker.topics.get(topic).port;

        byte[] buffer = new byte[2048];
        MulticastSocket socket = new MulticastSocket(port);
        InetAddress group = InetAddress.getByName(ip);
        socket.joinGroup(group);
        while (subscribed) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            String msg = new String(packet.getData(), packet.getOffset(), packet.getLength());

            try {
                process.process(msg, topic);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        socket.leaveGroup(group);
        socket.close();
    }

}
