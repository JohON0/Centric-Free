package pa.centric.client.modules.impl.render;

import net.optifine.shaders.Shaders;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.modules.settings.imp.BooleanOption;
import pa.centric.client.modules.settings.imp.SliderSetting;
import pa.centric.events.Event;
import pa.centric.events.impl.render.EventRender;
import pa.centric.util.ClientUtils;

@ModuleAnnotation(name = "Custom Fog", category = Type.Render)
public class CustomFog extends Function {


    public SliderSetting power = new SliderSetting("Сила", 20, 5,50, 1);
    public BooleanOption confirm = new BooleanOption("я подтверждаю что у меня мощный ПК", false);

    public CustomFog() {
        addSettings(power,confirm);
    }

    public boolean firstStart;

    @Override
    protected void onEnable() {
        super.onEnable();
        if (!confirm.get()) {
            ClientUtils.sendMessage("Вы не нажали на галочку!");
            setState(false);
        } else {
            Shaders.setShaderPack(Shaders.SHADER_PACK_NAME_DEFAULT);
        }
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventRender e) {
            if (e.isRender2D()) {

            }
        }
    }

    public int getDepth() {
        return 6;
    }

}
