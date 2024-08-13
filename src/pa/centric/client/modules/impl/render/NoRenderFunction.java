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

    public MultiBoxSetting element = new MultiBoxSetting("Элементы",
            new BooleanOption("Огонь на экране", true),
            new BooleanOption("Плохие эффекты", true),
            new BooleanOption("Линия босса", false),
            new BooleanOption("Нанесение Таба", false),
            new BooleanOption("Таблица", false),
            new BooleanOption("Тайтлы", false),
            new BooleanOption("Тотем", true),
            new BooleanOption("Дождь", false),
            new BooleanOption("Туман", true),
            new BooleanOption("Камера-клип", true));

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
     * Обрабатывает событие, связанное с отрисовкой оверлеев.
     * Если тип оверлея соответствует определенному элементу и этот элемент активен,
     * устанавливает флаг отмены отрисовки оверлея.
     *
     * @param event событие отрисовки оверлея
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
     * Обрабатывает событие обновления игры.
     * Если определенный элемент активен, выполняет определенные действия.
     * - Если элемент 6 активен и в мире идет дождь, отключает дождь и грозу.
     * - Если элемент 1 активен и у игрока активно слепота или тошнота,
     *   удаляет эффекты слепоты и тошноты у игрока.
     *
     * @param event событие обновления игры
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
