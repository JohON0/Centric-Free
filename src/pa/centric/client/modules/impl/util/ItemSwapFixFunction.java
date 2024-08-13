package pa.centric.client.modules.impl.util;

import com.google.common.eventbus.Subscribe;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.server.SHeldItemChangePacket;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.events.Event;
import pa.centric.events.impl.packet.EventPacket;

@ModuleAnnotation(name = "ItemSwapFix", category = Type.Util)
public class ItemSwapFixFunction extends Function {
    @Subscribe
    public void onEvent(Event e) {
        if (e instanceof EventPacket eventPacket) {
            SHeldItemChangePacket wrapper2;
            int serverSlot;
            if (ItemSwapFixFunction.mc.player == null) {
                return;
            }
            IPacket<?> iPacket = eventPacket.getPacket();
            if (iPacket instanceof SHeldItemChangePacket && (serverSlot = (wrapper2 = (SHeldItemChangePacket) iPacket).getHeldItemHotbarIndex()) != ItemSwapFixFunction.mc.player.inventory.currentItem) {
                ItemSwapFixFunction.mc.player.connection.sendPacket(new CHeldItemChangePacket(Math.max(ItemSwapFixFunction.mc.player.inventory.currentItem - 1, 0)));
                ItemSwapFixFunction.mc.player.connection.sendPacket(new CHeldItemChangePacket(ItemSwapFixFunction.mc.player.inventory.currentItem));
                e.setCancel(true);
            }
        }
    }
}
