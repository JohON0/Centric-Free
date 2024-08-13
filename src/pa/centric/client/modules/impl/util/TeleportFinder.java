package pa.centric.client.modules.impl.util;

import net.minecraft.network.IPacket;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.events.Event;
import pa.centric.events.impl.packet.EventPacket;

import java.lang.reflect.Method;

@ModuleAnnotation(name = "Teleport Finder", category = Type.Util)
public class TeleportFinder extends Function {
    @Override
    public void onEvent(Event event) {

        if (event instanceof EventPacket e) {
           if (e.isReceivePacket()) {
               IPacket<?> packet = e.getPacket();

               for (Method m : packet.getClass().getMethods()) {
                   if (m.getName().toLowerCase().contains("entityid")) {
                       System.out.println(packet);
                   }
               }
           }
        }

    }
}
