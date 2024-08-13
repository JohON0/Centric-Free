package pa.centric.client.modules.impl.player;

import java.util.Arrays;
import java.util.Random;
import pa.centric.events.Event;
import pa.centric.events.impl.player.EventUpdate;
import pa.centric.client.modules.Type;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.settings.imp.BooleanOption;
import pa.centric.client.modules.settings.imp.ModeSetting;
import pa.centric.util.misc.TimerUtil;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Item;
@SuppressWarnings("all")
/**
@author JohON0
 */
@ModuleAnnotation(name="ChestStealer", category= Type.Player)
public class ChestStealer extends Function {
    private final TimerUtil timerUtil = new TimerUtil();
    public ModeSetting mode = new ModeSetting("Режим", "Обычный", "Обычный", "Декодировка", "Рандом");
    private BooleanOption closeIfEmpty = new BooleanOption("Закрывать, когда пустой", false);
    private final int[] values = new int[]{0, 1, 2, 3, 6, 8, 11, 15, 19, 22, 23, 26, 27, 28, 29, 35, 37, 41, 43, 45, 48, 49, 53};

    public ChestStealer() {
        this.addSettings(this.mode, this.closeIfEmpty);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventUpdate && ChestStealer.mc.player.openContainer instanceof ChestContainer) {
            ChestContainer container = (ChestContainer)ChestStealer.mc.player.openContainer;
            if (this.mode.is("Рандом")) {
                int index = 0;
                while ((float)index < (float)container.inventorySlots.size() / 1.5f) {
                    int slot = new Random().nextInt(0, container.inventorySlots.size());
                    if (container.getLowerChestInventory().getStackInSlot(slot).getItem() != Item.getItemById(0) && this.timerUtil.hasTimeElapsed(0L)) {
                        ChestStealer.mc.playerController.windowClick(container.windowId, slot, 0, ClickType.QUICK_MOVE, ChestStealer.mc.player);
                        this.timerUtil.reset();
                    }
                    ++index;
                }
            } else {
                for (int index = 0; index < container.inventorySlots.size(); ++index) {
                    int finalIndex = index;
                    if (this.mode.get() == "Расшифровка" && !Arrays.stream(this.values).anyMatch(i -> i == finalIndex) || container.getLowerChestInventory().getStackInSlot(index).getItem() == Item.getItemById(0) || !this.timerUtil.hasTimeElapsed(0L)) continue;
                    ChestStealer.mc.playerController.windowClick(container.windowId, finalIndex, 0, ClickType.QUICK_MOVE, ChestStealer.mc.player);
                    this.timerUtil.reset();
                }
            }
            if (container.getLowerChestInventory().isEmpty() && this.closeIfEmpty.get()) {
                ChestStealer.mc.player.closeScreen();
            }
        }
    }
}

