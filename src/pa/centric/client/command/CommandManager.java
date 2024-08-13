package pa.centric.client.command;

import net.minecraft.util.text.TextFormatting;
import pa.centric.client.command.impl.*;
import pa.centric.client.helper.conduction;
import pa.centric.util.ClientUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager {
    public List<Command> commandList = new ArrayList<>();

    public XRayCommand xRayCommand;
    public boolean isMessage;


    public void init() {
        commandList.addAll(Arrays.asList(
                this.xRayCommand = new XRayCommand(),
                new HClipCommand(),
                new VClipCommand(),
                new HelpCommand(),
                new MacroCommand(),
                new BindCommand(),
                new ConfigCommand(),
                new FriendCommand(),
                new PanicCommand(),
                new LoginCommand(),
                new StaffCommand(),
                new GPSCommand(),
                new ParseCommand(),
                new ToggleCommand()
        ));
    }

    public void runCommands(String message) {
        if (ClientUtils.legitMode || conduction.FUNCTION_MANAGER.noCommands.state) {
            isMessage = false;
            return;
        }

        if (message.startsWith(".")) {
            for (Command command : conduction.COMMAND_MANAGER.getCommands()) {
                if (message.startsWith("." + command.command)) {
                    try {
                        command.run(message.split(" "));
                    } catch (Exception ex) {
                        command.error();
                        ex.printStackTrace();
                    }
                    isMessage = true;
                    return;
                }
            }
            ClientUtils.sendMessage(TextFormatting.RED + "Команда не найдена!");
            ClientUtils.sendMessage(TextFormatting.GRAY + "Список всех команд: .help");
            isMessage = true;

        } else {
            isMessage = false;
        }
    }

    public List<Command> getCommands() {
        return commandList;
    }
}
