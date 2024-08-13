
package pa.centric.client.ui.clickgui.theme;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import pa.centric.client.helper.conduction;
import pa.centric.client.ui.midnight.Style;
import pa.centric.util.render.RenderUtils;
import pa.centric.util.render.animation.AnimationMath;

public class DrawThemes {
    public List<ThemeObjects> objects = new ArrayList<ThemeObjects>();
    float animation;
    boolean colorOpen;
    public float openAnimation;
    public int edit;
    float x;
    float y;
    float width;
    float height;
    float hsb;
    float satur;
    float brithe;
    private final DrawThemes themeDrawing;
    boolean drag;

    public DrawThemes() {
        Style custom = conduction.STYLE_MANAGER.styles.get(conduction.STYLE_MANAGER.styles.size() - 1);
        for (Style style : conduction.STYLE_MANAGER.styles) {
            if (style.name.equalsIgnoreCase("Ñâ\u043e\u0439 \u0446\u0432\u0435\u0442")) continue;
            this.objects.add(new ThemeObjects(style));
        }
        float[] rgb2 = RenderUtils.IntColor.rgb(custom.colors[this.edit]);
        float[] hsb = Color.RGBtoHSB((int)(rgb2[0] * 255.0f), (int)(rgb2[1] * 255.0f), (int)(rgb2[2] * 255.0f), null);
        this.hsb = hsb[0];
        this.satur = hsb[1];
        this.brithe = hsb[2];
        this.themeDrawing = this;

    }

    public void draw(MatrixStack stack, int mouseX, int mouseY, float x, float y, float width2, float height2) {
        this.x = x;
        this.y = y + 20.0f;
        this.width = width2;
        this.height = height2;
        this.openAnimation = AnimationMath.lerp(this.openAnimation, this.colorOpen ? 1.0f : 0.0f, 15.0f);
        float rowLimit = 1.0f;
        float offset = 2.1f;
        float off = 10.0f;
        for (int i = 0; i < this.themeDrawing.objects.size(); ++i) {
            ThemeObjects object = this.themeDrawing.objects.get(i);
            object.width = 100.0f;
            object.height = 20.0f;
            object.x = x + 523.0f + (float)i % rowLimit * (object.width + offset);
            object.y = y - 275.0f + off + offset * ((float)i / rowLimit) + 290.0f;
            object.draw(stack, mouseX, mouseY);
            if ((float)i % rowLimit != rowLimit - 1.0f) continue;
            off += offset + 17.0f;
        }
        for (ThemeObjects object : this.objects) {
            object.draw(stack, mouseX, mouseY);
        }
        Style custom = conduction.STYLE_MANAGER.styles.get(conduction.STYLE_MANAGER.styles.size() - 1);
        this.animation = AnimationMath.lerp(this.animation, conduction.STYLE_MANAGER.getCurrentStyle() == custom ? 1.0f : (RenderUtils.isInRegion((double)mouseX, (double)mouseY, x + 10.0f, y + height2 - 65.0f, width2 - 20.0f, 50.0f) ? 0.5f : 0.0f), 5.0f);
    }

    public void click(int mouseX, int mouseY, int button) {
        if (RenderUtils.isInRegion(mouseX, mouseY, this.x + 10.0f, this.y + this.height - 65.0f, this.width - 20.0f, 50.0f) && button == 0) {
            Style c = conduction.STYLE_MANAGER.styles.get(conduction.STYLE_MANAGER.styles.size() - 1);
            conduction.STYLE_MANAGER.setCurrentStyle(c);
        }
        for (ThemeObjects object : this.objects) {
            if (!RenderUtils.isInRegion(mouseX, mouseY, object.x, object.y, object.width, object.height)) continue;
            conduction.STYLE_MANAGER.setCurrentStyle(object.style);
        }
    }
}

