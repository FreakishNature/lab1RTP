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
