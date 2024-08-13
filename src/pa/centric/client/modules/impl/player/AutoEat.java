package pa.centric.client.modules.impl.player;

import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.events.Event;
import pa.centric.events.impl.player.EventUpdate;

@ModuleAnnotation(name = "AutoEat", category = Type.Player)
public class AutoEat extends Function {

    private boolean isEating = false;

    @Override
    public void onEvent(final Event event) {
        if (mc.player == null || mc.world == null) return;

        if (event instanceof EventUpdate e) {
            mc.gameSettings.keyBindUseItem.pressed = isEating;

            if (mc.player.getFoodStats().getFoodLevel() < 15) {
                int slot = findEatSlot();

                if (slot == -1) return;

                mc.player.inventory.currentItem = slot;

                isEating = true;
            } else {
                isEating = mc.player.getFoodStats().needFood();
            }
        }
    }

    public int findEatSlot() {
        for (int slot = 0; slot < 9; slot++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(slot);

            if (stack.getUseAction() == UseAction.EAT) {
                return slot;
            }
        }

        return -1;
    }
}
