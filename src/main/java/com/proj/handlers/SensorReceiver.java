package com.proj.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proj.actors.Handler;
import com.proj.model.Iot;
import com.proj.model.LegacySensors;
import com.proj.model.Sensors;
import com.proj.model.SensorsResponse;

import java.io.IOException;

public class SensorReceiver extends Handler {
    static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws JsonProcessingException {
        String msg = "{\"message\": {\"light_sensor_1\": 242.0,\"light_sensor_2\": 128.0,\"unix_timestamp_100us\": 15889898889746}}";
        System.out.println(mapper.writeValueAsString(
                mapper.readValue(msg, SensorsResponse.class).getMessage()));
    }
    @Override
    public void receive(Object msg) throws IOException {
        if(msg == null) { return; }
        if(msg.toString().contains("panic")){
            throw new IOException("Panic exception");
        }
        system.sendMessage("broker",
                mapper.readValue(msg.toString(), SensorsResponse.class).getMessage()
        );
    }
}
