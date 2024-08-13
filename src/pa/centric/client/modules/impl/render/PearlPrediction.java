package pa.centric.client.modules.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import org.lwjgl.opengl.GL11;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.helper.conduction;
import pa.centric.util.render.RenderUtils;
import pa.centric.events.Event;
import pa.centric.events.impl.render.EventRender;

@ModuleAnnotation(name = "Pearl Prediction", category = Type.Render)
public class PearlPrediction extends Function {
    @Override
    public void onEvent(Event event) {
        if (event instanceof EventRender && ((EventRender) event).isRender3D()) {

            RenderSystem.pushMatrix();
            RenderSystem.translated(-mc.getRenderManager().renderPosX(), -mc.getRenderManager().renderPosY(),-mc.getRenderManager().renderPosZ());
            RenderSystem.enableBlend();
            RenderSystem.disableTexture();
            RenderSystem.disableDepthTest();
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            RenderSystem.lineWidth(2);
            buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);

            for (Entity e : mc.world.getAllEntities()) {
                if (e instanceof EnderPearlEntity pearl) {
                    renderLine(pearl);
                }
            }
            buffer.finishDrawing();
            WorldVertexBufferUploader.draw(buffer);
            RenderSystem.enableDepthTest();
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            RenderSystem.popMatrix();
        }


    }

    private void renderLine(EnderPearlEntity pearl) {

        Vector3d pearlPosition = pearl.getPositionVec().add(0, 0, 0);
        Vector3d pearlMotion = pearl.getMotion();
        Vector3d lastPosition;

        for (int i = 0; i <= 300; i++) {
            lastPosition = pearlPosition;
            pearlPosition = pearlPosition.add(pearlMotion);
            pearlMotion = updatePearlMotion(pearl, pearlMotion);
            float[] colors = getLineColor(i);
            buffer.pos(lastPosition.x, lastPosition.y, lastPosition.z).color(colors[0], colors[1], colors[2], 1).endVertex();
            buffer.pos(pearlPosition.x, pearlPosition.y, pearlPosition.z).color(colors[0], colors[1], colors[2], 1).endVertex();
        }
    }

    private Vector3d updatePearlMotion(EnderPearlEntity pearl, Vector3d originalPearlMotion) {
        Vector3d pearlMotion = originalPearlMotion;
        if (pearl.isInWater()) {
            pearlMotion = pearlMotion.scale(0.8f);
        } else {
            pearlMotion = pearlMotion.scale(0.99f);
        }

        if (!pearl.hasNoGravity())
            pearlMotion.y -= pearl.getGravityVelocity();

        return pearlMotion;
    }

    private float[] getLineColor(int index) {
        int color = conduction.STYLE_MANAGER.getCurrentStyle().getColor(index * 2);
        return RenderUtils.IntColor.rgb(color);
    }
}
