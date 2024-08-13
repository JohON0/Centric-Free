package pa.centric.client.ui;

import com.mojang.blaze3d.matrix.MatrixStack;
import pa.centric.util.animations.Animation;
import pa.centric.util.animations.Direction;
import pa.centric.util.font.FontUtils;
import pa.centric.util.render.BloomHelper;
import pa.centric.util.render.ColorUtil;
import pa.centric.util.render.RenderUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.math.vector.Vector4f;
import org.joml.Vector4i;
import pa.centric.client.helper.conduction;
import pa.centric.util.IMinecraft;
import pa.centric.util.animations.impl.DecelerateAnimation;
import pa.centric.util.render.animation.AnimationMath;

import java.util.concurrent.CopyOnWriteArrayList;

import static pa.centric.util.render.ColorUtil.rgba;

public class NotificationRender {

    private final CopyOnWriteArrayList<Notification> notifications = new CopyOnWriteArrayList<>();

    public void add(String text, String content, int time) {
        notifications.add(new Notification(text, content, time));
    }


    public void draw(MatrixStack stack) {
        int yOffset = 0;
        for (Notification notification : notifications) {

            if (System.currentTimeMillis() - notification.getTime() > (notification.time2 * 1000L) - 300) {
                notification.animation.setDirection(Direction.BACKWARDS);
            } else {
                notification.yAnimation.setDirection(Direction.FORWARDS);
                notification.animation.setDirection(Direction.FORWARDS);
            }
            notification.alpha = (float) notification.animation.getOutput();
            if (System.currentTimeMillis() - notification.getTime() > notification.time2 * 1000L) {
                notification.yAnimation.setDirection(Direction.BACKWARDS);
            }
            if (notification.yAnimation.finished(Direction.BACKWARDS)) {
                notifications.remove(notification);
                continue;
            }
            float x = IMinecraft.mc.getMainWindow().scaledWidth() - (FontUtils.sfbold[14].getWidth(notification.getText()) + 8) - 10;
            float y = IMinecraft.mc.getMainWindow().scaledHeight() - 25;
            notification.yAnimation.setEndPoint(yOffset);
            notification.yAnimation.setDuration(300);
            y -= (float) (notification.draw(stack) * notification.yAnimation.getOutput());
            notification.setX(x);
            notification.setY(AnimationMath.fast(notification.getY(), y, 15));
            yOffset++;
        }
    }


    private class Notification {
        @Getter
        @Setter
        private float x, y = IMinecraft.mc.getMainWindow().scaledHeight() + 24;

        @Getter
        private String text;
        @Getter
        private String content;

        @Getter
        private long time = System.currentTimeMillis();

        public Animation animation = new DecelerateAnimation(500, 1, Direction.FORWARDS);
        public Animation yAnimation = new DecelerateAnimation(500, 1, Direction.FORWARDS);
        float alpha;
        int time2 = 3;

        public Notification(String text, String content, int time) {
            this.text = text;
            this.content = content;
            time2 = time;
        }

        public float draw(MatrixStack stack) {
            float width = FontUtils.sfbold[14].getWidth(text) + 8;

            int firstColor = RenderUtils.reAlphaInt(conduction.STYLE_MANAGER.getCurrentStyle().getColor(0), (int) (255 * alpha));
            int secondColor = RenderUtils.reAlphaInt(conduction.STYLE_MANAGER.getCurrentStyle().getColor(100), (int) (255 * alpha));
            int thirdColor = RenderUtils.reAlphaInt(conduction.STYLE_MANAGER.getCurrentStyle().getColor(0), (int) (255 * alpha));
            int fourthColor = RenderUtils.reAlphaInt(conduction.STYLE_MANAGER.getCurrentStyle().getColor(100), (int) (255 * alpha));

            if (false)
                BloomHelper.registerRenderCall(() ->
                        RenderUtils.Render2D.drawRoundedCorner(x + width, y, 4, 20, new Vector4f(0, 0, 3, 3), new Vector4i(
                                firstColor, secondColor, thirdColor, fourthColor)));
             RenderUtils.Render2D.drawShadow(x + 2, y, width + 2, 23, 8, RenderUtils.reAlphaInt( ColorUtil.rgba(31,31,31,100), (int) (255 * alpha)));
            RenderUtils.Render2D.drawRound(x + 2, y, width+2, 23, 3, RenderUtils.reAlphaInt( ColorUtil.rgba(31,31,31,200), (int) (255 * alpha)));
            FontUtils.sfbold[16].drawString(stack, content, x + 6, y + 5, RenderUtils.reAlphaInt( -1, (int) (255 * alpha)));
            FontUtils.sfbold[14].drawString(stack, text, x + 6, y + 16, RenderUtils.reAlphaInt( -1, (int) (255 * alpha)));


            return 25;
        }
    }
}
