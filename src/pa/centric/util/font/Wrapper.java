package pa.centric.util.font;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;

public interface Wrapper {

	Tessellator TESSELLATOR = Tessellator.getInstance();
	BufferBuilder BUILDER = TESSELLATOR.getBuffer();

}
