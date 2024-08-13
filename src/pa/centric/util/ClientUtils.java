package pa.centric.util;

import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.Packet;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.entities.User;
import com.jagrosh.discordipc.entities.pipe.PipeStatus;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.server.SUpdateBossInfoPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Util;
import net.minecraft.util.text.*;
import org.lwjgl.glfw.GLFW;
import pa.centric.Centric;
import pa.centric.client.helper.conduction;
import pa.centric.client.modules.FunctionManager;
import pa.centric.util.math.KeyMappings;
import pa.centric.util.render.ColorUtil;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ClientUtils implements IMinecraft {

    public static User me;

    private static final String discordID = "1217114062123434074";
    private static final DiscordRichPresence discordRichPresence = new DiscordRichPresence();
    private static final DiscordRPC discordRPC = DiscordRPC.INSTANCE;
    public static ServerData serverData;
    public static boolean legitMode = false;
    private static boolean pvpMode;
    private static UUID uuid;
    private static Clip currentClip = null;

    public static String getKey(int integer) {
        if (integer < 0) {
            return switch (integer) {
                case -100 -> I18n.format("key.mouse.left");
                case -99 -> I18n.format("key.mouse.right");
                case -98 -> I18n.format("key.mouse.middle");
//                case -101 -> I18n.format("key.mouse.4");
//                case -102 -> I18n.format("key.mouse.5");
                default -> "MOUSE" + (integer + 101);

            };
        } else {
            return (GLFW.glfwGetKeyName(integer, -1) == null ? KeyMappings.reverseKeyMap.get(integer) : GLFW.glfwGetKeyName(integer, -1)) ;
        }
    }

    static IPCClient client = new IPCClient(1217114062123434074L);

    public static void startRPC() {
        FunctionManager functionManager = conduction.FUNCTION_MANAGER;

        client.setListener(new IPCListener(){
            @Override
            public void onPacketReceived(IPCClient client, Packet packet) {
                IPCListener.super.onPacketReceived(client, packet);
            }

            @Override
            public void onReady(IPCClient client)
            {
                RichPresence.Builder builder = new RichPresence.Builder();
                builder.setDetails("centric always on top")
                        .setStartTimestamp(OffsetDateTime.now())
                        .setLargeImage("https://i.imgur.com/RUlvBnE.gif", "version: " + Centric.build);
            client.sendRichPresence(builder.build());
            }
        });
        try {
            client.connect();
        } catch (NoDiscordClientException e) {
            System.out.println(Centric.prefix + "DiscordRPC: " + e.getMessage());
        }
    }

    public static void updateBossInfo(SUpdateBossInfoPacket packet) {
        if (packet.getOperation() == SUpdateBossInfoPacket.Operation.ADD) {
            if (StringUtils.stripControlCodes(packet.getName().getString()).toLowerCase().contains("pvp")) {
                pvpMode = true;
                uuid = packet.getUniqueId();
            }
        } else if (packet.getOperation() == SUpdateBossInfoPacket.Operation.REMOVE) {
            if (packet.getUniqueId().equals(uuid))
                pvpMode = false;
        }
    }

    public static boolean isPvP() {
        return pvpMode;
    }

    public static void stopRPC() {
        if (client.getStatus() == PipeStatus.CONNECTED)
            client.close();
    }

    public static void playSound(String sound, float value, boolean nonstop) {
        if (currentClip != null && currentClip.isRunning()) {
            currentClip.stop();
        }
        try {
            currentClip = AudioSystem.getClip();
            InputStream is = mc.getResourceManager().getResource(new ResourceLocation("centric/sounds/clientsounds/" + sound + ".wav")).getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bis);
            if (audioInputStream == null) {
                System.out.println(Centric.prefix + "Sound not found!");
                return;
            }

            currentClip.open(audioInputStream);
            currentClip.start();
            FloatControl floatControl = (FloatControl) currentClip.getControl(FloatControl.Type.MASTER_GAIN);
            float min = floatControl.getMinimum();
            float max = floatControl.getMaximum();
            float volumeInDecibels = (float) (min * (1 - (value / 100.0)) + max * (value / 100.0));
            floatControl.setValue(volumeInDecibels);
            if (nonstop) {
                currentClip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        currentClip.setFramePosition(0);
                        currentClip.start();
                    }
                });
            }
        } catch (Exception exception) {
            // Обработка исключения
            exception.printStackTrace();
        }
    }

    public void stopSound() {
        if (currentClip != null) {
            currentClip.stop();
            currentClip.close();
            currentClip = null;
        }
    }
    public static boolean isConnectedToServer(String ip) {
        return mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP != null && mc.getCurrentServerData().serverIP.contains(ip);
    }
    private List<String> getOnlinePlayers() {
        return mc.player.connection.getPlayerInfoMap().stream()
                .map(NetworkPlayerInfo::getGameProfile)
                .map(GameProfile::getName)
                .collect(Collectors.toList());
    }
    public static void sendMessage(String message) {
        if (mc.player == null) return;
        mc.player.sendMessage(gradient("Centric", conduction.STYLE_MANAGER.getCurrentStyle().getColor(100), conduction.STYLE_MANAGER.getCurrentStyle().getColor(200)).append(new StringTextComponent(TextFormatting.DARK_GRAY + " -> " + TextFormatting.RESET + message)), Util.DUMMY_UUID);
    }
    public static StringTextComponent gradient(String message, int first, int end) {

        StringTextComponent text = new StringTextComponent("");
        for (int i = 0; i < message.length(); i++) {
            text.append(new StringTextComponent(String.valueOf(message.charAt(i))).setStyle(Style.EMPTY.setColor(new Color(ColorUtil.interpolateColor(first, end, (float) i / message.length())))));
        }

        return text;

    }

    public static ITextComponent replace(ITextComponent original, String find, String replaceWith) {
        if (original == null || find == null || replaceWith == null) {
            return original;
        }

        String originalText = original.getString();
        String replacedText = originalText.replace(find, replaceWith);
        return new StringTextComponent(replacedText);
    }
}
