package pa.centric.client.ui.clickgui.components.sets;

import pa.centric.client.helper.conduction;
import pa.centric.util.font.FontUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import pa.centric.client.modules.settings.imp.ModeSetting;
import pa.centric.client.ui.clickgui.components.ModuleComponent;
import pa.centric.client.ui.clickgui.components.Component;
import pa.centric.util.render.ColorUtil;
import pa.centric.util.render.RenderUtils;

import java.awt.*;

public class ModeComponent extends Component {

    public ModeSetting set;
    public ModuleComponent object;

    public ModeComponent(ModuleComponent object, ModeSetting set) {
        this.object = object;
        this.set = set;
        setting = set;
    }

    @Override
    public void draw(MatrixStack stack, int mouseX, int mouseY) {
        super.draw(stack, mouseX, mouseY);
        int offset = 0;
        float offsetY;
        int lines = 1;
        float size = 140;

        for (String mode : set.modes) {
            float preOffset = offset + FontUtils.sfbold[14].getWidth(mode) + 3;
            if (preOffset > size) {
                lines++;
                offset = 0;
            }
            offset += FontUtils.sfbold[14].getWidth(mode) + 3;
        }

        height += 13;
        FontUtils.sfbold[15].drawString(stack, set.getName() + ":", x + 7, y + height / 2f - 14, -1);
        RenderUtils.Render2D.drawRoundedRect(x + 6.5f, y + 10, size + 5, 13 * lines, 4, new Color(23, 23, 23, 255).getRGB());
        height += 13 * (lines - 1);
        offset = 0;
        offsetY = 0;
        int i = 0;
        for (String mode : set.modes) {
            float preOff = offset + FontUtils.sfbold[14].getWidth(mode) + 3;
            if (preOff > size) {
                offset = 0;
                offsetY += 13;
            }
            if (set.getIndex() == i) {
                RenderUtils.Render2D.drawGradientRound(x + 10.5f + offset, y + 13 + offsetY,FontUtils.sfbold[14].getWidth(mode),7, 2,
                        RenderUtils.reAlphaInt(ColorUtil.getColorStyle(0), 200),
                        RenderUtils.reAlphaInt(ColorUtil.getColorStyle(90), 200),
                        RenderUtils.reAlphaInt(ColorUtil.getColorStyle(180), 200),
                        RenderUtils.reAlphaInt(ColorUtil.getColorStyle(270), 200));
            } else {
                RenderUtils.Render2D.drawGradientRound(x + 10.5f + offset, y + 13 + offsetY,FontUtils.sfbold[14].getWidth(mode),7f, 2, ColorUtil.rgba(35, 35, 35, 255),ColorUtil.rgba(35, 35, 35, 255),ColorUtil.rgba(35, 35, 35, 255),ColorUtil.rgba(35, 35, 35, 255));
            }
            FontUtils.sfbold[14].drawString(stack, mode, x + 10.5f + offset, y + 15 + offsetY, new Color(255, 255, 255, 255).getRGB());
            offset += FontUtils.sfbold[14].getWidth(mode) + 3;
            i++;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        float offset = 0;
        float offsetY = 0;
        int i = 0;
        float size = 140;

        for (String mode : set.modes) {
            float preOff = offset + FontUtils.sfbold[14].getWidth(mode) + 3;
            if (preOff > size) {
                offset = 0;
                offsetY += 13;
            }

            if (RenderUtils.isInRegion(mouseX, mouseY, x + 10.5f + offset, y + 12 + offsetY, FontUtils.sfbold[14].getWidth(mode), FontUtils.sfbold[14].getFontHeight() / 2f + 3)) {
                set.setIndex(i);
            }

            offset += FontUtils.sfbold[14].getWidth(mode) + 3;
            i++;
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