package pa.centric.client.ui.clickgui.components;

import com.mojang.blaze3d.matrix.MatrixStack;
import pa.centric.client.modules.settings.Setting;
import pa.centric.client.modules.settings.imp.BindSetting;

public abstract class Component implements IComponent {

    public float x, y, width, height;

    public Setting setting;

    public boolean isHovered(int mouseX, int mouseY, float width, float height) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    public boolean isHovered(int mouseX, int mouseY, float x,float y, float width, float height) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    public boolean isHovered(int mouseX, int mouseY) {
        return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
    }

    public boolean isHovered(int mouseX, int mouseY, float height) {
        return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
    }

    @Override
    public abstract void mouseClicked(int mouseX, int mouseY, int mouseButton);

    @Override
    public abstract void mouseReleased(int mouseX, int mouseY, int mouseButton);

    @Override
    public abstract void keyTyped(int keyCode, int scanCode, int modifiers);

    @Override
    public abstract void charTyped(char codePoint, int modifiers);

    @Override
    public void draw(MatrixStack stack, int mouseX, int mouseY) {
    }

    @Override
    public void exit() {
    }
}