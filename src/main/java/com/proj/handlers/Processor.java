package com.proj.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proj.actors.Handler;
import com.proj.model.*;
import sun.management.Sensor;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Processor extends Handler{
    Map<String,Iot> iotSensors = new HashMap<>();
    Map<String, Sensors> sensors = new HashMap<>();
    Map<String, LegacySensorsXml> legacySensors = new HashMap<>();
    Set<String> timestamps = new HashSet<>();

@Override
public void receive(Object msg) throws IOException {
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


            }
        });
    }
};
