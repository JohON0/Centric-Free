package pa.centric.client.modules.impl.render;

import net.minecraft.client.MainWindow;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.util.math.MathHelper;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.helper.conduction;
import pa.centric.client.modules.settings.imp.ModeSetting;
import pa.centric.util.render.animation.AnimationMath;
import pa.centric.events.Event;
import pa.centric.events.impl.render.EventRender;
import pa.centric.client.ui.midnight.Style;

import static pa.centric.util.render.ColorUtil.*;
import static pa.centric.util.render.RenderUtils.Render2D.*;


@ModuleAnnotation(name = "Crosshair", category = Type.Render)
public class Crosshair extends Function {
    private final ModeSetting mode = new ModeSetting("Тип", "Обычный", "Обычный", "Кольцо", "Орбиз");
    private float circleAnimation = 0.0F;

    public Crosshair () {
        addSettings(mode);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventRender e) {
            update();
        }
    }

    public void update() {
        switch (mode.get()) {
            case "Обычный":
                defaultmode();
                break;
            case "Кольцо":
                koltsomode();
                break;
            case "Орбиз":
                orbizmode();
                break;
        }
    }
    private void defaultmode() {
        if (mc.gameSettings.getPointOfView() != PointOfView.FIRST_PERSON) {
            return;
        }

        final MainWindow mainWindow = mc.getMainWindow();

        final float x = (float) mainWindow.scaledWidth() / 2.0F;
        final float y = (float) mainWindow.scaledHeight() / 2.0F;

        final float calculateCooldown = mc.player.getCooledAttackStrength(1.0F);
        final float endRadius = MathHelper.clamp(calculateCooldown * 360, 0, 360);

        this.circleAnimation = AnimationMath.lerp(this.circleAnimation, -endRadius, 4);

        final int mainColor = rgba(30, 30, 30, 255);
        Style style = conduction.STYLE_MANAGER.getCurrentStyle();

        drawCircle(x, y, 0, 360, 3.5f, 3, false, mainColor);
        drawCircle(x, y, 0, circleAnimation, 3.5f, 3, false, style);
    }

    private void koltsomode() {
        if (mc.gameSettings.getPointOfView() != PointOfView.FIRST_PERSON) {
            return;
        }

        final MainWindow mainWindow = mc.getMainWindow();

        final float x = (float) mainWindow.scaledWidth() / 2.0F;
        final float y = (float) mainWindow.scaledHeight() / 2.0F;

        final float calculateCooldown = mc.player.getCooledAttackStrength(1.0F);
        final float endRadius = MathHelper.clamp(calculateCooldown * 360, 0, 360);

        this.circleAnimation = AnimationMath.lerp(this.circleAnimation, -endRadius, 4);

        final int mainColor = rgba(30, 30, 30, 255);
        Style style = conduction.STYLE_MANAGER.getCurrentStyle();

        drawCircle(x, y, 0, 360, 3.5f, 2.5F, false, style);
    }
    private void orbizmode() {
        if (mc.gameSettings.getPointOfView() != PointOfView.FIRST_PERSON) {
            return;
        }

        final MainWindow mainWindow = mc.getMainWindow();

        final float x = (float) mainWindow.scaledWidth() / 2.0F;
        final float y = (float) mainWindow.scaledHeight() / 2.0F;

        final float calculateCooldown = mc.player.getCooledAttackStrength(1.0F);
        final float endRadius = MathHelper.clamp(calculateCooldown * 360, 0, 360);

        this.circleAnimation = AnimationMath.lerp(this.circleAnimation, -endRadius, 4);

        final int mainColor = rgba(30, 30, 30, 255);
        Style style = conduction.STYLE_MANAGER.getCurrentStyle();

        drawCircle(x, y, 0, 360, 2.5f, 0.5F, true, style);
    }
}