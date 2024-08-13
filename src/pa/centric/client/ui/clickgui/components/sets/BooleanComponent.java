package pa.centric.client.ui.clickgui.components.sets;

import pa.centric.client.helper.conduction;
import pa.centric.util.font.FontUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import pa.centric.client.modules.settings.imp.BooleanOption;
import pa.centric.client.ui.clickgui.components.ModuleComponent;
import pa.centric.client.ui.clickgui.components.Component;
import pa.centric.util.render.ColorUtil;
import pa.centric.util.render.RenderUtils;
import pa.centric.util.render.animation.AnimationMath;

import java.awt.*;

public class BooleanComponent extends Component {

    public ModuleComponent object;
    public BooleanOption set;
    public float enabledAnimation;

    public BooleanComponent(ModuleComponent object, BooleanOption set) {
        this.object = object;
        this.set = set;
        setting = set;
    }

    @Override
    public void draw(MatrixStack stack, int mouseX, int mouseY) {
        super.draw(stack, mouseX, mouseY);
        y -= 2;
        height += 1;
        FontUtils.sfbold[15].drawString(stack, set.getName(), x + 7, y + height / 2f - FontUtils.sfbold[13].getFontHeight() / 2f + 0.5f, -1);
        //RenderUtils.Render2D.drawRoundedCorner(x + width - 26, y + 2.5f, 16, 8, 5, set.get() ? new Color(255, 255, 255, 130).getRGB() : new Color(37, 37, 37).getRGB());
        RenderUtils.Render2D.drawRoundCircle(x + width - 25+10, y + 6.5f, 11,new Color(50, 50, 50, 200).getRGB());
        RenderUtils.Render2D.drawRoundCircle(x + width - 25+10, y + 6.5f, 7, set.get() ? RenderUtils.reAlphaInt(ColorUtil.getColorStyle(90), 200) : new Color(10, 10, 10, 200).getRGB());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            if (isHovered(mouseX, mouseY)) {
                set.toggle();
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
    }

    @Override
    public void keyTyped(int keyCode, int scanCode, int modifiers) {
    }

    @Override
    public void charTyped(char codePoint, int modifiers) {
    }
}