package pa.centric.client.command;


import pa.centric.util.ClientUtils;
import pa.centric.util.IMinecraft;

public abstract class Command implements IMinecraft {
    public final String command, description;
    public Command() {
        command = this.getClass().getAnnotation(CommandInfo.class).name();
        description = this.getClass().getAnnotation(CommandInfo.class).description();
    }

    public abstract void run(String[] args) throws Exception;
    public abstract void error();
    public void sendMessage(String message) {
        ClientUtils.sendMessage(message);
    }

}
