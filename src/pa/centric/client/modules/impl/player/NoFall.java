package pa.centric.client.modules.impl.player;

import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.events.Event;
import pa.centric.events.impl.player.EventMotion;

@ModuleAnnotation(name = "NoFall", category = Type.Player)
public class NoFall extends Function {


    @Override
    public void onEvent(Event event) {
        if (event instanceof EventMotion e) {
            if (mc.player.ticksExisted % 3 == 0 && mc.player.fallDistance > 3) {
                e.setY(e.getY() + 0.2f);
            }
        }
    }
}
