package pa.centric.client.command.impl;

import net.minecraft.util.text.TextFormatting;
import pa.centric.client.helper.conduction;
import pa.centric.util.ClientUtils;
import pa.centric.client.command.Command;
import pa.centric.client.command.CommandInfo;

@CommandInfo(name = "help", description = "υελοσες αλÿ")
public class HelpCommand extends Command {
    @Override
    public void run(String[] args) throws Exception {
        for (Command cmd : conduction.COMMAND_MANAGER.getCommands()) {
            if (cmd instanceof HelpCommand) continue;
            ClientUtils.sendMessage(cmd.command + " | " + TextFormatting.GRAY + cmd.description);
        }
    }

    @Override
    public void error() {
    }
}
