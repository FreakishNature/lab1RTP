import actors.Actor;
import actors.ActorSystem;
import actors.Handler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.*;


import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.sse.SseEventSource;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;
import static handlers.Handlers.*;

public class Application {
    private static ActorSystem actorSystem = new ActorSystem();
    private static String url = "http://localhost:4000/iot";

    public static void main(String[] args) throws InterruptedException {
        actorSystem.createActor("dataReceiver", dataReceiver);
        actorSystem.createActor("processor", processor);
        actorSystem.createActor("forecaster_1",forecaster);
        actorSystem.createActor("forecaster_2",forecaster);
        actorSystem.createActor("printer",printer);
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
            Thread.sleep(3000);
        }





//        actorSystem.getActor("dataReceiver");
        actorSystem.shutDown();

//        String data = inboundSseEvent.readData();
    }
}
