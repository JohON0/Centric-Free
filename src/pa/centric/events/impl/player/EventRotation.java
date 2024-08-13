package pa.centric.events.impl.player;

import pa.centric.events.Event;

public class EventRotation extends Event {

    public float yaw,pitch;

    public EventRotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

}
