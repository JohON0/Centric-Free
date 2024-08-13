package pa.centric.client.modules.impl.util;

import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.modules.settings.imp.ButtonSetting;
import pa.centric.events.Event;

@ModuleAnnotation(name = "Auto Buy", category = Type.Util)
public class AutoMist extends Function {

    public ButtonSetting buttonSetting = new ButtonSetting("Открыть панель", () -> {

    });

    public AutoMist() {
        super();
        addSettings(buttonSetting);
    }

    @Override
    public void onEvent(Event event) {

    }
}
