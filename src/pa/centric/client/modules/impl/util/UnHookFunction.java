package pa.centric.client.modules.impl.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;
import net.optifine.shaders.Shaders;
import org.lwjgl.glfw.GLFW;
import pa.centric.client.helper.conduction;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.settings.imp.BindSetting;
import pa.centric.client.ui.unHookUI;
import pa.centric.util.ClientUtils;
import pa.centric.util.discord.DiscordHelper;
import pa.centric.util.misc.TimerUtil;
import pa.centric.events.Event;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@ModuleAnnotation(name = "UnHookFunction", category = Type.Util)
public class UnHookFunction extends Function {
    public static final List<Function> functionsToBack = new CopyOnWriteArrayList<>();
    public BindSetting unHookKey = new BindSetting("Кнопка возрата", GLFW.GLFW_KEY_HOME);
    public TimerUtil timerUtil = new TimerUtil();

    public UnHookFunction() {
        addSettings(unHookKey);
    }

    @Override
    protected void onEnable() {
        timerUtil.reset();
        Minecraft.getInstance().displayGuiScreen(new unHookUI(new StringTextComponent("Unhoock Function")));
        super.onEnable();
    }

    public void onUnhook() {
        functionsToBack.clear();
        for (int i = 0; i < conduction.FUNCTION_MANAGER.getFunctions().size(); i++) {
            Function function = conduction.FUNCTION_MANAGER.getFunctions().get(i);
            if (function.state && function != this) {
                functionsToBack.add(function);
                function.setState(false);
            }

        }
        mc.fileResourcepacks = new File(System.getenv("appdata") + "\\.minecraft" + "\\resourcepacks");
        Shaders.shaderPacksDir = new File(System.getenv("appdata") + "\\.minecraft" + "\\shaderpacks");
        toggle();

    }

    @Override
    public void onEvent(Event event) {

    }

    @Override
    protected void onDisable() {
        super.onDisable();
    }
}
