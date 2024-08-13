package pa.centric.util.sound;

import net.minecraft.util.SoundEvents;
import pa.centric.util.IMinecraft;


public class SoundUtil implements IMinecraft {
    public static void playSound(float pitch, float volume) {
        if (mc.player == null)
            return;
        mc.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING, volume, pitch);
    }

}
