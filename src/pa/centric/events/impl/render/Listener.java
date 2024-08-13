package pa.centric.events.impl.render;

public interface Listener<T> {
    void handle(T event);

}
