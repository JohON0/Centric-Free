package pa.centric.client.modules.impl.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import org.joml.Vector4d;
import org.joml.Vector4i;
import org.lwjgl.opengl.GL11;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.modules.settings.imp.BooleanOption;
import pa.centric.client.modules.settings.imp.MultiBoxSetting;
import pa.centric.client.helper.conduction;
import pa.centric.util.font.FontUtils;
import pa.centric.util.render.ColorUtil;
import pa.centric.util.render.RenderUtils;
import pa.centric.events.Event;
import pa.centric.events.impl.render.EventRender;
import pa.centric.client.ui.midnight.Style;
import pa.centric.util.math.PlayerPositionTracker;

import java.util.HashMap;
import java.util.Map;

import static pa.centric.util.math.PlayerPositionTracker.isInView;
import static pa.centric.util.render.ColorUtil.rgba;

/**
 * @author JohON0
 */
@ModuleAnnotation(name = "ItemESP", category = Type.Render)
public class ItemESP extends Function {

    public MultiBoxSetting elements = new MultiBoxSetting("Элементы предметов",
            new BooleanOption("Боксы", false),
            new BooleanOption("Имена", true));

    public ItemESP() {
        addSettings(elements);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventRender render) {
            if (render.isRender3D()) {
                updateItemsPositions(render.partialTicks);
            }

            if (render.isRender2D()) {
                renderItemElements(render.matrixStack);
            }
        }
    }

    public HashMap<Vector4d, ItemEntity> positions = new HashMap<>();

    private void updateItemsPositions(float partialTicks) {
        positions.clear();
        for (Entity entity : mc.world.getAllEntities()) {
            if (isInView(entity) && entity instanceof ItemEntity item) {
                Vector4d position = PlayerPositionTracker.updatePlayerPositions(item, partialTicks);
                if (position != null) {
                    positions.put(position, item);
                }
            }
        }
    }

    private void renderItemElements(MatrixStack stack) {
        GL11.glColor4f(1, 1, 1, 1);
        int main = ColorUtil.rgba(255, 255, 255, 255);
        int back = ColorUtil.rgba(0, 0, 0, 128);
        Style current = conduction.STYLE_MANAGER.getCurrentStyle();
        Vector4i colors = new Vector4i(
                current.getColor(0),
                current.getColor(90),
                current.getColor(180),
                current.getColor(270)
        );

        for (Map.Entry<Vector4d, ItemEntity> entry : positions.entrySet()) {
            Vector4d position = entry.getKey();
            ItemEntity item = entry.getValue();

            if (position != null) {
                double x = position.x;
                double y = position.y;
                double endX = position.z;
                double endY = position.w;

                double widthPos = endX - x;
                double heightPos = endY - y;

                if (elements.get(0)) {
                    // Отрисовка фона прямоугольника
                    RenderUtils.Render2D.drawMcRect(x - 0.5F, y - 0.5F, endX + 1, y + 1, back);
                    RenderUtils.Render2D.drawMcRect(x - 0.5F, endY - 0.5F, endX + 1, endY + 1, back);
                    RenderUtils.Render2D.drawMcRect(x - 0.5F, y + 1, x + 1, endY - 0.5F, back);
                    RenderUtils.Render2D.drawMcRect(endX - 0.5F, y + 1, endX + 1, endY - 0.5F, back);

                    float size = 1;

                    RenderUtils.Render2D.drawMCHorizontal(x, y, endX, y + 0.5F * size, colors.y, colors.x);
                    RenderUtils.Render2D.drawMCHorizontal(x, endY, endX, endY + 0.5F * size, colors.w, colors.z);
                    RenderUtils.Render2D.drawMCVertical(x, y, x + 0.5F * size, endY + 0.5F * size, colors.w, colors.y);
                    RenderUtils.Render2D.drawMCVertical(endX, y, endX + 0.5F * size, endY + 0.5F * size, colors.z, colors.x);

                }
                if (elements.get(1)) {
                        String tag = (item.getItem().getDisplayName().getString() +
                                (item.getItem().getCount() < 1 ? "" : " x" + item.getItem().getCount()));
                        FontUtils.sfbold[10].drawStringWithShadow(stack, tag, (x + ((endX - x) / 2) - FontUtils.sfbold[10].getWidth(tag) / 2),
                                y - 5, -1);

                }
            }
        }
    }
}
