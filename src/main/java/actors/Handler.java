package actors;

import java.io.IOException;

public abstract class Handler {
    public ActorSystem system;
    abstract public void receive(Object msg) throws IOException;
}
