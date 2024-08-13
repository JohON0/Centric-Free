package pa.centric.client.helper;

import pa.centric.client.config.ConfigManager;
import pa.centric.client.config.LastAccountConfig;
import pa.centric.client.ui.NotificationRender;
import pa.centric.client.ui.clickgui.Gui;
import pa.centric.proxy.ProxyConnection;
import pa.centric.client.ui.alt.AltConfig;
import pa.centric.client.command.CommandManager;
import pa.centric.client.command.macro.MacroManager;
import pa.centric.client.friend.FriendManager;
import pa.centric.client.modules.FunctionManager;

import pa.centric.client.ui.alt.ui.AltManagerUI;
import pa.centric.client.ui.StyleManager;
import pa.centric.client.ui.themeui.UITheme;

public class conduction {

    public static FunctionManager FUNCTION_MANAGER;
    public static CommandManager COMMAND_MANAGER;
    public static FriendManager FRIEND_MANAGER;
    public static MacroManager MACRO_MANAGER;
    public static LastAccountConfig LAST_ACCOUNT_CONFIG;


    public static StaffManager STAFF_MANAGER;
    public static Gui CLICK_GUI;
    public static UITheme DARK_WHITE_THEME;
    public static ConfigManager CONFIG_MANAGER;
    public static StyleManager STYLE_MANAGER;
    public static NotificationRender NOTIFICATION_MANAGER;
    public static AltManagerUI ALT;
    public static AltConfig ALT_CONFIG;

    public static ProxyConnection PROXY_CONN;
}
