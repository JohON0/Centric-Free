package pa.centric.client.modules.impl.util;

import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.settings.imp.ModeSetting;
import pa.centric.events.Event;
import pa.centric.events.impl.packet.EventPacket;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;

@ModuleAnnotation(name = "No Server Rot", category = Type.Util)
public class NoServerRotFunction extends Function {
    private ModeSetting serverRotMode = new ModeSetting("Тип", "Обычный", "Обычный", "RW");

    public NoServerRotFunction() {
        addSettings(serverRotMode);
    }

    @Override
    public void onEvent(final Event event) {
        if (!serverRotMode.is("RW")) {
            if (event instanceof EventPacket packet) {
                if (packet.isReceivePacket()) {
                    if (packet.getPacket() instanceof SPlayerPositionLookPacket packet1) {
                        packet1.yaw = mc.player.rotationYaw;
                        packet1.pitch = mc.player.rotationPitch;
                    }
                }
            }
        }
    }
}
