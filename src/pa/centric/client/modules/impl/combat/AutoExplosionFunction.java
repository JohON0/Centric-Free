package pa.centric.client.modules.impl.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.modules.settings.imp.BooleanOption;
import pa.centric.util.math.MathUtil;
import pa.centric.util.movement.MoveUtil;
import pa.centric.events.Event;
import pa.centric.events.impl.player.EventInput;
import pa.centric.events.impl.player.EventMotion;
import pa.centric.events.impl.player.EventObsidianPlace;
import pa.centric.events.impl.player.EventUpdate;
import pa.centric.util.misc.TimerUtil;

import java.util.List;


@ModuleAnnotation(name = "AutoExplosion", category = Type.Combat)
public class AutoExplosionFunction extends Function {
    private BlockPos position = null;
    private Entity crystalEntity = null;
    private int oldSlot = -1;
    private final TimerUtil timerUtil = new TimerUtil();

    private final BooleanOption saveSelf = new BooleanOption("�� �������� ����", true);
    public final BooleanOption correction = new BooleanOption("��������� ��������", true);

    public AutoExplosionFunction() {
        addSettings(saveSelf,correction);
    }

    public Vector2f server;

    public boolean check() {
        return server != null && correction.get() && crystalEntity != null && position != null && state;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventInput e) {
            if (check()) {
                MoveUtil.fixMovement(e, server.x);
            }
        }
        if (event instanceof EventObsidianPlace obsidianPlace) {
            handleObsidianPlace(obsidianPlace.getPos());
        } else if (event instanceof EventUpdate updateEvent) {
            handleUpdateEvent(updateEvent);
        } else if (event instanceof EventMotion motionEvent) {
            handleMotionEvent(motionEvent);
        }
    }

    /**
     * ���������� ������� EventUpdate.
     * ��������� ������� ��������� � ��������� ������� � ������� ���.
     */
    private void handleUpdateEvent(EventUpdate updateEvent) {
        if (position != null) {
            List<Entity> crystals = mc.world.getEntitiesWithinAABBExcludingEntity(null,
                            new AxisAlignedBB(position.getX(),
                                    position.getY(),
                                    position.getZ(),
                                    position.getX() + 1.0,
                                    position.getY() + 2.0,
                                    position.getZ() + 1.0))
                    .stream()
                    .filter(entity -> entity instanceof EnderCrystalEntity)
                    .toList();

            crystals.forEach(this::attackEntity);
        }

        if (crystalEntity != null && !crystalEntity.isAlive()) {
            crystalEntity = null;
            position = null;
            server = null;
        }
    }


    /**
     * ���������� ������� EventObsidianPlace.
     * ��������� �������� � ��������� ������� (���� �������� �������� mc.player).
     */
    private void handleObsidianPlace(BlockPos position) {
        final int crystalSlot = getSlotWithCrystal();

        this.oldSlot = mc.player.inventory.currentItem;


        if (crystalSlot == -1 || position == null) {
            return;
        }

        mc.player.inventory.currentItem = crystalSlot;

        BlockRayTraceResult rayTraceResult = new BlockRayTraceResult(
                new Vector3d(position.getX() + 0.5f, position.getY() + 0.5f, position.getZ() + 0.5f),
                Direction.UP,
                position,
                false
        );

        if (mc.playerController.processRightClickBlock(mc.player, mc.world, Hand.MAIN_HAND, rayTraceResult)
                == ActionResultType.SUCCESS) {
            mc.player.swingArm(Hand.MAIN_HAND);
        }
        if (oldSlot != -1)
            mc.player.inventory.currentItem = this.oldSlot;
        this.oldSlot = -1;
        this.position = position;
    }

    /**
     * ���������� ������� EventMotion.
     * ������������� �������� ������ � ����������� ���������.
     */
    private void handleMotionEvent(EventMotion motionEvent) {
        if (!isValid(crystalEntity)) {
            return;
        }

        server = MathUtil.rotationToEntity(crystalEntity);

        motionEvent.setYaw(server.x);
        motionEvent.setPitch(server.y);
        mc.player.rotationYawHead = server.x;
        mc.player.renderYawOffset = server.x;
        mc.player.rotationPitchHead = server.y;
    }

    /**
     * �������� �������� � ��������� 300 ��.
     *
     * @param base ����� �������
     */
    private void attackEntity(Entity base) {
        if (!isValid(base)
                || mc.player.getCooledAttackStrength(1) < 1) {
            return;
        }
        if (timerUtil.hasTimeElapsed(400)) {

            mc.playerController.attackEntity(mc.player, base);
            mc.player.swingArm(Hand.MAIN_HAND);
            timerUtil.reset();
        }
        crystalEntity = base;
    }

    /**
     * �������� ���� � ���������� � ��������� ������.
     *
     * @return ���� � ����������, ��� -1 ���� �������� �� ������.
     */
    private int getSlotWithCrystal() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.END_CRYSTAL) {
                return i;
            }
        }
        return -1;
    }

    /**
     * ���������, ��������� �� ����� �� ���������� ���������� �� ������� ���������.
     *
     * @return true, ���� ����� ��������� �� ���������� ����������, ����� false.
     */
    private boolean isCorrectDistance() {
        if (position == null) {
            return false;
        }
        return mc.player.getPositionVec().distanceTo(
                new Vector3d(position.getX(),
                        position.getY(),
                        position.getZ())) <= mc.playerController.getBlockReachDistance();
    }

    /**
     * ���������, ������� �� ��������
     *
     * @return true, ���� �������� �������, ����� false.
     */
    private boolean isValid(Entity base) {
        if (base == null) {
            return false;
        }
        if (position == null) {
            return false;
        }
        if (saveSelf.get() && !(base.getPosition().getY() > mc.player.getPosition().getY())) {
            return false;
        }

        return isCorrectDistance();
    }

    @Override
    protected void onDisable() {
        server = null;
        position = null;
        crystalEntity = null;
        oldSlot = -1;
        super.onDisable();
    }
}