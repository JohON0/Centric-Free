package pa.centric.client.command.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.RandomStringUtils;
import pa.centric.client.command.Command;
import pa.centric.client.command.CommandInfo;

import java.util.UUID;


@CommandInfo(name = "rename", description = "Позволяет сменить ник прямо во время игры")

public class LoginCommand extends Command {
    @Override
    public void run(String[] args) throws Exception {
        String username;
        if (args[1].equalsIgnoreCase("rand")) {
            username = "Cc" + RandomStringUtils.randomAlphabetic(5);
        } else if (args.length == 2 && args[1].length() < 20) {
            username = args[1];
        } else {
            error();
            return;
        }
        String uuid = UUID.randomUUID().toString();
        Minecraft.getInstance().session = new Session(username, uuid, "", "mojang");
        sendMessage(TextFormatting.GREEN + "Вы успешно вошли как " + TextFormatting.WHITE + Minecraft.getInstance().session.getUsername());
    }

    @Override
    public void error() {
        sendMessage(TextFormatting.GRAY + "Ошибка в использовании" + TextFormatting.WHITE + ":");

        sendMessage(TextFormatting.WHITE + ".l " + TextFormatting.GRAY + "<"
                + "name" + TextFormatting.GRAY + ">");

        sendMessage(TextFormatting.WHITE + ".l rand" + TextFormatting.GRAY + " (Генерирует случайное имя)");
    }
}
