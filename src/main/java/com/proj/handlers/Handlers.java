package com.proj.handlers;

import com.proj.actors.Handler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proj.model.ResponseSensor1;
import com.proj.model.ResponseSensor2;
import org.apache.log4j.Logger;

import java.io.IOException;

public class Handlers {
    static ObjectMapper mapper = new ObjectMapper();
    static Logger log = Logger.getLogger(Handler.class);

    public static Handler dataReceiver = new Handler() {
        @Override
        public void receive(Object msg) throws IOException {
            if(msg == null) { return; }
            String data = msg.toString();
            system.sendMessage("processor",data);
        }
    };

    public static Handler processor = new Handler() {
        @Override
        public void receive(Object msg) throws IOException {
            if(msg == null) { return; }
            if(msg.toString().contains("panic")){
                throw new IOException("Panic exception");
            }
            try {
                system.sendMessage("forecaster_1",
                        mapper.readValue(msg.toString(), ResponseSensor1.class).getMessage()
                );

                system.sendMessage("forecaster_2",
                        mapper.readValue(msg.toString(), ResponseSensor2.class).getMessage()
                );
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    };



    public static Handler printer = new Handler() {
        @Override
        public void receive(Object msg) throws IOException {
            if ( msg == null) return;
//            system.getLoad();
            log.info("--------------------------");
            log.info(msg);
            log.info("--------------------------");
        }
    };


}
