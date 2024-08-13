package pa.centric.client.modules.impl.util;

import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Item;
import net.minecraft.network.play.server.SJoinGamePacket;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.impl.player.ChestStealer;
import pa.centric.client.modules.settings.imp.ModeSetting;
import pa.centric.client.modules.settings.imp.SliderSetting;
import pa.centric.util.ClientUtils;
import pa.centric.util.misc.TimerUtil;
import pa.centric.events.Event;
import pa.centric.events.impl.packet.EventPacket;
import pa.centric.events.impl.player.EventUpdate;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;

/**
@author JohON0
@date 13.06.2024
 */
@SuppressWarnings("all")
@ModuleAnnotation(name = "HolyJoiner", category = Type.Util)
public class HolyJoiner extends Function {
    private final ModeSetting ankaselect = new ModeSetting("Выбор анархии", "Лайт 1.16", "Лайт 1.20","Лайт 1.16", "Классик", "Спидран");
    private final SliderSetting classicanka = new SliderSetting("Номер анархии", 1, 1, 14, 1).setVisible(() -> ankaselect.is("Классик"));
    private final SliderSetting lite120 = new SliderSetting("Номер анархии", 1, 1, 3, 1).setVisible(() -> ankaselect.is("Лайт 1.20"));
    private final SliderSetting lite116 = new SliderSetting("Номер анархии", 1, 1, 30, 1).setVisible(() -> ankaselect.is("Лайт 1.16"));
    private final SliderSetting speedrun = new SliderSetting("Номер анархии", 1, 1, 3, 1).setVisible(() -> ankaselect.is("Спидран"));

    private final TimerUtil timerUtil = new TimerUtil();

    public HolyJoiner() {
        addSettings(ankaselect,classicanka,lite120,lite116,speedrun);
    }

    @Override
    protected void onEnable() {
        if (ankaselect.is("Лайт 1.16")) {
            mc.player.sendChatMessage("/lite");
        }
        if (ankaselect.is("Лайт 1.20")) {
            mc.player.sendChatMessage("/lite120");
        }
        if (ankaselect.is("Классик")) {
            mc.player.sendChatMessage("/anarchy");
        }
        if (ankaselect.is("Спидран")) {
            mc.player.sendChatMessage("/speedrun");
        }

        super.onEnable();
    }

    @Override
    public void onEvent(Event event) {
        if (ClientUtils.isConnectedToServer("mc.holyworld.ru")) {
        } else {
            ClientUtils.sendMessage("Данный модуль работает только на сервере HolyWorld");
            toggle();
        }
        if (event instanceof EventUpdate) {
            handleEventUpdate();
        }
        if (event instanceof EventPacket eventPacket) {
            if (eventPacket.getPacket() instanceof SJoinGamePacket) {
                try {
                    if (mc.ingameGUI.getTabList().header == null) {
                        return;
                    }

                    this.toggle();
                } catch (Exception ignored) {
                }
            }
        }


    }

    private void handleEventUpdate() {
        if (mc.currentScreen == null) {
        } else if (mc.currentScreen instanceof ChestScreen) {
            try {
                ContainerScreen container = (ContainerScreen) mc.currentScreen;
                for (int i = 0; i < container.getContainer().inventorySlots.size(); i++) {
                    int slot = 0;
                    int finalslot;
                    ChestContainer containerselect = (ChestContainer) ChestStealer.mc.player.openContainer;
                    if (ankaselect.is("Лайт 1.16")) {
                        finalslot = lite116.getValue().intValue();
                        if (lite116.getValue().intValue() >= 8) {
                            finalslot = lite116.getValue().intValue() + 1;
                        }
                        slot = finalslot;
                        if (containerselect.getLowerChestInventory().getStackInSlot(slot).getItem() != Item.getItemById(0) && this.timerUtil.hasTimeElapsed(0L)) {
                            ChestStealer.mc.playerController.windowClick(containerselect.windowId, slot, 0, ClickType.PICKUP, ChestStealer.mc.player);
                            toggle();
                            this.timerUtil.reset();
                        }
                    }
                    if (ankaselect.is("Лайт 1.20")) {
                        slot = lite120.getValue().intValue() + 10;
                        if (containerselect.getLowerChestInventory().getStackInSlot(slot).getItem() != Item.getItemById(0) && this.timerUtil.hasTimeElapsed(0L)) {
                            ChestStealer.mc.playerController.windowClick(containerselect.windowId, slot, 0, ClickType.PICKUP, ChestStealer.mc.player);
                            toggle();
                            this.timerUtil.reset();
                        }
                    }
                    if (ankaselect.is("Классик")) {
                        if (classicanka.getValue().intValue() >= 1 && classicanka.getValue().intValue() <= 4)
                            slot = classicanka.getValue().intValue() * 2 - 1;

                        if (classicanka.getValue().intValue() >= 5 && classicanka.getValue().intValue() <= 7)
                            slot = classicanka.getValue().intValue() * 2 + 1;

                        if (classicanka.getValue().intValue() >= 8 && classicanka.getValue().intValue() <= 11)
                            slot = classicanka.getValue().intValue() * 2 + 3;

                        if (classicanka.getValue().intValue() >= 12 && classicanka.getValue().intValue() <= 14)
                            slot = classicanka.getValue().intValue() * 2 + 5;

                        if (containerselect.getLowerChestInventory().getStackInSlot(slot).getItem() != Item.getItemById(0) && this.timerUtil.hasTimeElapsed(0L)) {
                            ChestStealer.mc.playerController.windowClick(containerselect.windowId, slot, 0, ClickType.PICKUP, ChestStealer.mc.player);
                            toggle();
                            this.timerUtil.reset();
                        }
                    }
                    if (ankaselect.is("Спидран")) {
                        if (speedrun.getValue().intValue() == 1)
                        slot = speedrun.getValue().intValue() + 10;
                        if (speedrun.getValue().intValue() == 2)
                            slot = speedrun.getValue().intValue() + 11;
                        if (speedrun.getValue().intValue() == 3)
                            slot = speedrun.getValue().intValue() + 12;
                        if (containerselect.getLowerChestInventory().getStackInSlot(slot).getItem() != Item.getItemById(0) && this.timerUtil.hasTimeElapsed(0L)) {
                            ChestStealer.mc.playerController.windowClick(containerselect.windowId, slot, 0, ClickType.PICKUP, ChestStealer.mc.player);
                            toggle();
                            this.timerUtil.reset();
                        }
                    }
                }
            }
                catch (Exception ignored) {}
        }
    }
}

