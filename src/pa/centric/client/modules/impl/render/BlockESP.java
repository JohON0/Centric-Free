package pa.centric.client.modules.impl.render;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.tileentity.*;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.modules.settings.imp.BooleanOption;
import pa.centric.client.modules.settings.imp.MultiBoxSetting;
import pa.centric.events.Event;
import pa.centric.events.impl.render.EventRender;
import pa.centric.util.render.RenderUtils;

import java.awt.*;


@ModuleAnnotation(name = "BlockESP", category = Type.Render)
public class BlockESP extends Function {

    private final MultiBoxSetting blocksSelection = new MultiBoxSetting("Выбрать блоки",
            new BooleanOption("Сундуки", true),
            new BooleanOption("Эндер Сундуки", true),
            new BooleanOption("Спавнера", true),
            new BooleanOption("Шалкера", true),
            new BooleanOption("Кровати", true),
            new BooleanOption("Вагонетка", true)
    );

    public BlockESP() {
        addSettings(blocksSelection);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventRender e) {
            if (e.isRender3D()) {
                for (TileEntity t : mc.world.loadedTileEntityList) {
                    if (t instanceof ChestTileEntity) {
                        if (blocksSelection.get(0))
                            RenderUtils.Render3D.drawBlockBox(t.getPos(), -1);
                    }
                    if (t instanceof EnderChestTileEntity) {
                        if (blocksSelection.get(1))
                            RenderUtils.Render3D.drawBlockBox(t.getPos(), -1);
                    }
                    if (t instanceof MobSpawnerTileEntity) {
                        if (blocksSelection.get(2))
                            RenderUtils.Render3D.drawBlockBox(t.getPos(), -1);
                    }
                    if (t instanceof ShulkerBoxTileEntity) {
                        if (blocksSelection.get(3))
                            RenderUtils.Render3D.drawBlockBox(t.getPos(), -1);
                    }
                    if (t instanceof BedTileEntity) {
                        if (blocksSelection.get(4))
                            RenderUtils.Render3D.drawBlockBox(t.getPos(), -1);
                    }
                }

                for (Entity entity : mc.world.getAllEntities()) {
                    if (!(entity instanceof MinecartEntity) || !blocksSelection.get(5)) continue;


                    RenderUtils.Render3D.drawBlockBox(entity.getPosition(), -1);

                }

            }
        }
    }
}
