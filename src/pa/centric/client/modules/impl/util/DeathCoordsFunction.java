package pa.centric.client.modules.impl.util;

import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.util.text.TextFormatting;
import pa.centric.client.helper.conduction;
import pa.centric.client.modules.Function;
import pa.centric.events.Event;
import pa.centric.events.impl.player.EventUpdate;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.util.ClientUtils;


@ModuleAnnotation(name = "DeathCoords", category = Type.Util)
public class DeathCoordsFunction extends Function {
    @Override
    public void onEvent(Event event) {
        if (event instanceof EventUpdate) {
            checkDeathCoordinates();
        }
    }

    public void checkDeathCoordinates() {
        if (isPlayerDead()) {
            int positionX = mc.player.getPosition().getX();
            int positionY = mc.player.getPosition().getY();
            int positionZ = mc.player.getPosition().getZ();

            if (mc.player.deathTime < 1) {
                printDeathCoordinates(positionX, positionY, positionZ);
            }
        }
    }

    private boolean isPlayerDead() {
        return mc.player.getHealth() < 1.0f && mc.currentScreen instanceof DeathScreen;
    }

    private void printDeathCoordinates(int x, int y, int z) {
        String message = "Координаты смерти: " + TextFormatting.GRAY + "X: " + x + " Y: " + y + " Z: " + z + TextFormatting.RESET;
        ClientUtils.sendMessage(message);
        conduction.NOTIFICATION_MANAGER.add("DeathCoords", message,8);
    }
}
