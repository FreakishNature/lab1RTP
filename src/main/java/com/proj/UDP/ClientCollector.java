package com.proj.UDP;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

public class ClientCollector  {
    static ObjectMapper mapper = new ObjectMapper();
    static Map<String, Iot> iotSensors = new ConcurrentHashMap<>();
    static Map<String, Sensors> sensors = new ConcurrentHashMap<>();
    static Map<String, LegacySensorsXml> legacySensors = new ConcurrentHashMap<>();
    static Set<String> timestamps = new ConcurrentSkipListSet<>();

    static void sendMessage(String sensor) throws IOException {
//        System.out.println(sensor);
        Server.sendUDPMessage(sensor, MessageBroker.topics.get("DATA_UNITED").host, MessageBroker.topics.get("DATA_UNITED").port);
    }

    static String aproximateTime(String timestamp){
        return String.valueOf((Math.floor(Double.parseDouble(timestamp) / 1000 )));
    }

    static void collect(Object msg, String topic) throws JsonProcessingException {
        if(topic.equals("IOT")){
            Iot sensor = mapper.readValue(msg.toString(),Iot.class);
            iotSensors.put(aproximateTime(sensor.getTimestamp()), sensor);
            timestamps.add(aproximateTime(sensor.getTimestamp()));
        }
        if(topic.equals("SENSORS")){
            Sensors sensor = mapper.readValue(msg.toString(),Sensors.class);
            sensors.put(aproximateTime(sensor.getTimestamp()),sensor);
            timestamps.add(aproximateTime(sensor.getTimestamp()));

        }
        if(topic.equals("LEGACY_SENSORS")){
            LegacySensorsXml sensor = mapper.readValue(msg.toString(),LegacySensorsXml.class);
            legacySensors.put(aproximateTime(sensor.getUnix_timestamp_100us()),sensor);
            timestamps.add(aproximateTime(sensor.getUnix_timestamp_100us()));
        }
        synchronized (ClientCollector.class){
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

                    MessageSensor1 messageSensor2 = new MessageSensor1(
                            legacySensors.get(ts).getTemperature_celsius().getValue().get(1),
                            legacySensors.get(ts).getHumidity_percent().getValue().get(1),
                            iotSensors.get(ts).getWindSpeedSensor2(),
                            iotSensors.get(ts).getAtmoPressureSensor2(),
                            sensors.get(ts).getLightSensor2()
                    );

                    iotSensors.remove(ts);
                    sensors.remove(ts);
                    legacySensors.remove(ts);
                    timestamps.remove(ts);

                    try {
                        sendMessage(mapper.writeValueAsString(messageSensor1));
                        sendMessage(mapper.writeValueAsString(messageSensor2));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static void receiveUDPMessage(String topic) throws IOException, ClassNotFoundException {
        String ip = MessageBroker.topics.get(topic).host;
        int port = MessageBroker.topics.get(topic).port;

        byte[] buffer = new byte[2048];
        MulticastSocket socket = new MulticastSocket(port);
        InetAddress group = InetAddress.getByName(ip);
        socket.joinGroup(group);
        while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            String msg = new String(packet.getData(), packet.getOffset(), packet.getLength());

            collect(msg,topic);

            if ("STOP".equals(msg)) {
                System.out.println("No more messages. Stopping : " + msg);
                break;
            }
        }
        socket.leaveGroup(group);
        socket.close();
    }



    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        Thread legacyThread = new Thread(()->{
            try {
                receiveUDPMessage("LEGACY_SENSORS");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        Thread sensorsThread = new Thread(()->{
            try {
                receiveUDPMessage("SENSORS");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        Thread iorThread = new Thread(()->{
            try {
                receiveUDPMessage("IOT");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        legacyThread.start();
        sensorsThread.start();
        iorThread.start();

        legacyThread.join();
        sensorsThread.join();
        iorThread.join();
    }
}