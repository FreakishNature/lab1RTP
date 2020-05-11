package com.proj.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.proj.actors.Handler;
import com.proj.model.LegacySensors;
import com.proj.model.LegacySensorsXml;
import com.proj.model.Sensor;

import java.io.IOException;

public class LegacySensorReceiver extends Handler {
    ObjectMapper mapper = new ObjectMapper();
    XmlMapper xmlMapper = new XmlMapper();

    @Override
    public void receive(Object msg) throws IOException {
        if(msg == null) { return; }
        if(msg.toString().contains("panic")){
            throw new IOException("Panic exception");
        }

        system.sendMessage("broker",
            xmlMapper.readValue(mapper.readValue(msg.toString(), LegacySensors.class).getMessage(), LegacySensorsXml.class)
        );
    }
}
