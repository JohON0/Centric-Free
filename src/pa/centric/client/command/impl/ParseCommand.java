package pa.centric.client.command.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.util.text.TextFormatting;
import pa.centric.util.ClientUtils;
import pa.centric.client.command.Command;
import pa.centric.client.command.CommandInfo;
import pa.centric.util.IMinecraft;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@CommandInfo(name = "parse", description = "Парсит игроков с таба")
public class ParseCommand extends Command implements IMinecraft {


    @Override
    public void run(String[] args) throws Exception {
        final File PARSE_DIR = new File(Minecraft.getInstance().gameDir, "\\Centric\\Client_1_16_5");

        if (!PARSE_DIR.exists()) {
            PARSE_DIR.mkdirs();
        }
        if (args.length > 1) {
            switch (args[1].toLowerCase()) {
                case "start" -> {
                    Collection<NetworkPlayerInfo> playerInfos = mc.player.connection.getPlayerInfoMap();
                    List<String> donate = new ArrayList<>();

                    File file = new File(PARSE_DIR, mc.getCurrentServerData().serverIP + ".txt");
                    FileWriter fileWriter = new FileWriter(file);

                    for (NetworkPlayerInfo playerInfo : playerInfos) {
                        if (playerInfo.getPlayerTeam().getPrefix().getString().length() >= 3) {
                            String text = TextFormatting.getTextWithoutFormattingCodes(playerInfo.getPlayerTeam().getPrefix().getString().substring(0, playerInfo.getPlayerTeam().getPrefix().getString().length() - 1));
                            if (!donate.contains(text))
                                donate.add(text);
                        }
                    }
                    if (donate.size() == 0) {
                        ClientUtils.sendMessage("Донатеры в табе не найдены!");
                        return;
                    }

                    try {
                        for (String don : donate) {
                            fileWriter.append("// " + don + "\n\n");
                            for (NetworkPlayerInfo playerInfo : playerInfos) {
                                if (playerInfo.getPlayerTeam().getPrefix().getString().contains(don))
                                    fileWriter.append(playerInfo.getGameProfile().getName() + "\n");
                            }
                            fileWriter.append("\n");
                        }

                        fileWriter.flush();
                        fileWriter.close();
                        try {
                            Runtime.getRuntime().exec("explorer " + PARSE_DIR.getAbsolutePath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ClientUtils.sendMessage("Успешно.");
                    } catch (Exception e) {
                        ClientUtils.sendMessage("Ошибка. Обратитесь к разработчику");
                        ClientUtils.sendMessage(e.getMessage());
                    }
                }
                case "dir" -> {
                    try {
                        Runtime.getRuntime().exec("explorer " + PARSE_DIR.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            error();
        }
    }

    @Override
    public void error() {
        sendMessage(TextFormatting.GRAY + "Ошибка в использовании" + TextFormatting.WHITE + ":");
        sendMessage(TextFormatting.WHITE + "." + "parse start" + TextFormatting.GRAY
                + " - запустить парсер");
        sendMessage(TextFormatting.WHITE + "." + "parse dir" + TextFormatting.GRAY
                + " - открыть папку с серверами");
    }
}
