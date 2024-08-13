
package pa.centric.client.modules.impl.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.network.play.server.SHeldItemChangePacket;
import net.minecraft.util.Hand;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.modules.settings.imp.BooleanOption;
import pa.centric.client.modules.settings.imp.ModeSetting;
import pa.centric.util.misc.TimerUtil;
import pa.centric.util.movement.MoveUtil;
import pa.centric.events.Event;
import pa.centric.events.impl.packet.EventPacket;
import pa.centric.events.impl.player.EventDamage;
import pa.centric.events.impl.player.EventNoSlow;
import pa.centric.util.IMinecraft;
import pa.centric.util.misc.DamageUtil;

@ModuleAnnotation(name="NoSlow", category = Type.Movement)
public class NoSlowFunction
        extends Function {
    public ModeSetting mode = new ModeSetting("Мод", "Vanilla", "Vanilla", "Matrix", "Grim", "HolyWorld", "Intave", "SkyTime");
    public BooleanOption swapfix = new BooleanOption("Фикс свапа", false).setVisible(() -> mode.is("ReallyWorld"));
    DamageUtil damageUtil = new DamageUtil();
    private TimerUtil timerutil = new TimerUtil();
    boolean restart = true;

    public NoSlowFunction() {
        this.addSettings(this.mode, this.swapfix);
    }

    @Override
    public void onEvent(Event event) {
        EventPacket eventPacket;
        if (Minecraft.getInstance().player.isElytraFlying()) {
            return;
        }
        if (event instanceof EventNoSlow) {
            EventNoSlow eventNoSlow = (EventNoSlow)event;
            this.handleEventUpdate(eventNoSlow);
        } else if (event instanceof EventDamage) {
            EventDamage eventDamage = (EventDamage)event;
            this.damageUtil.processDamage(eventDamage);
        } else if (event instanceof EventPacket && (eventPacket = (EventPacket)event).isReceivePacket()) {
            this.damageUtil.onPacketEvent(eventPacket);
        }
    }

    private void handleEventUpdate(EventNoSlow eventNoSlow) {
        if (Minecraft.getInstance().player.isHandActive()) {
            switch (this.mode.get()) {
                //обычный ноуслоу пон
                case "Vanilla": {
                    eventNoSlow.setCancel(true);
                    break;
                }
                //холик байпас
                case "HolyWorld": {
                    this.handleGrimACMode(eventNoSlow);
                    break;
                }
                //матрикс дефолт
                case "Matrix": {
                    this.handleMatrixMode(eventNoSlow);
                    break;
                }
                //интейв авхвахвах
                case "Intave": {
                    this.handleIntaveMode(eventNoSlow);
                    break;
                }
                //рв в страхе
                case "Grim": {
                    this.handleGrimNewMode(eventNoSlow);
                }
                //ну это пенный селфкод
                case "SkyTime": {
                    this.skytimenoslow(eventNoSlow);
                    break;
                }
            }
        }
    }

    /*
     * Enabled aggressive block sorting
     */
    private void handleMatrixMode(EventNoSlow eventNoSlow) {
        boolean bl = (double) IMinecraft.mc.player.fallDistance > 0.725;
        eventNoSlow.setCancel(true);
        if (IMinecraft.mc.player.isOnGround()) {
            if (!IMinecraft.mc.player.movementInput.jump) {
                if (IMinecraft.mc.player.ticksExisted % 2 != 0) return;
                boolean bl2 = IMinecraft.mc.player.moveStrafing == 0.0f;
                float f = bl2 ? 0.5f : 0.4f;
                IMinecraft.mc.player.motion.x *= (double)f;
                IMinecraft.mc.player.motion.z *= (double)f;
                return;
            }
        }
        if (!bl) return;
        boolean bl3 = (double)IMinecraft.mc.player.fallDistance > 1.4;
        float f = bl3 ? 0.95f : 0.97f;
        IMinecraft.mc.player.motion.x *= (double)f;
        IMinecraft.mc.player.motion.z *= (double)f;
    }


    private void handleIntaveMode(EventNoSlow eventNoSlow) {
        block8: {
            block7: {
                this.damageUtil.time(1500L);
                if (IMinecraft.mc.player.getHeldItemOffhand().getUseAction() == UseAction.BLOCK) break block7;
                if (IMinecraft.mc.player.getHeldItemOffhand().getUseAction() != UseAction.EAT) break block8;
            }
            if (IMinecraft.mc.player.getActiveHand() == Hand.MAIN_HAND) {
                return;
            }
        }
        if (IMinecraft.mc.player.isOnGround()) {
            if (!IMinecraft.mc.player.movementInput.jump && !this.damageUtil.isNormalDamage()) {
                return;
            }
        }
        if (IMinecraft.mc.player.getActiveHand() == Hand.MAIN_HAND) {
            IMinecraft.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.OFF_HAND));
            eventNoSlow.setCancel(true);
            return;
        }
        eventNoSlow.setCancel(true);
        this.sendItemChangePacket();
    }
    private void skytimenoslow(EventNoSlow noslowevent) {
        block10: {
            block1: {
                this.damageUtil.time(1500L);
                if (IMinecraft.mc.player.getHeldItemOffhand().getUseAction() == UseAction.BLOCK) break block1;
                if (IMinecraft.mc.player.getHeldItemOffhand().getUseAction() != UseAction.EAT) break block10;
            }
            if (IMinecraft.mc.player.getActiveHand() == Hand.MAIN_HAND) {
                return;
            }
        }
        if (mc.player.isOnGround()) {
            if (mc.player.movementInput.jump && !this.damageUtil.isNormalDamage()) {
                return;
            }
        }
        if (mc.player.getActiveHand() == Hand.MAIN_HAND) {
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.OFF_HAND));
            noslowevent.setCancel(true);
            return;
        }
        noslowevent.setCancel(true);
        this.sendItemChangePacket();
    }
    private void handleGrimNewMode(EventNoSlow eventNoSlow) {
            boolean mainHandActive;
            boolean offHandActive = mc.player.isHandActive() && mc.player.getActiveHand() == Hand.OFF_HAND;
            boolean bl = mainHandActive =mc.player.isHandActive() && mc.player.getActiveHand() == Hand.MAIN_HAND;
            if ((mc.player.getItemInUseCount() >= 25 || mc.player.getItemInUseCount() <= 4) && mc.player.getHeldItemOffhand().getItem() != Items.SHIELD) {
                return;
            }
            if (mc.player.isHandActive() && !mc.player.isPassenger()) {
                mc.playerController.syncCurrentPlayItem();
                if (offHandActive && !mc.player.getCooldownTracker().hasCooldown(mc.player.getHeldItemOffhand().getItem())) {
                    int old = mc.player.inventory.currentItem;
                    mc.player.connection.sendPacket(new CHeldItemChangePacket(old + 1 > 8 ? old - 1 : old + 1));
                    mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
                    mc.player.setSprinting(false);
                    eventNoSlow.setCancel(true);
                }
                if (mainHandActive && !mc.player.getCooldownTracker().hasCooldown(mc.player.getHeldItemMainhand().getItem())) {
                    mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.OFF_HAND));
                    if (mc.player.getHeldItemOffhand().getUseAction().equals((Object)UseAction.NONE)) {
                        eventNoSlow.setCancel(true);
                    }
                }
                mc.playerController.syncCurrentPlayItem();
            }
        }

    private void handleGrimACMode(EventNoSlow eventNoSlow) {
        block6: {
            block5: {
                block4: {
                    if (IMinecraft.mc.player.getHeldItemOffhand().getUseAction() != UseAction.BLOCK) break block4;
                    if (IMinecraft.mc.player.getActiveHand() == Hand.MAIN_HAND) break block5;
                }
                if (IMinecraft.mc.player.getHeldItemOffhand().getUseAction() != UseAction.EAT) break block6;
                if (IMinecraft.mc.player.getActiveHand() != Hand.MAIN_HAND) break block6;
            }
            return;
        }
        if (IMinecraft.mc.player.getActiveHand() == Hand.MAIN_HAND) {
            IMinecraft.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.OFF_HAND));
            eventNoSlow.setCancel(true);
            return;
        }
        eventNoSlow.setCancel(true);
        this.sendItemChangePacket();
    }

    private void sendItemChangePacket() {
        if (MoveUtil.isMoving()) {
            IMinecraft.mc.player.connection.sendPacket(new CHeldItemChangePacket(IMinecraft.mc.player.inventory.currentItem % 8 + 1));
            IMinecraft.mc.player.connection.sendPacket(new CHeldItemChangePacket(IMinecraft.mc.player.inventory.currentItem));
        }
    }

    @Override
    protected void onEnable() {
        super.onEnable();
    }
}

