package pa.centric.client.modules.impl.render;

import net.minecraft.potion.Effects;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.modules.settings.imp.BooleanOption;
import pa.centric.client.modules.settings.imp.MultiBoxSetting;
import pa.centric.events.Event;
import pa.centric.events.impl.player.EventOverlaysRender;
import pa.centric.events.impl.player.EventUpdate;

@ModuleAnnotation(name = "No Render", category = Type.Render)
public class NoRenderFunction extends Function {

    public MultiBoxSetting element = new MultiBoxSetting("��������",
            new BooleanOption("����� �� ������", true),
            new BooleanOption("������ �������", true),
            new BooleanOption("����� �����", false),
            new BooleanOption("��������� ����", false),
            new BooleanOption("�������", false),
            new BooleanOption("������", false),
            new BooleanOption("�����", true),
            new BooleanOption("�����", false),
            new BooleanOption("�����", true),
            new BooleanOption("������-����", true));

    public NoRenderFunction() {
        addSettings(element);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventOverlaysRender) {
            handleEventOverlaysRender((EventOverlaysRender) event);
        } else if (event instanceof EventUpdate) {
            handleEventUpdate((EventUpdate) event);
        }
    }

    /**
     * ������������ �������, ��������� � ���������� ��������.
     * ���� ��� ������� ������������� ������������� �������� � ���� ������� �������,
     * ������������� ���� ������ ��������� �������.
     *
     * @param event ������� ��������� �������
     */
    private void handleEventOverlaysRender(EventOverlaysRender event) {
        EventOverlaysRender.OverlayType overlayType = event.getOverlayType();

        boolean cancelOverlay = switch (overlayType) {
            case FIRE_OVERLAY -> element.get(0);
            case BOSS_LINE -> element.get(2);
            case SCOREBOARD -> element.get(4);
            case TITLES -> element.get(5);
            case TOTEM -> element.get(6);
            case FOG -> element.get(8);
        };

        if (cancelOverlay) {
            event.setCancel(true);
        }
    }

    /**
     * ������������ ������� ���������� ����.
     * ���� ������������ ������� �������, ��������� ������������ ��������.
     * - ���� ������� 6 ������� � � ���� ���� �����, ��������� ����� � �����.
     * - ���� ������� 1 ������� � � ������ ������� ������� ��� �������,
     *   ������� ������� ������� � ������� � ������.
     *
     * @param event ������� ���������� ����
     */
    private void handleEventUpdate(EventUpdate event) {
        
        boolean isRaining = element.get(6) && mc.world.isRaining();

        boolean hasEffects = element.get(1) &&
                (mc.player.isPotionActive(Effects.BLINDNESS)
                || mc.player.isPotionActive(Effects.NAUSEA));

        if (isRaining) {
            mc.world.setRainStrength(0);
            mc.world.setThunderStrength(0);
        }

        if (hasEffects) {
            mc.player.removePotionEffect(Effects.NAUSEA);
            mc.player.removePotionEffect(Effects.BLINDNESS);
        }
    }
}
