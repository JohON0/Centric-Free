package pa.centric.util.world;

import net.minecraft.network.play.server.SHeldItemChangePacket;
import pa.centric.events.Event;
import pa.centric.events.impl.packet.EventPacket;
import pa.centric.util.IMinecraft;

public class HandUtilka {
    public static boolean isEnabled;
    private boolean isChangingItem;
    private int originalSlot = -1;

    public HandUtilka() {
    }

    public void onPacketEvent(Event event) {
        if (event instanceof EventPacket ev) {
            if (!ev.isReceivePacket()) {
                return;
            }

            if (ev.getPacket() instanceof SHeldItemChangePacket) {
                this.isChangingItem = true;
            }
        }

    }

    public void handleItemChange(boolean resetItem) {
        if (this.isChangingItem && this.originalSlot != -1) {
            isEnabled = true;
            IMinecraft.mc.player.inventory.currentItem = this.originalSlot;
            if (resetItem) {
                this.isChangingItem = false;
                this.originalSlot = -1;
                isEnabled = false;
            }
        }

    }

    public void setOriginalSlot(int slot) {
        this.originalSlot = slot;
    }
}
