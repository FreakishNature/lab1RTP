package com.proj.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proj.actors.Handler;
import com.proj.model.IotResponse;
import com.proj.model.MessageSensor1;

import java.io.IOException;

public class CollectorReceiver extends Handler {
    ObjectMapper mapper = new ObjectMapper();

    @Override
    public void receive(Object msg) throws IOException {
        if(msg == null) { return; }
        if(msg.toString().contains("panic")){
            throw new IOException("Panic exception");
        }
        system.sendMessage("broker",
                mapper.readValue(msg.toString(), MessageSensor1.class)
        );
    }
}
