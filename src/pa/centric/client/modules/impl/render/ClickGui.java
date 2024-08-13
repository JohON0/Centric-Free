package pa.centric.client.modules.impl.render;

import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.modules.settings.imp.BooleanOption;
import pa.centric.client.modules.settings.imp.SliderSetting;
import pa.centric.events.Event;

@ModuleAnnotation(name = "Click Gui", category = Type.Render)
public class ClickGui extends Function {
    public BooleanOption alternativestyle = new BooleanOption("Размытие", false);
    public BooleanOption blur = new BooleanOption("Размытие", true);
    public SliderSetting blurVal = new SliderSetting("Сила размытия", 15, 5, 20, 1);
    public BooleanOption glow = new BooleanOption("Свечение", true);

    public ClickGui() {
        super();
        addSettings(blur,blurVal,glow);
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        setState(false);
    }

    @Override
    public void onEvent(Event event) {

    }
}
