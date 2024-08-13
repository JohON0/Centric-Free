package pa.centric.client.modules.impl.combat;

import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.modules.settings.imp.SliderSetting;
import pa.centric.events.Event;
import pa.centric.events.impl.packet.EventPacket;

@ModuleAnnotation(name = "SuperBow", category = Type.Combat)
public class SuperBow extends Function {

    private final SliderSetting power = new SliderSetting("Сила", 30, 1, 100, 1);

    public SuperBow() {
        addSettings(power);
    }

    @Override
    public void onEvent(Event event) {
        if (mc.player == null || mc.world == null) return;

        if (event instanceof EventPacket e) {
            if (e.getPacket() instanceof CPlayerDiggingPacket p) {
                if (p.getAction() == CPlayerDiggingPacket.Action.RELEASE_USE_ITEM) {
                    mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_SPRINTING));
                    for (int i = 0; i < power.getValue().intValue(); i++) {
                        mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY() - 0.000000001, mc.player.getPosZ(), true));
                        mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(mc.player.getPosX(), mc.player.getPosY() + 0.000000001, mc.player.getPosZ(), false));
                    }
                }
            }
        }
    }
}
