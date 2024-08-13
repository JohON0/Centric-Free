package pa.centric.client.modules.impl.movement;

import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.modules.settings.imp.SliderSetting;
import pa.centric.events.Event;
import pa.centric.events.impl.player.EventTravel;
import pa.centric.util.IMinecraft;
import pa.centric.util.movement.MoveUtil;


@ModuleAnnotation(name = "WaterSpeed", category = Type.Movement)
public class WaterSpeed extends Function {


    public SliderSetting speed = new SliderSetting("Скорость", 0.41f, 0.1f, 0.5f, 0.01f);
    public SliderSetting motionY = new SliderSetting("Скорость по Y", 0.0f, 0.0f, 0.1f, 0.01f);

    public WaterSpeed() {
        addSettings(speed, motionY);
    }

    private float currentValue;

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventTravel move) {
            if (IMinecraft.mc.player.collidedVertically || IMinecraft.mc.player.collidedHorizontally) {
                return;
            }
            if (IMinecraft.mc.player.isSwimming()) {
                float speed = this.speed.getValue().floatValue() / 10F;
                if (IMinecraft.mc.gameSettings.keyBindJump.pressed) {
                    IMinecraft.mc.player.motion.y += motionY.getValue().floatValue();
                }
                if (IMinecraft.mc.gameSettings.keyBindSneak.pressed) {
                    IMinecraft.mc.player.motion.y -= motionY.getValue().floatValue();
                }

                MoveUtil.setMotion(MoveUtil.getMotion());
                move.speed += speed;
            }
        }
    }

    public float calculateNewValue(float value, float increment) {
        return value * Math.min((currentValue += increment) / 100.0f, 1.0f);
    }

}
