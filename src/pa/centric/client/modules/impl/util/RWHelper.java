package pa.centric.client.modules.impl.util;

import com.google.common.eventbus.Subscribe;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.network.play.client.CChatMessagePacket;
import net.minecraft.network.play.server.SUpdateBossInfoPacket;
import net.minecraft.util.text.TextFormatting;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.modules.settings.imp.MultiBoxSetting;
import pa.centric.events.Event;
import pa.centric.events.impl.packet.EventPacket;
import pa.centric.client.modules.settings.imp.BooleanOption;
import pa.centric.util.ClientUtils;
import pa.centric.util.StopWatch;

import java.awt.*;
import java.util.UUID;

@ModuleAnnotation(name = "RWAssistent", category = Type.Util)
public class RWHelper extends Function {
    boolean joined;
    StopWatch stopWatch = new StopWatch();
    private final MultiBoxSetting s = new MultiBoxSetting("�������",
            new BooleanOption("����������� ����������� �����", true),
            new BooleanOption("��������� ����", true),
            new BooleanOption("���� �����", true));
    private UUID uuid;
    int x = -1;
    int z = -1;
    private TrayIcon trayIcon;
    String[] banWords = new String[]{"�����", "���������", "������",
            "�������", "������", "������", "���������", "���������",
            "�����", "�����", "�������", "expa", "celka",
            "nurik", "expensive", "celestial", "nursultan",
            "������", "funpay", "fluger",
            "������", "akrien", "�������",
            "ft", "funtime", "���������", "rich", "���",
            "��� ������", "wild", "����", "excellent",
            "���������", "hvh", "���", "matix", "impact",
            "������", "������", "wurst"};

    public RWHelper() {
        this.addSettings(this.s);
    }

    @Subscribe
    public void onEvent(Event event) {
            if (event instanceof EventPacket eventPacket) {
                if (s.get("����������� ����������� �����")) {
                if (((EventPacket) eventPacket).isSendPacket()) {
                    if (((EventPacket) event).getPacket() instanceof CChatMessagePacket p) {
                        boolean contains = false;
                        for (String str : banWords) {
                            if (!p.getMessage().toLowerCase().contains(str)) continue;
                            contains = true;
                            break;

                        }
                        if (contains) {
                            ClientUtils.sendMessage(TextFormatting.RED + " ���������� ����������� ����� � ����� ���������. " +
                                    "�������� ��������, ����� �������� ���� �� ReallyWorld.");
                            event.setCancel(true);
                        }
                    }
                }
            }
            if (eventPacket.isReceivePacket()) {
                IPacket<IServerPlayNetHandler> p;
                IPacket<?> iPacket;
                iPacket = eventPacket.getPacket();
                if (iPacket instanceof SUpdateBossInfoPacket) {
                    SUpdateBossInfoPacket packet = (SUpdateBossInfoPacket) iPacket;
                    if (s.get("���� �����")) {
                        this.updateBossInfo(packet);
                    }
                }
            }
        }
    }

    public void updateBossInfo(SUpdateBossInfoPacket packet) {
        if (packet.getOperation() == SUpdateBossInfoPacket.Operation.ADD) {
            String name = packet.getName().getString().toLowerCase().replaceAll("\\s+", " ");
            if (name.contains("�������")) {
                this.parseAirDrop(name);
                this.uuid = packet.getUniqueId();
            } else if (name.contains("��������")) {
                this.parseMascot(name);
                this.uuid = packet.getUniqueId();
            } else if (name.contains("������")) {
                this.parseScrooge(name);
                this.uuid = packet.getUniqueId();
            }
        } else if (packet.getOperation() == SUpdateBossInfoPacket.Operation.REMOVE && packet.getUniqueId().equals(this.uuid)) {
            this.resetCoordinatesAndRemoveWaypoints();
        }
    }

    private void parseAirDrop(String name) {
        this.x = RWHelper.extractCoordinate(name, "x: ");
        this.z = RWHelper.extractCoordinate(name, "z: ");
        RWHelper.mc.player.sendChatMessage(".gps add " + this.x + " 100 " + this.z);
    }

    private void parseMascot(String name) {
        String[] words = name.split("\\s+");
        for (int i = 0; i < words.length; ++i) {
            if (!RWHelper.isInteger(words[i]) || i + 1 >= words.length || !RWHelper.isInteger(words[i + 1])) continue;
            this.x = Integer.parseInt(words[i]);
            this.z = Integer.parseInt(words[i + 1]);
            RWHelper.mc.player.sendChatMessage(".gps add " + this.x + " 100 " + this.z);
        }
    }

    private void parseScrooge(String name) {
        int startIndex = name.indexOf("����������");
        if (startIndex == -1) {
            return;
        }
        String coordinatesSubstring = name.substring(startIndex + "����������".length()).trim();
        String[] words = coordinatesSubstring.split("\\s+");
        if (words.length >= 2) {
            this.x = Integer.parseInt(words[0]);
            this.z = Integer.parseInt(words[1]);
            RWHelper.mc.player.sendChatMessage(".gps add " + this.x + " 100 " + this.z);
        }
    }

    private void resetCoordinatesAndRemoveWaypoints() {
        this.x = 0;
        this.z = 0;
        RWHelper.mc.player.sendChatMessage(".gps remove ");
    }

    private static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static int extractCoordinate(String text, String coordinateIdentifier) {
        int coordinateStartIndex = text.indexOf(coordinateIdentifier);
        if (coordinateStartIndex != -1) {
            int coordinateValueStart = coordinateStartIndex + coordinateIdentifier.length();
            int coordinateValueEnd = text.indexOf(" ", coordinateValueStart);
            if (coordinateValueEnd == -1) {
                coordinateValueEnd = text.length();
            }
            String coordinateValueString = text.substring(coordinateValueStart, coordinateValueEnd);
            return Integer.parseInt(coordinateValueString.trim());
        }
        return 0;
    }
}