package com.proj.UDP;

import org.eclipse.paho.client.mqttv3.*;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;

public class MqttAdapterSubscriber extends Subscriber implements Callable<Void> {

    private IMqttClient client;
    public static String TOPIC = "test";
    private Random rnd = new Random();

    public MqttAdapterSubscriber(IMqttClient client) {
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


    public static void main(String[] args) throws InterruptedException, MqttException {
        String server = "tcp://mqtt.eclipse.org:1883";      //public MQTT broker hosted by the Paho project
        String publisherId = UUID.randomUUID().toString();
        IMqttClient client = new MqttClient(server,publisherId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);  // discard unsent messages from a previous run
        options.setConnectionTimeout(10);
        client.connect(options);


        MqttAdapterSubscriber subscriber = new MqttAdapterSubscriber();

        subscriber.subscribe("DATA_UNITED", (msg,topic)->{
            if ( !client.isConnected()) {
                return null;
            }
            MqttMessage msg = readEngineTemp();
            msg.setQos(0);
            msg.setRetained(true);
            System.out.println(msg.toString());
            client.publish(TOPIC,msg);
        });

        Thread.sleep(60_000);

        subscriber.unsubscribe("DATA_UNITED");
    }

}
