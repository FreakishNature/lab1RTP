package com.proj.MQTT;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Test {
    public static void main(String[] args) throws Exception {
        String server = "tcp://mqtt.eclipse.org:1883";      //public MQTT broker hosted by the Paho project
        String publisherId = UUID.randomUUID().toString();
        IMqttClient publisher = new MqttClient(server,publisherId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);  // discard unsent messages from a previous run
        options.setConnectionTimeout(10);
        publisher.connect(options);

        StubSensor sensor = new StubSensor(publisher);

        sensor.call(); // sends message to MQTT broker

        // receives message from MQTT broker, own message is received in this case as publisher=subscriber
        CountDownLatch receivedSignal = new CountDownLatch(10);
        publisher.subscribe(StubSensor.TOPIC, (topic, msg) -> {
            byte[] payload = msg.getPayload();
            System.out.println(new String(payload));
            receivedSignal.countDown();
        });
        receivedSignal.await(5, TimeUnit.SECONDS);

        sensor.call();

    }
}
