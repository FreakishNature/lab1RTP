package com.proj;

import com.proj.actors.ActorSystem;
import com.proj.handlers.Forecaster;


import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.sse.SseEventSource;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import static com.proj.handlers.Handlers.*;

public class Application {
    private static ActorSystem actorSystem = new ActorSystem();
    private static String url;

    public static void main(String[] args) throws InterruptedException {
        int forecastInterval1 = 5;
        int forecastInterval2 = 5;

        if(args.length < 3){
            url = "http://localhost:4000/iot";
        } else {
            url = "http://" + args[2] + ":4000/iot";
            forecastInterval1 = Integer.parseInt(args[0]);
            forecastInterval2 = Integer.parseInt(args[1]);
        }


        actorSystem.createActorGroup("forecaster_1",
                new Forecaster(forecastInterval1)
        );

        actorSystem.createActorGroup("forecaster_2",
                new Forecaster(forecastInterval2)
        );

        actorSystem.createActorGroup("dataReceiver", dataReceiver);
        actorSystem.createActorGroup("processor", processor);
        actorSystem.createActorGroup("printer",printer);
        actorSystem.start();

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(url);
        try (SseEventSource source = SseEventSource
                .target(target)
                .reconnectingEvery(5, TimeUnit.SECONDS)
                .build()) {
            source.register(inboundSseEvent -> {
                        String data = inboundSseEvent.readData();
                        actorSystem.sendMessage("dataReceiver", data);
                    }
            );
            source.open();
            Thread.sleep(60_000);
        }

        actorSystem.shutDown();
    }
}
