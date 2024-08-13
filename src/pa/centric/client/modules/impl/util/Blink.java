package pa.centric.client.modules.impl.util;


import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.settings.imp.BooleanOption;
import pa.centric.client.modules.settings.imp.SliderSetting;
import pa.centric.util.font.FontUtils;
import pa.centric.util.misc.TimerUtil;
import pa.centric.util.render.ColorUtil;
import pa.centric.util.render.RenderUtils;
import pa.centric.util.render.animation.AnimationMath;
import pa.centric.events.Event;
import pa.centric.events.impl.packet.EventPacket;
import pa.centric.events.impl.player.EventMotion;
import pa.centric.events.impl.render.EventRender;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

@ModuleAnnotation(name = "Blink", category = Type.Util)
public final class Blink extends Function {

    private final CopyOnWriteArrayList<IPacket> packets = new CopyOnWriteArrayList<>();
    private BooleanOption delay = new BooleanOption("Пульсации", false);
    private SliderSetting delayS = new SliderSetting("Задержка", 100, 50, 1000, 50).setVisible(() -> delay.get());


    public Blink() {
        super();
        addSettings(delay, delayS);
    }

    private long started;

    @Override
    protected void onEnable() {
        super.onEnable();
        started = System.currentTimeMillis();
        lastPos = mc.player.getPositionVec();
    }

    float animation;

    public TimerUtil timerUtil = new TimerUtil();

    Vector3d lastPos = new Vector3d(0, 0, 0);

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventRender e) {
            if (e.isRender3D()) {
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                {
                    RenderUtils.Render3D.drawBox(AxisAlignedBB.fromVector(lastPos).expand(0, 1, 0).offset(-mc.getRenderManager().info.getProjectedView().x, -mc.getRenderManager().info.getProjectedView().y, -mc.getRenderManager().info.getProjectedView().z).offset(-0.5f, 0, -0.5f).grow(-0.2, 0, -0.2), -1);
                }
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
            if (e.isRender2D()) {
                float width = 120;
                float height = 10;
                float state = 1 - (System.currentTimeMillis() - started) / 30000F;

                animation = AnimationMath.lerp(animation, width * state, 10);

                RenderUtils.Render2D.drawShadow((e.scaledResolution.getScaledWidth() / 2f) - (width / 2f), 25, width, height, 10, new Color(23, 23, 23, 200).getRGB());
                RenderUtils.Render2D.drawRect((e.scaledResolution.getScaledWidth() / 2f) - (width / 2f), 25, width, height, new Color(23, 23, 23, 200).getRGB());
                RenderUtils.Render2D.drawShadow((e.scaledResolution.getScaledWidth() / 2f) - (width / 2f), 25, animation, height, 10, ColorUtil.getColorStyle(10));
                RenderUtils.Render2D.drawRect((e.scaledResolution.getScaledWidth() / 2f) - (width / 2f), 25, animation, height, ColorUtil.getColorStyle(10));

                FontUtils.sfbold[14].drawCenteredString(e.matrixStack, "Blink", (e.scaledResolution.getScaledWidth() / 2f), 5 + height, -1);

            }
        }

        if (event instanceof EventPacket e) {
            if (mc.player != null && mc.world != null && !mc.isSingleplayer() && !mc.player.getShouldBeDead()) {
                if (e.isSendPacket()) {
                    packets.add(e.getPacket());
                    e.setCancel(true);
                }
            } else toggle();
        }

        if (event instanceof EventMotion e) {
            if ((System.currentTimeMillis() - started) >= 29900) {
                toggle();
            }
            if (delay.get() && timerUtil.hasTimeElapsed(delayS.getValue().longValue())) {
                for (IPacket packet : packets) {
                    mc.player.connection.getNetworkManager().sendPacketWithoutEvent(packet);
                }
                packets.clear();
                started = System.currentTimeMillis();
                timerUtil.reset();
            }
        }


    }


    @Override
    public void onDisable() {
        super.onDisable();
        for (IPacket packet : packets) {
            mc.player.connection.sendPacket(packet);
        }

        packets.clear();
    }


}