package pa.centric.client.modules.impl.render;

import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import pa.centric.events.Event;
import pa.centric.events.impl.player.EventUpdate;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.modules.settings.imp.ModeSetting;

@ModuleAnnotation(name = "FullBright", category = Type.Render)
public class FullBrightFunction extends Function {
    public ModeSetting fbType = new ModeSetting("Âטה", "Ãאללא", "Ãאללא", "Çוכüו");

    public FullBrightFunction() {
        addSettings(fbType);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventUpdate && fbType.is("Çוכüו")) {
            mc.player.addPotionEffect(new EffectInstance(Effects.NIGHT_VISION, 1337, 1));
        }
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        if (fbType.is("Ãאללא")) {
            mc.gameSettings.gamma = 1_000_000;
        }
    }

    @Override
    protected void onDisable() {
        if (fbType.is("Ãאללא")) {
            mc.gameSettings.gamma = 1;
        } else {
            mc.player.removePotionEffect(Effects.NIGHT_VISION);
        }
        super.onDisable();
    }
}
