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
    int port = 4321;

    public static Map<String,Topic> topics = new HashMap<>();
    public static String UNITE_SUBSCRIBER_HOST = "230.0.0.10";
    public static String UNITE_BROKER_HOST = "230.0.0.10";
    public static int UNITE_PORT = 4000;

    static {
        topics.put("DATA_UNITED",new Topic("230.0.0.0",4005));

        topics.put("IOT",new Topic("230.0.0.1",4001));
        topics.put("SENSORS",new Topic("230.0.0.2",4002));
        topics.put("LEGACY_SENSORS",new Topic("230.0.0.3",4003));

        topics.put("FORECASTER",new Topic("230.0.0.1",4006));
    }

    public static String AVERAGE_ROUTE = "230.0.0.1";

    @Override
    public void receive(Object msg) throws IOException {
        if(msg instanceof Iot){
            Server.sendUDPMessage(
                    mapper.writeValueAsString(msg),
                    topics.get("IOT").getHost(),
                    topics.get("IOT").getPort()
            );
        }
        if(msg instanceof Sensors){
            Server.sendUDPMessage(
                    mapper.writeValueAsString(msg),
                    topics.get("SENSORS").getHost(),
                    topics.get("SENSORS").getPort()
            );
        }
        if(msg instanceof LegacySensorsXml){
            Server.sendUDPMessage(
                    mapper.writeValueAsString(msg),
                    topics.get("LEGACY_SENSORS").getHost(),
                    topics.get("LEGACY_SENSORS").getPort()
            );
        }

        if(msg instanceof Sensor){
//            Server.sendUDPMessage(
//                    mapper.writeValueAsString(msg),
//                    topics.get("DATA_UNITED").getHost(),
//                    topics.get("DATA_UNITED").getPort()
//            );
        }

    }
}
