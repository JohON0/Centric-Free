package pa.centric.client.modules.impl.player;

import net.minecraft.client.gui.screen.DeathScreen;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.events.Event;
import pa.centric.events.impl.player.EventUpdate;


@ModuleAnnotation(name = "AutoRespawn", category = Type.Player)
public class AutoRespawnFunction extends Function {

    @Override
    public void onEvent(final Event event) {
        if (event instanceof EventUpdate) {
            if (mc.currentScreen instanceof DeathScreen && mc.player.deathTime > 2) {
                mc.player.respawnPlayer();
                mc.displayGuiScreen(null);
            }
        }
    }
}
