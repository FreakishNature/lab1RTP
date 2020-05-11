package com.proj;

import com.proj.actors.ActorSystem;
import com.proj.handlers.*;


import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.sse.SseEventSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.TimeUnit;
import static com.proj.handlers.Handlers.*;

public class Application {
    private static ActorSystem actorSystem = new ActorSystem();
    private static String url = "http://localhost:4000";


    static void connectToServer(String path,String saveToActor) throws InterruptedException {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(url + path);
        try (SseEventSource source = SseEventSource
                .target(target)
                .reconnectingEvery(5, TimeUnit.SECONDS)
                .build()) {
            source.register(inboundSseEvent -> {
                        String data = inboundSseEvent.readData();
                        actorSystem.sendMessage(saveToActor, data);
                    }
            );
            source.open();
            Thread.sleep(60_000);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int forecastInterval1 = 5;
        int forecastInterval2 = 5;

//        if(args.length < 3){
//            url = "http://localhost:4000/iot";
//        } else {
//            url = "http://" + args[2] + ":4000/iot";
//            forecastInterval1 = Integer.parseInt(args[0]);
//            forecastInterval2 = Integer.parseInt(args[1]);
//        }


        actorSystem.createActorGroup("forecaster",
                new Forecaster(forecastInterval1)
        );

        actorSystem.createActorGroup("dataReceiverIot", new IotReceiver());
        actorSystem.createActorGroup("unitedReceiver", new UnitedReceiver());
        actorSystem.createActorGroup("dataReceiverSensors", new SensorReceiver());
        actorSystem.createActorGroup("dataReceiverLegacySensors", new LegacySensorReceiver());

        actorSystem.createActorGroup("broker", new MessageBroker());

        actorSystem.createActorGroup("printer",printer);
        actorSystem.start();

        Thread sensorThread = new Thread(()->{
            try {
                connectToServer("/sensors","dataReceiverSensors");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread legasySensorsThread = new Thread(()->{
            try {
                connectToServer("/legacy_sensors","dataReceiverLegacySensors");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread iotThread = new Thread(()->{
            try {
                connectToServer("/iot","dataReceiverIot");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread uniteThread = new Thread(()->{
            byte[] buffer = new byte[2048];
            MulticastSocket socket = null;
            try {
                socket = new MulticastSocket(MessageBroker.topics.get("COLLECTOR").getPort());
                InetAddress group = InetAddress.getByName(MessageBroker.topics.get("COLLECTOR").getHost());
                socket.joinGroup(group);
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    String msg = new String(packet.getData(), packet.getOffset(), packet.getLength());
                    System.out.println("Received - " +msg);
                    actorSystem.sendMessage("unitedReceiver", msg);

                    if ("STOP".equals(msg)) {
                        System.out.println("No more messages. Stopping : " + msg);
                        break;
                    }
                }
                socket.leaveGroup(group);
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        sensorThread.start();
        legasySensorsThread.start();
        iotThread.start();
        uniteThread.start();


        uniteThread.join();
        sensorThread.join();
        legasySensorsThread.join();
        iotThread.join();
        actorSystem.shutDown();
    }
}
