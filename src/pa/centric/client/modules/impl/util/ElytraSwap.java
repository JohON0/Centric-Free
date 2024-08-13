package pa.centric.client.modules.impl.util;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.modules.settings.imp.BindSetting;
import pa.centric.client.modules.settings.imp.BooleanOption;
import pa.centric.util.ClientUtils;
import pa.centric.util.world.InventoryUtil;
import pa.centric.events.Event;
import pa.centric.events.impl.game.EventKey;
import pa.centric.events.impl.player.EventUpdate;

@ModuleAnnotation(name = "ElytraSwap", category = Type.Util)
public class ElytraSwap extends Function {

    private BindSetting swapKey = new BindSetting("Кнопка свапа", 0);
    private BindSetting fireworkKey = new BindSetting("Кнопка феерверков", 0);
    private ItemStack oldStack = null;
    boolean startFallFlying;
    private BooleanOption autoFly = new BooleanOption("Автоматический взлёт", true);

    private BooleanOption notif = new BooleanOption("Оповещение", true);

    public ElytraSwap() {
        addSettings(swapKey, fireworkKey, notif, autoFly);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventUpdate) {
            if (autoFly.get() && mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() == Items.ELYTRA) {
                if (mc.player.isOnGround()) {
                    startFallFlying = false;
                    mc.gameSettings.keyBindJump.setPressed(false);
                    mc.player.jump();
                    return;
                }

                if (!mc.player.isElytraFlying() && !startFallFlying && mc.player.motion.y < 0.0) {
                    mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                    startFallFlying = true;
                }
            }
        }
        if (event instanceof EventKey e) {
            ItemStack itemStack = mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST);


            if (e.key == swapKey.getKey()) {
                int elytraSlot = InventoryUtil.getItemSlot(Items.ELYTRA);


                if (elytraSlot == -1) {
                    ClientUtils.sendMessage(TextFormatting.RED + "Не найдена элитра в инвентаре!");
                    return;
                }

                if (reasonToEquipElytra(itemStack)) {
                    ItemStack n = mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST);
                    oldStack = n.copy();
                    InventoryUtil.moveItem(elytraSlot, 6, true);
                    if (notif.get())
                        ClientUtils.sendMessage(TextFormatting.RED + "Свапнул на элитру!");

                } else if (oldStack != null) {
                    int oldStackSlot = InventoryUtil.getItemSlot(oldStack.getItem());
                    InventoryUtil.moveItem(oldStackSlot, 6, true);
                    if (notif.get())
                        ClientUtils.sendMessage(TextFormatting.RED + "Свапнул на нагрудник!");
                    oldStack = null;

                }
            }
            if (e.key == fireworkKey.getKey() && itemStack.getItem() == Items.ELYTRA) useFirework();
        }
    }

    private void useFirework() {
        int fireWorksSlot = InventoryUtil.getFireWorks();


        boolean offHand = mc.player.getHeldItemOffhand().getItem() == Items.FIREWORK_ROCKET;

        if (!offHand && fireWorksSlot == -1) {
            ClientUtils.sendMessage(TextFormatting.RED + "Нет феерверков!");
            return;
        }

        if (!offHand) mc.player.connection.sendPacket(new CHeldItemChangePacket(fireWorksSlot));
        mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(offHand ? Hand.OFF_HAND : Hand.MAIN_HAND));
        if (!offHand) mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));

    }

    private boolean reasonToEquipElytra(ItemStack stack) {
        return stack.getItem() != Items.ELYTRA;
    }
}
