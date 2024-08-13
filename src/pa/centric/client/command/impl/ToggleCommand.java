package pa.centric.client.command.impl;

import net.minecraft.util.text.TextFormatting;
import pa.centric.client.helper.conduction;
import pa.centric.client.modules.Function;
import pa.centric.client.command.Command;
import pa.centric.client.command.CommandInfo;
import pa.centric.util.ClientUtils;

@CommandInfo(name = "t", description = "��������/��������� ������.")
public class ToggleCommand extends Command {
    @Override
    public void run(String[] args) throws Exception {
        if (args.length == 2) {
            Function func = conduction.FUNCTION_MANAGER.get(args[1]);
            func.setState(!func.isState());

            if (func.isState()) ClientUtils.sendMessage(TextFormatting.GREEN + "������ " + func.name + " �������.");
            else ClientUtils.sendMessage(TextFormatting.RED + "������ " + func.name + " ��������.");
        } else {
            ClientUtils.sendMessage(TextFormatting.RED + "�� ������� �������� �������� ������!");
        }
    }

    @Override
    public void error() {

    }
}
