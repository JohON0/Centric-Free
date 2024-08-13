package pa.centric.client.modules.impl.movement;

import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.modules.settings.imp.BooleanOption;
import pa.centric.util.movement.MoveUtil;
import pa.centric.events.Event;
import pa.centric.events.impl.player.EventUpdate;


@ModuleAnnotation(name = "Sprint", category = Type.Movement)
public class SprintFunction extends Function {

    public BooleanOption keepSprint = new BooleanOption("Keep Sprint", true);

    public SprintFunction() {
        addSettings(keepSprint);
    }

    @Override
    public void onEvent(final Event event) {
        if (event instanceof EventUpdate) {
            if (!mc.player.isSneaking() && !mc.player.collidedHorizontally)
                mc.player.setSprinting(MoveUtil.isMoving());
        }

    }
}
