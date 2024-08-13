package pa.centric.events.impl.game;

import pa.centric.events.Event;

public class EventKey extends Event {

    public int key;

    public EventKey(int key) {
        this.key = key;
    }
}
