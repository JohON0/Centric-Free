package pa.centric.client.ui.clickgui.components;

import com.mojang.blaze3d.matrix.MatrixStack;

public interface IComponent {

    void draw(MatrixStack stack, int mouseX, int mouseY);

    void exit();

    void mouseClicked(int mouseX, int mouseY, int mouseButton);

    void mouseReleased(int mouseX, int mouseY, int mouseButton);

    void keyTyped(int keyCode, int scanCode, int modifiers);

    void charTyped(char codePoint, int modifiers);
}