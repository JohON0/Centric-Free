package pa.centric.client.modules.impl.render;

import lombok.Getter;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.world.GameType;
import pa.centric.Centric;
import pa.centric.client.helper.conduction;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.modules.settings.imp.BooleanOption;
import pa.centric.client.modules.settings.imp.MultiBoxSetting;
import pa.centric.client.ui.midnight.Style;
import pa.centric.util.animations.Animation;
import pa.centric.util.animations.Direction;
import pa.centric.util.animations.impl.EaseInOutQuad;
import pa.centric.util.render.ColorUtil;
import pa.centric.util.render.RenderUtils;
import pa.centric.util.render.SmartScissor;
import pa.centric.util.render.StencilUtil;
import pa.centric.util.render.animation.AnimationMath;
import pa.centric.util.font.FontUtils;
import pa.centric.util.math.MathUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import pa.centric.events.Event;
import pa.centric.events.impl.player.EventUpdate;
import pa.centric.events.impl.render.EventRender;
import pa.centric.util.ClientUtils;
import pa.centric.util.drag.Dragging;
import pa.centric.util.font.styled.StyledFont;
import pa.centric.util.misc.HudUtil;
import pa.centric.util.misc.TimerUtil;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import pa.centric.util.uidutil.UIDReader;
import pa.centric.util.uidutil.UIDUtils;
import pa.centric.util.world.BetterText;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static net.minecraft.world.GameType.*;
import static net.optifine.CustomGuiProperties.EnumContainer.CREATIVE;
import static pa.centric.client.modules.impl.render.Hud.Status.*;

@ModuleAnnotation(name = "Hud", category = Type.Render)
public class Hud extends Function {


    public MultiBoxSetting elements = new MultiBoxSetting("Элементы",
            new BooleanOption("Логотип", true),
            new BooleanOption("Список модулей", true),
            new BooleanOption("Список модераторов", true),
            new BooleanOption("Список зелий", true),
            new BooleanOption("Уведомления", true),
            new BooleanOption("Таймер индикатор", true),
            new BooleanOption("Таргет Худ", true),
            new BooleanOption("Кейбинды", true),
            new BooleanOption("Броня", true),
            new BooleanOption("Информация", true),
            new BooleanOption("Инвентарь", true),
            new BooleanOption("Хотбар", true)
            );
//            new BooleanOption("Писюн", true));

    public MultiBoxSetting limitations = new MultiBoxSetting("Ограничения",
            new BooleanOption("Скрывать Combat", true),
            new BooleanOption("Только Movement",false),
            new BooleanOption("Только Render",false),
            new BooleanOption("Только Player",false),
            new BooleanOption("Только Util",false),
            new BooleanOption("Только бинды",false).setVisible(() -> elements.get(1)));
    private final TimerUtil timerUtil;
    int kills;
    public Hud() {
        this.timerUtil = new TimerUtil();
        this.kills = 0;
        addSettings(elements, limitations);
    }


    @Override
    public void onEvent(final Event event) {
        if (elements.get(10) && conduction.FUNCTION_MANAGER.auraFunction.target instanceof PlayerEntity && conduction.FUNCTION_MANAGER.auraFunction.target != null && conduction.FUNCTION_MANAGER.auraFunction.target != mc.player && !conduction.FUNCTION_MANAGER.auraFunction.target.isAlive()) {
            this.kills++;
            this.timerUtil.reset();
        }
        if (event instanceof EventUpdate && elements.get(2)) {
            staffPlayers.clear();

            for (ScorePlayerTeam team : mc.world.getScoreboard().getTeams().stream().sorted(Comparator.comparing(Team::getName)).collect(Collectors.toList())) {
                String name = team.getMembershipCollection().toString();
                name = name.substring(1, name.length() - 1);
                if (namePattern.matcher(name).matches()) {
                    if (prefixMatches.matcher(team.getPrefix().getString().toLowerCase(Locale.ROOT)).matches() || conduction.STAFF_MANAGER.isStaff(name)) {
                        staffPlayers.add(new StaffPlayer(name, team.getPrefix()));                    }
                }
            }
        }


        if (event instanceof EventRender) {
            EventRender eventRender = (EventRender) event;
            if (eventRender.isRender2D()) {
                onRender(eventRender);
            }
        }
    }

    private void onRender(final EventRender renderEvent) {
        final MatrixStack stack = renderEvent.matrixStack;

        if (elements.get(0)) {
            waterMark(stack);
        }

        if (elements.get(1)) {
            arrayList(stack);
        }

        if (elements.get(2)) {
            staffList(stack);
        }

        if (elements.get(3)) {
            potionStatus(stack);
        }

        if (elements.get(5)) {
            timerHUD(stack);
        }

        if (elements.get(6)) {
            targetHUD(stack);
        }

        if (elements.get(7)) {
            keyBinds(stack);
        }
        if (elements.get(8)) {
            armor(renderEvent);
        }
        if (elements.get(9)) {
            information(stack, renderEvent);
        }
        if (elements.get(10)) {
            inventoryhud(stack);
        }
//        if (elements.get(12)) {
//            penis(stack);
//        }
    }

    public Dragging keyBinds = Centric.createDrag(this, "Keybinds", 200, 50);
    private float heightDynamic = 0;
    private int activeModules = 0;

    private void keyBinds(final MatrixStack stack) {
        float posX = keyBinds.getX();
        float posY = keyBinds.getY();

        int headerHeight = 16;
        int width = 90;
        int padding = 4;
        int offset = 12;

        float height = activeModules * offset;
        this.heightDynamic = AnimationMath.fast(this.heightDynamic, height, 10);

        RenderUtils.Render2D.drawShadow(posX, posY, width, headerHeight + heightDynamic + padding / 2 + 2, 8, ColorUtil.rgba(20, 20, 20, 50), ColorUtil.rgba(20, 20, 20, 50));
        RenderUtils.Render2D.drawShadow(posX, posY, width, headerHeight + heightDynamic + padding / 2 + 2, 4, ColorUtil.rgba(20, 20, 20, 50), ColorUtil.rgba(20, 20, 20, 50));
        RenderUtils.Render2D.drawRoundedCorner(posX, posY, width, headerHeight + heightDynamic + padding / 2 + 2, 5, ColorUtil.rgba(20, 20, 20, 200));
        //RenderUtils.Render2D.drawRoundedCorner(posX + 1, posY + 1 + headerHeight, width - 2, heightDynamic + padding / 2 + 2 - 2, 5, ColorUtil.rgba(25, 25, 25, 150));
        FontUtils.sfbold[16].drawCenteredString(stack, ClientUtils.gradient("Keybinds", conduction.STYLE_MANAGER.getCurrentStyle().getColor(100), conduction.STYLE_MANAGER.getCurrentStyle().getColor(200)), posX + width / 2, posY + 6.5f, ColorUtil.rgba(150, 150, 150, 150));

        SmartScissor.push();
        SmartScissor.setFromComponentCoordinates(posX, posY + 2, width, headerHeight + heightDynamic + padding / 2f + 2);
        int index = 0;
        for (Function f : conduction.FUNCTION_MANAGER.getFunctions()) {
            if (f.bind != 0 && f.state) {
                String text = ClientUtils.getKey(f.bind);

                if (text == null) {
                    continue;
                }

                if (text.length() > 6) {
                    text = text.substring(0, 6);
                }

                String bindText = text.toUpperCase();
                float bindWidth = FontUtils.sfbold[16].getWidth(bindText);
                float y = posY + 2 + headerHeight + padding + (index * offset);

                FontUtils.sfsemibold[14].drawString(stack, f.name, posX + padding, y, -1);
                FontUtils.sfsemibold[14].drawString(stack, "["+bindText + "]", posX - 6 + width - bindWidth - padding, y, -1);

                index++;
            }
        }

        SmartScissor.unset();
        SmartScissor.pop();

        activeModules = index;
        keyBinds.setWidth(width);
        keyBinds.setHeight(activeModules * offset + headerHeight);
    }

    public Dragging inventory = Centric.createDrag(this, "Inventory", 500, 50);
    private void inventoryhud(MatrixStack stack) {
        //code by attack.dev
        float x = this.inventory.getX();
        float y = this.inventory.getY();
        float width = 16.0F;
        float height = 16.0F;
        float y1 = 17.0F;
        float x1 = 0.7F;
        int headerHeight = 16;
        int padding = 4;
        RenderUtils.Render2D.drawShadow(x - 1, y - 18, width + 138, headerHeight + padding / 2 + 2 + 51, 8, ColorUtil.rgba(20, 20, 20, 50), ColorUtil.rgba(20, 20, 20, 50));
        RenderUtils.Render2D.drawShadow(x - 1, y - 18, width + 138, headerHeight + padding / 2 + 2 + 51, 4, ColorUtil.rgba(20, 20, 20, 50), ColorUtil.rgba(20, 20, 20, 50));
        RenderUtils.Render2D.drawRoundedCorner(x - 2, y - 18, width + 138, headerHeight + padding / 2 + 2 + 51, 5, ColorUtil.rgba(20, 20, 20, 200));
        RenderUtils.Render2D.drawRoundedCorner(x - 1, y + 1 + headerHeight, width - 2, padding / 2 + 2 - 2, 5, ColorUtil.rgba(25, 25, 25, 150));
        FontUtils.sfbold[16].drawCenteredString(stack, ClientUtils.gradient("Inventory", conduction.STYLE_MANAGER.getCurrentStyle().getColor(100), conduction.STYLE_MANAGER.getCurrentStyle().getColor(200)), (double)(x + 74), (double)(y - 13), ColorUtil.rgba(150, 150, 150, 150));
        for(int i = 9; i < 36; ++i) {
            RenderUtils.Render2D.drawRoundedCorner(x,y, width, height, 3.0F, (new Color(14, 14, 14, 150)).getRGB());
            ItemStack slot = mc.player.inventory.getStackInSlot(i);
            HudUtil.drawItemStack(slot, x + 0.6F, y + 1.0F, true, true, 0.9F);
            x += width;
            x += x1;
            if (i == 17) {
                y += y1;
                x -= width * 9.0F;
                x -= x1 * 9.0F;
            }

            if (i == 26) {
                y += y1;
                x -= width * 9.0F;
                x -= x1 * 9.0F;
            }
        }
        this.inventory.setWidth(width * 9.0F + x1 * 9.0F);
        this.inventory.setHeight(height * 3.0F + 1.0F);
    }
//жоски селф код от джоханчика
    public Dragging penis = Centric.createDrag(this, "Penis", 500,50);
    private void penis(final MatrixStack stack) {
        float x = this.penis.getX();
        float y = this.penis.getY();
        int радиусяйца = 15;
        int длинаписюна = 50;
        int ширинаписюна = 10;
        int длиннаголовки = 10;
        int ширинаголовки = 10;

        RenderUtils.Render2D.drawRoundedRect(x + 1, y-50, ширинаписюна, длинаписюна,5,new Color(255, 223, 196, 255).getRGB());
        RenderUtils.Render2D.drawRoundedCorner(x + 1, y-50, ширинаголовки, длиннаголовки,new Vector4f(5,0,5,0),new Color(243, 40, 40, 255).getRGB());
        RenderUtils.Render2D.drawRoundCircle(x, y, радиусяйца, new Color(255, 223, 196, 255).getRGB());
        RenderUtils.Render2D.drawRoundCircle(x+13, y, радиусяйца, new Color(255, 223, 196, 255).getRGB());
    }

    public CopyOnWriteArrayList<net.minecraft.util.text.TextComponent> components = new CopyOnWriteArrayList<>();
    private final Pattern namePattern = Pattern.compile("^\\w{3,16}$");
    private final Pattern prefixMatches = Pattern.compile(".*(mod|der|adm|help|wne|мод|хелп|помо|адм|владе|отри|таф|taf|curat|курато|dev|раз|supp|сапп|yt|ютуб|ст|мл|сотрудник).*");
    public Dragging staffList = Centric.createDrag(this, "StaffList", 420.0F, 50.0F);
    private int activeStaff = 0;
    private float hDynam = 0;
    private float widthDynamic = 0;
    private float nameWidth = 0;
    List<StaffPlayer> staffPlayers = new ArrayList<>();




    private void staffList(final MatrixStack matrixStack) {
        float posX = staffList.getX();
        float posY = staffList.getY();

        int roundDegree = 4;
        int headerHeight = 16;
        float width = Math.max(nameWidth, 100);
        int padding = 5;
        int offset = 10;

        int headerColor = new Color(25, 25, 25, 255).getRGB();
        int backgroundColor = ColorUtil.rgba(20, 20, 20, 255);

        int firstColor = ColorUtil.getColorStyle(0);
        int secondColor = ColorUtil.getColorStyle(90);
        float height = activeStaff * offset;

        this.hDynam = AnimationMath.fast(this.hDynam, height, 10);
        this.widthDynamic = AnimationMath.fast(this.widthDynamic, width, 10);
        RenderUtils.Render2D.drawShadow(posX, posY+1, widthDynamic+4, headerHeight + hDynam + padding / 2f, 10, ColorUtil.rgba(20, 20, 20, 50), ColorUtil.rgba(20, 20, 20, 50));
        RenderUtils.Render2D.drawShadow(posX, posY+1, widthDynamic+4, headerHeight + hDynam + padding / 2f, 10, ColorUtil.rgba(20, 20, 20, 50), ColorUtil.rgba(20, 20, 20, 50));
        RenderUtils.Render2D.drawRoundedCorner(posX, posY, width+3, headerHeight + hDynam + (float) padding / 2 + 2, 5, ColorUtil.rgba(20, 20, 20, 200));
        //RenderUtils.Render2D.drawRoundedCorner(posX + 1, posY + headerHeight + 1, width+2,  hDynam + padding / 2, 5, ColorUtil.rgba(25, 25, 25, 150));
        // RenderUtils.Render2D.drawRoundedCorner(posX, posY, widthDynamic, headerHeight+2, new Vector4f(5, 0, 5, 0), backgroundColor);
        FontUtils.sfbold[16].drawCenteredString(matrixStack, ClientUtils.gradient("Staff Statistics", firstColor, secondColor), posX + widthDynamic / 2f+3, posY + 6.5f, -1);

        int index = 0;

        SmartScissor.push();
        SmartScissor.setFromComponentCoordinates(posX, posY, widthDynamic, headerHeight + hDynam + padding / 2f);
        if (!staffPlayers.isEmpty()) {
            for (StaffPlayer staff : staffPlayers) {
                String name = staff.getName();
                ITextComponent prefix = staff.getPrefix();
                int status = staff.getStatus().getColor().getRGB();

                FontUtils.sfsemibold[14].drawString(matrixStack, prefix, posX + padding-5, posY + headerHeight + padding + (index * offset), -1);
                FontUtils.sfsemibold[14].drawString(matrixStack, name, posX + padding + FontUtils.sfsemibold[14].getWidth(prefix.getString())-5, posY + headerHeight + padding + (index * offset), -1);
                nameWidth = FontUtils.sfsemibold[14].getWidth(prefix.getString() + name);
                RenderUtils.Render2D.drawRoundCircle( posX + nameWidth+5, posY + headerHeight + padding + (index * offset)+2, 5, status);
                index++;
            }
        } else {
            nameWidth = 0;
        }
        SmartScissor.unset();
        SmartScissor.pop();

        activeStaff = index;
        staffList.setWidth(widthDynamic);
        staffList.setHeight(hDynam + headerHeight);
    }

    private String repairString(String input) {
        StringBuilder sb = new StringBuilder(input.length());
        for (char c : input.toCharArray()) {
            if (c >= 65281 && c <= 65374) {
                sb.append((char) (c - 65248));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private class StaffPlayer {

        @Getter
        String name;
        @Getter
        ITextComponent prefix;
        @Getter
        Status status;

        private StaffPlayer(String name, ITextComponent prefix) {
            this.name = name;
            this.prefix = prefix;

            updateStatus();
        }

        private void updateStatus() {
            for (AbstractClientPlayerEntity player : mc.world.getPlayers()) {
                if (player.getNameClear().equals(name)) {
                    status = NEAR;
                    return;
                }
            }

            for (NetworkPlayerInfo info : mc.getConnection().getPlayerInfoMap()) {
                if (info.getGameProfile().getName().equals(name)) {
                    if (info.getGameType() == SPECTATOR) {
                        status = SPEC;
                        return;
                    }

                    status = NONE;
                    return;
                }
            }

            status = VANISHED;
        }
    }

    public enum Status {
        NONE(Color.green),
        NEAR(Color.yellow),
        SPEC(Color.red),
        VANISHED(Color.orange);

        @Getter
        final Color color;

        Status(Color color) {
            this.color = color;
        }
    }

    public Dragging potionStatus = Centric.createDrag(this, "Potions", 310, 50);
    private float hDynamic = 0;
    private int activePotions = 0;

    private void potionStatus(final MatrixStack matrixStack) {
        float posX = potionStatus.getX();
        float posY = potionStatus.getY();

        int headerHeight = 16;
        int width = 90;
        int padding = 4;
        int offset = 12;

        float height = activePotions * offset;
        this.hDynamic = AnimationMath.fast(this.hDynamic, height, 10);

        RenderUtils.Render2D.drawShadow(posX, posY, width, headerHeight + hDynamic + padding / 2 + 2, 4, ColorUtil.rgba(20, 20, 20, 50), ColorUtil.rgba(20, 20, 20, 50));
        RenderUtils.Render2D.drawShadow(posX, posY, width, headerHeight + hDynamic + padding / 2 + 2, 4, ColorUtil.rgba(20, 20, 20, 50), ColorUtil.rgba(20, 20, 20, 50));
        RenderUtils.Render2D.drawRoundedCorner(posX, posY, width, headerHeight + hDynamic + padding / 2 + 2, 5, ColorUtil.rgba(20, 20, 20, 200));
//        RenderUtils.Render2D.drawRoundedCorner(posX + 1, posY + 1 + headerHeight, width - 2, hDynamic + padding / 2 + 2 - 2, 5, ColorUtil.rgba(25, 25, 25, 150));
        FontUtils.sfbold[16].drawCenteredString(matrixStack, ClientUtils.gradient("Potions", conduction.STYLE_MANAGER.getCurrentStyle().getColor(100), conduction.STYLE_MANAGER.getCurrentStyle().getColor(200)), posX + width / 2, posY + 6.5f, ColorUtil.rgba(150, 150, 150, 150));

        SmartScissor.push();
        SmartScissor.setFromComponentCoordinates(posX, posY + 2, width, headerHeight + hDynamic + padding / 2f + 2);
        int index = 0;
        for (EffectInstance p : mc.player.getActivePotionEffects()) {
            if (p.isShowIcon()) {
                String durationText = EffectUtils.getPotionDurationString(p, 1);
                float durationWidth = FontUtils.sfbold[16].getWidth(durationText);
                float y = posY + 2 + headerHeight + padding + (index * offset);

                FontUtils.sfbold[14].drawString(matrixStack, I18n.format(p.getEffectName()) + " " + getPotionAmplifer(p), posX  + padding, y, -1);
                FontUtils.sfbold[14].drawString(matrixStack, durationText, posX + width - durationWidth - padding - 2, y, -1);

                index++;
            }
        }

        SmartScissor.unset();
        SmartScissor.pop();

        activePotions = index;
        potionStatus.setWidth(width);
        potionStatus.setHeight(activePotions * offset + headerHeight);
    }
    BetterText betterText = new BetterText(List.of("",
            "centric free",
            "build: " + Centric.build), 5000);
    private void waterMark(final MatrixStack matrixStack) {
        final float x = 4, y = 6;

//        String title = "centric free " + Centric.build;
        String dubtitle = "/ "+ mc.debugFPS + "fps" + " / " + HudUtil.calculatePing() + " ping";
//        final float width1 = Math.max(80, FontUtils.sfbold[14].getWidth(betterText.output));
        final float titleHeight = 14;

        final float titleWidths = FontUtils.sfbold[14].getWidth(betterText.output);
        float titleWidth = titleWidths;
        final float addTitleWidth = FontUtils.sfbold[14].getWidth(dubtitle)+7;
        final float fullWidth = titleWidth + addTitleWidth;

        RenderUtils.Render2D.drawShadow(x, y, fullWidth, titleHeight, 4, ColorUtil.rgba(20, 20, 20, 50), ColorUtil.rgba(20, 20, 20, 50));
        RenderUtils.Render2D.drawRoundedCorner(x, y, fullWidth, titleHeight, 5, ColorUtil.rgba(20, 20, 20, 200));
        FontUtils.sfsemibold[14].drawString(matrixStack, dubtitle, x + 5 + titleWidth, y + FontUtils.sfbold[14].getFontHeight() / 2f+1, ColorUtil.rgba(200, 200, 200, 255));
        FontUtils.sfbold[14].drawString(matrixStack, ClientUtils.gradient(betterText.output, conduction.STYLE_MANAGER.getCurrentStyle().getColor(100), conduction.STYLE_MANAGER.getCurrentStyle().getColor(200)), x + 3, y + FontUtils.sfbold[14].getFontHeight() / 2f + 1, -1);
    }

    List<Function> sortedFeatures = new ArrayList<>();
    TimerUtil delay = new TimerUtil();

    private void arrayList(final MatrixStack stack) {
        float x = 4;
        float y = elements.get(0) ? 32 : 8;
        float height = 13;
        float yOffset = 0;

        final StyledFont font = FontUtils.sfbold[15];

        if (delay.hasTimeElapsed(10000)) {
            sortedFeatures = HudUtil.getSorted(font);
            delay.reset();
        }

        int firstColor;
        int secondColor;

        yOffset = 0;
        for (Function feature : sortedFeatures) {
            if ((limitations.get(0) && feature.category == Type.Combat) ||
                    (limitations.get(1) && feature.category == Type.Movement) ||
                    (limitations.get(2) && feature.category == Type.Render) ||
                    (limitations.get(3) && feature.category == Type.Player) ||
                    (limitations.get(4) && feature.category == Type.Util) ||
                    (limitations.get(5) && feature.bind == 0)) {
                continue;
            }

            feature.animation = AnimationMath.fast(feature.animation, feature.state ? 1 : 0, 10);
            if (feature.animation >= 0.01) {
                float width = font.getWidth(feature.name) + 8;
                secondColor =  conduction.STYLE_MANAGER.getCurrentStyle().getColor((int) (yOffset));

                RenderSystem.pushMatrix();
                RenderSystem.translatef(x + width / 2F, y + yOffset-2, 0);
                RenderSystem.scalef(1, feature.animation, 1);
                RenderSystem.translatef(-(x + width / 2F), -(y + yOffset), 0);
                float s = 5;
                RenderUtils.Render2D.drawShadow(x, y + yOffset-2, width, height, 8, ColorUtil.rgba(20, 20, 20, 255), ColorUtil.rgba(20, 20, 0, 190));
                RenderUtils.Render2D.drawShadow(x, y + yOffset-2, width, height, 4, ColorUtil.rgba(20, 20, 20, 150), ColorUtil.rgba(20, 20, 0, 190));
                RenderUtils.Render2D.drawRoundedCorner(x, y + yOffset-2, width, height, 5, ColorUtil.rgba(20, 20, 20, 200));
                font.drawString(stack, ClientUtils.gradient(feature.name, conduction.STYLE_MANAGER.getCurrentStyle().getColor(100), conduction.STYLE_MANAGER.getCurrentStyle().getColor(200)), x + 3 + 1.5f, y + yOffset + font.getFontHeight() / 2f-2, -1);
                RenderSystem.popMatrix();

                yOffset += height * feature.animation;
            }
        }
    }
//    public final Dragging HUDKeyStrokes = Centric.createDrag(this, "KeyStrokes", 500, 90);

//    private void onRenderKeyStrokes(MatrixStack stack) {
//
//        float x = this.HUDKeyStrokes.getX();
//        float y = this.HUDKeyStrokes.getY();
//        float width = 20.0F;
//        float height = 20.0F;
//        float leftY = y;
//        float leftX = x;
//        float forwardY = y - 23.0F;
//        float forwardX = x + 23.0F;
//        float rightY = y;
//        float rightX = x + 47.0F;
//        float backY = y;
//        float backX = x + 23.0F;
//        float SPACEY = y + 23.0F;
//        float SPACEX = x;
//        float SPACEWidth = width + 48.0F;
//        float SPACEHeight = height - 7.0F;
//        StyledFont small = FontUtils.sfbold[16];
//        StyledFont small2 = FontUtils.sfbold[13];
//        if (!InputMappings.isKeyDown(mc.getMainWindow().getHandle(), 65))
//            RenderUtils.Render2D.drawShadow(leftX, leftY, width, height, 12, ColorUtil.getColorStyle(0.0F));
//        if (InputMappings.isKeyDown(mc.getMainWindow().getHandle(), 65))
//            RenderUtils.Render2D.drawShadow(leftX + 1.0F, leftY + 1.0F, width - 2.0F, height - 2.0F, 12, ColorUtil.getColorStyle(0.0F));
//        if (!InputMappings.isKeyDown(mc.getMainWindow().getHandle(), 87))
//            RenderUtils.Render2D.drawShadow(forwardX, forwardY, width, height, 12, ColorUtil.getColorStyle(0.0F));
//        if (InputMappings.isKeyDown(mc.getMainWindow().getHandle(), 87))
//            RenderUtils.Render2D.drawShadow(forwardX + 1.0F, forwardY + 1.0F, width - 2.0F, height - 2.0F, 12, ColorUtil.getColorStyle(0.0F));
//        if (!InputMappings.isKeyDown(mc.getMainWindow().getHandle(), 68))
//            RenderUtils.Render2D.drawShadow(rightX, rightY, width, height, 12, ColorUtil.getColorStyle(0.0F));
//        if (InputMappings.isKeyDown(mc.getMainWindow().getHandle(), 68))
//            RenderUtils.Render2D.drawShadow(rightX + 1.0F, rightY + 1.0F, width - 2.0F, height + -2.0F, 12, ColorUtil.getColorStyle(0.0F));
//        if (!InputMappings.isKeyDown(mc.getMainWindow().getHandle(), 83))
//            RenderUtils.Render2D.drawShadow(backX, backY, width, height, 12, ColorUtil.getColorStyle(0.0F));
//        if (InputMappings.isKeyDown(mc.getMainWindow().getHandle(), 83))
//            RenderUtils.Render2D.drawShadow(backX + 1.0F, backY + 0.5F, width - 2.0F, height - 2.0F, 12, ColorUtil.getColorStyle(0.0F));
//        if (!InputMappings.isKeyDown(mc.getMainWindow().getHandle(), 32))
//            RenderUtils.Render2D.drawShadow(SPACEX, SPACEY, SPACEWidth, SPACEHeight, 12, ColorUtil.getColorStyle(0.0F));
//        if (InputMappings.isKeyDown(mc.getMainWindow().getHandle(), 32))
//            RenderUtils.Render2D.drawShadow(SPACEX + 2.5F, SPACEY + 1.0F, SPACEWidth - 5.0F, SPACEHeight - 2.0F, 12, ColorUtil.getColorStyle(0.0F));
//        if (!InputMappings.isKeyDown(mc.getMainWindow().getHandle(), 65)) {
//                //RenderUtils.Render2D.drawRoundedCorner(leftX - 0.5F, leftY - 0.5F, width + 1.0F, height + 1.0F, 6.0F, ColorUtil.getColorStyle(0.0F));
//            RenderUtils.Render2D.drawRoundedCorner(leftX, leftY, width, height, 6.0F, (new Color(23, 22, 22, 242)).getRGB());
//            small.drawString(stack, "A", (leftX + 7.0F), (leftY + 9.0F), (new Color(255, 255, 255, 242)).getRGB());
//        }
//        if (InputMappings.isKeyDown(mc.getMainWindow().getHandle(), 65)) {
//                //RenderUtils.Render2D.drawRoundedCorner(leftX + 1.0F - 0.5F, leftY + 1.0F - 0.5F, width + 1.0F - 2.0F, height + 1.0F - 2.0F, 6.0F, ColorUtil.getColorStyle(0.0F));
//            RenderUtils.Render2D.drawRoundedCorner(leftX + 1.0F, leftY + 1.0F, width - 2.0F, height - 2.0F, 6.0F, (new Color(23, 22, 22, 242)).getRGB());
//            small.drawString(stack, ClientUtils.gradient("A",conduction.STYLE_MANAGER.getCurrentStyle().getColor(100),conduction.STYLE_MANAGER.getCurrentStyle().getColor(100)), (leftX + 7.0F), (leftY + 9.0F), (new Color(255, 255, 255, 242)).getRGB());
//        }
//        if (!InputMappings.isKeyDown(mc.getMainWindow().getHandle(), 87)) {
//                //RenderUtils.Render2D.drawRoundedCorner(forwardX - 0.5F, forwardY - 0.5F, width + 1.0F, height + 1.0F, 6.0F, ColorUtil.getColorStyle(0.0F));
//            RenderUtils.Render2D.drawRoundedCorner(forwardX, forwardY, width, height, 6.0F, (new Color(23, 22, 22, 242)).getRGB());
//            small.drawString(stack, "W", (forwardX + 6.5F), (forwardY + 9.0F), (new Color(255, 255, 255, 242)).getRGB());
//        }
//        if (InputMappings.isKeyDown(mc.getMainWindow().getHandle(), 87)) {
//                //RenderUtils.Render2D.drawRoundedCorner(forwardX + 1.0F - 0.5F, forwardY + 1.0F - 0.5F, width + 1.0F - 2.0F, height + 1.0F - 2.0F, 6.0F, ColorUtil.getColorStyle(0.0F));
//            RenderUtils.Render2D.drawRoundedCorner(forwardX + 1.0F, forwardY + 1.0F, width - 2.0F, height - 2.0F, 6.0F, (new Color(23, 22, 22, 242)).getRGB());
//            small.drawString(stack, ClientUtils.gradient("W",conduction.STYLE_MANAGER.getCurrentStyle().getColor(100),conduction.STYLE_MANAGER.getCurrentStyle().getColor(100)), (forwardX + 6F), (forwardY + 9.0F), (new Color(255, 255, 255, 242)).getRGB());
//        }
//        if (!InputMappings.isKeyDown(mc.getMainWindow().getHandle(), 68)) {
//                //RenderUtils.Render2D.drawRoundedCorner(rightX - 0.5F, rightY - 0.5F, width + 1.0F, height + 1.0F, 6.0F, ColorUtil.getColorStyle(0.0F));
//            RenderUtils.Render2D.drawRoundedCorner(rightX, rightY, width, height, 6.0F, (new Color(23, 22, 22, 242)).getRGB());
//            small.drawString(stack, "D", (rightX + 7.0F), (rightY + 9.0F), (new Color(255, 255, 255, 242)).getRGB());
//        }
//        if (InputMappings.isKeyDown(mc.getMainWindow().getHandle(), 68)) {
//                //RenderUtils.Render2D.drawRoundedCorner(rightX + 1.0F - 0.5F, rightY + 1.0F - 0.5F, width + 1.0F - 2.0F, height + 1.0F - 2.0F, 6.0F, ColorUtil.getColorStyle(0.0F));
//            RenderUtils.Render2D.drawRoundedCorner(rightX + 1.0F, rightY + 1.0F, width - 2.0F, height + -2.0F, 6.0F, (new Color(23, 22, 22, 242)).getRGB());
//            small.drawString(stack, ClientUtils.gradient("D",conduction.STYLE_MANAGER.getCurrentStyle().getColor(100),conduction.STYLE_MANAGER.getCurrentStyle().getColor(100)), (rightX + 7.0F), (rightY + 8.0F), (new Color(255, 255, 255, 242)).getRGB());
//        }
//        if (!InputMappings.isKeyDown(mc.getMainWindow().getHandle(), 83)) {
//                //RenderUtils.Render2D.drawRoundedCorner(backX - 0.5F, backY - 0.5F, width + 1.0F, height + 1.0F, 6.0F, ColorUtil.getColorStyle(0.0F));
//            RenderUtils.Render2D.drawRoundedCorner(backX, backY, width, height, 6.0F, (new Color(23, 22, 22, 242)).getRGB());
//            small.drawString(stack, "S", (backX + 7.5F), (backY + 9.0F), (new Color(255, 255, 255, 242)).getRGB());
//        }
//        if (InputMappings.isKeyDown(mc.getMainWindow().getHandle(), 83)) {
//                //RenderUtils.Render2D.drawRoundedCorner(backX + 1.0F - 0.5F, backY + 0.5F - 0.5F, width + 1.0F - 2.0F, height + 1.0F - 2.0F, 6.0F, ColorUtil.getColorStyle(0.0F));
//            RenderUtils.Render2D.drawRoundedCorner(backX + 1.0F, backY + 0.5F, width - 2.0F, height - 2.0F, 6.0F, (new Color(23, 22, 22, 242)).getRGB());
//            small.drawString(stack, ClientUtils.gradient("S",conduction.STYLE_MANAGER.getCurrentStyle().getColor(100),conduction.STYLE_MANAGER.getCurrentStyle().getColor(100)), (backX + 7.5F), (backY + 8.0F), (new Color(255, 255, 255, 242)).getRGB());
//        }
//        StyledFont small1 = FontUtils.sfbold[14];
//        if (!InputMappings.isKeyDown(mc.getMainWindow().getHandle(), 32)) {
//                //RenderUtils.Render2D.drawRoundedCorner(SPACEX - 0.5F, SPACEY - 0.5F, SPACEWidth + 1.0F, SPACEHeight + 1.0F, 4.0F, ColorUtil.getColorStyle(0.0F));
//            RenderUtils.Render2D.drawRoundedCorner(SPACEX, SPACEY, SPACEWidth, SPACEHeight, 4.0F, (new Color(23, 22, 22, 242)).getRGB());
//            small1.drawString(stack,"SPACE", (SPACEX + 20.5F), (SPACEY + 5.5F), (new Color(255, 255, 255, 242)).getRGB());
//        }
//        if (InputMappings.isKeyDown(mc.getMainWindow().getHandle(), 32)) {
//                //RenderUtils.Render2D.drawRoundedCorner(SPACEX - 0.5F + 2.5F, SPACEY - 0.5F + 1.0F, SPACEWidth + 1.0F - 5.0F, SPACEHeight + 1.0F - 2.0F, 4.0F, ColorUtil.getColorStyle(0.0F));
//            RenderUtils.Render2D.drawRoundedCorner(SPACEX + 2.5F, SPACEY + 1.0F, SPACEWidth - 5.0F, SPACEHeight - 2.0F, 4.0F, ( (new Color(23, 22, 22, 242)).getRGB()));
//            small1.drawString(stack, ClientUtils.gradient("SPACE",conduction.STYLE_MANAGER.getCurrentStyle().getColor(100),conduction.STYLE_MANAGER.getCurrentStyle().getColor(100)), (SPACEX + 20.5F), (SPACEY + 5.5F), (new Color(255, 255, 255, 242)).getRGB());
//        }
//        this.HUDKeyStrokes.setWidth(width + 50.0F);
//        this.HUDKeyStrokes.setHeight(height + 20.0F);
//    }





        public final Dragging timerHUD = Centric.createDrag(this, "TimerHUD", 500, 90);
    public float perc = 0;
    private void timerHUD(MatrixStack matrixStack) {
        Style style = conduction.STYLE_MANAGER.getCurrentStyle();

        float posX = timerHUD.getX();
        float posY = timerHUD.getY();
        int firstColor = ColorUtil.getColorStyle(0);
        int secondColor = ColorUtil.getColorStyle(90);
        int backgroundColor = ColorUtil.rgba(20, 20, 20, 200);
        float quotient = conduction.FUNCTION_MANAGER.timerFunction.maxViolation / conduction.FUNCTION_MANAGER.timerFunction.timerAmount.getValue().floatValue();
        float minimumValue = Math.min(conduction.FUNCTION_MANAGER.timerFunction.getViolation(), quotient);
        perc = AnimationMath.lerp(perc, ((quotient - minimumValue) / quotient), 10);

        String text = (int) (perc * 100) + "%";
        float width = 80;
        int width2 = 45+5;
        timerHUD.setWidth(width);
        timerHUD.setHeight(20);
        RenderUtils.Render2D.drawShadow(posX, posY, width-20, 27, 10, ColorUtil.rgba(20, 20, 20, 50), ColorUtil.rgba(20, 20, 20, 50));
        RenderUtils.Render2D.drawRoundedRect(posX, posY, width-20, 27,6, backgroundColor);
        RenderUtils.Render2D.drawRoundedRect(posX + 4f, posY + 23 - 19, width2+2, 9,3, new Color(100,100,100, 74).getRGB());
        RenderUtils.Render2D.drawGradientRound(posX + 5f, posY + 24 - 19, width2*perc, 7,3, firstColor, secondColor, ColorUtil.getColorStyle(180), ColorUtil.getColorStyle(270));
        FontUtils.sfbold[16].drawString(matrixStack,  text, posX + width - FontUtils.sfbold[16].getWidth(text) - 4 - 27-9f, posY + 17.5, -1);
    }

    float health = 0;
    public final Dragging targetHUD = Centric.createDrag(this, "TargetHUD", 542, 380);
    private final Animation targetHudAnimation = new EaseInOutQuad(400, 1);
    private PlayerEntity target = null;
    private double scale = 0.0D;

    private void targetHUD(final MatrixStack stack) {
        float posX = targetHUD.getX();
        float posY = targetHUD.getY();

        targetHUD.setWidth(120);
        targetHUD.setHeight(46);

        this.target = getTarget(this.target);
        this.scale = targetHudAnimation.getOutput();

        if (scale == 0.0F) {
            target = null;
        }

        if (target == null) {
            return;
        }

        final String targetName = this.target.getName().getString();
        String substring = targetName.substring(0, Math.min(targetName.length(), 10));
        this.health = AnimationMath.fast(health, target.getHealth() / target.getMaxHealth(), 5);
        this.health = MathHelper.clamp(this.health, 0, 1);
        float healthValue = Float.parseFloat((float) MathUtil.round(this.health * 20 + target.getAbsorptionAmount(), 0.5f) + "");
        String healthft;
        if (healthValue >= 1000) {
            healthft = "Неизвестно";
        } else {
            healthft = String.valueOf(healthValue);
        }
        float round_degree = 7;
        GlStateManager.pushMatrix();
        AnimationMath.sizeAnimation(posX + (100 / 2), posY + (38 / 2), scale);
        int backgroundColor = ColorUtil.rgba(20, 20, 20, 200);
        RenderUtils.Render2D.drawShadow(posX, posY - 4, 100, targetHUD.getHeight(), (int) round_degree, ColorUtil.rgba(20, 20, 20, 50), ColorUtil.rgba(20, 20, 20, 50));
        RenderUtils.Render2D.drawRoundedCorner(posX, posY - 4, 100, targetHUD.getHeight(), new Vector4f(20, 7, 7, 7), backgroundColor);
        List<ItemStack> stacks = new ArrayList<>(Arrays.asList(target.getHeldItemMainhand(), target.getHeldItemOffhand()));
        target.getArmorInventoryList().forEach(stacks::add);
        RenderUtils.Render2D.drawRoundedCorner(posX + 35, posY - 14, 60, 10, new Vector4f(round_degree, 0, round_degree, 0), backgroundColor);
        drawItemStack(posX + 85, posY - 13, -10);


        FontUtils.sfbold[18].drawString(stack, substring, posX + 32 + 4, posY + 2, -1);
        float distance = (float) MathUtil.round(mc.player.getDistance(target), 0.1f);
        FontUtils.sfbold[13].drawString(stack, "hp: " + healthft, posX + 32 + 4, posY + 13, -1);
        FontUtils.sfbold[13].drawString(stack, "Distance: " + distance, posX + 32 + 4, posY + 21, -1);
        SmartScissor.push();
        SmartScissor.setFromComponentCoordinates(posX, posY, 100, targetHUD.getHeight());
        StencilUtil.initStencilToWrite();
        RenderUtils.Render2D.drawRoundedRect(posX + 4, posY + 10, 28, 28, 4, Color.BLACK.getRGB());
        StencilUtil.readStencilBuffer(1);
        mc.getTextureManager().bindTexture(((AbstractClientPlayerEntity) target).getLocationSkin());
        AbstractGui.drawScaledCustomSizeModalRect(posX + 4, posY + 10, 8F, 8F, 8F, 8F, 28, 28, 64F, 64F);
        StencilUtil.uninitStencilBuffer();
        SmartScissor.unset();
        SmartScissor.pop();
//        RenderUtils.Render2D.drawRound(posX + 4 + 24 + 4 + 3, posY + 39 - 18.5f, (100-24-17.2f), 7f, 3, ColorUtil.rgba(100,100,100,50));

        RenderUtils.Render2D.drawRound(posX + 4 + 24 + 7, posY + 51 - 20.5f, (100 - 24 - 17f) * health, 6, 2,
                ColorUtil.interpolateColor(ColorUtil.rgba(255, 46, 46, 255), ColorUtil.green, health));
        GlStateManager.popMatrix();
    }
    private void information(final MatrixStack stack, final EventRender renderEvent) {
        int color = ColorUtil.rgba(255, 255, 255, 190);
        float x = 4;
        float y = renderEvent.scaledResolution.scaledHeight() - FontUtils.sfbold[11].getFontHeight() - (mc.currentScreen instanceof ChatScreen ? 6 * mc.gameSettings.guiScale : 0) - 1;
        float w = 7;
        if (conduction.FUNCTION_MANAGER != null && conduction.FUNCTION_MANAGER.nameProtect.state) {
            String[] texts = {

                    "nickname: " + "центрик.tech" + " | " + "bps: " + String.format("%.2f", Math.hypot(mc.player.getPosX() - mc.player.prevPosX, mc.player.getPosZ() - mc.player.prevPosZ) * 20),
                    "xyz: " + TextFormatting.GREEN + (int) mc.player.getPosX() + ", " + (int) mc.player.getPosY() + ", " + (int) mc.player.getPosZ() + TextFormatting.RED + " (" + (int) mc.player.getPosX() / 8 + ", " + (int) mc.player.getPosY() + ", " + (int) mc.player.getPosZ() / 8 + ")",};

            for (int i = 0; i < texts.length; i++) {
                FontUtils.sfbold[13].drawString(stack, texts[i], x, y - (w * i), i == 0 ? color : color);
            }
        } else {
            String[] texts = {

                    "nickname: " + mc.player.getNameClear() + " | " + "bps: " + String.format("%.2f", Math.hypot(mc.player.getPosX() - mc.player.prevPosX, mc.player.getPosZ() - mc.player.prevPosZ) * 20),
                    "xyz: " + TextFormatting.GREEN + (int) mc.player.getPosX() + ", " + (int) mc.player.getPosY() + ", " + (int) mc.player.getPosZ() + TextFormatting.RED + " (" + (int) mc.player.getPosX() / 8 + ", " + (int) mc.player.getPosY() + ", " + (int) mc.player.getPosZ() / 8 + ")",};

            for (int i = 0; i < texts.length; i++) {
                FontUtils.sfbold[13].drawString(stack, texts[i], x, y - (w * i), i == 0 ? color : color);
            }
        }
    }

    private void armor(final EventRender renderEvent) {
        int count = 0;
        for (int i = 0; i < mc.player.inventory.getSizeInventory(); i++) {
            ItemStack s = mc.player.inventory.getStackInSlot(i);
            if (s.getItem() == Items.TOTEM_OF_UNDYING) {
                count++;
            }
        }

        float xPos = renderEvent.scaledResolution.scaledWidth() / 2f;
        float yPos = renderEvent.scaledResolution.scaledHeight();

        boolean totemInInv = mc.player.inventory.mainInventory.stream().map(ItemStack::getItem).collect(Collectors.toList()).contains(Items.TOTEM_OF_UNDYING);
        int off = totemInInv ? +5 : 0;

        if (mc.player.isCreative()) {
            yPos += 17;
        }

        for (ItemStack s : mc.player.inventory.armorInventory) {
            NameTags.drawItemStack(s, xPos - off + 74 * (mc.gameSettings.guiScale / 2f), yPos - 56 * (mc.gameSettings.guiScale / 2f), null, false);
            off += 15;
        }

        if (totemInInv) {
            NameTags.drawItemStack(new ItemStack(Items.TOTEM_OF_UNDYING), xPos - off + 73 * (mc.gameSettings.guiScale / 2f), yPos - 56 * (mc.gameSettings.guiScale / 2f), String.valueOf(count), false);
        }
    }

    private void drawItemStack(float x, float y, float offset) {
        List<ItemStack> stackList = new ArrayList<>(Arrays.asList(target.getHeldItemMainhand(), target.getHeldItemOffhand()));
        stackList.addAll((Collection<? extends ItemStack>) target.getArmorInventoryList());

        final AtomicReference<Float> posX = new AtomicReference<>(x);

        stackList.stream().filter(stack -> !stack.isEmpty()).forEach(stack -> HudUtil.drawItemStack(stack, posX.getAndAccumulate(offset, Float::sum), y, true, true, 0.6f));
    }

    private PlayerEntity getTarget(PlayerEntity nullTarget) {
        PlayerEntity target = nullTarget;

        if (conduction.FUNCTION_MANAGER.auraFunction.getTarget() instanceof PlayerEntity) {
            target = (PlayerEntity) conduction.FUNCTION_MANAGER.auraFunction.getTarget();
            targetHudAnimation.setDirection(Direction.FORWARDS);
        } else if (mc.currentScreen instanceof ChatScreen) {
            target = mc.player;
            targetHudAnimation.setDirection(Direction.FORWARDS);
        } else {
            targetHudAnimation.setDirection(Direction.BACKWARDS);
        }

        return target;
    }

    public String getPotionAmplifer(EffectInstance e) {
        if (e.getAmplifier() == 1) {
            return "2";
        } else if (e.getAmplifier() == 2) {
            return "3";
        } else if (e.getAmplifier() == 3) {
            return "4";
        } else if (e.getAmplifier() == 4) {
            return "5";
        } else if (e.getAmplifier() == 5) {
            return "6";
        } else if (e.getAmplifier() == 6) {
            return "7";
        } else if (e.getAmplifier() == 7) {
            return "8";
        } else if (e.getAmplifier() == 8) {
            return "9";
        } else if (e.getAmplifier() == 9) {
            return "10";
        } else {
            return "";
        }
    }
}