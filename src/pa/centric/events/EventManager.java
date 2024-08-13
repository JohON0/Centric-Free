package pa.centric.events;

import net.minecraft.client.Minecraft;
import pa.centric.client.helper.conduction;
import pa.centric.client.modules.Function;
import pa.centric.util.ClientUtils;

public class EventManager {

    /**
     * �������� ������� � �������� ��� ���� �������� ������� ��� ���������.
     *
     * @param event ������� ��� ������.
     */
    public static void call(final Event event) {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().world == null) {
            return;
        }

        if (event.isCancel()) {
            return;
        }

        // ������� ���� �������� ������� � ����� �������
        if (!ClientUtils.legitMode) {
            callEvent(event);
        }
    }

    /**
     * �������� ��������� ������� � �������� ��� ���� �������� ������� ��� ���������.
     *
     * @param event ������� ��� ������.
     */
    private static void callEvent(Event event) {
        for (final Function module : conduction.FUNCTION_MANAGER.getFunctions()) {
            if (!module.isState())
                continue;

            module.onEvent(event);
        }
    }
}