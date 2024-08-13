package pa.centric.client.modules.impl.player;

import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.modules.settings.imp.BooleanOption;
import pa.centric.client.modules.settings.imp.MultiBoxSetting;
import pa.centric.events.Event;
import pa.centric.events.impl.player.EventUpdate;

@ModuleAnnotation(name = "NoDelay", category = Type.Player)
public class NoDelay extends Function {

    private final MultiBoxSetting actions = new MultiBoxSetting("Действия",
            new BooleanOption("Прыжок", true),
            new BooleanOption("Ставить", false)
    );

    public NoDelay() {
        addSettings(actions);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventUpdate) {
            if (actions.get(0)) mc.player.jumpTicks = 0;
            if (actions.get(1)) mc.rightClickDelayTimer = 0;
        }
    }
}
