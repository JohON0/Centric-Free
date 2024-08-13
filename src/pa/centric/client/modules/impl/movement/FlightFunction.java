package pa.centric.client.modules.impl.movement;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.network.IPacket;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.modules.settings.imp.BooleanOption;
import pa.centric.client.modules.settings.imp.ModeSetting;
import pa.centric.client.modules.settings.imp.SliderSetting;
import pa.centric.events.Event;
import pa.centric.events.impl.player.EventUpdate;
import pa.centric.util.IMinecraft;
import pa.centric.util.movement.MoveUtil;
import pa.centric.util.world.InventoryUtil;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author JohON0
 */
@ModuleAnnotation(name = "Flight", category = Type.Movement)
public class FlightFunction extends Function {
    private final ModeSetting flMode = new ModeSetting("Flight Mode",
            "Motion",
            "Motion", "Glide", "��������");

    private final SliderSetting motion
            = new SliderSetting("Speed XZ",
            1F,
            0F,
            8F,
            0.1F).setVisible(() -> !flMode.is("��������"));

    private final SliderSetting motionY
            = new SliderSetting("Speed Y",
            1F,
            0F,
            8F,
            0.1F).setVisible(() -> !flMode.is("��������"));

    private final BooleanOption setPitch = new BooleanOption("������������ ������", false).setVisible(() -> flMode.is("��������"));
    private int originalSlot = -1;
    public long lastUseTridantTime = 0;

    public CopyOnWriteArrayList<IPacket<?>> packets = new CopyOnWriteArrayList<>();

    public FlightFunction() {
        addSettings(flMode, motion, motionY, setPitch);
    }

    @Override
    public void onEvent(final Event event) {

        if (event instanceof EventUpdate) {

            handleFlyMode();
        }
    }

    /**
     * ������������ ��������� ����� ������.
     */
    private void handleFlyMode() {
        switch (flMode.get()) {
            case "Motion" -> handleMotionFly();
            case "Glide" -> handleGlideFly();
            case "��������" -> handleTridentFly();
        }
    }


    private void handleTridentFly() {
        final int slot = InventoryUtil.getTrident();
        if ((IMinecraft.mc.player.isInWater() || IMinecraft.mc.world.getRainStrength(1) == 1)
                && EnchantmentHelper.getRiptideModifier(IMinecraft.mc.player.getHeldItemMainhand()) > 0) {
            if (slot != -1) {
                originalSlot = IMinecraft.mc.player.inventory.currentItem;
                if (IMinecraft.mc.gameSettings.keyBindUseItem.pressed && setPitch.get()) {
                    IMinecraft.mc.player.rotationPitch = -90;
                }
                IMinecraft.mc.gameSettings.keyBindUseItem.setPressed(IMinecraft.mc.player.ticksExisted % 20 < 15);
            }
        }
    }

    /**
     * ������������ ����� ������ "Motion".
     */
    private void handleMotionFly() {
        final float motionY = this.motionY.getValue().floatValue();
        final float speed = this.motion.getValue().floatValue();

        IMinecraft.mc.player.motion.y = 0;

        if (IMinecraft.mc.gameSettings.keyBindJump.pressed) {
            IMinecraft.mc.player.motion.y = motionY;
        } else if (IMinecraft.mc.player.isSneaking()) {
            IMinecraft.mc.player.motion.y = -motionY;
        }

        MoveUtil.setMotion(speed);
    }

    /**
     * ������������ ����� ������ "Glide".
     */
    private void handleGlideFly() {
        if (IMinecraft.mc.player.isOnGround()) {
            IMinecraft.mc.player.motion.y = 0.42;  // ������������� ������������ �������� ��� ���������� �� �����
        } else {
            IMinecraft.mc.player.setVelocity(0, -0.003, 0);  // ������������� ������������ �������� ��� ������
            MoveUtil.setMotion(motion.getValue().floatValue());  // ������������� �������������� �������� ��������
        }
    }

    @Override
    protected void onDisable() {
        IMinecraft.mc.timer.timerSpeed = 1;

        if (flMode.is("��������")) {
            if (originalSlot != -1) {
                IMinecraft.mc.player.inventory.currentItem = originalSlot;
                originalSlot = -1;
            }
            if (IMinecraft.mc.gameSettings.keyBindUseItem.isKeyDown()) {
                IMinecraft.mc.gameSettings.keyBindUseItem.setPressed(false);
            }
        }
        super.onDisable();
    }
}
