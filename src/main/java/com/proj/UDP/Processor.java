package com.proj.UDP;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.eclipse.paho.client.mqttv3.MqttException;

public interface Processor {
    void process(String msg, String topic) throws JsonProcessingException, MqttException;
}
