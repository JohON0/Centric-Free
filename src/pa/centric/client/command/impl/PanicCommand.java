package pa.centric.client.command.impl;

import pa.centric.client.helper.conduction;
import pa.centric.client.command.Command;
import pa.centric.client.command.CommandInfo;
import pa.centric.util.ClientUtils;

@CommandInfo(name = "panic", description = "Выключает все функции чита")

public class PanicCommand extends Command {
    @Override
    public void run(String[] args) throws Exception {
        if (args.length == 1) {
            conduction.FUNCTION_MANAGER.getFunctions().stream().filter(function -> function.state).forEach(function -> function.setState(false));
            ClientUtils.sendMessage("Выключил все модули!");
        } else error();
    }

    @Override
    public void error() {

    }
}
