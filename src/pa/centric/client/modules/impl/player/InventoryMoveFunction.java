package pa.centric.client.modules.impl.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.EditSignScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.events.Event;
import pa.centric.events.impl.player.EventUpdate;


@ModuleAnnotation(name = "InventoryMove", category = Type.Player)
public class InventoryMoveFunction extends Function {


    @Override
    public void onEvent(final Event event) {
        if (event instanceof EventUpdate) {
            handleEventUpdate();
        }
    }

    /**
     * Обрабатывает событие типа EventUpdate.
     */
    private void handleEventUpdate() {
        // Создаем массив с соответствующими игровыми клавишами
        final KeyBinding[] keys = {mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack,
                mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindJump,
                mc.gameSettings.keyBindSprint};

        // Проверяем, отображается ли экран чата  или экран редактирования знака
        if (mc.currentScreen instanceof ChatScreen || mc.currentScreen instanceof EditSignScreen)
            return;
        // Проходимся по массиву клавиш
        for (KeyBinding keyBinding : keys) {
            // Устанавливаем состояние клавиши на основе текущего состояния
            keyBinding.setPressed(InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), keyBinding.getDefault().getKeyCode()));
        }
    }
}
