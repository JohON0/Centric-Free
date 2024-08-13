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
        private final MultiBoxSetting mode = new MultiBoxSetting("Тип", new BooleanOption[]{new BooleanOption("Использование по бинду", true)});
        private final BooleanOption eventdelay = new BooleanOption("АвтоЭвент", true);
        private final BindSetting disorientationKey = (new BindSetting("Бинд дезорента", 0)).setVisible(() -> {
            return this.mode.get("Использование по бинду");
        });
        private final BindSetting shulkerKey = (new BindSetting("Бинд шалкера", 0)).setVisible(() -> {
            return this.mode.get("Использование по бинду");
        });
        private final BindSetting trapKey = (new BindSetting("Бинд трапки", 0)).setVisible(() -> {
            return this.mode.get("Использование по бинду");
        });
        private final BindSetting flameKey = (new BindSetting("Бинд смерча", 0)).setVisible(() -> {
            return this.mode.get("Использование по бинду");
        });
        private final BindSetting blatantKey = (new BindSetting("Бинд явки", 0)).setVisible(() -> {
            return this.mode.get("Использование по бинду");
        });
        private final BindSetting bowKey = (new BindSetting("Бинд арбалета", 0)).setVisible(() -> {
            return this.mode.get("Использование по бинду");
        });
        private final BindSetting otrigaKey = (new BindSetting("Бинд отрыги", 0)).setVisible(() -> {
            return this.mode.get("Использование по бинду");
        });
        private final BindSetting serkaKey = (new BindSetting("Бинд серки", 0)).setVisible(() -> {
            return this.mode.get("Использование по бинду");
        });
        private final BindSetting plastKey = (new BindSetting("Бинд пласта", 0)).setVisible(() -> {
            return this.mode.get("Использование по бинду");
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
                    hbSlot = this.getItemForName("дезориентация", true);
                    invSlot = this.getItemForName("дезориентация", false);
                    if (invSlot == -1 && hbSlot == -1) {
                        this.print("Дезориентация не найдена!");
                        this.disorientationThrow = false;
                        return;
                    }

                    if (!mc.player.getCooldownTracker().hasCooldown(Items.ENDER_EYE)) {
                        this.print("Заюзал дезориентацию!");
                        old = this.findAndTrowItem(hbSlot, invSlot);
                        if (old > 8) {
                            mc.playerController.pickItem(old);
                        }
                    }

                    this.disorientationThrow = false;
                }

                int slot;
                if (this.shulkerThrow) {
                    hbSlot = this.getItemForName("ящик", true);
                    invSlot = this.getItemForName("ящик", false);
                    if (invSlot == -1 && hbSlot == -1) {
                        this.print("Шалкер не найден");
                        this.trapThrow = false;
                        return;
                    }

                    if (!mc.player.getCooldownTracker().hasCooldown(Items.SHULKER_BOX)) {
                        this.print("Заюзал шалкер!");
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
                    hbSlot = this.getItemForName("трапка", true);
                    invSlot = this.getItemForName("трапка", false);
                    if (invSlot == -1 && hbSlot == -1) {
                        this.print("Трапка не найдена");
                        this.trapThrow = false;
                        return;
                    }

                    if (!mc.player.getCooldownTracker().hasCooldown(Items.NETHERITE_SCRAP)) {
                        this.print("Заюзал трапку!");
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
                    hbSlot = this.getItemForName("огненный", true);
                    invSlot = this.getItemForName("огненный", false);
                    if (invSlot == -1 && hbSlot == -1) {
                        this.print("Огненный смерч не найден");
                        this.flameThrow = false;
                        return;
                    }

                    if (!mc.player.getCooldownTracker().hasCooldown(Items.FIRE_CHARGE)) {
                        this.print("Заюзал огненный смерч!");
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
                    hbSlot = this.getItemForName("арбалет", true);
                    invSlot = this.getItemForName("арбалет", false);
                    if (invSlot == -1 && hbSlot == -1) {
                        this.print("Арбалет не найден");
                        this.bowThrow = false;
                        return;
                    }

                    if (!mc.player.getCooldownTracker().hasCooldown(Items.CROSSBOW)) {
                        this.print("Заюзал арбалет!");
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
                    hbSlot = this.getItemForName("серная", true);
                    invSlot = this.getItemForName("серная", false);
                    if (invSlot == -1 && hbSlot == -1) {
                        this.print("Серка не найдена");
                        this.serkaThrow = false;
                        return;
                    }

                    if (!mc.player.getCooldownTracker().hasCooldown(Items.SPLASH_POTION)) {
                        this.print("Заюзал серку!");
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
                    hbSlot = this.getItemForName("отрыжки", true);
                    invSlot = this.getItemForName("отрыжки", false);
                    if (invSlot == -1 && hbSlot == -1) {
                        this.print("Отрыга не найдена");
                        this.otrigaThrow = false;
                        return;
                    }

                    if (!mc.player.getCooldownTracker().hasCooldown(Items.SPLASH_POTION)) {
                        this.print("Заюзал отрыгу!");
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
                    hbSlot = this.getItemForName("пласт", true);
                    invSlot = this.getItemForName("пласт", false);
                    if (invSlot == -1 && hbSlot == -1) {
                        this.print("Пласт не найден");
                        this.plastThrow = false;
                        return;
                    }

                    if (!mc.player.getCooldownTracker().hasCooldown(Items.DRIED_KELP)) {
                        this.print("Заюзал пласт!");
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
                    hbSlot = this.getItemForName("явная", true);
                    invSlot = this.getItemForName("явная", false);
                    if (invSlot == -1 && hbSlot == -1) {
                        this.print("Явная пыль не найдена");
                        this.blatantThrow = false;
                        return;
                    }

                    if (!mc.player.getCooldownTracker().hasCooldown(Items.TNT)) {
                        this.print("Заюзал явную пыль!");
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
