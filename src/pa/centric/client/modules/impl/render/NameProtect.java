package pa.centric.client.modules.impl.render;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.modules.settings.imp.BooleanOption;
import pa.centric.client.modules.settings.imp.TextSetting;
import pa.centric.events.Event;
import pa.centric.util.ClientUtils;

@ModuleAnnotation(name = "NameProtect", category = Type.Player)
public class NameProtect extends Function {

    public TextSetting name = new TextSetting("Ник", "центрик.tech");
    public BooleanOption friends = new BooleanOption("Друзья", false);


    public NameProtect() {
        addSettings(name, friends);
    }

    @Override
    public void onEvent(Event event) {

    }

    public String patch(String text) {
        String out = text;
        if (this.state) {
            out = text.replaceAll(Minecraft.getInstance().session.getUsername(), name.text);
        }
        return out;
    }

    public ITextComponent patchFriendTextComponent(ITextComponent text, String name) {
        ITextComponent out = text;
        if (this.friends.get() && this.state) {
            out = ClientUtils.replace(text, name, this.name.text);
        }
        return out;
    }
}
