package pa.centric.client.ui.clickgui.theme;

import pa.centric.client.ui.midnight.Style;
import pa.centric.util.font.FontUtils;
import pa.centric.client.helper.conduction;
import com.mojang.blaze3d.matrix.MatrixStack;
import pa.centric.util.render.BloomHelper;
import pa.centric.util.render.RenderUtils;
import pa.centric.util.render.animation.AnimationMath;
import org.joml.Vector4i;

import java.awt.*;

public class ThemeObjects {

    public float x, y, width, height;
    public Style style;
    public float anim;

    public ThemeObjects(Style style) {
        this.style = style;
    }

    public void draw(MatrixStack stack, int mouseX, int mouseY) {
        RenderUtils.Render2D.drawRoundedCorner(x-20, y, width+40, height, 8, new Color(10, 10, 10, 255).getRGB());
        anim = AnimationMath.lerp(anim,  conduction.STYLE_MANAGER.getCurrentStyle() == style ? 0.6f : RenderUtils.isInRegion(mouseX, mouseY, x, y, width, height) ? 0.6f : 0, 5);
        //RenderUtils.Render2D.drawRoundedCorner(x, y, width, height, 8, new Color(23, 23, 23, 255).getRGB());

        Vector4i colors = new Vector4i(style.colors[0], style.colors[0], style.colors[1], style.colors[1]);

        Vector4i finalColors = colors;

        BloomHelper.registerRenderCall(() -> FontUtils.sfbold[16].drawString(stack, style.name, x-15, y + height - 12,(int) (255 * anim)));

        FontUtils.sfbold[16].drawString(stack, style.name, x-15, y + height - 12,-1);

                RenderUtils.Render2D.drawShadow(x+width-10, y + height - 13, 20, 8, 2,
                        RenderUtils.reAlphaInt(finalColors.x, (int) (255 * anim)),
                        RenderUtils.reAlphaInt(finalColors.y, (int) (255 * anim)),
                        RenderUtils.reAlphaInt(finalColors.z, (int) (255 * anim)),
                        RenderUtils.reAlphaInt(finalColors.w, (int) (255 * anim))
                );
//                RenderUtils.Render2D.drawShadow(x+width+5, y + height - 15, 11, 11, 5,
//                        RenderUtils.reAlphaInt(finalColors.z, (int) (255 * anim))
//                );
        RenderUtils.Render2D.drawGradientRound(x+width-10, y + height - 13, 20, 8, 2, finalColors.x,finalColors.y,finalColors.z,finalColors.w);
//        RenderUtils.Render2D.drawRoundedCorner(x+width+5, y + height - 15, 10, 10, 2, finalColors.z);
    }
}