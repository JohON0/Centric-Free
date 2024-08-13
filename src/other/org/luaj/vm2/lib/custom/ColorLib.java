package other.org.luaj.vm2.lib.custom;

import other.org.luaj.vm2.LuaValue;
import other.org.luaj.vm2.lib.OneArgFunction;
import other.org.luaj.vm2.lib.TwoArgFunction;
import pa.centric.util.render.ColorUtil;

public class ColorLib extends TwoArgFunction {
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaValue library = tableOf();
        library.set("get", new get());
        env.set("color", library);
        return library;
    }

    static class get extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg1) {
            return LuaValue.valueOf(ColorUtil.getColorStyle(arg1.toint()));
        }
    }

}
