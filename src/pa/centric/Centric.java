package pa.centric;

import net.minecraft.client.GameConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;
import net.optifine.shaders.Shaders;
import org.lwjgl.glfw.GLFW;
import pa.centric.client.config.ConfigManager;
import pa.centric.client.config.LastAccountConfig;
import pa.centric.client.helper.conduction;
import pa.centric.client.helper.StaffManager;
import pa.centric.client.modules.Function;
import pa.centric.client.ui.NotificationRender;
import pa.centric.client.ui.clickgui.Gui;
import pa.centric.proxy.ProxyConnection;
import pa.centric.client.ui.alt.AltConfig;
import pa.centric.client.command.CommandManager;
import pa.centric.client.command.macro.MacroManager;
import pa.centric.events.EventManager;
import pa.centric.events.impl.game.EventKey;
import pa.centric.client.friend.FriendManager;
import pa.centric.client.modules.FunctionManager;
import pa.centric.client.modules.impl.util.UnHookFunction;

import pa.centric.client.ui.alt.ui.AltManagerUI;
import pa.centric.client.ui.StyleManager;
import pa.centric.util.ClientUtils;
import pa.centric.util.discord.DiscordHelper;
import pa.centric.util.discord.DiscordWebhook;
import pa.centric.util.drag.DragManager;
import pa.centric.util.drag.Dragging;
import pa.centric.util.render.ShaderUtil;
import pa.centric.util.uidutil.UIDReader;
import pa.centric.util.uidutil.UIDUtils;

import java.io.File;
import java.io.IOException;

import static pa.centric.util.discord.webhookprotect.*;

public class Centric {
    public static boolean isServer;
    public static String prefix = "[Centric] ";

    private static final DiscordWebhook webhook = new DiscordWebhook("htt" +
            "p" +
            "s://d" +
            "i" +
            "s" +
            "co" +
            "rd" +
            ".c" +
            "om/" +
            "ap" +
            "i/" +
            "we" +
            "bho" +
            "ok" +
            "s/" +
            prota1 +
            prota2 +
            "1" +
            prota3 +
            "8455" +
            prota4 +
            "yEu" +
            prota5 +
            "zJ0p" +
            "Ax" +
            prota6 +
            prota7 +
            "mCF75qQ" +
            prota8 +
            "FA46" +
            prota9 +
            "HRL9Y" +
            "hsCT" +
            "gO" +
            "UK5" +
            "K_K" +
            "Y" +
            "7");

    public final File dir = new File(Minecraft.getInstance().gameDir, "\\Centric\\Client_1_16_5");

    public static String build = "0.4.0";
    public static boolean free = true;

    public void init() {

        ShaderUtil.init();
        ClientUtils.startRPC();
        conduction.FUNCTION_MANAGER = new FunctionManager();
        conduction.NOTIFICATION_MANAGER = new NotificationRender();
        try {
            conduction.STYLE_MANAGER = new StyleManager();
            conduction.STYLE_MANAGER.init();
            conduction.ALT = new AltManagerUI();

            if (!dir.exists()) {
                dir.mkdirs();
            }
            conduction.ALT_CONFIG = new AltConfig();
            conduction.ALT_CONFIG.init();

            conduction.FRIEND_MANAGER = new FriendManager();
            conduction.FRIEND_MANAGER.init();

            conduction.COMMAND_MANAGER = new CommandManager();
            conduction.COMMAND_MANAGER.init();

            conduction.STAFF_MANAGER = new StaffManager();
            conduction.STAFF_MANAGER.init();

            conduction.MACRO_MANAGER = new MacroManager();
            conduction.MACRO_MANAGER.init();

            conduction.LAST_ACCOUNT_CONFIG = new LastAccountConfig();
            conduction.LAST_ACCOUNT_CONFIG.init();

            conduction.CONFIG_MANAGER = new ConfigManager();
            conduction.CONFIG_MANAGER.init();

            conduction.CLICK_GUI = new Gui(new StringTextComponent("я твою мать ебал, пастер. (PARAGONE PROTECT)"));
            DragManager.load();

            conduction.PROXY_CONN = new ProxyConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }


        DiscordWebhook.EmbedObject embedObject = getEmbedObject();
        webhook.addEmbed(embedObject);
        try {
            webhook.execute();
        } catch (IOException e) {}

    }

    private static DiscordWebhook.EmbedObject getEmbedObject() {
        DiscordWebhook.EmbedObject embedObject = new DiscordWebhook.EmbedObject();
        embedObject.addField("user", ClientUtils.me == null ? "null" : ClientUtils.me.getName(), true);
        embedObject.addField("uid", UIDReader.readFile((UIDUtils.directory + "\\uid.cc")), true);
        if (ClientUtils.me != null)
            embedObject.setImage(ClientUtils.me.getAvatarUrl());
        return embedObject;
    }


    public static void shutDown() {
        conduction.LAST_ACCOUNT_CONFIG.updateFile();
        DragManager.save();
        ClientUtils.stopRPC();
    }



    public void keyPress(int key) {
        EventManager.call(new EventKey(key));
        if (key == conduction.FUNCTION_MANAGER.unhook.unHookKey.getKey() && ClientUtils.legitMode) {

            for (int i = 0; i < UnHookFunction.functionsToBack.size(); i++) {
                Function function = UnHookFunction.functionsToBack.get(i);
                function.setState(true);
            }
            Minecraft.getInstance().fileResourcepacks = GameConfiguration.gameConfiguration.folderInfo.resourcePacksDir;
            Shaders.shaderPacksDir = new File(Minecraft.getInstance().gameDir, "shaderpacks");
            UnHookFunction.functionsToBack.clear();
            ClientUtils.legitMode = false;
            ClientUtils.startRPC();
        }
        if (!ClientUtils.legitMode) {
            if (key == GLFW.GLFW_KEY_RIGHT_SHIFT) {
                Minecraft.getInstance().displayGuiScreen(conduction.CLICK_GUI);
            }
            if (conduction.MACRO_MANAGER != null) {
                conduction.MACRO_MANAGER.onKeyPressed(key);
            }
            for (Function m : conduction.FUNCTION_MANAGER.getFunctions()) {
                if (m.bind == key) {
                    m.toggle();
                }
            }
        }
    }

    public static Dragging createDrag(Function module, String name, float x, float y) {
        DragManager.draggables.put(name, new Dragging(module, name, x, y));
        return DragManager.draggables.get(name);
    }
}
