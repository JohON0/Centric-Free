package pa.centric.events.impl.player;

import pa.centric.events.Event;

public class EventStrafe extends Event {

    public float yaw;

    public EventStrafe(float yaw) {
        this.yaw = yaw;
    }

}
