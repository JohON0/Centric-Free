package pa.centric.client.ui.themeui;

import pa.centric.util.render.ColorUtil;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;

public class UITheme {
    public static int guibgColor;
    public static int lineColor;
    public static int typeColor;
    public static String avatar;
    public static int textColor;
    public static int gradientline1;
    public static int gradientline2;
    public static TextFormatting textwtColor;
    public UITheme(boolean true_or_false) {
        if (true_or_false) {
            lineColor = ColorUtil.rgba(55,55,55,255);
            typeColor = ColorUtil.rgba(15,15,15,255);
            guibgColor = new Color(21, 21, 21, 250).getRGB();
            textColor = ColorUtil.rgba(240,240,240,255);
            gradientline1 = ColorUtil.rgba(200,200,200,255);
            gradientline2 = ColorUtil.rgba(50,50,50,255);
            textwtColor = TextFormatting.WHITE;
            avatar = "crystalcc/images/clickgui/profile/black.png";
        } else {
            lineColor = ColorUtil.rgba(158,158,158,255);
            typeColor = ColorUtil.rgba(230,230,230,255);
            guibgColor = ColorUtil.rgba(205,205,205,200);
            textColor = ColorUtil.rgba(10,0,10,255);
            gradientline1 = ColorUtil.rgba(50,50,50,255);
            gradientline1 = ColorUtil.rgba(200,200,200,255);

            textwtColor = TextFormatting.BLACK;
            avatar = "crystalcc/images/clickgui/profile/white.png";

        }
    }
    public void setTheme(boolean n) {
        new UITheme(n);
    }
}
