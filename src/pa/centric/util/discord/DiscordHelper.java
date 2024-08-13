package pa.centric.util.discord;


import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRichPresence;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import discordrpc.helpers.RPCButton;
import pa.centric.Centric;
import pa.centric.util.IMinecraft;

public class DiscordHelper implements IMinecraft {
    private static final String discordID = "1217114062123434074";
    private static final DiscordRichPresence discordRichPresence = new DiscordRichPresence();
    private static final DiscordRPC discordRPC = DiscordRPC.INSTANCE;

    public static void startRPC() { //        return Arrays.asList("state", "details", "startTimestamp", "endTimestamp", "largeImageKey", "largeImageText", "smallImageKey", "smallImageText", "partyId", "partySize", "partyMax", "matchSecret", "joinSecret", "spectateSecret", "instance");
        try {
            DiscordEventHandlers eventHandlers = new DiscordEventHandlers();
            discordRPC.Discord_Initialize(discordID, eventHandlers, true, null);
            DiscordHelper.discordRichPresence.details = "centric always on top";
            DiscordHelper.discordRichPresence.state = "";
            DiscordHelper.discordRichPresence.largeImageKey = "https://i.imgur.com/RUlvBnE.gif";
            DiscordHelper.discordRichPresence.largeImageText = "build:" + Centric.build;
            discordRPC.Discord_UpdatePresence(discordRichPresence);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void stopRPC() {
        discordRPC.Discord_Shutdown();
    }
}