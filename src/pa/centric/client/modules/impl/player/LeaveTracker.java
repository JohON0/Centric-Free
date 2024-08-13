package pa.centric.client.modules.impl.player;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.events.Event;
import pa.centric.events.impl.player.EventEntityLeave;
import pa.centric.util.ClientUtils;

@ModuleAnnotation(name = "LeaveTracker", category =  Type.Player)
public class LeaveTracker extends Function {
    @Subscribe
    public void onEvent(Event event) {
        if (event instanceof EventEntityLeave eventleave) {
            Entity entity = eventleave.getEntity();
            if (!this.isEntityValid(entity)) {
                return;
            }
            String message = "Игрок " + entity.getDisplayName().getString() + " ливнул на " + entity.getPositionVec();
            ClientUtils.sendMessage(message);
        }
    }

    private boolean isEntityValid(Entity entity) {
        if (!(entity instanceof AbstractClientPlayerEntity) || entity instanceof ClientPlayerEntity) {
            return false;
        }
        return !(LeaveTracker.mc.player.getDistance(entity) < 100.0f);
    }
}
