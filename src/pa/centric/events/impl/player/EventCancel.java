package pa.centric.events.impl.player;

import pa.centric.events.Event;

public class EventCancel extends Event {
    private boolean isCancel;

    public void cancel() {
        isCancel = true;
    }
    public void open() {
        isCancel = false;
    }
    public boolean isCancel() {
        return isCancel;
    }

}
