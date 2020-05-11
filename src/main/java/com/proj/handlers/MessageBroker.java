package com.proj.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proj.UDP.Server;
import com.proj.UDP.Topic;
import com.proj.actors.Handler;
import com.proj.model.Iot;
import com.proj.model.LegacySensorsXml;
import com.proj.model.Sensor;
import com.proj.model.Sensors;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MessageBroker extends Handler {
    ObjectMapper mapper = new ObjectMapper();
    public static Map<String, Topic> topics = new HashMap<>();

    static {
        topics.put("COLLECTOR", new Topic("230.0.0.10", 4000));
        topics.put("JOINED_DATA", new Topic("230.0.0.0", 4005));

        topics.put("IOT", new Topic("230.0.0.1", 4001));
        topics.put("SENSORS", new Topic("230.0.0.2", 4002));
        topics.put("LEGACY_SENSORS", new Topic("230.0.0.3", 4003));

        topics.put("FORECASTER", new Topic("230.0.0.1", 4006));
    }

    @Override
    public void receive(Object msg) throws IOException {
        if (msg instanceof Iot) {
            Server.sendUDPMessage(
                    mapper.writeValueAsString(msg),
                    topics.get("IOT").getHost(),
                    topics.get("IOT").getPort()
            );
        }
        if (msg instanceof Sensors) {
            Server.sendUDPMessage(
                    mapper.writeValueAsString(msg),
                    topics.get("SENSORS").getHost(),
                    topics.get("SENSORS").getPort()
            );
        }
        if (msg instanceof LegacySensorsXml) {
            Server.sendUDPMessage(
                    mapper.writeValueAsString(msg),
                    topics.get("LEGACY_SENSORS").getHost(),
                    topics.get("LEGACY_SENSORS").getPort()
            );
        }

        if (msg instanceof Sensor) {
            Server.sendUDPMessage(
                    mapper.writeValueAsString(msg),
                    topics.get("JOINED_DATA").getHost(),
                    topics.get("JOINED_DATA").getPort()
            );
        }

    }
}
