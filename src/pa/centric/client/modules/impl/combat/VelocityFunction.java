package pa.centric.client.modules.impl.combat;

import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.server.SConfirmTransactionPacket;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.modules.settings.imp.ModeSetting;
import pa.centric.events.Event;
import pa.centric.events.impl.packet.EventPacket;
import pa.centric.events.impl.player.EventUpdate;
import pa.centric.util.IMinecraft;

@ModuleAnnotation(name = "Velocity", category = Type.Combat)
public class VelocityFunction extends Function {

    private final ModeSetting mode = new ModeSetting("Mode", "Cancel", "Cancel","StormHvH", "Grim", "Grim Updated");

    public VelocityFunction() {
        addSettings(mode);
    }

    private int toSkip;
    private int await;

    BlockPos blockPos;

    boolean damaged;

    @Override
    public void onEvent(final Event event) {
        if (IMinecraft.mc.player == null || IMinecraft.mc.world == null) return;

        if (event instanceof EventPacket e && e.isReceivePacket()) {
            switch (mode.get()) {
                case "Cancel" -> {
                    if (e.getPacket() instanceof SEntityVelocityPacket p) {
                        if (p.getEntityID() != IMinecraft.mc.player.getEntityId()) return;

                        e.setCancel(true);
                    }
                }


                case "Grim" -> {
                    if (e.getPacket() instanceof SEntityVelocityPacket p) {
                        if (p.getEntityID() != IMinecraft.mc.player.getEntityId() || toSkip < 0) return;

                        toSkip = 8;
                        event.setCancel(true);
                    }

                    if (e.getPacket() instanceof SConfirmTransactionPacket) {
                        if (toSkip < 0) toSkip++;

                        else if (toSkip > 1) {
                            toSkip--;
                            event.setCancel(true);
                        }
                    }

                    if (e.getPacket() instanceof SPlayerPositionLookPacket) toSkip = -8;
                }

                case "Grim Updated" -> {
                    if (e.getPacket() instanceof SEntityVelocityPacket p) {
                        if (p.getEntityID() != IMinecraft.mc.player.getEntityId() || await > -5) {
                            return;
                        }

                        await = 2;
                        damaged = true;
                        event.setCancel(true);
                    }
                }
            }
        }
        if (event instanceof EventPacket eventPacket && eventPacket.isReceivePacket()) {
            if (mode.get().equals("StormHvH")) {
                if (eventPacket.getPacket() instanceof SEntityVelocityPacket entityID) {
                    if (entityID.getEntityID() != mc.player.getEntityId())
                        return;
                    eventPacket.setCancel(true);
                }
            }
        }

        if (event instanceof EventUpdate) {
            if (mode.is("Grim Updated")) {
                await--;

                if (damaged) {
                    blockPos = new BlockPos(IMinecraft.mc.player.getPositionVec());
                    IMinecraft.mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK, blockPos, Direction.UP));
                    IMinecraft.mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, blockPos, Direction.UP));
                    damaged = false;
                }
            }
        }
    }

    private void reset() {
        toSkip = 0;
        await = 0;
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        reset();
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        reset();
    }
}