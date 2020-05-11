package com.proj.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proj.actors.Handler;
import com.proj.model.Iot;

import java.io.IOException;

public class IotReceiver extends Handler {
    ObjectMapper mapper = new ObjectMapper();

    @Override
    public void receive(Object msg) throws IOException {
        if(msg == null) { return; }
        if(msg.toString().contains("panic")){
            throw new IOException("Panic exception");
        }
        system.sendMessage("processor",
                mapper.readValue(msg.toString(), Iot.class)
        );
    }
}