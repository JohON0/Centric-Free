package pa.centric.client.modules.impl.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import pa.centric.client.helper.conduction;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.util.render.BloomHelper;
import pa.centric.util.render.ColorUtil;
import pa.centric.util.render.RenderUtils;
import pa.centric.events.Event;
import pa.centric.events.impl.render.EventRender;

import java.awt.*;
import java.util.Iterator;

@ModuleAnnotation(
        name = "Arrows",
        category = Type.Render
)
public class ArrowsFunction extends Function {
    public void onEvent(Event event) {
        if (event instanceof EventRender) {
            EventRender render = (EventRender)event;
            if (render.isRender2D()) {
                this.render2D(render);
            }
        }

    }

    private void render2D(EventRender render) {
        Iterator var2 = mc.world.getPlayers().iterator();

        while(var2.hasNext()) {
            Entity entity = (Entity)var2.next();
            if (entity != mc.player && entity.botEntity) {
                float angle = getAngle(entity);
                double x2 = (double)(50.0F * MathHelper.cos(Math.toRadians((double)angle)) + (float)render.scaledResolution.scaledWidth() / 2.0F);
                double y2 = (double)(50.0F * MathHelper.sin(Math.toRadians((double)angle)) + (float)render.scaledResolution.scaledHeight() / 2.0F);
                GlStateManager.pushMatrix();
                GlStateManager.disableBlend();
                GlStateManager.translated(x2, y2, 0.0D);
                GlStateManager.rotatef(angle, 0.0F, 0.0F, 1.0F);
                int clr = conduction.FRIEND_MANAGER.isFriend(entity.getName().getString()) ? ColorUtil.rgba(0, 255, 0, 255) : conduction.STYLE_MANAGER.getCurrentStyle().getColor(entity.getEntityId());
                RenderUtils.Render2D.drawImage(new ResourceLocation("centric/images/arrows.png"), -4.0F, -2.0F, 16.0F, 16.0F, -1);
                BloomHelper.registerRenderCall(() -> {
                    GlStateManager.pushMatrix();
                    GlStateManager.disableBlend();
                    GlStateManager.translated(x2, y2, 0.0D);
                    GlStateManager.rotatef(angle, 0.0F, 0.0F, 1.0F);
                    RenderUtils.Render2D.drawTriangle(-11.0F, 1.0F, 5.0F, 7.0F, new Color(clr));
                    GlStateManager.enableBlend();
                    GlStateManager.popMatrix();
                });
                GlStateManager.enableBlend();
                GlStateManager.popMatrix();
            }
        }

    }

    private static float getAngle(Entity entity) {
        double x = entity.lastTickPosX + (entity.getPosX() - entity.lastTickPosX) * (double)mc.getRenderPartialTicks() - mc.getRenderManager().info.getProjectedView().getX();
        double z = entity.lastTickPosZ + (entity.getPosZ() - entity.lastTickPosZ) * (double)mc.getRenderPartialTicks() - mc.getRenderManager().info.getProjectedView().getZ();
        double cos = (double)MathHelper.cos((double)mc.player.rotationYaw * 0.017453292519943295D);
        double sin = (double)MathHelper.sin((double)mc.player.rotationYaw * 0.017453292519943295D);
        double rotY = -(z * cos - x * sin);
        double rotX = -(x * cos + z * sin);
        float angle = (float)(Math.atan2(rotY, rotX) * 180.0D / 3.141592653589793D);
        return angle;
    }
}
