package pa.centric.client.command.impl;

import net.minecraft.client.Minecraft;
import pa.centric.util.IMinecraft;
import pa.centric.client.command.Command;
import pa.centric.client.command.CommandInfo;

@CommandInfo(name = "vclip", description = "Телепортирует вас вверх.")
public class VClipCommand extends Command {
    @Override
    public void run(String[] args) throws Exception {
        Minecraft.getInstance().player.setPosition(IMinecraft.mc.player.getPosX(), IMinecraft.mc.player.getPosY() + Double.parseDouble(args[1]), IMinecraft.mc.player.getPosZ());
    }

    @Override
    public void error() {

    }
}
