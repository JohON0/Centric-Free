package pa.centric.client.ui.clickgui;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.text.TextFormatting;
import pa.centric.Centric;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.Type;
import pa.centric.client.ui.clickgui.configs.ConfigDraw;
import pa.centric.client.ui.clickgui.theme.DrawThemes;
import pa.centric.util.IMinecraft;
import pa.centric.util.font.FontUtils;
import pa.centric.util.math.KeyMappings;
import pa.centric.util.render.*;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import pa.centric.client.helper.conduction;
import pa.centric.client.ui.clickgui.components.ModuleComponent;
import pa.centric.client.ui.clickgui.components.Component;
import pa.centric.util.ClientUtils;
import pa.centric.util.render.animation.AnimationMath;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static pa.centric.util.IMinecraft.mc;
import static pa.centric.client.ui.clickgui.components.ModuleComponent.binding;

public class Gui extends Screen {
    private final int offx = 310;
    private final int offy = 55;
    private Vector2f position = new Vector2f(0, 0);
    public static Vector2f size = new Vector2f(450, 350);
    private Vector2f positiontheme = new Vector2f(mc.getMainWindow().scaledWidth() * 1.8f, mc.getMainWindow().scaledHeight() / 2f);
    private Vector2f positionconfig = new Vector2f(mc.getMainWindow().scaledWidth() * 2, mc.getMainWindow().scaledHeight() / 2f);
    private Vector2f configs = new Vector2f(mc.getMainWindow().scaledWidth() / 2f - size.x / 2f + offx, mc.getMainWindow().scaledHeight() / 2f - size.y / 2f + offy);

    private Vector2f themes = new Vector2f(mc.getMainWindow().scaledWidth() / 2f - size.x / 2f + offx, mc.getMainWindow().scaledHeight() / 2f - size.y / 2f + offy);
    public static Vector2f sizetheme = new Vector2f(150, 200);
    boolean colorOpen;
    float openAnimation;
    public int edit;
    float x,y, width, height;
    float hsb;
    float satur;
    float brithe;
    public float bar = 138;
    private Type currentCategory = Type.Combat;
    private final DrawThemes themeDrawing = new DrawThemes();
    private final ConfigDraw configDrawing = new ConfigDraw();
    private final ArrayList<ModuleComponent> objects = new ArrayList<>();

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        scrolling += (float) (delta * 30);
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    public Gui(ITextComponent titleIn) {
        super(titleIn);
        scrolling = 0;
        for (Function feature : conduction.FUNCTION_MANAGER.getFunctions()) {
            objects.add(new ModuleComponent(feature));
        }
    }

    @Override
    protected void init() {
        super.init();
        if(conduction.FUNCTION_MANAGER.clickGui.alternativestyle.get()){
            size = new Vector2f(224, 300);
            bar = 45;
        }
        else{
            size = new Vector2f(473, 320);
            bar = 128;
        }
        position = new Vector2f(mc.getMainWindow().scaledWidth() / 2f - size.x / 2f, mc.getMainWindow().scaledHeight() / 2f - size.y / 2f);
    }

    public static float scrolling;
    public static float scrollingOut;
    public boolean searching;
    private String searchText = "";

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        Vec2i fixed = ScaleMath.getMouse(mouseX, mouseY);
        mouseX = fixed.getX();
        mouseY = fixed.getY();
        //блюр на фоне
        if (conduction.FUNCTION_MANAGER.clickGui.blur.get()) {
            GaussianBlur.startBlur();
            RenderUtils.Render2D.drawRect(0, 0, width, height, -1);
            GaussianBlur.endBlur(conduction.FUNCTION_MANAGER.clickGui.blurVal.getValue().floatValue(), 1);
            mc.gameRenderer.setupOverlayRendering(2);
        }   



        for(int i = 0;i<=1;i++){
            RenderUtils.Render2D.drawRoundedCorner(position.x, position.y, size.x, size.y, new Vector4f(12, 12, 5, 5), ColorUtil.rgba(20, 20, 20, 150));
            RenderUtils.Render2D.drawRoundedCorner(position.x, position.y, bar, size.y, new Vector4f(12, 12, 5, 5), new Color(16, 16, 16, 100).getRGB());
            RenderUtils.Render2D.drawShadow(position.x, position.y, size.x, size.y, 12,new Color(16, 16, 16, 150).getRGB());
                if(!conduction.FUNCTION_MANAGER.clickGui.alternativestyle.get() && i == 0 && conduction.FUNCTION_MANAGER.clickGui.blur.get()) GaussianBlur.endBlur(conduction.FUNCTION_MANAGER.clickGui.blurVal.getValue().floatValue(), 1);
        }

        //рендер панельки тем
        int offxx = 26;
        int offyy = 115;
        int textoffx = 45;
        int textoffy = 110;
        RenderUtils.Render2D.drawShadow(positiontheme.x - offxx, positiontheme.y - offyy, sizetheme.x, sizetheme.y, 8, ColorUtil.rgba(20, 20, 20, 150));
        RenderUtils.Render2D.drawRoundedCorner(positiontheme.x - offxx, positiontheme.y - offyy, sizetheme.x, sizetheme.y, 8, ColorUtil.rgba(20, 20, 20, 150));
        FontUtils.sfbold[25].drawCenteredString(matrixStack,"Theme",positiontheme.x + textoffx, positiontheme.y - textoffy,-1);
        themeDrawing.draw(matrixStack,mouseX,mouseY,themes.x-5, themes.y,size.x,size.y);
        //панель конфигов
//        RenderUtils.Render2D.drawShadow(positionconfig.x/2-420, positionconfig.y-115, sizetheme.x, sizetheme.y, 8, new Color(15, 15, 15, 255).getRGB());
//        RenderUtils.Render2D.drawRoundedCorner(positionconfig.x/2-420, positionconfig.y-115, sizetheme.x, sizetheme.y, 8, new Color(15, 15, 15, 255).getRGB());
//        FontUtils.sfbold[25].drawCenteredString(matrixStack,"Configs",positionconfig.x/2-348, positionconfig.y-110,-1);
//        configDrawing.draw(matrixStack,mouseX,mouseY,configs.x, configs.y,sizetheme.x+20, sizetheme.y);
//


        if(!conduction.FUNCTION_MANAGER.clickGui.alternativestyle.get()) {
            RenderUtils.Render2D.drawRoundedCorner(position.x + 7, position.y + size.y - 55 - 8, bar - 15, 19, 6, new Color(20, 20, 20, 200).getRGB());
            SmartScissor.push();
            SmartScissor.setFromComponentCoordinates(position.x + 7, position.y + size.y - 55 - 8, bar - 15, 19);
            if (!searching && searchText.isEmpty()) {
                FontUtils.sfbold[16].drawCenteredString(matrixStack, "search function", position.x + 65, position.y + size.y - 47 - 8.5f, -1);
//                FontUtils.icons[35].drawString(matrixStack, "B", position.x +  40, position.y + size.y - 51.5f - 8, -1);
            } else {
                FontUtils.sfbold[16].drawString(matrixStack, searchText + (searching ? (System.currentTimeMillis() % 1000 > 500 ? "_" : "") : ""), position.x + 12 + 12, position.y + size.y - 47 - 8.5f, -1);
            }
            SmartScissor.unset();
            SmartScissor.pop();

        }


        FontUtils.iconlogo[80].drawStringWithShadow(matrixStack, ClientUtils.gradient("A",conduction.STYLE_MANAGER.getCurrentStyle().getColor(100),conduction.STYLE_MANAGER.getCurrentStyle().getColor(500)), position.x + 50, position.y + 10, ColorUtil.getColorStyle(0));

        float x = position.x;
        float y = position.y;




        float len;
        float categorys = 59;
        for (Type type : Type.values()) {
            len = type.ordinal() * 25;

            type.anim = AnimationMath.fast((float) type.anim, type == currentCategory ? 1 : 0, 20);

            float offsetX = 10;
            float offsetY = 3;

            if(!conduction.FUNCTION_MANAGER.clickGui.alternativestyle.get()) {
                type.anim = AnimationMath.fast((float) type.anim, type == currentCategory ? 1 : 0, 10);
                if (type.anim > 0.001) {

                    RenderUtils.Render2D.drawShadow(position.x + 5f, position.y + 57 + len, (float) bar - 10, 20, (int) (15),
                            RenderUtils.reAlphaInt(ColorUtil.getColorStyle(0), (int) (100 * type.anim)),
                            RenderUtils.reAlphaInt(ColorUtil.getColorStyle(90), (int) (100 * type.anim)),
                            RenderUtils.reAlphaInt(ColorUtil.getColorStyle(180), (int) (100 * type.anim)),
                            RenderUtils.reAlphaInt(ColorUtil.getColorStyle(270), (int) (100 * type.anim)));

                    RenderUtils.Render2D.drawGradientRound(position.x + 5f, position.y + 57 + len, (float) bar - 10, 20, 3,
                            RenderUtils.reAlphaInt(ColorUtil.getColorStyle(0), (int) (100 * type.anim)),
                            RenderUtils.reAlphaInt(ColorUtil.getColorStyle(90), (int) (100 * type.anim)),
                            RenderUtils.reAlphaInt(ColorUtil.getColorStyle(180), (int) (100 * type.anim)),
                            RenderUtils.reAlphaInt(ColorUtil.getColorStyle(270), (int) (100 * type.anim)));
                }
                FontUtils.sfbold[21].drawString(matrixStack, type.name(), position.x + offsetX + 30, position.y + offsetY + categorys + 2  + len, type != currentCategory ? new Color(255, 255, 255, 100).getRGB() : -1);
                FontUtils.icontypes[30].drawString(matrixStack, type.icon, position.x + offsetX + 10, position.y + offsetY + categorys + 1  + len, type != currentCategory ? new Color(255, 255, 255, 100).getRGB() : ColorUtil.getColorStyle(100));

            }
        }

        float avatar_offset = 7;
        float avatar_offset_y = -3;
        if (ClientUtils.me != null) {
            GlStateManager.bindTexture(RenderUtils.Render2D.downloadImage(ClientUtils.me.getAvatarUrl()));
            RenderUtils.Render2D.drawTexture(position.x + avatar_offset + 5 + 2, position.y + size.y - 30 + avatar_offset_y, 20, 20, 2, 1);
            if (conduction.FUNCTION_MANAGER != null && conduction.FUNCTION_MANAGER.nameProtect.state) {
                FontUtils.sfbold[14].drawString(matrixStack, "user: " + "центрик.tech", position.x + avatar_offset + 5 + 25, position.y + size.y - 28 + avatar_offset_y, -1);
            }else {
                FontUtils.sfbold[14].drawString(matrixStack, "user: " + mc.session.getUsername(), position.x + avatar_offset + 5 + 25, position.y + size.y - 28 + avatar_offset_y, -1);
            }
            FontUtils.sfbold[14].drawString(matrixStack, "version: " + Centric.build, position.x + avatar_offset + 5 + 25, position.y + size.y - 18 + avatar_offset_y,-1);
        } else {
            if (conduction.FUNCTION_MANAGER != null && conduction.FUNCTION_MANAGER.nameProtect.state) {
                FontUtils.sfbold[14].drawString(matrixStack, "user: " + "центрик.tech", position.x + avatar_offset + 5 + 25, position.y + size.y - 28 + avatar_offset_y, -1);
            }else {
                FontUtils.sfbold[14].drawString(matrixStack, "user: " + mc.session.getUsername(), position.x + avatar_offset + 5 + 25, position.y + size.y - 28 + avatar_offset_y, -1);
            }


            StencilUtil.initStencilToWrite();
            RenderUtils.Render2D.drawRoundedRect(position.x + avatar_offset + 2 + 2, position.y + size.y - 32 + avatar_offset_y, 22, 22, 2, ColorUtil.rgba(0, 0, 0, 255 * 0.22));
            StencilUtil.readStencilBuffer(1);
            assert mc.player != null;
            mc.getTextureManager().bindTexture(mc.player.getLocationSkin());
            AbstractGui.drawScaledCustomSizeModalRect(position.x + avatar_offset + 2 + 2, position.y + size.y - 32 + avatar_offset_y, 8, 8, 8, 8, 22, 22, 64, 64);
            StencilUtil.uninitStencilBuffer();

            GaussianBlur.startBlur();
            RenderUtils.Render2D.drawRoundedRect(position.x + avatar_offset + 2 + 2, position.y + size.y - 32 + avatar_offset_y, 22, 22, 2, ColorUtil.rgba(0, 0, 0, 255 * 0.22));
            GaussianBlur.endBlur(1.022f, 1);

            FontUtils.sfbold[14].drawString(matrixStack, "version: " + Centric.build, position.x + avatar_offset + 5 + 25, position.y + size.y - 18 + avatar_offset_y, -1);

        }


//        if (currentCategory == Type.Theme) {
////            themeDrawing.draw(matrixStack, mouseX, mouseY, position.x + bar, position.y, size.x - bar, size.y);
//            BloomHelper.draw(10, 1, false);
//        }

        if (currentCategory == Type.Configs) {
            configDrawing.draw(matrixStack, mouseX, mouseY, position.x + bar, position.y, size.x - bar, size.y);
            BloomHelper.draw(10, 1, false);
        }

        if (currentCategory != Type.Configs) {
            drawObjects(matrixStack, mouseX, mouseY);
        }

        scrollingOut = AnimationMath.fast(scrollingOut, scrolling, 15);

        mc.gameRenderer.setupOverlayRendering();
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (currentCategory == Type.Configs)
            configDrawing.charTyped(codePoint);
        if (configDrawing.searching && configDrawing.search.length() < 23) {
            configDrawing.search += codePoint;
        }
        if (searching && searchText.length() < 13) {
            searchText += codePoint;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (ModuleComponent m : objects) {
            if (m.feature.category == currentCategory) {
                m.keyTyped(keyCode, scanCode, modifiers);
            }
        }
        if (currentCategory == Type.Configs) {
            configDrawing.keyTyped(keyCode);
        }
        if (configDrawing.searching) {
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                if (!configDrawing.search.isEmpty())
                    configDrawing.search = configDrawing.search.substring(0, configDrawing.search.length() - 1);
            }
            if (keyCode == GLFW.GLFW_KEY_ENTER) {
                configDrawing.searching = false;
            }
        }
        if (searching) {
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                if (!searchText.isEmpty())
                    searchText = searchText.substring(0, searchText.length() - 1);
            }
            if (keyCode == GLFW.GLFW_KEY_ENTER) {
                searching = false;
            }
        }
        if (binding != null) {
            if (keyCode == GLFW.GLFW_KEY_DELETE) {
                binding.feature.bind = 0;
            } else {
                conduction.NOTIFICATION_MANAGER.add("Модуль " + TextFormatting.GRAY + binding.feature.name + TextFormatting.WHITE + " был забинжен на кнопку "  + KeyMappings.reverseKeyMap.get(keyCode), "Module",3);

                binding.feature.bind = keyCode;
            }
            binding = null;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        Vec2i fixed = ScaleMath.getMouse((int) mouseX, (int) mouseY);
        mouseX = fixed.getX();
        mouseY = fixed.getY();
        RenderUtils.SmartScissor.push();
        RenderUtils.SmartScissor.setFromComponentCoordinates(position.x, position.y, size.x, size.y - 1);
        for (ModuleComponent m : objects) {
            if (searching || !searchText.isEmpty()) {
                if (!searchText.isEmpty())
                    if (!m.feature.name.toLowerCase().contains(searchText.toLowerCase())) continue;
                m.mouseReleased((int) mouseX, (int) mouseY, button);
            } else {
                if (m.feature.category == currentCategory)
                    m.mouseReleased((int) mouseX, (int) mouseY, button);
            }
        }

        for (Component object : objects) {
            if (object instanceof ModuleComponent) {
                ModuleComponent m = (ModuleComponent) object;
            }
        }
        RenderUtils.SmartScissor.unset();
        RenderUtils.SmartScissor.pop();
        return super.mouseReleased(mouseX, mouseY, button);

    }

    @Override
    public void onClose() {
        super.onClose();
        searching = false;
        for (ModuleComponent m : objects) {
            m.exit();
        }
    }

    public void drawObjects(MatrixStack stack, int mouseX, int mouseY) {
        Vec2i fixed = ScaleMath.getMouse(mouseX, mouseY);
        mouseX = fixed.getX();
        mouseY = fixed.getY();

        List<ModuleComponent> doubles = objects.stream().filter(moduleObject -> (!searchText.isEmpty()) || moduleObject.feature.category == currentCategory).collect(Collectors.toList());

        List<ModuleComponent> first = objects.stream().filter(moduleObject -> (!searchText.isEmpty()) || moduleObject.feature.category == currentCategory).filter(moduleObject -> objects.indexOf(moduleObject) % 2 == 0).collect(Collectors.toList());
        List<ModuleComponent> second = objects.stream().filter(moduleObject -> (!searchText.isEmpty()) || moduleObject.feature.category == currentCategory).filter(moduleObject -> objects.indexOf(moduleObject) % 2 != 0).collect(Collectors.toList());

        RenderUtils.SmartScissor.push();
        RenderUtils.SmartScissor.setFromComponentCoordinates(position.x, position.y, size.x, size.y - 1);
        float offset = scrollingOut;
        float sizePanel = 0;
        float sizePanel1 = 0;
        if(!conduction.FUNCTION_MANAGER.clickGui.alternativestyle.get()){
            for (ModuleComponent object : first) {
                if (!searchText.isEmpty())
                    if (!object.feature.name.toLowerCase().contains(searchText.toLowerCase())) continue;
                object.x = position.x + bar + 10;
                object.y = position.y + 6 + offset;
                object.width = 160;
                object.height = 22;
                for (Component object1 : object.object) {
                    if (object1.setting.visible()) {
                        object.height += object1.height;
                    }
                }
                object.height += 3;
                if (!(object.y - object.height - 50 > size.y))
                    object.draw(stack, mouseX, mouseY);
                offset += object.height += 9;
                sizePanel += object.height += 5;
            }
            offset = scrollingOut;
            for (ModuleComponent object : second) {
                if (!searchText.isEmpty())
                    if (!object.feature.name.toLowerCase().contains(searchText.toLowerCase())) continue;
                object.x = position.x + bar + 10 + 169;
                object.y = position.y + 6 + offset;
                object.width = 160;
                object.height = 22;
                for (Component object1 : object.object) {
                    if (object1.setting.visible()) {
                        object.height += object1.height;
                    }
                }
                object.height += 3;
                if (!(object.y - object.height - 50 > size.y))
                    object.draw(stack, mouseX, mouseY);
                offset += object.height += 9;
                sizePanel1 += object.height += 5;
            }
        } else {
            for (ModuleComponent object : doubles) {
                if (!searchText.isEmpty())
                    if (!object.feature.name.toLowerCase().contains(searchText.toLowerCase())) continue;
                object.x = position.x + bar + 10;
                object.y = position.y + 6 + offset;
                object.width = 160;
                object.height = 22;
                for (Component object1 : object.object) {
                    if (object1.setting.visible()) {
                        object.height += object1.height;
                    }
                }
                object.height += 3;
                if (!(object.y - object.height - 50 > size.y))
                    object.draw(stack, mouseX, mouseY);
                offset += object.height += 9;
                sizePanel += object.height += 5;
            }
        }
        float max = Math.max(sizePanel, sizePanel1);
        if (max < size.y) {
            scrolling = 0;
        } else {
            scrolling = MathHelper.clamp(scrolling, -(max - size.y), 0);
        }
        RenderUtils.SmartScissor.unset();
        RenderUtils.SmartScissor.pop();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Vec2i fixed = ScaleMath.getMouse((int) mouseX, (int) mouseY);
        mouseX = fixed.getX();
        mouseY = fixed.getY();
        RenderUtils.SmartScissor.push();
        RenderUtils.SmartScissor.setFromComponentCoordinates(position.x, position.y, size.x, size.y - 1);

        //}

        if (currentCategory == Type.Configs) {
            configDrawing.click((int) mouseX, (int) mouseY);
        }

        float len;
        for (Type type : Type.values()) {
            len = type.ordinal() * 25;
            if (RenderUtils.isInRegion(mouseX, mouseY, position.x, position.y + 59 + len, bar, 16)) {
                currentCategory = type;
                searching = false;
            }
        }
        if (RenderUtils.isInRegion(mouseX, mouseY, positiontheme.x-50, positiontheme.y-115, sizetheme.x, sizetheme.y))
            themeDrawing.click((int) mouseX, (int) mouseY, button);
        for (ModuleComponent m : objects) {
            if (searching || !searchText.isEmpty()) {
                if (!searchText.isEmpty())
                    if (!m.feature.name.toLowerCase().contains(searchText.toLowerCase())) continue;
                m.mouseClicked((int) mouseX, (int) mouseY, button);
            } else {
                if (m.feature.category == currentCategory)
                    m.mouseClicked((int) mouseX, (int) mouseY, button);
            }
        }

        if(!conduction.FUNCTION_MANAGER.clickGui.alternativestyle.get()) {
            if (RenderUtils.isInRegion(mouseX, mouseY, position.x + 7, position.y + size.y - 55 - 15, bar - 15, 19)) {
                searching = !searching;
            }
        }
        RenderUtils.SmartScissor.unset();
        RenderUtils.SmartScissor.pop();

        return super.mouseClicked(mouseX, mouseY, button);
    }
}