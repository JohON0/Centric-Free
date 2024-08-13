package pa.centric.client.command.impl;

import net.minecraft.util.text.TextFormatting;
import pa.centric.client.command.macro.Macro;
import pa.centric.client.helper.conduction;
import pa.centric.util.math.KeyMappings;
import pa.centric.client.command.Command;
import pa.centric.client.command.CommandInfo;


@CommandInfo(name = "macro", description = "Позволяет отправить команду по нажатию кнопки")
public class MacroCommand extends Command {

    @Override
    public void run(String[] args) throws Exception {
        if (args.length > 1) {


            switch (args[1]) {
                case "add" -> {
                    String buttonName = args[2].toUpperCase();
                    Integer keycode = null;

                    try {
                        keycode = KeyMappings.keyMap.get(buttonName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (keycode != null) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 3; i < args.length; i++) {
                            sb.append(args[i]).append(" ");
                        }
                        conduction.MACRO_MANAGER.addMacros(new Macro(sb.toString(), keycode));
                        sendMessage(TextFormatting.GREEN + "Добавлен макрос для кнопки" + TextFormatting.RED + " \""
                                + args[2].toUpperCase() + TextFormatting.RED + "\" " + TextFormatting.WHITE + "с командой "
                                + TextFormatting.RED + sb);
                    } else {
                        sendMessage("Не найдена кнопка с названием " + buttonName);
                    }
                }
                case "clear" -> {

                    if (conduction.MACRO_MANAGER.getMacros().isEmpty()) {
                        sendMessage(TextFormatting.RED + "Список макросов пуст");
                    } else {
                        sendMessage(TextFormatting.GREEN + "Список Макросов " + TextFormatting.WHITE + "успешно очищен");
                        conduction.MACRO_MANAGER.getMacros().clear();
                        conduction.MACRO_MANAGER.updateFile();
                    }
                }
                case "remove" -> {
                    String buttonName = args[2].toUpperCase();
                    Integer keycode = null;

                    try {
                        keycode = KeyMappings.keyMap.get(buttonName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (keycode != null) {
                        sendMessage(TextFormatting.GREEN + "Макрос " + TextFormatting.WHITE + "был удален с кнопки "
                                + TextFormatting.RED + "\"" + args[2] + "\"");

                        conduction.MACRO_MANAGER.deleteMacro(keycode);
                    } else {
                        sendMessage("Не найдена кнопка с названием " + buttonName);

                    }
                }
                case "list" -> {
                    if (conduction.MACRO_MANAGER.getMacros().isEmpty()) {
                        sendMessage("Список макросов пуст");
                    } else {
                        sendMessage(TextFormatting.GREEN + "Список макросов: ");
                        conduction.MACRO_MANAGER.getMacros()
                                .forEach(macro -> sendMessage(TextFormatting.WHITE + "Команда: " + TextFormatting.RED
                                        + macro.getMessage() + TextFormatting.WHITE + ", Кнопка: " + TextFormatting.RED
                                        + macro.getKey()));
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
        sendMessage(TextFormatting.WHITE + "." + "macro add " + TextFormatting.GRAY + "<"
                + TextFormatting.RED + "key" + TextFormatting.GRAY + ">" + TextFormatting.GRAY + " message");
        sendMessage(TextFormatting.WHITE + "." + "macro remove " + TextFormatting.GRAY + "<"
                + TextFormatting.RED + "key" + TextFormatting.GRAY + ">");
        sendMessage(TextFormatting.WHITE + "." + "macro list");
        sendMessage(TextFormatting.WHITE + "." + "macro clear");
    }
}
