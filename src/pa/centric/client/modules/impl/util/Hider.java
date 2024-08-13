package pa.centric.client.modules.impl.util;

import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.modules.settings.imp.BooleanOption;
import pa.centric.events.Event;

@ModuleAnnotation(name = "Hider", category = Type.Util)
public class Hider extends Function {
    public BooleanOption pass = new BooleanOption("������", true);
    public BooleanOption teleport = new BooleanOption("��������", true);

    public Hider() {
        addSettings(pass,teleport);
    }
    public void onEvent(Event event) {
    }
}
