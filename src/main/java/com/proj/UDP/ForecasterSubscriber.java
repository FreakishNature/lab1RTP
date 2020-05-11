package com.proj.UDP;

import com.proj.handlers.MessageBroker;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class ForecasterSubscriber implements Runnable {

    public static void main(String[] args) {
        Thread t = new Thread(new ForecasterSubscriber());
        t.start();
    }

    public void receiveUDPMessage(String ip, int port) throws IOException {
        byte[] buffer = new byte[2048];
        MulticastSocket socket = new MulticastSocket(port);
        InetAddress group = InetAddress.getByName(ip);
        socket.joinGroup(group);
        while (true) {
//            System.out.println("Waiting for multicast message...");
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            String msg = new String(packet.getData(), packet.getOffset(), packet.getLength());

            System.out.println("Message received: " + msg);
            if ("STOP".equals(msg)) {
                System.out.println("No more messages. Stopping : " + msg);
                break;
            }
        }
        socket.leaveGroup(group);
        socket.close();
    }

    @Override
    public void run() {
        try {
            receiveUDPMessage(
                    MessageBroker.topics.get("DATA_UNITED").host,
                    MessageBroker.topics.get("DATA_UNITED").port
            );
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}