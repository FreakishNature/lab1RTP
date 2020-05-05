package com.proj.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Server extends Thread {

    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];

    public Server() throws SocketException {
        socket = new DatagramSocket(4445);
    }

    public void run() {
        running = true;

        while (running) {
            try {
                DatagramPacket receivedPacket
                        = new DatagramPacket(buf, buf.length);
                socket.receive(receivedPacket);
                InetAddress address = receivedPacket.getAddress();
                int port = receivedPacket.getPort();

                DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, address, port);
                String received
                        = new String(receivedPacket.getData(), 0, receivedPacket.getLength());


                if (received.equals("stop")) {
                    running = false;
                }
                socket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        socket.close();
    }

    public static void main(String[] args) throws SocketException {
        new Server().start();
    }
}