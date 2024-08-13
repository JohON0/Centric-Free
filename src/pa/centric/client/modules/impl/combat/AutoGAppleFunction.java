package pa.centric.client.modules.impl.combat;

import net.minecraft.item.Items;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.modules.settings.imp.BooleanOption;
import pa.centric.client.modules.settings.imp.SliderSetting;
import pa.centric.events.Event;
import pa.centric.events.impl.player.EventUpdate;



@ModuleAnnotation(name = "AutoGApple", category = Type.Combat)
public class AutoGAppleFunction extends Function {
    private final SliderSetting healthThreshold = new SliderSetting("Здоровье", 13.0F, 3.5F, 20.0F, 0.5f);
    private final BooleanOption withAbsorption = new BooleanOption("Учитывать золотые сердечки", true);
    private boolean isEating;

    public AutoGAppleFunction() {
        this.addSettings(healthThreshold, withAbsorption);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventUpdate eventUpdate) {
            handleEating();
        }
    }

    /**
     * Обрабатывает состояние поедания.
     */
    private void handleEating() {
        if (canEat()) {
            startEating();
        } else if (isEating) {
            stopEating();
        }
    }

    /**
     * Проверяет, может ли игрок начать есть.
     *
     * @return true, если игрок может начать есть, в противном случае - false.
     */
    public boolean canEat() {
        float health = mc.player.getHealth();
        if (withAbsorption.get()) {
            health += mc.player.getAbsorptionAmount();
        }

        return !mc.player.getShouldBeDead()
                && mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE
                && health <= healthThreshold.getValue().floatValue()
                && !mc.player.getCooldownTracker().hasCooldown(Items.GOLDEN_APPLE);
    }

    /**
     * Начинает процесс поедания.
     */
    private void startEating() {
        if (!mc.gameSettings.keyBindUseItem.isKeyDown()) {
            mc.gameSettings.keyBindUseItem.setPressed(true);
            isEating = true;
        }
    }

    /**
     * Останавливает процесс поедания.
     */
    private void stopEating() {
        mc.gameSettings.keyBindUseItem.setPressed(false);
        isEating = false;
    }
}
