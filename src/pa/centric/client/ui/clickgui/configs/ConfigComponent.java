package pa.centric.client.ui.clickgui.configs;

import pa.centric.Centric;
import pa.centric.client.config.Config;
import pa.centric.util.ClientUtils;
import pa.centric.util.font.FontUtils;
import pa.centric.util.misc.TimerUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import pa.centric.client.helper.conduction;
import pa.centric.client.config.ConfigManager;
import pa.centric.util.render.ColorUtil;
import pa.centric.util.render.RenderUtils;
import pa.centric.util.render.animation.AnimationMath;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConfigComponent {

    public float x, y, width, height;
    public String cfg;
    public String staticF;
    public TimerUtil timerUtil = new TimerUtil();

    public ConfigComponent(String cfg) {
        this.staticF = cfg;
        this.cfg = cfg;
    }

    public void draw(MatrixStack stack, int mouseX, int mouseY) {
        if (timerUtil.hasTimeElapsed(1000)) {
            clicked = 0;
        }
        String cfgname = cfg + (nameChange ? System.currentTimeMillis() % 1000 > 500 ? "" : "_" : "");
        String subcfgstring = cfgname.substring(0, Math.min(cfgname.length(), 13));

        FontUtils.sfbold[20].drawString(stack,subcfgstring , x + 7, y + 7, -1);
        float offset = 21;
        RenderUtils.Render2D.drawRoundedRect(x + width - offset, y + height - offset + 3, 29 / 2f, 29 / 2f, 3, ColorUtil.rgba(15, 15, 15, 255));
        RenderUtils.Render2D.drawRoundedRect(x + width - offset * 2, y + height - offset + 3, 29 / 2f, 29 / 2f, 3, ColorUtil.rgba(15, 15, 15, 255));
        RenderUtils.Render2D.drawRoundedRect(x + width - offset * 3, y + height - offset + 3, 29 / 2f, 29 / 2f, 3, ColorUtil.rgba(15, 15, 15, 255));
        FontUtils.configIcon[15].drawString(stack, "J", x + width - offset * 3 + 3.5f, y + height - offset + 9, -1);
        FontUtils.configIcon[15].drawString(stack, "M", x + width - offset * 2 + 3.5f, y + height - offset + 9, -1);
        FontUtils.configIcon[15].drawString(stack, "I", x + width - offset * 1 + 3.5f, y + height - offset + 9, -1);
    }

    boolean nameChange;
    int clicked;

    public void charTyped(char chars) {
        if (nameChange && cfg.length() < 15) {
            cfg += chars;
        }
    }

    public void keyTyped(int key) {
        if (key == GLFW.GLFW_KEY_ENTER) {
            if (nameChange) {
                nameChange = false;
                Config cfg = conduction.CONFIG_MANAGER.findConfig(staticF);
                if (cfg != null) {
                    cfg.getFile().renameTo(new File(ConfigManager.CONFIG_DIR, this.cfg + ".cfg"));
                    staticF = this.cfg;
                }
            }
        }
        if (key == GLFW.GLFW_KEY_BACKSPACE) {
            if (nameChange) {
                if (!cfg.isEmpty()) {
                    cfg = cfg.substring(0, cfg.length() - 1);
                }
            }
        }
    }

    public void click(int mx, int my) {
        float offset = 20;
        if (RenderUtils.isInRegion(mx, my, x + width - offset * 3, y + height - offset, 29 / 2f, 29 / 2f)) {
            try {
                Files.delete(conduction.CONFIG_MANAGER.findConfig(cfg).getFile().toPath());
            } catch (IOException e) {
                System.out.println(Centric.prefix + "Config UI: " + e.getMessage());
            }
            ConfigDraw.configDrawing.objects.remove(this);
        }
        if (RenderUtils.isInRegion(mx, my, x, y, width, height)) {
            timerUtil.reset();
            clicked++;
            if (clicked >= 2) {
                nameChange = true;
                ConfigDraw.configDrawing.searching = false;
            }
        }
        if (RenderUtils.isInRegion(mx, my, x + width - offset * 2, y + height - offset, 29 / 2f, 29 / 2f)) {
            conduction.CONFIG_MANAGER.saveConfiguration(cfg);
            ClientUtils.sendMessage("Сохранил конфиг " + cfg);
        }
        if (RenderUtils.isInRegion(mx, my, x + width - offset * 1, y + height - offset, 29 / 2f, 29 / 2f)) {
            conduction.CONFIG_MANAGER.loadConfiguration(cfg, false);
            ClientUtils.sendMessage("Загрузил конфиг " + cfg);
        }
    }
}