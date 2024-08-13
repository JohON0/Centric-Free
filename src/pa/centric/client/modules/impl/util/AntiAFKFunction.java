package pa.centric.client.modules.impl.util;

import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.util.misc.TimerUtil;
import pa.centric.util.movement.MoveUtil;
import pa.centric.events.Event;
import pa.centric.events.impl.player.EventUpdate;


@ModuleAnnotation(name = "AntiAFK", category = Type.Util)
public class AntiAFKFunction extends Function {

    private final TimerUtil timerUtil = new TimerUtil();

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventUpdate) {

            if (!MoveUtil.isMoving()) {
                if (timerUtil.hasTimeElapsed(15000)) {
                    mc.player.sendChatMessage("/BEFF LOX");
                    timerUtil.reset();
                }
            } else {
                timerUtil.reset();
            }
        }
    }
}
