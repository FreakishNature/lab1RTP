package com.proj.UDP;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proj.handlers.MessageBroker;
import com.proj.model.*;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class CollectorSubscriber extends Subscriber {
    static ObjectMapper mapper = new ObjectMapper();
    static Map<String, Iot> iotSensors = new ConcurrentHashMap<>();
    static Map<String, Sensors> sensors = new ConcurrentHashMap<>();
    static Map<String, LegacySensorsXml> legacySensors = new ConcurrentHashMap<>();
    static Set<String> timestamps = new ConcurrentSkipListSet<>();

    static void sendMessage(String sensor) throws IOException {
        Server.sendUDPMessage(sensor, MessageBroker.topics.get("COLLECTOR").host, MessageBroker.topics.get("COLLECTOR").port);
    }

    static String approximateTime(String timestamp) {
        return String.valueOf((Math.floor(Double.parseDouble(timestamp) / 1000)));
    }

    static void collect(Object msg, String topic) throws JsonProcessingException {
        if (topic.equals("IOT")) {
            Iot sensor = mapper.readValue(msg.toString(), Iot.class);
            iotSensors.put(approximateTime(sensor.getTimestamp()), sensor);
            timestamps.add(approximateTime(sensor.getTimestamp()));
        }
        if (topic.equals("SENSORS")) {
            Sensors sensor = mapper.readValue(msg.toString(), Sensors.class);
            sensors.put(approximateTime(sensor.getTimestamp()), sensor);
            timestamps.add(approximateTime(sensor.getTimestamp()));

        }
        if (topic.equals("LEGACY_SENSORS")) {
            LegacySensorsXml sensor = mapper.readValue(msg.toString(), LegacySensorsXml.class);
            legacySensors.put(approximateTime(sensor.getUnix_timestamp_100us()), sensor);
            timestamps.add(approximateTime(sensor.getUnix_timestamp_100us()));
        }
        synchronized (CollectorSubscriber.class) {
            timestamps.forEach(ts -> {
                if (iotSensors.get(ts) != null &&
                        sensors.get(ts) != null &&
                        legacySensors.get(ts) != null) {

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
                        prettyPrint(messageSensor1);
                        prettyPrint(messageSensor2);

                        sendMessage(mapper.writeValueAsString(messageSensor1));
                        sendMessage(mapper.writeValueAsString(messageSensor2));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static void prettyPrint(Sensor sensor) {
        System.out.println(
                "\n Temperature - " + sensor.getTemperatureSensor() +
                        "\n Humidity - " + sensor.getHumiditySensor() +
                        "\n Atmosphere pressure - " + sensor.getAtmoPressureSensor() +
                        "\n Light - " + sensor.getLightSensor() +
                        "\n Wind speed - " + sensor.getWindSpeedSensor());
    }


    public static void main(String[] args) throws InterruptedException {

        CollectorSubscriber subscriber = new CollectorSubscriber();

        subscriber.subscribe("LEGACY_SENSORS", CollectorSubscriber::collect);
        subscriber.subscribe("SENSORS", CollectorSubscriber::collect);
        subscriber.subscribe("IOT", CollectorSubscriber::collect);

        Thread.sleep(60_000);

        subscriber.unsubscribe("LEGACY_SENSORS");
        subscriber.unsubscribe("SENSORS");
        subscriber.unsubscribe("IOT");
    }
}