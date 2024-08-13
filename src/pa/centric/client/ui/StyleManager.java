package pa.centric.client.ui;

import net.minecraft.util.math.MathHelper;
import pa.centric.client.ui.midnight.Style;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StyleManager {
    public List<Style> styles = new ArrayList<>();
    private Style currentStyle = null;

    public void init() {
        styles.addAll(Arrays.asList(
                new Style("Тайна звездного неба", HexColor.toColor("#483D8B"), HexColor.toColor("#FF69B4")),
                new Style("Звездный портал", HexColor.toColor("#000080"), HexColor.toColor("#FFD700")),
                new Style("Пурпурный вихрь", HexColor.toColor("#800080"), HexColor.toColor("#FF4500")),
                new Style("Лунная симфония", HexColor.toColor("#D6A5D6"), HexColor.toColor("#4169E1")),
                new Style("Искрящая магия звезд", HexColor.toColor("#FFD700"), HexColor.toColor("#000080")),
                new Style("Жемчужное море", HexColor.toColor("#00CED1"), HexColor.toColor("#FFD700")),
                new Style("Ледяное крыло", HexColor.toColor("#B0E0E6"), HexColor.toColor("#F0FFFF")),
                new Style("Астольфо", HexColor.toColor("#00CED1"), HexColor.toColor("#FF1493"))
        ));
        currentStyle = styles.get(0);
    }

    public static Color astolfo(float yDist, float yTotal, float saturation, float speedt) {
        float speed = 1800f;
        float hue = (System.currentTimeMillis() % (int) speed) + (yTotal - yDist) * speedt;
        while (hue > speed) {
            hue -= speed;
        }
        hue /= speed;
        if (hue > 0.5) {
            hue = 0.5F - (hue - 0.5f);
        }
        hue += 0.5F;
        return Color.getHSBColor(hue, saturation, 1F);
    }

    public void setCurrentStyle(Style style) {
        currentStyle = style;
    }

    public Style getCurrentStyle() {
        return currentStyle;
    }

    public static class HexColor {
        public static int toColor(String hexColor) {
            int argb = Integer.parseInt(hexColor.substring(1), 16);
            return reAlphaInt(argb, 255);
        }

        public static int reAlphaInt(final int color, final int alpha) {
            return (MathHelper.clamp(alpha, 0, 255) << 24) | (color & 16777215);
        }
    }
}
