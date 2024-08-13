package pa.centric.util.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static com.mojang.blaze3d.systems.RenderSystem.glUniform1;
import static pa.centric.util.IMinecraft.mc;

public class Gaus {

    private static final ShaderUtil gaussianBlur = new ShaderUtil("blur");
    private static final ShaderUtil gaussianBlur2 = new ShaderUtil("blur3");
    private static Framebuffer framebuffer = new Framebuffer(1, 1, false, false);

    private static void setupUniforms(ShaderUtil shader, float dir1, float dir2, float radius, float[] overlayColor) {
        shader.setUniform("textureIn", 0);
        shader.setUniformf("texelSize", 1.0F / (float) mc.getMainWindow().getWidth(), 1.0F / (float) mc.getMainWindow().getHeight());
        shader.setUniformf("direction", dir1, dir2);
        shader.setUniformf("radius", radius);
        shader.setUniformf("overlayColor", overlayColor[0], overlayColor[1], overlayColor[2], overlayColor[3]); // Установка цвета overlay

        final FloatBuffer weightBuffer = BufferUtils.createFloatBuffer(256);
        for (int i = 0; i <= radius; i++) {
            weightBuffer.put(calculateGaussianValue(i, radius / 2));
        }

        weightBuffer.rewind();
        glUniform1(shader.getUniform("weights"), weightBuffer);
    }

    public static void startBlur() {
        StencilUtil.initStencilToWrite();
    }

    public static void endBlur(float radius, float compression, float[] overlayColor) {
        StencilUtil.readStencilBuffer(1);

        framebuffer = ShaderUtil.createFrameBuffer(framebuffer);

        framebuffer.framebufferClear(false);
        framebuffer.bindFramebuffer(false);
        gaussianBlur.attach();
        setupUniforms(gaussianBlur, compression, 0, radius, overlayColor);

        GlStateManager.bindTexture(mc.getFramebuffer().framebufferTexture);
        ShaderUtil.drawQuads();
        framebuffer.unbindFramebuffer();
        gaussianBlur.detach();

        mc.getFramebuffer().bindFramebuffer(false);
        gaussianBlur2.attach();
        gaussianBlur2.setUniformf("direction", 0, compression);

        GlStateManager.bindTexture(framebuffer.framebufferTexture);
        ShaderUtil.drawQuads();
        gaussianBlur2.detach();

        StencilUtil.uninitStencilBuffer();
        GlStateManager.color4f(-1, -1, 1, -1);
        GlStateManager.bindTexture(0);
    }

    public static void blur(float radius, float compression, float[] overlayColor) {
        framebuffer = ShaderUtil.createFrameBuffer(framebuffer);

        framebuffer.framebufferClear(false);
        framebuffer.bindFramebuffer(false);
        gaussianBlur.attach();
        setupUniforms(gaussianBlur, compression, 0, radius, overlayColor);

        GlStateManager.bindTexture(mc.getFramebuffer().framebufferTexture);
        ShaderUtil.drawQuads();
        framebuffer.unbindFramebuffer();
        gaussianBlur.detach();

        mc.getFramebuffer().bindFramebuffer(false);
        gaussianBlur2.attach();
        setupUniforms(gaussianBlur2, 0, compression, radius, overlayColor);

        GlStateManager.bindTexture(framebuffer.framebufferTexture);
        ShaderUtil.drawQuads();
        gaussianBlur2.detach();

        GlStateManager.color4f(-1, -1, 1, -1);
        GlStateManager.bindTexture(0);
    }

    public static float calculateGaussianValue(float x, float sigma) {
        double output = 1.0 / Math.sqrt(2.0 * Math.PI * (sigma * sigma));
        return (float) (output * Math.exp(-(x * x) / (2.0 * (sigma * sigma))));
    }

}
