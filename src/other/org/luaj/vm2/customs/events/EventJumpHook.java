package other.org.luaj.vm2.customs.events;

import other.org.luaj.vm2.customs.EventHook;
import pa.centric.events.Event;
import pa.centric.events.impl.player.EventJump;

public class EventJumpHook extends EventHook {

    private EventJump jump;

    public EventJumpHook(Event event) {
        super(event);
        this.jump = (EventJump) event;
    }




    @Override
    public String getName() {
        return "jump_event";
    }
}
