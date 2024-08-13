package pa.centric.events.impl.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.MainWindow;
import net.minecraft.client.renderer.ActiveRenderInfo;
import pa.centric.events.Event;

@Getter
@AllArgsConstructor
public final class Render2DEvent extends Event {
    private final MatrixStack matrix;
    private final ActiveRenderInfo activeRenderInfo;
    private final MainWindow mainWindow;
    private final float partialTicks;
}