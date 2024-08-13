package pa.centric.client.command.impl;

import net.minecraft.util.text.TextFormatting;
import pa.centric.client.helper.conduction;
import pa.centric.client.modules.Function;
import pa.centric.client.command.Command;
import pa.centric.client.command.CommandInfo;
import pa.centric.util.ClientUtils;

@CommandInfo(name = "t", description = "Включить/выключить модуль.")
public class ToggleCommand extends Command {
    @Override
    public void run(String[] args) throws Exception {
        if (args.length == 2) {
            Function func = conduction.FUNCTION_MANAGER.get(args[1]);
            func.setState(!func.isState());

            if (func.isState()) ClientUtils.sendMessage(TextFormatting.GREEN + "Модуль " + func.name + " включен.");
            else ClientUtils.sendMessage(TextFormatting.RED + "Модуль " + func.name + " выключен.");
        } else {
            ClientUtils.sendMessage(TextFormatting.RED + "Вы указали неверное название модуля!");
        }
    }

    @Override
    public void error() {

    }
}
