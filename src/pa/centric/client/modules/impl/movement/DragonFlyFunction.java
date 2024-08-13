package pa.centric.client.modules.impl.movement;

import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.modules.settings.imp.SliderSetting;
import pa.centric.util.movement.MoveUtil;
import pa.centric.events.Event;
import pa.centric.events.impl.player.EventMove;


@ModuleAnnotation(name = "DragonFly", category = Type.Movement)
public class DragonFlyFunction extends Function {
    private final SliderSetting dragonFlySpeed = new SliderSetting("Скорость флая", 1.6f, 1.0f, 10.0F, 0.01f);
    private final SliderSetting dragonFlyMotionY = new SliderSetting("Скорость флая по Y", 0.6f, 0.1f, 5, 0.01f);

    public DragonFlyFunction() {
        addSettings(dragonFlySpeed,dragonFlyMotionY);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventMove move) {
            handleDragonFly(move);
        }
    }

    private void handleDragonFly(EventMove move) {
        if (mc.player.abilities.isFlying) {

            if (!mc.player.isSneaking() && mc.gameSettings.keyBindJump.isKeyDown()) {
                move.motion().y = dragonFlyMotionY.getValue().floatValue();
            }
            if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                move.motion().y = -dragonFlyMotionY.getValue().floatValue();
            }

            MoveUtil.MoveEvent.setMoveMotion(move, dragonFlySpeed.getValue().floatValue());
        }
    }
}
