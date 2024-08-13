package other.org.luaj.vm2.lib;

import other.org.luaj.vm2.LuaValue;

class TableLibFunction extends LibFunction {
	public LuaValue call() {
		return argerror(1, "table expected, got no value");
	}
}
