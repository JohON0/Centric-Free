package pa.centric.client.modules.impl.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.client.CChatMessagePacket;
import pa.centric.client.helper.conduction;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.modules.settings.imp.BooleanOption;
import pa.centric.client.modules.settings.imp.ModeSetting;
import pa.centric.client.modules.settings.imp.SliderSetting;
import pa.centric.events.Event;
import pa.centric.events.impl.player.EventMotion;
import pa.centric.util.ClientUtils;

import java.awt.*;

@ModuleAnnotation(name = "AutoLeave", category = Type.Util)
public class AutoLeave extends Function {

    public SliderSetting range = new SliderSetting("Дистанция", 15, 5, 40, 1);
    public ModeSetting mode = new ModeSetting("Что делать?", "/spawn", "/spawn", "/hub", "kick");
    public BooleanOption health = new BooleanOption("По здоровью", false);
    public SliderSetting healthSlider = new SliderSetting("Здоровье", 10, 5, 20, 1).setVisible(() -> health.get());

    public AutoLeave() {
        addSettings(range, mode, health, healthSlider);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventMotion e) {
            if (health.get()) {
                if (mc.player.getHealth() <= healthSlider.getValue().floatValue()) {
                    if (mode.is("kick")) {
                        mc.player.connection.getNetworkManager().closeChannel(ClientUtils.gradient("Вы вышли с сервера! \n" +" Мало хп!", new Color(121, 208, 255).getRGB(), new Color(96, 133, 255).getRGB()));
                    } else {
                        mc.player.connection.sendPacket(new CChatMessagePacket(mode.get()));
                    }
                }
                setState(false);
                return;
            }

            for (PlayerEntity player : mc.world.getPlayers()) {
                if (player == mc.player) continue;
                if (player.isBot) continue;
                if (conduction.FRIEND_MANAGER.isFriend(player.getGameProfile().getName())) {
                    continue;
                }

                if (mc.player.getDistance(player) <= range.getValue().floatValue()) {
                    if (mode.is("kick")) {
                        mc.player.connection.getNetworkManager().closeChannel(ClientUtils.gradient("Вы вышли с сервера! \n" + player.getGameProfile().getName(), new Color(121, 208, 255).getRGB(), new Color(96, 133, 255).getRGB()));
                    } else {
                        mc.player.connection.sendPacket(new CChatMessagePacket(mode.get()));
                    }
                    setState(false);
                    break;
                }
            }
        }
    }
}
