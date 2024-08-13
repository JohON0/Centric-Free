package pa.centric.client.modules.impl.util;

import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.util.text.TextFormatting;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.modules.settings.imp.ModeSetting;
import pa.centric.events.Event;
import pa.centric.events.impl.packet.EventPacket;
import pa.centric.util.ClientUtils;

import java.util.concurrent.TimeUnit;


@ModuleAnnotation(name = "EventsDelay", category = Type.Util)
public class EventDelayModule extends Function {
    private final ModeSetting server = new ModeSetting("Сервер", "FunTime", "FunTime");

    public EventDelayModule() {
        addSettings(server);
    }
    public void onEvent(Event event) {
        if (event instanceof EventPacket packetEvent) {
            if (packetEvent.isReceivePacket() && packetEvent.getPacket() instanceof SChatPacket) {
                SChatPacket packetChat = (SChatPacket)packetEvent.getPacket();
                this.handleReceivePacket(packetChat);
            }
        }

    }

    private int getResult(String message) {
        String[] msgS = message.toLowerCase().split(":");
        String o = msgS[1].replace(" ", "").replace("сек", "");
        return Integer.parseInt(o);
    }

    public String calcTime(String message) {
        int time = this.getResult(message);
        if (time > 60) {
            long min = TimeUnit.SECONDS.toMinutes((long)time);
            long sec = TimeUnit.SECONDS.toSeconds((long)time) - min * 60L;
            return "" + min + " мин. " + sec + " сек.";
        } else {
            return "" + time + " сек.";
        }
    }

    private void handleReceivePacket(SChatPacket packet) {
        String message = TextFormatting.getTextWithoutFormattingCodes(packet.getChatComponent().getString());
        if (message.toLowerCase().contains("[1]") && message.toLowerCase().contains(": ")) {
            String eventtime = this.calcTime(message);
            String msg = null;
            if (server.is("FunTime")) {
                msg =  "До следующего ивента: " + eventtime;
            }
            if (server.is("HolyWorld")) {
                msg =  "Ближайшее событые: через " + eventtime;
            }
            ClientUtils.sendMessage(msg);
        }
    }
}
