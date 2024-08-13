package pa.centric.client.modules.impl.movement;

import net.minecraft.network.play.client.CConfirmTransactionPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.modules.settings.imp.ModeSetting;
import pa.centric.util.movement.MoveUtil;
import pa.centric.events.Event;
import pa.centric.events.impl.packet.EventPacket;
import pa.centric.events.impl.player.EventMove;
import pa.centric.events.impl.player.EventUpdate;
import pa.centric.util.misc.TimerUtil;


@ModuleAnnotation(name = "Speed", category = Type.Movement)
public class SpeedFunction extends Function {

    private final ModeSetting spdMode = new ModeSetting("Режим", "Matrix", "Matrix", "Timer", "Sunrise DMG", "Grim", "Intave");


    public SpeedFunction() {
        addSettings(spdMode);
    }

    public boolean boosting;

    @Override
    protected void onEnable() {
        super.onEnable();
        timerUtil.reset();
        boosting = false;
    }

    public TimerUtil timerUtil = new TimerUtil();

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventPacket e) {
            handlePacketEvent(e);
        } else if (event instanceof EventMove) {
            handleEventMove((EventMove) event);
        } else if (event instanceof EventUpdate) {
            handleEventUpdate((EventUpdate) event);
        }
    }

    private void handleEventMove(EventMove eventMove) {
        if (spdMode.is("Matrix")) {
            if (!mc.player.isOnGround() && mc.player.fallDistance >= 0.5f && eventMove.toGround()) {
                applyMatrixSpeed();
            }
        }
    }

    private void handleEventUpdate(EventUpdate eventUpdate) {
        switch (spdMode.get()) {
            case "Matrix":
                if (mc.player.isOnGround() && MoveUtil.isMoving())
                    mc.player.jump();
                break;
            case "Grim":
                handleGrimMode();
                break;
            case "Timer":
                handleTimerMode();
                break;
            case "Sunrise DMG":
                handleSunriseDamageMode();
                break;
            case "Intave":
                intave();
        }
    }

    private void handlePacketEvent(EventPacket e) {
        if (spdMode.is("Really World")) {
            if (e.getPacket() instanceof CConfirmTransactionPacket p) {
                e.setCancel(true);
            }
            if (e.getPacket() instanceof SPlayerPositionLookPacket p) {
                mc.player.func_242277_a(new Vector3d(p.getX(), p.getY(), p.getZ()));
                mc.player.setRawPosition(p.getX(), p.getY(), p.getZ());
                toggle();
            }
        }
    }

    private void handleGrimMode() {
        if (timerUtil.hasTimeElapsed(1150)) {
            boosting = true;
        }
        if (timerUtil.hasTimeElapsed(7000)) {
            boosting = false;
            timerUtil.reset();
        }
        if (boosting) {
            if (mc.player.isOnGround() && !mc.gameSettings.keyBindJump.pressed) {
                mc.player.jump();
            }
            mc.timer.timerSpeed = mc.player.ticksExisted % 2 == 0 ? 1.5f : 1.2f;
        } else {
            mc.timer.timerSpeed = 0.05f;
        }
    }

    private void applyMatrixSpeed() {
        double speed = 2;
        mc.player.motion.x *= speed;
        mc.player.motion.z *= speed;
        MoveUtil.StrafeMovement.oldSpeed *= speed;
    }

    private void handleTimerMode() {
        if (mc.player.isInWater() || mc.player.isInLava() || mc.player.isOnLadder()) {
            return;
        }

        float timerValue = 1;
        if (mc.player.fallDistance <= 0.1f) {
            timerValue = 1.34f;
        }
        if (mc.player.fallDistance > 1.0f) {
            timerValue = 0.6f;
        }

        if (MoveUtil.isMoving()) {
            mc.timer.timerSpeed = 1;
            if (mc.player.isOnGround()) {
                if (!mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.player.jump();
                }
            } else {
                mc.timer.timerSpeed = timerValue;
            }
        } else {
            mc.timer.timerSpeed = 1.0f;
        }
    }
    private void intave() {
        mc.gameSettings.keyBindJump.setPressed(false);
        if (MoveUtil.isMoving()) {
            if (mc.player.isOnGround()) {
                mc.player.jump();
                mc.timer.timerSpeed = 1.0f;
            }

            if (mc.player.getMotion().y > 0.003) {
                mc.player.getMotion().x *= 1.0015;
                mc.player.getMotion().z *= 1.0015;
                mc.timer.timerSpeed = 1.06f;
            }
        }
    }
    private void handleSunriseDamageMode() {
        double radians = MoveUtil.getDirection();

        if (MoveUtil.isMoving()) {
            if (mc.player.isOnGround()) {
                applySunriseGroundMotion(radians);
            } else if (mc.player.isInWater()) {
                applySunriseWaterMotion(radians);
            } else if (!mc.player.isOnGround()) {
                applySunriseAirMotion(radians);
            } else {
                applySunriseDefaultMotion(radians);
            }
        }
    }

    private void applySunriseGroundMotion(double radians) {
        mc.player.addVelocity(-MathHelper.sin(radians) * 9.5 / 24.5, 0, MathHelper.cos(radians) * 9.5 / 24.5);
        MoveUtil.setMotion(MoveUtil.getMotion());
    }

    private void applySunriseWaterMotion(double radians) {
        mc.player.addVelocity(-MathHelper.sin(radians) * 9.5 / 24.5, 0, MathHelper.cos(radians) * 9.5 / 24.5);
        MoveUtil.setMotion(MoveUtil.getMotion());
    }

    private void applySunriseAirMotion(double radians) {
        mc.player.addVelocity(-MathHelper.sin(radians) * 0.11 / 24.5, 0, MathHelper.cos(radians) * 0.11 / 24.5);
        MoveUtil.setMotion(MoveUtil.getMotion());
    }

    private void applySunriseDefaultMotion(double radians) {
        mc.player.addVelocity(-MathHelper.sin(radians) * 0.005 * MoveUtil.getMotion(), 0,
                MathHelper.cos(radians) * 0.005 * MoveUtil.getMotion());
        MoveUtil.setMotion(MoveUtil.getMotion());
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1;
        super.onDisable();
    }
}