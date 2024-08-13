package pa.centric.client.modules.impl.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.util.render.BloomHelper;
import pa.centric.util.render.OutlineUtils;
import pa.centric.util.render.ShaderUtil;
import pa.centric.events.Event;
import pa.centric.events.impl.player.EventModelRender;
import pa.centric.events.impl.render.EventRender;

@ModuleAnnotation(name = "GlowESP", category = Type.Render)
public class GlowESP extends Function {

    public static Framebuffer framebuffer = new Framebuffer(1,1,true,false);

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventModelRender e) {
            framebuffer.bindFramebuffer(false);
            e.render();
            framebuffer.unbindFramebuffer();
            mc.getFramebuffer().bindFramebuffer(true);
        }

        if (event instanceof EventRender e) {
            if (e.isRender2D()) {
                GlStateManager.enableBlend();

                OutlineUtils.registerRenderCall(() -> {
                    framebuffer.bindFramebufferTexture();
                    ShaderUtil.drawQuads();
                });

                BloomHelper.registerRenderCallHand(() -> {
                    framebuffer.bindFramebufferTexture();
                    ShaderUtil.drawQuads();
                });



                OutlineUtils.draw(1, -1);
                BloomHelper.drawC(10, 1, true, -1, 2);
                OutlineUtils.setupBuffer(framebuffer);

                mc.getFramebuffer().bindFramebuffer(true);
            }
        }

    }
}
