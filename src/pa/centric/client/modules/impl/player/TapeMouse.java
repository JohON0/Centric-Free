package pa.centric.client.modules.impl.player;

import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.events.Event;
import pa.centric.events.impl.player.EventUpdate;

@ModuleAnnotation(name = "TapeMouse", category = Type.Player)
public class  TapeMouse extends Function {


    @Override
    public void onEvent(Event event) {
        if (event instanceof EventUpdate e) {
            if (mc.player.getCooledAttackStrength(1f) >= 1) {
                mc.clickMouse();
            }
        }
    }
}
