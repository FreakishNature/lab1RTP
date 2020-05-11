package com.proj.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Server {

    public static void sendUDPMessage(String message,
                                      String address, int port) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress group = InetAddress.getByName(address);
        byte[] msg = message.getBytes();
        DatagramPacket packet = new DatagramPacket(msg, msg.length,
                group, port);
        System.out.println("Sending msg: " + message);
        socket.send(packet);
        socket.close();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String address = "230.0.0.0";
        int port = 4321;

        for (int i = 0; i < 100; i++) {
            sendUDPMessage("This is a multicast msg nr." + i, address, port);
            Thread.sleep(1000);
        }
        sendUDPMessage("STOP", address, port);
    }
}