package pa.centric.client.modules.impl.render;

import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.modules.settings.imp.SliderSetting;
import pa.centric.events.Event;

@ModuleAnnotation(name = "HitColor", category = Type.Render)
public class HitColor extends Function {

    public SliderSetting intensivity = new SliderSetting("Интенсивность", 0.3f, 0.1f, 1, 0.1f);

    public HitColor() {
        super();
        addSettings(intensivity);
    }

    @Override
    public void onEvent(Event event) {

    }
}
