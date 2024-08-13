package pa.centric.client.config;

import com.google.gson.JsonObject;
import net.minecraft.util.text.TextFormatting;
import pa.centric.client.helper.conduction;
import pa.centric.client.ui.midnight.Style;
import pa.centric.util.ClientUtils;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

import static pa.centric.util.IMinecraft.mc;

public final class Config {

    private final File file;
    public String author;

    public Config(String name) {
        this.file = new File(ConfigManager.CONFIG_DIR, name + ".cfg");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public JsonObject save() {
        JsonObject jsonObject = new JsonObject();

        JsonObject modulesObject = new JsonObject();
        conduction.FUNCTION_MANAGER.getFunctions().forEach(module -> modulesObject.add(module.name, module.save()));
        jsonObject.add("Features", modulesObject);

        JsonObject stylesObject = new JsonObject();
        for (Style style : conduction.STYLE_MANAGER.styles) {
            String colors = Arrays.stream(style.colors).mapToObj(String::valueOf).collect(Collectors.joining(","));
            JsonObject styleObject = new JsonObject();
            styleObject.addProperty("color", colors);
            styleObject.addProperty("selected", conduction.STYLE_MANAGER.getCurrentStyle() == style);
            stylesObject.add(style.name, styleObject);
        }
        jsonObject.add("styles", stylesObject);

        JsonObject otherObject = new JsonObject();
        if (!otherObject.has("author"))
            otherObject.addProperty("author", mc.player.getNameClear());
        if (!otherObject.has("time"))
            otherObject.addProperty("time", System.currentTimeMillis());

        jsonObject.add("Others", otherObject);
        return jsonObject;
    }

    public void load(JsonObject object, String configuration, boolean start) {
        if (object.has("Features")) {
            JsonObject modulesObject = object.getAsJsonObject("Features");
            conduction.FUNCTION_MANAGER.getFunctions().forEach(module -> {
                if (!start && module.isState()) {
                    module.setState(false);
                }
                module.load(modulesObject.getAsJsonObject(module.name), start);
            });
        }

        try {
            if (object.has("styles")) {
                JsonObject stylesObject = object.getAsJsonObject("styles");
                for (Style style : conduction.STYLE_MANAGER.styles) {
                    if (stylesObject.has(style.name)) {
                        JsonObject styleObject = stylesObject.getAsJsonObject(style.name);
                        String colors = styleObject.get("color").getAsString();
                        boolean selected = styleObject.get("selected").getAsBoolean();

                        if (selected) {
                            conduction.STYLE_MANAGER.setCurrentStyle(style);
                        }

                        String[] colorArray = colors.split(",");
                        style.colors = Arrays.stream(colorArray).mapToInt(Integer::parseInt).toArray();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        ClientUtils.sendMessage("������������ " + TextFormatting.GRAY + configuration + TextFormatting.RESET + " ���������.");
    }

    public File getFile() {
        return file;
    }
}