package other.org.luaj.vm2.lib.custom;

import net.minecraft.client.Minecraft;
import other.org.luaj.vm2.LuaValue;
import other.org.luaj.vm2.lib.OneArgFunction;
import other.org.luaj.vm2.lib.TwoArgFunction;
import other.org.luaj.vm2.lib.ZeroArgFunction;
import pa.centric.util.math.GCDUtil;
import pa.centric.util.misc.HudUtil;

public class MinecraftLib extends TwoArgFunction {
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaValue library = tableOf();
        library.set("getFPS", new fps());
        library.set("getPing", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                return LuaValue.valueOf(HudUtil.calculatePing());
            }
        });
        library.set("getServerIP", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                return LuaValue.valueOf(HudUtil.serverIP());
            }
        });
        library.set("getBPS", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                return LuaValue.valueOf(HudUtil.calculateBPS());
            }
        });
        library.set("getFixSensivity", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                return LuaValue.valueOf(GCDUtil.getSensitivity(arg.tofloat()));
            }
        });
        env.set("minecraft", library);
        return library;
    }

    static class fps extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            return LuaValue.valueOf(Minecraft.debugFPS);
        }
    }

}
