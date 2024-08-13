package pa.centric.client.modules.impl.player;

import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.util.Hand;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.util.misc.TimerUtil;
import pa.centric.events.Event;
import pa.centric.events.impl.packet.EventPacket;
import pa.centric.events.impl.player.EventUpdate;

@ModuleAnnotation(name = "AutoFish", category = Type.Player)
public class AutoFish extends Function {

    private final TimerUtil delay = new TimerUtil();
    private boolean isHooked = false;
    private boolean needToHook = false;

    @Override
    public void onEvent(final Event event) {
        if (mc.player == null || mc.world == null) return;

        if (event instanceof EventPacket e) {
            if (e.getPacket() instanceof SPlaySoundEffectPacket p) {
                if (p.getSound().getName().getPath().equals("entity.fishing_bobber.splash")) {
                    isHooked = true;
                    delay.reset();
                    //ClientUtils.sendMesage("–€¡¿!");
                }
            }
        }

        if (event instanceof EventUpdate e) {
            if (delay.hasTimeElapsed(600) && isHooked) {
                mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                isHooked = false;
                needToHook = true;
                //ClientUtils.sendMesage("«¿¡–¿À!");
                delay.reset();
            }

            if (delay.hasTimeElapsed(300) && needToHook) {
                mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                needToHook = false;
                //ClientUtils.sendMesage("’”…Õ”À!");
                delay.reset();
            }
        }
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        delay.reset();
        isHooked = false;
        needToHook = false;
    }
}
