package pa.centric.client.modules.impl.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.modules.settings.Setting;
import pa.centric.client.modules.settings.imp.BooleanOption;
import pa.centric.client.modules.settings.imp.MultiBoxSetting;
import pa.centric.events.Event;
import pa.centric.events.impl.player.EventUpdate;
import pa.centric.util.misc.TimerUtil;
import pa.centric.util.movement.MoveUtil;

import java.util.Iterator;
@ModuleAnnotation(name = "Bypass",category = Type.Util)
public class Bypass extends Function {
    private final MultiBoxSetting mode = new MultiBoxSetting("Режимы", new BooleanOption[]{new BooleanOption("Ускорение на шифте", true), new BooleanOption("Ускорение возле игрока", false)});

    public Bypass() {
        this.addSettings(this.mode);
    }

    public void onEvent(Event event) {
        if (event instanceof EventUpdate) {
            if (this.mode.get("Ускорение на шифте")) {
                if (mc.player.isSneaking()) {
                    if (mc.player.isOnGround()) {
                        MoveUtil.setMotion(0.17);
                    }
                }
            }
            if (this.mode.get("Ускорение возле игрока")) {
                Iterator var2 = mc.world.getPlayers().iterator();
                while(true) {
                    do {
                        do {
                            do {
                                PlayerEntity playerEntity;
                                do {
                                    do {
                                        if (!var2.hasNext()) {
                                            return;
                                        }

                                        playerEntity = (PlayerEntity)var2.next();
                                        Minecraft var10001 = mc;
                                    } while(playerEntity == mc.player);
                                } while(!((double)mc.player.getDistance(playerEntity) < 1.7));
                            } while(!mc.player.isOnGround());
                        } while(mc.gameSettings.keyBindJump.isKeyDown());
                        if (!mc.player.isInWater()) {
                            break;
                        }
                    } while(mc.player.isInLava());

                    MoveUtil.setMotion(0.2);
                }
            }
        }

    }

    public void onDisable() {
        super.onDisable();
    }
}