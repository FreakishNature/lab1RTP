package actors;

public abstract class Handler {
    public ActorSystem system;
    abstract public void receive(Object msg) throws Exception;
}
