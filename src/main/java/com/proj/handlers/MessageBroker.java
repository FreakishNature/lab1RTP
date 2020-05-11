package com.proj.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proj.UDP.Server;
import com.proj.actors.Handler;
import com.proj.model.Sensor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MessageBroker extends Handler {
    ObjectMapper mapper = new ObjectMapper();
    int port = 4321;

    String fullAccessRoute = "230.0.0.0";
    String temperatureAccessRoute = "230.0.0.1";
    String temperatureAndPressureAccessRoute = "230.0.0.2";

    @Override
    public void receive(Object msg) throws IOException {
        Sensor sensor = (Sensor)msg;

        Map<String,Double> temperatureData = new HashMap<>();
        temperatureData.put("temperature",sensor.getTemperatureSensor());

        Map<String,Double> temperatureAndPressureData = new HashMap<>();
        temperatureAndPressureData.put("temperature",sensor.getTemperatureSensor());
        temperatureAndPressureData.put("atmoPressure",sensor.getAtmoPressureSensor());

        Server.sendUDPMessage(
                mapper.writeValueAsString(msg)
                ,fullAccessRoute,
                port
        );

        Server.sendUDPMessage(
                mapper.writeValueAsString(temperatureData),
                temperatureAccessRoute,
                port
        );

        Server.sendUDPMessage(
                mapper.writeValueAsString(temperatureAndPressureData),
                temperatureAndPressureAccessRoute,
                port
        );

    }
}
