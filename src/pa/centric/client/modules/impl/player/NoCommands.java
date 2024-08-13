package pa.centric.client.modules.impl.player;

import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.events.Event;

@ModuleAnnotation(name = "NoCommands", category = Type.Player)
public class NoCommands extends Function {
    @Override
    public void onEvent(Event event) {

    }
}
