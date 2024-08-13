package pa.centric.client.modules.impl.util;

import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.modules.settings.imp.ModeSetting;
import pa.centric.client.modules.settings.imp.SliderSetting;
import pa.centric.events.Event;


@ModuleAnnotation(name = "Client Sounds", category = Type.Util)
public class ClientSounds extends Function {

    public ModeSetting mode = new ModeSetting("Òèï", "Òèï 1", "Òèï 1", "Òèï 2", "Òèï 3");
    public SliderSetting volume = new SliderSetting("Ãğîìêîñòü", 70.0f, 0.0f, 100.0f, 1.0f);

    public ClientSounds() {
        addSettings(mode, volume);
    }

    @Override
    public void onEvent(Event event) {
    }

    public String getFileName(boolean state) {
        switch (mode.get()) {
            case "Òèï 1" -> {
                return state ? "on" : "off".toString();
            }
            case "Òèï 2" -> {
                return state ? "on1" : "off1";
            }
            case "Òèï 3" -> {
                return state ? "on2" : "off2";
            }
        }
        return "";
    }
}

