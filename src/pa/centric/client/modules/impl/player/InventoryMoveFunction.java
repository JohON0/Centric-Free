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
     * ������������ ������� ���� EventUpdate.
     */
    private void handleEventUpdate() {
        // ������� ������ � ���������������� �������� ���������
        final KeyBinding[] keys = {mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack,
                mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindJump,
                mc.gameSettings.keyBindSprint};

        // ���������, ������������ �� ����� ����  ��� ����� �������������� �����
        if (mc.currentScreen instanceof ChatScreen || mc.currentScreen instanceof EditSignScreen)
            return;
        // ���������� �� ������� ������
        for (KeyBinding keyBinding : keys) {
            // ������������� ��������� ������� �� ������ �������� ���������
            keyBinding.setPressed(InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), keyBinding.getDefault().getKeyCode()));
        }
    }
}
