package pa.centric.client.ui.clickgui.binds;

//import pa.centric.client.ui.clickgui.binds.BindWindow;
import net.minecraft.util.text.TextFormatting;
import org.joml.Vector4i;
import pa.centric.client.helper.conduction;
import pa.centric.util.font.FontUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import pa.centric.client.modules.settings.imp.BindSetting;
import pa.centric.client.ui.clickgui.components.ModuleComponent;
import pa.centric.client.ui.clickgui.components.Component;
import pa.centric.util.ClientUtils;
import pa.centric.util.render.ColorUtil;
import pa.centric.util.render.RenderUtils;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class BindComponent extends Component {

    public BindSetting option;
    public boolean bind;


    public BindComponent(ModuleComponent component, BindSetting option) {
        this.option = option;
        this.setting = option;
    }

    @Override
    public void draw(MatrixStack matrixStack, int mouseX, int mouseY) {

        height -= 3;

        String bindString = option.getKey() == 0 ? "NONE" : ClientUtils.getKey(option.getKey());

        if (bindString == null) {
            bindString = "";
        }

        float width = FontUtils.sfbold[14].getWidth(bindString) + 4;
        if (bind) {
            RenderUtils.Render2D.drawRoundOutline(x + 5, y-2, width, 10, 2,0.3f,
                    new Color(20, 20, 20).brighter().getRGB(),new Vector4i(
                            conduction.STYLE_MANAGER.getCurrentStyle().getColor(100),
                            conduction.STYLE_MANAGER.getCurrentStyle().getColor(100),
                            conduction.STYLE_MANAGER.getCurrentStyle().getColor(100),
                            conduction.STYLE_MANAGER.getCurrentStyle().getColor(100)
                    ));
        } else {
            RenderUtils.Render2D.drawRoundedCorner(x + 5, y-2, width, 10, 3,
                    new Color(20, 20, 20).brighter().brighter().getRGB());
        }
//        RenderUtils.Render2D.drawRoundedCorner(x + 5, y, width, 10, 2, bind ?
//                new Color(20, 18, 21).brighter().brighter().getRGB() : new Color(20, 20, 20).brighter().getRGB());
        FontUtils.sfbold[14].drawCenteredString(matrixStack, bindString, x + 5 + (width / 2), y+1.5f, -1);
        FontUtils.sfbold[14].drawString(matrixStack, option.getName(), x + 5 + width + 3, y + 1, -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (bind && mouseButton > 1) {
            option.setKey(-100 + mouseButton);
            bind = false;
        }
        if (isHovered(mouseX, mouseY) && mouseButton == 0) {
            bind = true;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

    }

    @Override
    public void keyTyped(int keyCode, int scanCode, int modifiers) {
        if (bind) {
            if (keyCode == 261) {
                option.setKey(0);
                bind = false;
                return;
            }
            option.setKey(keyCode);
            bind = false;
        }
    }

    @Override
    public void charTyped(char codePoint, int modifiers) {

    }
}
