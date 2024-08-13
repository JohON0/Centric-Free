package pa.centric.client.command.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import org.joml.Vector4i;
import pa.centric.client.helper.conduction;
import pa.centric.util.ClientUtils;
import pa.centric.util.font.FontUtils;
import pa.centric.util.render.ColorUtil;
import pa.centric.util.render.RenderUtils;
import pa.centric.client.command.Command;
import pa.centric.client.command.CommandInfo;

import java.awt.*;

import static pa.centric.util.render.RenderUtils.Render2D.drawShadow;
import static pa.centric.util.render.RenderUtils.Render2D.drawTriangle;

@CommandInfo(name = "gps", description = "������������ ���� �� ���������")
public class GPSCommand extends Command {

    public static boolean enabled;

    public static Vector3d vector3d;

    @Override
    public void run(String[] args) throws Exception {
        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("off")) {
                double dst = Math.sqrt(Math.pow(vector3d.x - mc.player.getPosX(), 2) + Math.pow(vector3d.z - mc.player.getPosZ(), 2));
                ClientUtils.sendMessage(TextFormatting.GRAY + "��������� ��������!");
                enabled = false;
                vector3d = null;
                return;
            }
            if (args.length == 3) {
                int x = Integer.parseInt(args[1]), y = Integer.parseInt(args[2]);
                enabled = true;
                vector3d = new Vector3d(x, 0, y);
                ClientUtils.sendMessage(TextFormatting.GRAY + "��������� �������! ���������� " + x + ";" + y);
            }
        } else {
            error();
        }
    }

    public static void drawArrow(MatrixStack stack) {
        if (!enabled)
            return;

        double x = vector3d.x - mc.getRenderManager().info.getProjectedView().getX();
        double z = vector3d.z - mc.getRenderManager().info.getProjectedView().getZ();

        double cos = MathHelper.cos(mc.player.rotationYaw * (Math.PI * 2 / 360));
        double sin = MathHelper.sin(mc.player.rotationYaw * (Math.PI * 2 / 360));
        double rotY = -(z * cos - x * sin);
        double rotX = -(x * cos + z * sin);
        double dst = Math.sqrt(Math.pow(vector3d.x - mc.player.getPosX(), 2) + Math.pow(vector3d.z - mc.player.getPosZ(), 2));

        float angle = (float) (Math.atan2(rotY, rotX) * 180 / Math.PI);
        double x2 = 75 * MathHelper.cos(Math.toRadians(angle)) + mc.getMainWindow().getScaledWidth() / 2f;
        double y2 = 75 * (mc.player.rotationPitch / 90) * MathHelper.sin(Math.toRadians(angle)) + mc.getMainWindow().getScaledHeight() / 2f;

        GlStateManager.pushMatrix();
        GlStateManager.disableBlend();
        GlStateManager.translated(x2, y2, 0);
        GlStateManager.rotatef(angle, 0, 0, 1);

        int clr = conduction.STYLE_MANAGER.getCurrentStyle().getColor(100);

//        RenderUtils.Render2D.drawShadow(-3F, -3F, 8, 6F, 8, clr);
        RenderUtils.Render2D.drawImage(new ResourceLocation("centric/images/arrows.png"), -15F, -9F, 18.0F, 18.0F, new Vector4i(Color.BLACK.getRGB(),clr,Color.BLACK.getRGB(),clr));

        GlStateManager.rotatef(90, 0, 0, 1);
        FontUtils.sfbold[14].drawCenteredString(stack, (int) dst + "m", 0, 15, ColorUtil.rgba(230,230,230,255));

        GlStateManager.enableBlend();
        GlStateManager.popMatrix();
    }

    @Override
    public void error() {
        sendMessage(TextFormatting.GRAY + "������ � �������������" + TextFormatting.WHITE + ":");
        sendMessage(TextFormatting.WHITE + ".gps " + TextFormatting.GRAY + "<"
                + TextFormatting.RED + "x, z" + TextFormatting.GRAY + ">");
        sendMessage(TextFormatting.WHITE + ".gps " + TextFormatting.GRAY + "<"
                + TextFormatting.RED + "off" + TextFormatting.GRAY + ">");
    }
}
