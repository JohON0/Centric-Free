/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package pa.centric.client.ui.alt.ui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.util.text.StringTextComponent;
import org.apache.commons.lang3.RandomStringUtils;
import org.joml.Vector4i;
import org.lwjgl.glfw.GLFW;
import pa.centric.client.helper.conduction;
import pa.centric.client.ui.alt.Account;
import pa.centric.client.ui.alt.AltConfig;
import pa.centric.util.IMinecraft;
import pa.centric.util.font.FontUtils;
import pa.centric.util.render.*;
import pa.centric.util.render.animation.AnimationMath;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import static pa.centric.util.IMinecraft.mc;


public class AltManagerUI extends Screen {

    public AltManagerUI() {
        super(new StringTextComponent(""));
    }

    public ArrayList<Account> accounts = new ArrayList<>();

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            if (!altName.isEmpty())
                altName = altName.substring(0, altName.length() - 1);
        }

        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            if (!altName.isEmpty())
                accounts.add(new Account(altName));
            typing = false;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        altName += Character.toString(codePoint);
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Vec2i fixed = ScaleMath.getMouse((int) mouseX, (int) mouseY);
        mouseX = fixed.getX();
        mouseY = fixed.getY();

        if (RenderUtils.isInRegion(mouseX, mouseY, width - 170, 162, 100, 20)) {
            AltConfig.updateFile();
            accounts.add(new Account(RandomStringUtils.randomAlphabetic(8)));
        }

        if (RenderUtils.isInRegion(mouseX, mouseY, width - 170, 192, 100, 20)) {
            if (!altName.isEmpty() && altName.length() < 20) {
                accounts.add(new Account(altName));
                AltConfig.updateFile();
            }

            typing = false;
        }

        if (RenderUtils.isInRegion(mouseX, mouseY, width - 210, 132, 180, 20)) {
            typing = !typing;
        }

        float iter = scrollAn;
        Iterator<Account> iterator = accounts.iterator();
        while (iterator.hasNext()) {
            Account account = iterator.next();
            float panWidth = 150;
            float acX = 14;
            float acY = 6 + (iter * 47);

            if (RenderUtils.isInRegion(mouseX, mouseY, acX, acY, panWidth, 40)) {
                if (button == 0) {
                    mc.session = new Session(account.accountName, "", "", "mojang");
                } else {
                    iterator.remove();
                    AltConfig.updateFile();
                }
            }

            iter++;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);
    }

    @Override
    public void tick() {
        super.tick();
    }

    public float scroll;
    public float scrollAn;
    public boolean hoveredFirst;
    public boolean hoveredSecond;
    public float hoveredFirstAn;
    public float hoveredSecondAn;
    private String altName = "";
    private boolean typing;

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        scroll += (float) delta;
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Vec2i fixed = ScaleMath.getMouse(mouseX, mouseY);
        mouseX = fixed.getX();
        mouseY = fixed.getY();

        scrollAn = AnimationMath.lerp(scrollAn, scroll, 5);
        hoveredFirst = RenderUtils.isInRegion(mouseX, mouseY, width - 170, 155, 100, 20);
        hoveredSecond = RenderUtils.isInRegion(mouseX, mouseY, width - 170, 185, 100, 20);
        hoveredFirstAn = AnimationMath.lerp(hoveredFirstAn, hoveredFirst ? 1 : 0, 10);
        hoveredSecondAn = AnimationMath.lerp(hoveredSecondAn, hoveredSecond ? 1 : 0, 10);

        mc.gameRenderer.setupOverlayRendering(2);

        float width = mc.getMainWindow().scaledWidth();
        float height = mc.getMainWindow().scaledHeight();

        RenderUtils.Render2D.drawImage(new ResourceLocation("centric/images/backmenu.png"), 0, 0, width, height, -1);

        RenderUtils.Render2D.drawRect(width - 240, 0, 240, height, ColorUtil.rgba(13, 13, 13, 25));
        RenderUtils.Render2D.drawRect(width - 220, 80, 200, 163, ColorUtil.rgba(23, 23, 23, 25));

        FontUtils.sfbold[35].drawCenteredString(matrixStack, "Список Аккаунтов", width - 240 / 2f, 25, -1);
        //Fonts.gilroy[30].drawCenteredString(matrixStack, "Convenient, fast, beautiful", width - 240 / 2f, 45, -1);
        FontUtils.sfbold[30].drawCenteredString(matrixStack, "Добавить Аккаунт", width - 240 / 2f, 70, -1);
        FontUtils.sfbold[20].drawString(matrixStack, "Ник", width - 208, 120, ColorUtil.rgba(255, 255, 255, 255));
        RenderUtils.Render2D.drawRoundedRect(width - 210, 132, 180, 20, 5, ColorUtil.rgba(35, 35, 35, 255));

        if (!typing) {
            FontUtils.sfbold[18].drawString(matrixStack, "", width - 205, 139, ColorUtil.rgba(255, 255, 255, 64));
        } else {
            FontUtils.sfbold[18].drawString(matrixStack, altName + (System.currentTimeMillis() % 1000 > 500 ? "_" : ""), width - 205, 139, ColorUtil.rgba(255, 255, 255, 255));
        }

        RenderUtils.Render2D.drawRoundedRect(width - 170, 162, 100, 20, 5, ColorUtil.rgba(35, 35, 35, 255));
        RenderUtils.Render2D.drawRoundedRect(width - 170, 192, 100, 20, 5, ColorUtil.rgba(35, 35, 35, 255));

        BloomHelper.registerRenderCall(() -> {
            FontUtils.sfbold[18].drawCenteredString(matrixStack, "Рандом Аккаунт", width - 170 + (100 / 2f), 169, ColorUtil.interpolateColor(ColorUtil.rgba(255, 255, 255, 64), -1, hoveredFirstAn));
            FontUtils.sfbold[18].drawCenteredString(matrixStack, "Создать", width - 170 + (100 / 2f), 199, ColorUtil.interpolateColor(ColorUtil.rgba(255, 255, 255, 64), -1, hoveredSecondAn));
        });

        FontUtils.sfbold[18].drawCenteredString(matrixStack, "Рандом Аккаунт", width - 170 + (100 / 2f), 169, ColorUtil.interpolateColor(ColorUtil.rgba(255, 255, 255, 64), -1, hoveredFirstAn));
        FontUtils.sfbold[18].drawCenteredString(matrixStack, "Создать", width - 170 + (100 / 2f), 199, ColorUtil.interpolateColor(ColorUtil.rgba(255, 255, 255, 64), -1, hoveredSecondAn));
        FontUtils.sfbold[20].drawCenteredString(matrixStack, "Ваш ник: " + mc.getSession().getUsername(), width - 170 + (100 / 2f), 222, ColorUtil.rgba(255, 255, 255, 255));
        float iter = scrollAn;
        float size = 0;
        SmartScissor.push();
        SmartScissor.setFromComponentCoordinates(14, 6, 150, height);
        for (Account account : accounts) {
            float panWidth = 135;
            float acX = 14;
            float acY = 6 + (iter * 47);

            RenderUtils.Render2D.drawRoundedRect(14, acY, panWidth, 40,7, ColorUtil.rgba(10, 10, 10, 200));
            Vector4f vector4f = new Vector4f(5, 5, 5, 5);
            ShaderUtil.CORNER_ROUND_SHADER_TEXTURE.attach();
            ShaderUtil.CORNER_ROUND_SHADER_TEXTURE.setUniform("size", (float) (30 * 2), (float) (30 * 2));
            ShaderUtil.CORNER_ROUND_SHADER_TEXTURE.setUniform("round", vector4f.x * 2, vector4f.y * 2, vector4f.z * 2, vector4f.w * 2);
            ShaderUtil.CORNER_ROUND_SHADER_TEXTURE.setUniform("smoothness", 0.f, 1.5f);
            ShaderUtil.CORNER_ROUND_SHADER_TEXTURE.setUniformf("alpha", 1);
            RenderUtils.Render2D.drawRoundOutline(acX + 5, acY + 5,30, 30,7,1f,ColorUtil.rgba(255, 255, 255, 0),new Vector4i(
                    conduction.STYLE_MANAGER.getCurrentStyle().getColor(100),
                    conduction.STYLE_MANAGER.getCurrentStyle().getColor(200),
                    conduction.STYLE_MANAGER.getCurrentStyle().getColor(400),
                    conduction.STYLE_MANAGER.getCurrentStyle().getColor(800)));
            StencilUtil.initStencilToWrite();
            RenderUtils.Render2D.drawRoundedRect(acX + 5F, acY + 5, 30, 30, 7, Color.BLACK.getRGB());
            StencilUtil.readStencilBuffer(1);
            mc.getTextureManager().bindTexture(account.skin);
            AbstractGui.drawScaledCustomSizeModalRect(acX + 5, acY + 5, 8F, 8F, 8F, 8F, 30, 30, 64, 64);
            StencilUtil.uninitStencilBuffer();
            ShaderUtil.CORNER_ROUND_SHADER_TEXTURE.detach();
            BloomHelper.registerRenderCall(() -> {
            });

            if (account.accountName.equalsIgnoreCase(mc.session.getUsername())) {
                BloomHelper.registerRenderCall(() -> FontUtils.sfbold[20].drawString(matrixStack, account.accountName, acX + 40, acY + 10, ColorUtil.rgba(255, 255, 255, 255)));
            }
            FontUtils.sfbold[20].drawString(matrixStack, account.accountName, acX + 40, acY + 10, ColorUtil.rgba(255, 255, 255, 255));

            Date dateAdded = new Date(account.dateAdded);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
            String formattedDate = dateFormat.format(dateAdded);

            Date timeAdded = new Date(account.dateAdded);
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            String formattedtime = timeFormat.format(timeAdded);

            FontUtils.sfbold[18].drawString(matrixStack, formattedDate + " | " + formattedtime, acX + 40, acY + 25, ColorUtil.rgba(255, 255, 255, 128));
            iter++;
            size++;
        }

        SmartScissor.unset();
        SmartScissor.pop();

        scroll = MathHelper.clamp(scroll, size > 4 ? -size + 4 : 0, 0);
        BloomHelper.draw(8, 0.8f, false);
        mc.gameRenderer.setupOverlayRendering();
    }
}