package other.org.luaj.vm2.customs.events;

import other.org.luaj.vm2.customs.EventHook;
import pa.centric.events.Event;
import pa.centric.events.impl.player.EventUpdate;

public class EventUpdateHook extends EventHook {

    private EventUpdate update;

    public EventUpdateHook(Event event) {
        super(event);
    }

    @Override
    public String getName() {
        return "update_event";
    }
}
