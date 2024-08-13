package pa.centric.client.ui.clickgui.configs;

import pa.centric.client.config.ConfigManager;
import pa.centric.util.font.FontUtils;
import pa.centric.util.misc.TimerUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import pa.centric.client.helper.conduction;
import pa.centric.client.ui.clickgui.Gui;
import pa.centric.util.render.RenderUtils;
import pa.centric.util.render.SmartScissor;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.RandomStringUtils;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class ConfigDraw {

    public static ConfigDraw configDrawing = new ConfigDraw();
    public CopyOnWriteArrayList<ConfigComponent> objects = new CopyOnWriteArrayList<>();

    public ConfigDraw() {
        objects.clear();
        for (String cfg : conduction.CONFIG_MANAGER.getAllConfigurations()) {
            objects.add(new ConfigComponent(cfg));
        }
        configDrawing = this;
    }

    public TimerUtil refresh = new TimerUtil();
    float x, y, width, height;
    public String search = "";
    public boolean searching;

    public void draw(MatrixStack stack, int mouseX, int mouseY, float x, float y, float width, float height) {
        if (refresh.hasTimeElapsed(1000)) {
            for (String cfg : conduction.CONFIG_MANAGER.getAllConfigurations()) {
                if (!objects.stream().map(o -> o.staticF).toList().contains(cfg))
                    objects.add(new ConfigComponent(cfg));
            }
            objects.removeIf(object -> conduction.CONFIG_MANAGER.findConfig(object.staticF) == null);
            refresh.reset();
        }

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        float offsetX = 10;
        float offsetY = 25 + 10 + Gui.scrollingOut;

        SmartScissor.push();
        SmartScissor.setFromComponentCoordinates(x, y + 33, width, height - 33);
        float size = 0;
        for (ConfigComponent object : objects) {
            if (!object.cfg.toLowerCase().contains(search.toLowerCase())) continue;
            object.x = x + offsetX;
            object.y = y + offsetY;
            object.width = 283 / 2f;
            object.height = 57 / 2f;
            RenderUtils.Render2D.drawRoundedCorner(object.x, object.y, object.width, object.height, 8, new Color(23, 23, 23, 255).getRGB());
            offsetX += object.width + 42 / 2f;
            if (offsetX > (!conduction.FUNCTION_MANAGER.clickGui.alternativestyle.get() ? 450 : 300) - 100 - object.width + 32 / 2f) {
                size += 80 / 2f + (55 - 48);
                offsetX = 10;
                offsetY += 33;
            }
        }

        if (size < 400 - 33 - 25) {
            Gui.scrolling = 0;
            Gui.scrollingOut = 0;
        } else {
            Gui.scrolling = MathHelper.clamp(Gui.scrolling, -(size - 250), 0);
        }

        for (ConfigComponent object : objects) {
            if (!object.cfg.toLowerCase().contains(search.toLowerCase())) continue;
            object.draw(stack, mouseX, mouseY);
        }

        SmartScissor.unset();
        SmartScissor.pop();
        RenderUtils.Render2D.drawRoundedCorner(x + 14 - 3, y + 13 - 4.5f, FontUtils.configIcon[22].getWidth("H") + 5, FontUtils.configIcon[22].getFontHeight() + 4, 4, new Color(20, 20, 20).getRGB());
        FontUtils.configIcon[22].drawString(stack, "H", x + 14, y + 13.5f, -1);
        RenderUtils.Render2D.drawRoundedCorner(x + 36 - 3, y + 13 - 4.5f, FontUtils.configIcon[22].getWidth("L") + 5, FontUtils.configIcon[22].getFontHeight() + 4, 4, new Color(20, 20, 20).getRGB());
        FontUtils.configIcon[22].drawString(stack, "L", x + 36, y + 13.5f, -1);

            RenderUtils.Render2D.drawRoundedCorner(x + 322 / 2f + 30, y + 8, 281 / 2f, 29 / 2f, 8, new Color(22, 22, 22).getRGB());
            if (!(searching || !search.isEmpty())) {
                FontUtils.sfbold[16].drawCenteredString(stack, "Search", x + 311 / 2f + 30 + (311 / 2f) / 2f, y + 13.5f, -1);

        }

        SmartScissor.push();
        SmartScissor.setFromComponentCoordinates(x + 312 / 2f + 30, y + 8, 301 / 2f, 29 / 2f);
        if (searching || !search.isEmpty()) {
            FontUtils.sfbold[16].drawCenteredString(stack, search + (searching ? ((System.currentTimeMillis() % 1000 > 500) ? "" : "_") : ""), x + 311 / 2f + 30 + (311 / 2f) / 2f, y + 13, -1);
        }
        SmartScissor.unset();
        SmartScissor.pop();
    }

    public void click(int mouseX, int mouseY) {
        for (ConfigComponent object : objects) {
            object.click(mouseX, mouseY);
        }

        if (RenderUtils.isInRegion(mouseX, mouseY, x + 14 - 3, y + 13 - 4.5f, FontUtils.configIcon[22].getWidth("H") + 5, FontUtils.configIcon[22].getFontHeight() + 4)) {
            conduction.CONFIG_MANAGER.saveConfiguration("newcfg" + RandomStringUtils.randomNumeric(3));
            objects.clear();
            for (String cfg : conduction.CONFIG_MANAGER.getAllConfigurations()) {
                objects.add(new ConfigComponent(cfg));
            }
        }

        if(!conduction.FUNCTION_MANAGER.clickGui.alternativestyle.get()) {
            if (RenderUtils.isInRegion(mouseX, mouseY, x + 301 / 2f + 30, y + 10, 301 / 2f, 29 / 2f)) {
                searching = !searching;
            }
        }

        if (RenderUtils.isInRegion(mouseX, mouseY, x + 36 - 3, y + 13 - 4.5f, FontUtils.configIcon[22].getWidth("L") + 5, FontUtils.configIcon[22].getFontHeight() + 4)) {
            try {
                Runtime.getRuntime().exec("explorer " + ConfigManager.CONFIG_DIR.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void charTyped(char chars) {
        for (ConfigComponent object : objects) {
            object.charTyped(chars);
        }
    }

    public void keyTyped(int key) {
        for (ConfigComponent object : objects) {
            object.keyTyped(key);
        }
    }
}