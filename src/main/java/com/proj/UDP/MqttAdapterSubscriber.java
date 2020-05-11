package com.proj.UDP;

import com.proj.MQTT.StubSensor;
import org.eclipse.paho.client.mqttv3.*;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MqttAdapterSubscriber extends Subscriber {

    private IMqttClient client;
    public static final String TOPIC = "FORECAST";

    public MqttAdapterSubscriber(IMqttClient client) {
        this.client = client;
    }


    public static void main(String[] args) throws InterruptedException, MqttException {
        String server = "tcp://mqtt.eclipse.org:1883";      //public MQTT broker hosted by the Paho project
        String publisherId = UUID.randomUUID().toString();
        IMqttClient client = new MqttClient(server, publisherId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);  // discard unsent messages from a previous run
        options.setConnectionTimeout(10);
        client.connect(options);


        MqttAdapterSubscriber subscriber = new MqttAdapterSubscriber(client);

        subscriber.subscribe("JOINED_DATA", (msg, topic) -> {
            System.out.println("Message received from MY broker: " + msg);
            if (!client.isConnected()) {
                return;
            }
            MqttMessage mqttMsg = new MqttMessage(msg.getBytes());
            mqttMsg.setQos(0);
            mqttMsg.setRetained(true);
            client.publish(TOPIC, mqttMsg);
            System.out.println("Message send to MQTT broker: " + msg);
        });

        // receives message from MQTT broker, own message is received in this case as publisher=subscriber
        CountDownLatch receivedSignal = new CountDownLatch(10);
        client.subscribe(TOPIC, (topic, msg) -> {
            byte[] payload = msg.getPayload();
            System.out.println("Message received from MQTT broker: " + msg);
            receivedSignal.countDown();
        });
        receivedSignal.await(5, TimeUnit.SECONDS);

        Thread.sleep(60_000);
        subscriber.unsubscribe("JOINED_DATA");
    }

}
