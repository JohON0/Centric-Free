package pa.centric.client.modules.impl.util;

import pa.centric.client.modules.Function;
import pa.centric.client.modules.settings.imp.BooleanOption;
import pa.centric.client.modules.settings.imp.MultiBoxSetting;
import pa.centric.events.Event;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;

@ModuleAnnotation(name = "Optimization", category = Type.Util)
public class Optimization extends Function {
    public final BooleanOption autoJump = new BooleanOption("Авто прыжок", true);
    public final BooleanOption ofSky = new BooleanOption("Оптимизация Облоков", true);
    public final BooleanOption ofCustomSky = new BooleanOption("Оптимизация Графики неба", true);
    public final BooleanOption entityShadows = new BooleanOption("Оптимизация Энтити", true);
    public final MultiBoxSetting optimizeSelection = new MultiBoxSetting("Оптимизировать", new BooleanOption("Освещение",true), new BooleanOption("Партиклы",true), new BooleanOption("Подсветка клиента.", false));

    public Optimization() {
        addSettings(optimizeSelection,ofCustomSky,ofSky,entityShadows);
    }

    @Override
    public void onEvent(Event event) {
        if (autoJump.get()) {
            mc.gameSettings.autoJump = false;
        }
        if (ofSky.get()) {
            mc.gameSettings.ofSky = false;
        }
        if (ofCustomSky.get()) {
            mc.gameSettings.ofCustomSky = false;
        }
        if (entityShadows.get()) {
            mc.gameSettings.entityShadows = false;
        }
        long maxMemory = Runtime.getRuntime().maxMemory();
        long totalglebxmancleanermemory = Runtime.getRuntime().totalMemory();
        long glebxmengivefreememory1tb = Runtime.getRuntime().freeMemory();
        long aleshaakriver = totalglebxmancleanermemory - glebxmengivefreememory1tb - maxMemory;
    }
    public void onDisable() {
        super.onDisable();
        mc.gameSettings.autoJump = true;
        mc.gameSettings.ofSky = true;
        mc.gameSettings.ofCustomSky = true;
        mc.gameSettings.entityShadows = true;
    }
}
