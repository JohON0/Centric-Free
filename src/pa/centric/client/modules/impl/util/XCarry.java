package pa.centric.client.modules.impl.util;

import net.minecraft.network.play.client.CCloseWindowPacket;
import pa.centric.client.modules.Function;
import pa.centric.events.Event;
import pa.centric.events.impl.packet.EventPacket;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;

@ModuleAnnotation(name = "XCarry", category = Type.Util)
public class XCarry extends Function {


    @Override
    public void onEvent(Event event) {
        if (event instanceof EventPacket) {
            if (((EventPacket) event).getPacket() instanceof CCloseWindowPacket) {
                event.setCancel(true);
            }
        }
    }
}
