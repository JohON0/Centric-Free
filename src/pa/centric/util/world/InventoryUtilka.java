package pa.centric.util.world;

import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.network.play.server.SHeldItemChangePacket;
import pa.centric.events.Event;
import pa.centric.events.impl.packet.EventPacket;
import pa.centric.util.IMinecraft;

public class InventoryUtilka implements IMinecraft {
    private static InventoryUtilka instance = new InventoryUtilka();

    public static int findEmptySlot(boolean inHotBar) {
        int start = inHotBar ? 0 : 9;
        int end = inHotBar ? 9 : 45;

        for (int i = start; i < end; ++i) {
            if (mc.player.inventory.getStackInSlot(i).isEmpty()) {
                return i;
            }
        }

        return -1;
    }

    public static void moveItem(int from, int to, boolean air) {
        if (from != to) {
            pickupItem(from, 0);
            pickupItem(to, 0);
            if (air) {
                pickupItem(from, 0);
            }

        }
    }

    public static void moveItemTime(int from, int to, boolean air, int time) {
        if (from != to) {
            pickupItem(from, 0, time);
            pickupItem(to, 0, time);
            if (air) {
                pickupItem(from, 0, time);
            }

        }
    }

    public static void moveItem(int from, int to) {
        if (from != to) {
            pickupItem(from, 0);
            pickupItem(to, 0);
            pickupItem(from, 0);
        }
    }

    public static void pickupItem(int slot, int button) {
        mc.playerController.windowClick(0, slot, button, ClickType.PICKUP, mc.player);
    }

    public static void pickupItem(int slot, int button, int time) {
        pickupItem(slot, button);
    }

    public int getAxeInInventory(boolean inHotBar) {
        int firstSlot = inHotBar ? 0 : 9;
        int lastSlot = inHotBar ? 9 : 36;

        for (int i = firstSlot; i < lastSlot; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() instanceof AxeItem) {
                return i;
            }
        }

        return -1;
    }

    public int findBestSlotInHotBar() {
        int emptySlot = this.findEmptySlot();
        return emptySlot != -1 ? emptySlot : this.findNonSwordSlot();
    }

    private int findEmptySlot() {
        for (int i = 0; i < 9; ++i) {
            if (mc.player.inventory.getStackInSlot(i).isEmpty() && mc.player.inventory.currentItem != i) {
                return i;
            }
        }

        return -1;
    }

    private int findNonSwordSlot() {
        for (int i = 0; i < 9; ++i) {
            if (!(mc.player.inventory.getStackInSlot(i).getItem() instanceof SwordItem) && !(mc.player.inventory.getStackInSlot(i).getItem() instanceof ElytraItem) && mc.player.inventory.currentItem != i) {
                return i;
            }
        }

        return -1;
    }

    public int getSlotInInventory(Item item) {
        int finalSlot = -1;

        for (int i = 0; i < 36; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
                finalSlot = i;
            }
        }

        return finalSlot;
    }

    public int getSlotInInventoryOrHotbar(Item item, boolean inHotBar) {
        int firstSlot = inHotBar ? 0 : 9;
        int lastSlot = inHotBar ? 9 : 36;
        int finalSlot = -1;

        for (int i = firstSlot; i < lastSlot; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
                finalSlot = i;
            }
        }

        return finalSlot;
    }

    public static int getSlotInInventoryOrHotbar() {
        int firstSlot = 0;
        int lastSlot = 9;
        int finalSlot = -1;

        for (int i = firstSlot; i < lastSlot; ++i) {
            if (Block.getBlockFromItem(mc.player.inventory.getStackInSlot(i).getItem()) instanceof SlabBlock) {
                finalSlot = i;
            }
        }

        return finalSlot;
    }

    public static InventoryUtilka getInstance() {
        return instance;
    }

    class Hand {
        public static boolean isEnabled;
        private boolean isChangingItem;
        private int originalSlot = -1;

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
}
