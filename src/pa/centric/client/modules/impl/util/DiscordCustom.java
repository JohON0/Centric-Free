//package pa.centric.client.modules.impl.util;
//
//import club.minnced.discord.rpc.DiscordEventHandlers;
//import club.minnced.discord.rpc.DiscordRichPresence;
//import pa.centric.Centric;
//import pa.centric.client.helper.conduction;
//import pa.centric.client.modules.Function;
//import pa.centric.client.modules.FunctionManager;
//import pa.centric.client.modules.ModuleAnnotation;
//import pa.centric.client.modules.Type;
//import pa.centric.client.modules.settings.imp.ModeSetting;
//import pa.centric.client.ui.NotificationRender;
//import pa.centric.events.Event;
//import pa.centric.util.ClientUtils;
//
//
//@ModuleAnnotation(name = "DiscordRichPresence", category =  Type.Util)
//public class DiscordCustom extends Function {
//    public static final ModeSetting pictureRPC = new ModeSetting("��������", "��� 1", "��� 1", "��� 2", "��� 3", "���", "������");
//    private static final String discordID = "1217114062123434074";
//    private static final DiscordRichPresence discordRichPresence = new DiscordRichPresence();
//    private static final club.minnced.discord.rpc.DiscordRPC discordRPC = club.minnced.discord.rpc.DiscordRPC.INSTANCE;
//    static String link;
//    public DiscordCustom() {
//        addSettings(pictureRPC);
//    }
//
//    @Override
//    public void onEvent(Event event) {
//    }
//    @Override
//    public void onEnable() {
//        conduction.NOTIFICATION_MANAGER.add("����� �������� �������� ����������� ������", "DiscordRichPresence",2);
//        if (pictureRPC.is("��� 1"))
//            link = "https://i.pinimg.com/564x/bb/6a/04/bb6a045c95ca977df5bbd7cb55f497bc.jpg";
//        if (pictureRPC.is("��� 2"))
//            link = "https://i.pinimg.com/564x/69/e9/51/69e951727910022663e006a5dcd75e4d.jpg";
//        if (pictureRPC.is("��� 3"))
//            link = "https://i.pinimg.com/736x/35/1a/51/351a51a0586caf3970adaa2e2a997fda.jpg";
//        if (pictureRPC.is("���"))
//            link = "https://i.imgur.com/aYAqYHR.jpeg";
//        if (pictureRPC.is("������"))
//            link = "https://i.imgur.com/RUlvBnE.gif";
//    }
//    @Override
//    public void onDisable() {
//        DiscordHelper
//    }
//}
