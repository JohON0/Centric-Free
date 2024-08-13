package other.org.luaj.vm2.lib.custom;

import other.org.luaj.vm2.LuaValue;
import other.org.luaj.vm2.lib.TwoArgFunction;
import other.org.luaj.vm2.lib.ZeroArgFunction;
import pa.centric.client.modules.Function;

public class UtilsLib extends TwoArgFunction {
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaValue library = tableOf();
        library.set("setState", new setstate());
        library.set("currentTimeMillis", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(System.currentTimeMillis());
            }
        });
        env.set("utils", library);
        return library;
    }

    static class setstate extends TwoArgFunction {

        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            Function function = (Function) arg1.checkuserdata();
            function.setState(arg2.toboolean());
            return arg2;
        }
    }

}
