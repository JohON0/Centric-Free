package pa.centric.client.modules.impl.render;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.modules.settings.imp.BooleanOption;
import pa.centric.events.Event;
import pa.centric.events.impl.game.EventMouseTick;
import pa.centric.events.impl.render.EventRender;
import pa.centric.util.render.ColorUtil;
import pa.centric.util.render.MarkerUtils.RenderUtil;

@ModuleAnnotation(name = "BlocksOutline", category = Type.Render)
public class BlocksOutline extends Function {
    public static BooleanOption box = new BooleanOption("Box", true);
    private BlockRayTraceResult result;

    public BlocksOutline() {
        this.addSettings(box);
    }

    @Override
    public void onEvent(Event event) {
        EventMouseTick e;
        if (event instanceof EventMouseTick && (e = (EventMouseTick)event).getButton() == 2 && this.result != null) {
            int i;
            Vector3d vec = Vector3d.copyCenteredHorizontally(this.result.getPos().up());
            for (i = 0; i < 5; ++i) {
            }
            for (i = 0; i < 10; ++i) {
            }
        }
        if (event instanceof EventRender) {
            this.result = (BlockRayTraceResult) Minecraft.getInstance().player.pick(5.0, 1.0f, false);
            if (this.result.getType() == RayTraceResult.Type.MISS) {
                this.result = null;
            }
            if (this.result != null) {
                RenderUtil.Render3D.drawBlockBox(this.result.getPos(), ColorUtil.getColorStyle(90.0f));
            }
        }
    }
}
