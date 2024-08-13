package pa.centric.client.command.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.vector.Vector3d;
import pa.centric.util.IMinecraft;
import pa.centric.client.command.Command;
import pa.centric.client.command.CommandInfo;

@CommandInfo(name = "hclip", description = "Телепортирует вас вперед.")
public class HClipCommand extends Command {
    @Override
    public void run(String[] args) throws Exception {
        Vector3d tp = Minecraft.getInstance().player.getLook(1F).mul(Double.parseDouble(args[1]), 0, Double.parseDouble(args[1]));
        Minecraft.getInstance().player.setPosition(IMinecraft.mc.player.getPosX() + tp.getX(), IMinecraft.mc.player.getPosY(), IMinecraft.mc.player.getPosZ() + tp.getZ());
    }

    @Override
    public void error() {

    }
}
