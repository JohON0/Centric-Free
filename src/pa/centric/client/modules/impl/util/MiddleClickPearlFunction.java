package pa.centric.client.modules.impl.util;

import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;
import pa.centric.client.helper.conduction;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.modules.impl.player.GappleCooldownFunction;
import pa.centric.client.modules.settings.imp.BindSetting;
import pa.centric.util.world.InventoryUtil;
import pa.centric.events.Event;
import pa.centric.events.impl.game.EventMouseTick;

@ModuleAnnotation(name = "MiddleClickPearl", category = Type.Util)
public class MiddleClickPearlFunction extends Function {
    @Override
    public void onEvent(final Event event) {
        if (event instanceof EventMouseTick mouseTick) {
            if (mouseTick.getButton() == 2) {
                handleMouseTickEvent();
            }
        }
    }

    /**
     * ������������ ������� EventMouseTick ��� ������� �� ������ ������ ���� (������ � ����� 2).
     */
    private void handleMouseTickEvent() {
        if (!mc.player.getCooldownTracker().hasCooldown(Items.ENDER_PEARL) && InventoryUtil.getPearls() >= 0) {
            sendHeldItemChangePacket(InventoryUtil.getPearls());

            sendPlayerRotationPacket(mc.player.rotationYaw, mc.player.rotationPitch, mc.player.isOnGround());
            useItem(Hand.MAIN_HAND);

            sendHeldItemChangePacket(mc.player.inventory.currentItem);
        }
    }

    /**
     * ���������� ����� ����� ��������� ��������.
     *
     * @param itemSlot ���� ��������
     */
    private void sendHeldItemChangePacket(int itemSlot) {
        mc.player.connection.sendPacket(new CHeldItemChangePacket(itemSlot));
        GappleCooldownFunction cooldown = conduction.FUNCTION_MANAGER.gappleCooldownFunction;
        GappleCooldownFunction.ItemEnum itemEnum = GappleCooldownFunction.ItemEnum.getItemEnum(Items.ENDER_PEARL);

        if (cooldown.state && itemEnum != null && cooldown.isCurrentItem(itemEnum)) {
            cooldown.lastUseItemTime.put(itemEnum.getItem(), System.currentTimeMillis());
        }
    }

    /**
     * ���������� ����� �������� ������, ���� ������ ���� �� � ������� ������� ��������.
     *
     * @param yaw      �������� �������� �� �����������
     * @param pitch    �������� �������� �� ���������
     * @param onGround �������� ��������� �� ����� �� �����
     */
    private void sendPlayerRotationPacket(float yaw, float pitch, boolean onGround) {
        if (conduction.FUNCTION_MANAGER.auraFunction.target != null) {
            mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket(yaw, pitch, onGround));
        }
    }

    /**
     * ���������� ����� ������������� �������� � ������� ����.
     *
     * @param hand �������� ����
     */
    private void useItem(Hand hand) {
        mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(hand));
        mc.player.swingArm(hand);
    }
}
