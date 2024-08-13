package pa.centric.client.modules.impl.player;

import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.modules.settings.imp.BooleanOption;
import pa.centric.client.modules.settings.imp.MultiBoxSetting;
import pa.centric.events.Event;


@ModuleAnnotation(name = "NoPush", category = Type.Player)
public class NoPushFunction extends Function {

    public final MultiBoxSetting modes = new MultiBoxSetting("Тип",
            new BooleanOption("Игроки", true),
            new BooleanOption("Блоки", true),
            new BooleanOption("Вода", true));

    public NoPushFunction() {
        addSettings(modes);
    }

    @Override
    public void onEvent(final Event event) {
    }
}
