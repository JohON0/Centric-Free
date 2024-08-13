package pa.centric.util.font;

import lombok.SneakyThrows;
import pa.centric.Centric;
import pa.centric.util.font.common.Lang;
import pa.centric.util.font.styled.StyledFont;


public class FontUtils {
    public static final String FONT_DIR = "/assets/minecraft/centric/font/";


    public static volatile StyledFont[] sfbold = new StyledFont[130];
    public static volatile StyledFont[] sfmedium = new StyledFont[130];
    public static volatile StyledFont[] sfsemibold = new StyledFont[130];
    public static volatile StyledFont[] icontypes = new StyledFont[130];
    public static volatile StyledFont[] configIcon = new StyledFont[24];
    public static volatile StyledFont[] iconlogo = new StyledFont[131];
    public static volatile StyledFont[] damageicons = new StyledFont[131];

    @SneakyThrows
    public static void init() {
        long time = System.currentTimeMillis();
        for (int i = 8; i < 131;i++) {
            iconlogo[i] = new StyledFont("icon.ttf", i, 0.0f, 0.0f, 0.0f, true, Lang.ENG_RU);
        }
        for (int i = 8; i < 131;i++) {
            damageicons[i] = new StyledFont("damageicons.ttf", i, 0.0f, 0.0f, 0.0f, true, Lang.ENG_RU);
        }
        for (int i = 10; i < 23;i++) {
            configIcon[i] = new StyledFont("configicons.ttf", i, 0.0f, 0.0f, 0.0f, true, Lang.ENG_RU);
        }

        for (int i = 8; i < 50;i++) {
            sfbold[i] = new StyledFont("golos-medium.ttf", i, 0.0f, 0.0f, 0.0f, true, Lang.ENG_RU);
        }
        for (int i = 8; i < 50;i++) {
            sfmedium[i] = new StyledFont("golos-regular.ttf", i, 0.0f, 0.0f, 0.0f, true, Lang.ENG_RU);
        }
        for (int i = 8; i < 50;i++) {
            sfsemibold[i] = new StyledFont("sf_semibold.ttf", i, 0.0f, 0.0f, 0.0f, true, Lang.ENG_RU);
        }
        for (int i = 8; i < 130;i++) {
            icontypes[i] = new StyledFont("icontypes.ttf", i, 0.0f, 0.0f, 0.0f, true, Lang.ENG_RU);
        }
        System.out.println(Centric.prefix + "Ўрифты загрузились за: " + (System.currentTimeMillis() - time) + " миллисекунды!");
    }
}