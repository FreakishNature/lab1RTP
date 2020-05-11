package com.proj.UDP;

import com.proj.handlers.MessageBroker;
import com.proj.model.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class ClienCollector implements Runnable {
    Map<String, Iot> iotSensors = new ConcurrentHashMap<>();
    Map<String, Sensors> sensors = new ConcurrentHashMap<>();
    Map<String, LegacySensorsXml> legacySensors = new ConcurrentHashMap<>();
    Set<String> timestamps = new ConcurrentSkipListSet<>();

    void sendMessage(Sensor sensor) throws IOException {
        Server.sendUDPMessage(sensor, MessageBroker.UNITE_BROKER_HOST, 4001);
    }

    void collect(Object msg){
        if(msg instanceof Iot){
            Iot sensor = (Iot)msg;
            iotSensors.put(sensor.getTimestamp(),sensor);
            timestamps.add(sensor.getTimestamp());
        }
        if(msg instanceof Sensors){
            Sensors sensor = (Sensors)msg;
            sensors.put(sensor.getTimestamp(),sensor);
            timestamps.add(sensor.getTimestamp());

        }
        if(msg instanceof LegacySensorsXml){
            LegacySensorsXml sensor = (LegacySensorsXml)msg;
            legacySensors.put(sensor.getUnix_timestamp_100us(),sensor);
            timestamps.add(sensor.getUnix_timestamp_100us());
        }

        timestamps.forEach(ts -> {
            if( iotSensors.get(ts) != null &&
                    sensors.get(ts) != null &&
                    legacySensors.get(ts) != null){

                MessageSensor1 messageSensor1 = new MessageSensor1(
                        legacySensors.get(ts).getTemperature_celsius().getValue().get(0),
                        legacySensors.get(ts).getHumidity_percent().getValue().get(0),
                        iotSensors.get(ts).getWindSpeedSensor1(),
                        iotSensors.get(ts).getAtmoPressureSensor1(),
                        sensors.get(ts).getLightSensor1()
                );

                MessageSensor2 messageSensor2 = new MessageSensor2(
                        legacySensors.get(ts).getTemperature_celsius().getValue().get(1),
                        legacySensors.get(ts).getHumidity_percent().getValue().get(1),
                        iotSensors.get(ts).getWindSpeedSensor2(),
                        iotSensors.get(ts).getAtmoPressureSensor2(),
                        sensors.get(ts).getLightSensor2()
                );

                try {
                    sendMessage(messageSensor1);
                    sendMessage(messageSensor2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void receiveUDPObject(String ip, int port) throws IOException, ClassNotFoundException {
        byte[] buffer = new byte[1024];
        MulticastSocket socket = new MulticastSocket(port);
        InetAddress group = InetAddress.getByName(ip);
        socket.joinGroup(group);
        while (true) {
            System.out.println("Waiting for multicast message...");
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));
            Object receivedObject = iStream.readObject();
            iStream.close();

            collect(receivedObject);

            String msg = new String(packet.getData(), packet.getOffset(), packet.getLength());

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
            receiveUDPObject(MessageBroker.UNITE_BROKER_HOST, 4001);
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Thread t = new Thread(new Client());
        t.start();
    }
}