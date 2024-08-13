package pa.centric.client.ui.clickgui.components.sets;

import pa.centric.client.helper.conduction;
import pa.centric.util.font.FontUtils;
import pa.centric.util.math.MathUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import pa.centric.client.modules.settings.imp.SliderSetting;
import pa.centric.client.ui.clickgui.components.ModuleComponent;
import pa.centric.client.ui.clickgui.components.Component;
import pa.centric.util.render.ColorUtil;
import pa.centric.util.render.RenderUtils;
import pa.centric.util.render.animation.AnimationMath;

import java.awt.*;

public class SliderComponent extends Component {

    public ModuleComponent object;
    public SliderSetting set;
    public boolean sliding;
    public float animatedVal;

    public SliderComponent(ModuleComponent object, SliderSetting set) {
        this.object = object;
        this.set = set;
        setting = set;
    }

    @Override
    public void draw(MatrixStack stack, int mouseX, int mouseY) {
        super.draw(stack, mouseX, mouseY);
        y-=6;
        x-=1;

        if (sliding) {
            float value = (mouseX - x - 90) / (width - 95) * (set.getMax() - set.getMin()) + set.getMin();
            value = (float) MathUtil.round(value, set.getIncrement());
            set.setValue(value);
        }

        float sliderWidth = ((set.getValue().floatValue() - set.getMin()) / (set.getMax() - set.getMin())) * (width - 95);
        animatedVal = AnimationMath.fast(animatedVal, sliderWidth, 10);
        height += 5;

            RenderUtils.Render2D.drawRoundedRect(x + 90, y + height / 2f + 3.7f, width - 95, 2.5f, 1, new Color(40, 40, 40).getRGB());
            RenderUtils.Render2D.drawGradientRound(x + 90, y + height / 2f + 3.5f, animatedVal, 2.5f, 1,
                    RenderUtils.reAlphaInt(ColorUtil.getColorStyle(0), 200),
                    RenderUtils.reAlphaInt(ColorUtil.getColorStyle(90), 200),
                    RenderUtils.reAlphaInt(ColorUtil.getColorStyle(180), 200),
                    RenderUtils.reAlphaInt(ColorUtil.getColorStyle(270), 200));
////        RenderUtils.Render2D.drawRoundedRect(x + 6 + animatedVal, y + height / 2f + 1, 5, 7, 1.5f, new Color(254, 254, 254).getRGB());
        RenderUtils.Render2D.drawRoundCircle(x + 88 + animatedVal, y + height / 2f + 4.5f, 5,
                RenderUtils.reAlphaInt(ColorUtil.getColorStyle(0), 200),
                RenderUtils.reAlphaInt(ColorUtil.getColorStyle(90), 200),
                RenderUtils.reAlphaInt(ColorUtil.getColorStyle(180), 200),
                RenderUtils.reAlphaInt(ColorUtil.getColorStyle(270), 200));

        FontUtils.sfbold[14].drawString(stack, set.getName(), x + 7.5f, y + height / 2f + 2.5f, -1);
        FontUtils.sfbold[13].drawString(stack, String.valueOf(set.getValue().floatValue()), x + width - 5.5f - FontUtils.sfbold[15].getWidth(String.valueOf(set.getValue().floatValue())), y + height / 2f - 4f, -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isHovered(mouseX, mouseY)) {
            sliding = true;
        }
    }

    @Override
    public void exit() {
        super.exit();
        sliding = false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        sliding = false;
    }

    @Override
    public void keyTyped(int keyCode, int scanCode, int modifiers) {

    }

    @Override
    public void charTyped(char codePoint, int modifiers) {
    }
}