package pa.centric.client.modules.impl.util;

import net.minecraft.item.AirItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.time.StopWatch;
import pa.centric.client.helper.conduction;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.modules.settings.imp.*;
import pa.centric.events.Event;
import pa.centric.events.impl.game.EventKey;
import pa.centric.events.impl.packet.EventPacket;
import pa.centric.events.impl.player.EventUpdate;
import pa.centric.util.world.HandUtilka;
import pa.centric.util.world.InventoryUtilka;

@SuppressWarnings("all")
@ModuleAnnotation(name = "FTAssistent", category = Type.Util)
public class FTAssistent extends Function {
        private final MultiBoxSetting mode = new MultiBoxSetting("���", new BooleanOption[]{new BooleanOption("������������� �� �����", true)});
        private final BooleanOption eventdelay = new BooleanOption("���������", true);
        private final BindSetting disorientationKey = (new BindSetting("���� ���������", 0)).setVisible(() -> {
            return this.mode.get("������������� �� �����");
        });
        private final BindSetting shulkerKey = (new BindSetting("���� �������", 0)).setVisible(() -> {
            return this.mode.get("������������� �� �����");
        });
        private final BindSetting trapKey = (new BindSetting("���� ������", 0)).setVisible(() -> {
            return this.mode.get("������������� �� �����");
        });
        private final BindSetting flameKey = (new BindSetting("���� ������", 0)).setVisible(() -> {
            return this.mode.get("������������� �� �����");
        });
        private final BindSetting blatantKey = (new BindSetting("���� ����", 0)).setVisible(() -> {
            return this.mode.get("������������� �� �����");
        });
        private final BindSetting bowKey = (new BindSetting("���� ��������", 0)).setVisible(() -> {
            return this.mode.get("������������� �� �����");
        });
        private final BindSetting otrigaKey = (new BindSetting("���� ������", 0)).setVisible(() -> {
            return this.mode.get("������������� �� �����");
        });
        private final BindSetting serkaKey = (new BindSetting("���� �����", 0)).setVisible(() -> {
            return this.mode.get("������������� �� �����");
        });
        private final BindSetting plastKey = (new BindSetting("���� ������", 0)).setVisible(() -> {
            return this.mode.get("������������� �� �����");
        });
        final StopWatch stopWatch = new StopWatch();
        HandUtilka handUtil = new HandUtilka();
        long delay;
        boolean disorientationThrow;
        boolean trapThrow;
        boolean flameThrow;
        boolean blatantThrow;
        boolean serkaThrow;
        boolean otrigaThrow;
        boolean bowThrow;
        boolean plastThrow;
        boolean shulkerThrow;

        public FTAssistent() {
            this.addSettings(this.mode, this.disorientationKey, this.trapKey, this.flameKey, this.blatantKey, this.serkaKey, this.bowKey, this.otrigaKey, this.plastKey, this.shulkerKey);
        }

        public void onEvent(Event event) {
            if (event instanceof EventKey e) {
                if (e.key == this.disorientationKey.getKey()) {
                    this.disorientationThrow = true;
                }

                if (e.key == this.shulkerKey.getKey()) {
                    this.shulkerThrow = true;
                }

                if (e.key == this.trapKey.getKey()) {
                    this.trapThrow = true;
                }

                if (e.key == this.flameKey.getKey()) {
                    this.flameThrow = true;
                }

                if (e.key == this.blatantKey.getKey()) {
                    this.blatantThrow = true;
                }

                if (e.key == this.otrigaKey.getKey()) {
                    this.otrigaThrow = true;
                }

                if (e.key == this.serkaKey.getKey()) {
                    this.serkaThrow = true;
                }

                if (e.key == this.bowKey.getKey()) {
                    this.bowThrow = true;
                }

                if (e.key == this.plastKey.getKey()) {
                    this.plastThrow = true;
                }
            }

            if (event instanceof EventUpdate e) {
                int hbSlot;
                int invSlot;
                int old;
                if (this.disorientationThrow) {
                    this.handUtil.handleItemChange(System.currentTimeMillis() - this.delay > 200L);
                    hbSlot = this.getItemForName("�������������", true);
                    invSlot = this.getItemForName("�������������", false);
                    if (invSlot == -1 && hbSlot == -1) {
                        this.print("������������� �� �������!");
                        this.disorientationThrow = false;
                        return;
                    }

                    if (!mc.player.getCooldownTracker().hasCooldown(Items.ENDER_EYE)) {
                        this.print("������ �������������!");
                        old = this.findAndTrowItem(hbSlot, invSlot);
                        if (old > 8) {
                            mc.playerController.pickItem(old);
                        }
                    }

                    this.disorientationThrow = false;
                }

                int slot;
                if (this.shulkerThrow) {
                    hbSlot = this.getItemForName("����", true);
                    invSlot = this.getItemForName("����", false);
                    if (invSlot == -1 && hbSlot == -1) {
                        this.print("������ �� ������");
                        this.trapThrow = false;
                        return;
                    }

                    if (!mc.player.getCooldownTracker().hasCooldown(Items.SHULKER_BOX)) {
                        this.print("������ ������!");
                        old = mc.player.inventory.currentItem;
                        slot = this.findAndTrowItem(hbSlot, invSlot);
                        if (slot > 8) {
                            mc.playerController.pickItem(slot);
                        }

                        if (InventoryUtilka.findEmptySlot(true) != -1 && mc.player.inventory.currentItem != old) {
                            mc.player.inventory.currentItem = old;
                        }
                    }

                    this.shulkerThrow = false;
                }

                if (this.trapThrow) {
                    hbSlot = this.getItemForName("������", true);
                    invSlot = this.getItemForName("������", false);
                    if (invSlot == -1 && hbSlot == -1) {
                        this.print("������ �� �������");
                        this.trapThrow = false;
                        return;
                    }

                    if (!mc.player.getCooldownTracker().hasCooldown(Items.NETHERITE_SCRAP)) {
                        this.print("������ ������!");
                        old = mc.player.inventory.currentItem;
                        slot = this.findAndTrowItem(hbSlot, invSlot);
                        if (slot > 8) {
                            mc.playerController.pickItem(slot);
                        }

                        if (InventoryUtilka.findEmptySlot(true) != -1 && mc.player.inventory.currentItem != old) {
                            mc.player.inventory.currentItem = old;
                        }
                    }

                    this.trapThrow = false;
                }

                if (this.flameThrow) {
                    hbSlot = this.getItemForName("��������", true);
                    invSlot = this.getItemForName("��������", false);
                    if (invSlot == -1 && hbSlot == -1) {
                        this.print("�������� ����� �� ������");
                        this.flameThrow = false;
                        return;
                    }

                    if (!mc.player.getCooldownTracker().hasCooldown(Items.FIRE_CHARGE)) {
                        this.print("������ �������� �����!");
                        old = mc.player.inventory.currentItem;
                        slot = this.findAndTrowItem(hbSlot, invSlot);
                        if (slot > 8) {
                            mc.playerController.pickItem(slot);
                        }

                        if (InventoryUtilka.findEmptySlot(true) != -1 && mc.player.inventory.currentItem != old) {
                            mc.player.inventory.currentItem = old;
                        }
                    }

                    this.flameThrow = false;
                }

                if (this.bowThrow) {
                    hbSlot = this.getItemForName("�������", true);
                    invSlot = this.getItemForName("�������", false);
                    if (invSlot == -1 && hbSlot == -1) {
                        this.print("������� �� ������");
                        this.bowThrow = false;
                        return;
                    }

                    if (!mc.player.getCooldownTracker().hasCooldown(Items.CROSSBOW)) {
                        this.print("������ �������!");
                        old = mc.player.inventory.currentItem;
                        slot = this.findAndTrowItem(hbSlot, invSlot);
                        if (slot > 8) {
                            mc.playerController.pickItem(slot);
                        }

                        if (InventoryUtilka.findEmptySlot(true) != -1 && mc.player.inventory.currentItem != old) {
                            mc.player.inventory.currentItem = old;
                        }
                    }

                    this.bowThrow = false;
                }

                if (this.serkaThrow) {
                    hbSlot = this.getItemForName("������", true);
                    invSlot = this.getItemForName("������", false);
                    if (invSlot == -1 && hbSlot == -1) {
                        this.print("����� �� �������");
                        this.serkaThrow = false;
                        return;
                    }

                    if (!mc.player.getCooldownTracker().hasCooldown(Items.SPLASH_POTION)) {
                        this.print("������ �����!");
                        old = mc.player.inventory.currentItem;
                        slot = this.findAndTrowItem(hbSlot, invSlot);
                        if (slot > 8) {
                            mc.playerController.pickItem(slot);
                        }

                        if (InventoryUtilka.findEmptySlot(true) != -1 && mc.player.inventory.currentItem != old) {
                            mc.player.inventory.currentItem = old;
                        }
                    }

                    this.serkaThrow = false;
                }

                if (this.otrigaThrow) {
                    hbSlot = this.getItemForName("�������", true);
                    invSlot = this.getItemForName("�������", false);
                    if (invSlot == -1 && hbSlot == -1) {
                        this.print("������ �� �������");
                        this.otrigaThrow = false;
                        return;
                    }

                    if (!mc.player.getCooldownTracker().hasCooldown(Items.SPLASH_POTION)) {
                        this.print("������ ������!");
                        old = mc.player.inventory.currentItem;
                        slot = this.findAndTrowItem(hbSlot, invSlot);
                        if (slot > 8) {
                            mc.playerController.pickItem(slot);
                        }

                        if (InventoryUtilka.findEmptySlot(true) != -1 && mc.player.inventory.currentItem != old) {
                            mc.player.inventory.currentItem = old;
                        }
                    }

                    this.otrigaThrow = false;
                }

                if (this.plastThrow) {
                    hbSlot = this.getItemForName("�����", true);
                    invSlot = this.getItemForName("�����", false);
                    if (invSlot == -1 && hbSlot == -1) {
                        this.print("����� �� ������");
                        this.plastThrow = false;
                        return;
                    }

                    if (!mc.player.getCooldownTracker().hasCooldown(Items.DRIED_KELP)) {
                        this.print("������ �����!");
                        old = mc.player.inventory.currentItem;
                        slot = this.findAndTrowItem(hbSlot, invSlot);
                        if (slot > 8) {
                            mc.playerController.pickItem(slot);
                        }

                        if (InventoryUtilka.findEmptySlot(true) != -1 && mc.player.inventory.currentItem != old) {
                            mc.player.inventory.currentItem = old;
                        }
                    }

                    this.plastThrow = false;
                }

                if (this.blatantThrow) {
                    hbSlot = this.getItemForName("�����", true);
                    invSlot = this.getItemForName("�����", false);
                    if (invSlot == -1 && hbSlot == -1) {
                        this.print("����� ���� �� �������");
                        this.blatantThrow = false;
                        return;
                    }

                    if (!mc.player.getCooldownTracker().hasCooldown(Items.TNT)) {
                        this.print("������ ����� ����!");
                        old = mc.player.inventory.currentItem;
                        slot = this.findAndTrowItem(hbSlot, invSlot);
                        if (slot > 8) {
                            mc.playerController.pickItem(slot);
                        }

                        if (InventoryUtilka.findEmptySlot(true) != -1 && mc.player.inventory.currentItem != old) {
                            mc.player.inventory.currentItem = old;
                        }
                    }

                    this.blatantThrow = false;
                }

                this.handUtil.handleItemChange(System.currentTimeMillis() - this.delay > 200L);
            }

            if (event instanceof EventPacket) {
                this.handUtil.onPacketEvent((EventPacket)event);
            }

        }

        private void print(String s) {
            conduction.NOTIFICATION_MANAGER.add(s, "messages", 2);
        }

        private int findAndTrowItem(int hbSlot, int invSlot) {
            if (hbSlot != -1) {
                this.handUtil.setOriginalSlot(mc.player.inventory.currentItem);
                mc.player.connection.sendPacket(new CHeldItemChangePacket(hbSlot));
                mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                mc.player.swingArm(Hand.MAIN_HAND);
                this.delay = System.currentTimeMillis();
                return hbSlot;
            } else if (invSlot != -1) {
                this.handUtil.setOriginalSlot(mc.player.inventory.currentItem);
                mc.playerController.pickItem(invSlot);
                mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                mc.player.swingArm(Hand.MAIN_HAND);
                this.delay = System.currentTimeMillis();
                return invSlot;
            } else {
                return -1;
            }
        }

        public void onDisable() {
            this.disorientationThrow = false;
            this.trapThrow = false;
            this.flameThrow = false;
            this.blatantThrow = false;
            this.plastThrow = false;
            this.otrigaThrow = false;
            this.serkaThrow = false;
            this.bowThrow = false;
            this.delay = 0L;
            super.onDisable();
        }

        private int getItemForName(String name, boolean inHotBar) {
            int firstSlot = inHotBar ? 0 : 9;
            int lastSlot = inHotBar ? 9 : 36;

            for(int i = firstSlot; i < lastSlot; ++i) {
                ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
                if (!(itemStack.getItem() instanceof AirItem)) {
                    String displayName = TextFormatting.getTextWithoutFormattingCodes(itemStack.getDisplayName().getString());
                    if (displayName != null && displayName.toLowerCase().contains(name)) {
                        return i;
                    }
                }
            }
            return -1;
        }
    }
