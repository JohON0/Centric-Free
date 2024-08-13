package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import java.util.Objects;

import pa.centric.client.helper.conduction;
import pa.centric.util.ClientUtils;
import pa.centric.util.font.FontUtils;
import pa.centric.util.render.ColorUtil;
import pa.centric.util.render.RenderUtils;
import pa.centric.util.render.animation.AnimationMath;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.joml.Vector4i;

import static com.mojang.blaze3d.systems.RenderSystem.color4f;

public abstract class Widget extends AbstractGui implements IRenderable, IGuiEventListener {
    public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");

    protected int width;

    protected int height;

    public int x;

    public int y;

    private ITextComponent message;

    private boolean wasHovered;

    protected boolean isHovered;

    public boolean active = true;

    public boolean visible = true;

    public float animation;

    protected float alpha = 1.0F;

    protected long nextNarration = Long.MAX_VALUE;

    private boolean focused;

    public Widget(int x, int y, int width, int height, ITextComponent title) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.message = title;
    }

    public int getHeightRealms() {
        return this.height;
    }

    protected int getYImage(boolean isHovered) {
        int i = 1;
        if (!this.active) {
            i = 0;
        } else if (isHovered) {
            i = 2;
        }
        return i;
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            this.isHovered = (mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height);
            if (this.wasHovered != isHovered())
                if (isHovered()) {
                    if (this.focused) {
                        queueNarration(200);
                    } else {
                        queueNarration(750);
                    }
                } else {
                    this.nextNarration = Long.MAX_VALUE;
                }
            if (this.visible)
                renderButton(matrixStack, mouseX, mouseY, partialTicks);
            narrate();
            this.wasHovered = isHovered();
        }
    }

    protected void narrate() {
        if (this.active && isHovered() && Util.milliTime() > this.nextNarration) {
            String s = getNarrationMessage().getString();
            if (!s.isEmpty()) {
                NarratorChatListener.INSTANCE.say(s);
                this.nextNarration = Long.MAX_VALUE;
            }
        }
    }

    protected IFormattableTextComponent getNarrationMessage() {
        return (IFormattableTextComponent) new TranslationTextComponent("gui.narrate.button", new Object[]{getMessage()});
    }

    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!ClientUtils.legitMode) {
            /**
             * @author JohON0.class
             */
            minecraft.getTextureManager().bindTexture(WIDGETS_LOCATION);
            animation = AnimationMath.lerp(animation, isHovered() ? 1f : 0, 10);
            RenderUtils.Render2D.drawRoundOutline(x, y, width, height+2, 3f, 0.1f, ColorUtil.interpolateColor(ColorUtil.rgba(10, 10, 10,100), ColorUtil.rgba(10, 10, 10,150), animation), new Vector4i(
                    ColorUtil.interpolateColor(ColorUtil.rgba(32, 32, 32,255), conduction.STYLE_MANAGER.getCurrentStyle().getColor(150), animation)));
            FontUtils.sfsemibold[18].drawCenteredString(matrixStack, this.getMessage(), x + width / 2f, y + height / 2f - FontUtils.sfsemibold[23].getFontHeight() / 2f + 5, RenderUtils.reAlphaInt(ColorUtil.interpolateColor(ColorUtil.rgba(132, 135, 147,255), -1, animation), (int) (255)));
        }
        else {
            FontRenderer fontrenderer = minecraft.fontRenderer;
            minecraft.getTextureManager().bindTexture(WIDGETS_LOCATION);
            color4f(1.0F, 1.0F, 1.0F, this.alpha);
            int i = getYImage(isHovered());
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            blit(matrixStack, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
            blit(matrixStack, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
            renderBg(matrixStack, minecraft, mouseX, mouseY);
            int j = this.active ? 16777215 : 10526880;
            drawCenteredString(matrixStack, fontrenderer, getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);

        }
    }

    protected void renderBg(MatrixStack matrixStack, Minecraft minecraft, int mouseX, int mouseY) {}

    public void onClick(double mouseX, double mouseY) {}

    public void onRelease(double mouseX, double mouseY) {}

    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {}

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible) {
            if (isValidClickButton(button)) {
                boolean flag = clicked(mouseX, mouseY);
                if (flag) {
                    playDownSound(Minecraft.getInstance().getSoundHandler());
                    onClick(mouseX, mouseY);
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isValidClickButton(button)) {
            onRelease(mouseX, mouseY);
            return true;
        }
        return false;
    }

    protected boolean isValidClickButton(int button) {
        return (button == 0);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isValidClickButton(button)) {
            onDrag(mouseX, mouseY, dragX, dragY);
            return true;
        }
        return false;
    }

    protected boolean clicked(double mouseX, double mouseY) {
        return (this.active && this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < (this.x + this.width) && mouseY < (this.y + this.height));
    }

    public boolean isHovered() {
        return (this.isHovered || this.focused);
    }

    public boolean changeFocus(boolean focus) {
        if (this.active && this.visible) {
            this.focused = !this.focused;
            onFocusedChanged(this.focused);
            return this.focused;
        }
        return false;
    }

    protected void onFocusedChanged(boolean focused) {}

    public boolean isMouseOver(double mouseX, double mouseY) {
        return (this.active && this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < (this.x + this.width) && mouseY < (this.y + this.height));
    }

    public void renderToolTip(MatrixStack matrixStack, int mouseX, int mouseY) {}

    public void playDownSound(SoundHandler handler) {
        handler.play((ISound)SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public void setMessage(ITextComponent message) {
        if (!Objects.equals(message.getString(), this.message.getString()))
            queueNarration(250);
        this.message = message;
    }

    public void queueNarration(int delay) {
        this.nextNarration = Util.milliTime() + delay;
    }

    public ITextComponent getMessage() {
        return this.message;
    }

    public boolean isFocused() {
        return this.focused;
    }

    protected void setFocused(boolean focused) {
        this.focused = focused;
    }
}
