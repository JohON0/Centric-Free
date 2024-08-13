package pa.centric.client.ui.clickgui.components;

import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.glfw.GLFW;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.settings.imp.*;
//import pa.centric.client.ui.clickgui.binds.BindWindow;
import pa.centric.client.ui.clickgui.binds.BindComponent;
import pa.centric.client.ui.clickgui.components.sets.*;
import pa.centric.util.ClientUtils;
import pa.centric.util.font.FontUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import pa.centric.client.modules.settings.Setting;
import pa.centric.client.helper.conduction;
import pa.centric.util.render.BloomHelper;
import pa.centric.util.render.RenderUtils;
import pa.centric.util.render.animation.AnimationMath;

import java.awt.*;
import java.util.ArrayList;
import java.util.Locale;

public class ModuleComponent extends Component {

    public ArrayList<Component> object = new ArrayList<>();
    public Function feature;
    public float animation;
    public static ModuleComponent binding;

    @Override
    public void exit() {
        super.exit();
        for (Component object1 : object) {
            object1.exit();
        }
    }

    public ModuleComponent(Function feature) {
        this.feature = feature;
        for (Setting setting : feature.settingList) {
            if (setting instanceof BooleanOption) {
                BooleanOption option = (BooleanOption) setting;
                object.add(new BooleanComponent(this, option));
            }
            if (setting instanceof SliderSetting) {
                SliderSetting option = (SliderSetting) setting;
                object.add(new SliderComponent(this, option));
            }
            if (setting instanceof ModeSetting) {
                ModeSetting option = (ModeSetting) setting;
                object.add(new ModeComponent(this, option));
            }
            if (setting instanceof MultiBoxSetting) {
                MultiBoxSetting option = (MultiBoxSetting) setting;
                object.add(new MultiComponent(this, option));
            }
            if (setting instanceof BindSetting) {
                BindSetting option = (BindSetting) setting;
                object.add(new BindComponent(this, option));
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        conduction.CLICK_GUI.searching = false;
        for (Component object1 : object) {
            object1.mouseClicked(mouseX, mouseY, mouseButton);
        }
        if (isHovered(mouseX, mouseY, 23)) {
            if (mouseButton == 0)
                feature.toggle();
        }
        if (RenderUtils.isInRegion(mouseX, mouseY, x + 5, y, width - 10, 20)) {
            if (mouseButton == 2) {
                binding = this;
            }
        }
    }


    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        for (Component object1 : object) {
            object1.mouseReleased(mouseX, mouseY, mouseButton);
        }
        if (binding == this && mouseButton > 2) {
            feature.bind = -100 + mouseButton;
            conduction.NOTIFICATION_MANAGER.add("Модуль " + TextFormatting.GRAY + binding.feature.name + TextFormatting.WHITE + " был забинжен на кнопку " + ClientUtils.getKey(-100 + mouseButton), "Module", 5);
            binding = null;
        }
    }

    @Override
    public void keyTyped(int keyCode, int scanCode, int modifiers) {
        for (Component object : object) {
            if (object instanceof BindComponent m) {
                if (m.bind) {
                    if (keyCode == GLFW.GLFW_KEY_DELETE) {
                        m.option.setKey(0);
                        m.bind = false;
                        continue;
                    }
                    m.option.setKey(keyCode);
                    //conduction.NOTIFICATION_MANAGER.add("Функция " + TextFormatting.GRAY + m.setting.getName().name + TextFormatting.WHITE + " была забинжена на кнопку " + ClientUtils.getKey(keyCode).toUpperCase(), "Module", 5);
                    m.bind = false;
                }
            }
            // Call the keyTyped method on the Component object if it's not a BindComponent
            if (object != null) {
                object.keyTyped(keyCode, scanCode, modifiers);
            }
        }
    }

    @Override
    public void charTyped(char codePoint, int modifiers) {
    }

    @Override
    public void draw(MatrixStack stack, int mouseX, int mouseY) {
        super.draw(stack, mouseX, mouseY);
        animation = AnimationMath.fast(animation, feature.state ? 8 : 0, 8f);

        if (!feature.settingList.isEmpty()) {
            RenderUtils.Render2D.drawRoundedCorner(x, y, width, height, 8, new Color(23, 23, 23, 255).getRGB());
            RenderUtils.Render2D.drawRoundedRect(x + 3, y + 20, width - 6, height - 23, 4, new Color(21, 21, 21, 255).getRGB());
            FontUtils.sfbold[16].drawString(stack, feature.name, x + 8, y + 9, feature.state ? RenderUtils.reAlphaInt(conduction.STYLE_MANAGER.getCurrentStyle().getColor(100), (int) (255 * animation)) : new Color(255, 255, 255, 255).getRGB());
        } else {
            RenderUtils.Render2D.drawRoundedCorner(x, y, width, height, 8, new Color(25, 25, 25, 255).getRGB());
            FontUtils.sfbold[16].drawString(stack, feature.name, x + 8, y + 9 + 1.5f, feature.state ? RenderUtils.reAlphaInt(conduction.STYLE_MANAGER.getCurrentStyle().getColor(100), (int) (255 * animation)) : new Color(255, 255, 255, 255).getRGB());
        }
        String key = ClientUtils.getKey(feature.bind);
        if (key != null) {
            RenderUtils.Render2D.drawRoundedCorner(x + width - 20 - FontUtils.sfbold[14].getWidth(key) + 5, y + 5, 10 + FontUtils.sfbold[14].getWidth(key), 10,
                    new Vector4f(3,3,3,3),
                    new Color(20,20,20,255).getRGB());
            FontUtils.sfbold[16].drawCenteredString(stack, key.toLowerCase(Locale.ROOT), x + width - 20 - FontUtils.sfbold[14].getWidth(key) + 5 + (10 + FontUtils.sfbold[14].getWidth(key)) / 2, y + 8, -1);
        }

        drawObjects(stack, mouseX, mouseY);
    }

    public void drawObjects(MatrixStack stack, int mouseX, int mouseY) {
        float offset = 3;
        for (Component object : object) {
            if (object.setting.visible()) {
                object.x = x;
                object.y = y + 22 + offset;
                object.width = 160;
                object.height = 16;
                object.draw(stack, mouseX, mouseY);
                offset += object.height;
            }
        }
    }
}