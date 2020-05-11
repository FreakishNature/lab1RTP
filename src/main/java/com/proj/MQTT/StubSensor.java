package com.proj.MQTT;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Random;
import java.util.concurrent.Callable;

public class StubSensor implements Callable<Void> {

    private IMqttClient client;
    public static String TOPIC = "test";
    private Random rnd = new Random();

    public StubSensor(IMqttClient client) {
        this.client = client;
    }

    @Override
    public Void call() throws Exception {
        if ( !client.isConnected()) {
            return null;
        }
        MqttMessage msg = readEngineTemp();
        msg.setQos(0);
        msg.setRetained(true);
        System.out.println(msg.toString());
        client.publish(TOPIC,msg);
        return null;
    }

    private MqttMessage readEngineTemp() {
        double temp =  80 + rnd.nextDouble() * 20.0;
        byte[] payload = String.format("T:%04.2f",temp)
                .getBytes();
        return new MqttMessage(payload);
    }
}