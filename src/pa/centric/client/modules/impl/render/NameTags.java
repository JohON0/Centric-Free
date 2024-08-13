package pa.centric.client.modules.impl.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.*;
import net.minecraft.world.GameType;
import org.joml.Vector4d;
import org.joml.Vector4i;
import pa.centric.client.helper.conduction;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.modules.settings.imp.BooleanOption;
import pa.centric.client.modules.settings.imp.MultiBoxSetting;
import pa.centric.events.impl.render.EventRender;
import pa.centric.util.ClientUtils;
import pa.centric.util.IMinecraft;
import pa.centric.util.font.FontUtils;
import pa.centric.util.math.MathUtil;
import pa.centric.util.math.PlayerPositionTracker;
import pa.centric.util.misc.ServerUtil;
import pa.centric.util.render.ColorUtil;
import pa.centric.util.render.RenderUtils;
import pa.centric.events.Event;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static pa.centric.util.math.PlayerPositionTracker.isInView;

@ModuleAnnotation(name = "NameTags", category = Type.Render)
public class NameTags extends Function {

    public MultiBoxSetting elements = new MultiBoxSetting("Элементы",
            new BooleanOption("Эффекты", false),
            new BooleanOption("Броня", true),
            new BooleanOption("Талисманы и Сферы", true),
            new BooleanOption("Чары", true));


    public NameTags() {
        addSettings(elements);
    }

    public HashMap<Vector4d, PlayerEntity> positions = new HashMap<>();

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventRender) {
            EventRender render = (EventRender) event;
            if (elements.get("Талисманы и Сферы")) {
                if (!ClientUtils.isConnectedToServer("mc.holyworld.ru")) {
                    ClientUtils.sendMessage("Талисманы и сферы показываются только на HolyWorld");
                    toggle();
                }
            }
            if (render.isRender3D()) {
                updatePlayerPositions(render.partialTicks);
            }
            if (render.isRender2D()) {
                renderPlayerElements(render.matrixStack);
            }
        }
    }
    private void updatePlayerPositions(float partialTicks) {
        positions.clear();
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (isInView(player) && player.botEntity) {
                if (mc.gameSettings.getPointOfView() == PointOfView.FIRST_PERSON && player == mc.player) {
                    continue;
                }
                Vector4d position = PlayerPositionTracker.updatePlayerPositions(player, partialTicks);
                if (position != null) {
                    positions.put(position, player);
                }
            }
        }
    }

    private void renderPlayerElements(MatrixStack stack) {
        Vector4i colors = new Vector4i(ColorUtil.getColorStyle(0), ColorUtil.getColorStyle(90), ColorUtil.getColorStyle(180), ColorUtil.getColorStyle(270));

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        for (Map.Entry<Vector4d, PlayerEntity> entry : positions.entrySet()) {
            Vector4d position = entry.getKey();
            PlayerEntity player = entry.getValue();
        }

        tessellator.draw();

        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();

        for (Map.Entry<Vector4d, PlayerEntity> entry : positions.entrySet()) {
            Vector4d position = entry.getKey();
            PlayerEntity player = entry.getValue();
            double x = position.x;
            double y = position.y;
            double endX = position.z;
            float height = (float) (position.w - position.y);
            String healthText = String.valueOf((float) player.getHealth());
                renderTags(stack, (float) x, (float) y + 6, (float) endX, player);

            if (elements.get(0)) {
                renderEffects(player, (float) y, (float) endX, stack);
            }
        }
    }


    private void renderEffects(PlayerEntity player,
                               float y,
                               float endX,
                               MatrixStack matrices) {
        EffectInstance[] effects = player.getActivePotionEffects().toArray(new EffectInstance[0]);
        int effectCount = effects.length;

        for (int i = 0; i < effectCount; i++) {
            EffectInstance p = effects[i];

            if (p == null) {
                continue;
            }

            String effectName = I18n.format(p.getEffectName());
            String effectAmplifier = I18n.format("enchantment.level." + (p.getAmplifier() + 1));
            String effectDuration = EffectUtils.getPotionDurationString(p, 1);
            String effectString = effectName + " " + effectAmplifier + TextFormatting.GRAY + "(" + effectDuration + ")" + TextFormatting.RESET;

            FontUtils.sfbold[12].drawStringWithShadow(matrices, effectString, endX + 2.5f, y - 2 + ((i + 1) * 8), -1);
        }
    }

    private String translateGamemode(GameType gamemode) {
        if (gamemode == null) return "[BOT]";
        return switch (gamemode) {
            case NOT_SET -> null;
            case SURVIVAL -> "[S]";
            case CREATIVE -> "[C]";
            case SPECTATOR -> "[SP]";
            case ADVENTURE -> "[A]";
        };
    }
    public static void drawItemStack(ItemStack stack, double x, double y, String altText, boolean withoutOverlay) {
        RenderSystem.translated(x, y, 0);
        mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, 0, 0);
        if (!withoutOverlay)
            mc.getItemRenderer().renderItemOverlayIntoGUI(mc.fontRenderer, stack, 0, 0, altText);
        RenderSystem.translated(-x, -y, 0);
    }

    private void renderTags(MatrixStack matrixStack, float posX, float posY, float endPosX, PlayerEntity entity) {
        float maxOffsetY = 0;

        ITextComponent text = entity.getDisplayName();
        TextComponent name = (TextComponent) text;

        String friendPrefix = conduction.FRIEND_MANAGER.isFriend(entity.getName().getString())
                ? TextFormatting.GREEN + "[F] "
                : "";
        ITextComponent friendText = ITextComponent.getTextComponentOrEmpty(friendPrefix);

        TextComponent friendPrefixComponent = (TextComponent) friendText;
        if (conduction.FRIEND_MANAGER.isFriend(entity.getName().getString()) && (conduction.FUNCTION_MANAGER.nameProtect.state && conduction.FUNCTION_MANAGER.nameProtect.friends.get())) {
            friendPrefixComponent.append(new StringTextComponent(TextFormatting.RED + "protected"));
        } else {
            friendPrefixComponent.append(name);
        }
        name = friendPrefixComponent;
        //селфкод показывает какой талик в руках
        /**
        @author JohON0
         @date 11.06.2024
         @time 17:00 МСК
         */
        String name_totem = "";
        //смотрит есть ли придмет в левой руке
        ItemStack offHandStack = entity.getHeldItemOffhand();
        //если в левой руке тотем, то тогда смотрит его название
        if (offHandStack.getItem() == Items.TOTEM_OF_UNDYING) {
            name_totem = TextFormatting.DARK_GRAY + " [" + TextFormatting.RED + offHandStack.getDisplayName().getString() + TextFormatting.DARK_GRAY + "]";
            //если название "Totem of Undying", то тогда убирает в теге название предмета

            if (offHandStack.getItem() == Items.TOTEM_OF_UNDYING) {
                if (offHandStack.getDisplayName().getString().equals("Totem of Undying")) {
                    name_totem = "";

                }
            }
        }
        String name_sfera = "";
        //смотрит есть ли придмет в левой руке
        //если в левой руке голова, то тогда смотрит его название
        if (offHandStack.getItem() == Items.PLAYER_HEAD) {
            name_sfera = TextFormatting.DARK_GRAY + " [" + TextFormatting.RED + offHandStack.getDisplayName().getString() + TextFormatting.DARK_GRAY + "]";
            //если название "Player Head", то тогда убирает в теге название предмета

            if (offHandStack.getItem() == Items.PLAYER_HEAD) {
                if (offHandStack.getDisplayName().getString().equals("Player Head")) {
                    name_sfera = "";

                }
            }
        }
        String talik = name_totem;
        String sfera = name_sfera;
        if (elements.get("Талисманы и Сферы")) {
            if (offHandStack.getItem() == Items.TOTEM_OF_UNDYING) {
                name.append(new StringTextComponent(TextFormatting.DARK_GRAY + " [" + TextFormatting.RED + (int) entity.getHealth() + TextFormatting.DARK_GRAY + "]" + talik));
            } else if (offHandStack.getItem() == Items.PLAYER_HEAD) {
                name.append(new StringTextComponent(TextFormatting.DARK_GRAY + " [" + TextFormatting.RED + (int) entity.getHealth() + TextFormatting.DARK_GRAY + "]" + sfera));
            }
        } else {
            name.append(new StringTextComponent(TextFormatting.DARK_GRAY + " [" + TextFormatting.RED + (int) entity.getHealth() + TextFormatting.DARK_GRAY + "]"));

        }



        float width = IMinecraft.mc.fontRenderer.getStringPropertyWidth(name);
        float height = 16;

        TextComponent finalName = name;
        MathUtil.scaleElements((posX + endPosX) / 2f, posY - height / 2, 0.5f, () -> {
            RenderUtils.Render2D.drawRoundedRect((posX + endPosX) / 2f - width / 2f - 5, posY - height - 10, width + 10, height, 3, ColorUtil.rgba(15, 15, 15, 200));
            IMinecraft.mc.fontRenderer.func_243246_a(matrixStack, finalName, (posX + endPosX) / 2f - width / 2f, posY - height - 5, -1);
        });

//            RenderUtils.Render2D.drawRoundedRect(posX - 1, posY - height - 31, FontUtils.sfbold[20].getWidth(name_totem) + 15, height + 2, 0, conduction.FRIEND_MANAGER.isFriend(entity.getName().getString()) ? new java.awt.Color(0, 255, 0, 100).getRGB() : new java.awt.Color(0,0,0,100).getRGB());
//            FontUtils.sfbold[20].drawString(matrixStack, name_totem, (posX + endPosX) / 2f - width / 2f, posY - height - 26, ColorUtil.red);
//        } else if (offHandStack.getItem() == Items.PLAYER_HEAD) { String name_sfera = offHandStack.getDisplayName().getString();
//            RenderUtils.Render2D.drawRoundedRect(posX - 1, posY - height - 31, FontUtils.sfbold[20].getWidth(name_sfera) + 15, height + 2, 0, conduction.FRIEND_MANAGER.isFriend(entity.getName().getString()) ? new java.awt.Color(0, 255, 0, 100).getRGB() : new java.awt.Color(0,0,0,100).getRGB()); FontUtils.sfbold[20].drawString(matrixStack, name_sfera, (posX + endPosX) / 2f - width / 2f, posY - height - 26, ColorUtil.red); }


        maxOffsetY += 25;
        List<ItemStack> stacks = new ArrayList<>(Arrays.asList(entity.getHeldItemMainhand(), entity.getHeldItemOffhand()));
        entity.getArmorInventoryList().forEach(stacks::add);
        stacks.removeIf(w -> w.getItem() instanceof AirItem);
        int totalSize = stacks.size() * 10;
        maxOffsetY += 19;
        AtomicInteger iterable = new AtomicInteger();

        if (elements.get(1)) {
            float finalMaxOffsetY = maxOffsetY;
            MathUtil.scaleElements((posX + endPosX) / 2, posY - maxOffsetY / 2, 0.7f, () -> {
                renderArmorAndEnchantment(stacks, matrixStack, posX, endPosX, posY, finalMaxOffsetY, totalSize, iterable);
            });
        }
    }

    private void renderArmorAndEnchantment(List<ItemStack> stacks, MatrixStack matrixStack, float posX, float endPosX, float posY, float finalMaxOffsetY, int totalSize, AtomicInteger iterable) {
        for (ItemStack stack : stacks) {
            if (stack.isEmpty()) {
                continue;
            }

            float v = posX + (endPosX - posX) / 2f - 10;
            drawItemStack(stack, v + iterable.get() * 25 - totalSize + 2, posY - finalMaxOffsetY + 10, null, false);
            iterable.getAndIncrement();

            ArrayList<String> lines = getEnchantment(stack);
            float center = (v + iterable.get() * 25) - totalSize - 15;
            int i = 0;
            for (String s : lines) {
                FontUtils.sfbold[14].drawCenteredStringWithOutline(matrixStack, s, center, posY - finalMaxOffsetY + 3 - (i * 10), 0xFFFFFFFF);
                i++;
            }
        }
    }

//    public float getHealth(PlayerEntity entity) {
//        if (ClientUtils.isConnectedToServer("mc.funtime.su")) {
//            ScoreObjective scoreBoard;
//            String resolvedHp = "";
//            if ((entity.getWorldScoreboard()).getObjectiveInDisplaySlot(2) != null) {
//                scoreBoard = (entity.getWorldScoreboard()).getObjectiveInDisplaySlot(2);
//                if (scoreBoard != null) {
//                    Score readableScoreboardScore = entity.getWorldScoreboard().getOrCreateScore(entity.getScoreboardName(), scoreBoard);
//                    TextComponent text = Score(readableScoreboardScore, scoreBoard.getNumberFormatOr(StyledNumberFormat.EMPTY));
//                    resolvedHp = text.getString();
//                }
//            }
//            float numValue = 0;
//            try {
//                numValue = Float.parseFloat(resolvedHp);
//            } catch (NumberFormatException ignored) {
//                // селфкод $$$
//            }
//            return numValue;
//        } else return entity.getHealth() + entity.getAbsorptionAmount();
//    }

    private ArrayList<String> getEnchantment(ItemStack stack) {
        ArrayList<String> list = new ArrayList<>();

        Item item = stack.getItem();
        if (item instanceof AxeItem) {
            handleAxeEnchantments(list, stack);
        } else if (item instanceof ArmorItem) {
            handleArmorEnchantments(list, stack);
        } else if (item instanceof BowItem) {
            handleBowEnchantments(list, stack);
        } else if (item instanceof SwordItem) {
            handleSwordEnchantments(list, stack);
        } else if (item instanceof ToolItem) {
            handleToolEnchantments(list, stack);
        }

        return list;
    }

    private void handleAxeEnchantments(ArrayList<String> list, ItemStack stack) {
        int sharpness = EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack);
        int efficiency = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack);
        int unbreaking = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack);

        if (sharpness > 0) {
            list.add("Shr" + sharpness);
        }
        if (efficiency > 0) {
            list.add("Eff" + efficiency);
        }
        if (unbreaking > 0) {
            list.add("Unb" + unbreaking);
        }
    }

    private void handleArmorEnchantments(ArrayList<String> list, ItemStack stack) {
        int protection = EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, stack);
        int thorns = EnchantmentHelper.getEnchantmentLevel(Enchantments.THORNS, stack);
        int unbreaking = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack);
        int mending = EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack);
        int feather = EnchantmentHelper.getEnchantmentLevel(Enchantments.FEATHER_FALLING, stack);
        int depth = EnchantmentHelper.getEnchantmentLevel(Enchantments.DEPTH_STRIDER, stack);
        int vanishingCurse = EnchantmentHelper.getEnchantmentLevel(Enchantments.VANISHING_CURSE, stack);
        int bindingCurse = EnchantmentHelper.getEnchantmentLevel(Enchantments.BINDING_CURSE, stack);
        int fireProt = EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_PROTECTION, stack);
        int blastProt = EnchantmentHelper.getEnchantmentLevel(Enchantments.BLAST_PROTECTION, stack);

        if (vanishingCurse > 0) {
            list.add("Vanish ");
        }
        if (fireProt > 0) {
            list.add("Firp" + fireProt);
        }
        if (blastProt > 0) {
            list.add("Bla" + blastProt);
        }
        if (bindingCurse > 0) {
            list.add("Bindi" + bindingCurse);
        }
        if (depth > 0) {
            list.add("Dep" + depth);
        }
        if (feather > 0) {
            list.add("Fea" + feather);
        }
        if (protection > 0) {
            list.add("Pro" + protection);
        }
        if (thorns > 0) {
            list.add("Thr" + thorns);
        }
        if (mending > 0) {
            list.add("Men" + mending);
        }
        if (unbreaking > 0) {
            list.add("Unb" + unbreaking);
        }
    }

    private void handleBowEnchantments(ArrayList<String> list, ItemStack stack) {
        int vanishingCurse = EnchantmentHelper.getEnchantmentLevel(Enchantments.VANISHING_CURSE, stack);
        int bindingCurse = EnchantmentHelper.getEnchantmentLevel(Enchantments.BINDING_CURSE, stack);
        int infinity = EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack);
        int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
        int punch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
        int mending = EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack);
        int flame = EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack);
        int unbreaking = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack);

        if (vanishingCurse > 0) {
            list.add("Vanish" + vanishingCurse);
        }
        if (bindingCurse > 0) {
            list.add("Binding" + bindingCurse);
        }
        if (infinity > 0) {
            list.add("Inf" + infinity);
        }
        if (power > 0) {
            list.add("Pow" + power);
        }
        if (punch > 0) {
            list.add("Pun" + punch);
        }
        if (mending > 0) {
            list.add("Men" + mending);
        }
        if (flame > 0) {
            list.add("Fla" + flame);
        }
        if (unbreaking > 0) {
            list.add("Unb" + unbreaking);
        }
    }

    private void handleSwordEnchantments(ArrayList<String> list, ItemStack stack) {
        int vanishingCurse = EnchantmentHelper.getEnchantmentLevel(Enchantments.VANISHING_CURSE, stack);
        int looting = EnchantmentHelper.getEnchantmentLevel(Enchantments.LOOTING, stack);
        int bindingCurse = EnchantmentHelper.getEnchantmentLevel(Enchantments.BINDING_CURSE, stack);
        int sweeping = EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING, stack);
        int sharpness = EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack);
        int knockback = EnchantmentHelper.getEnchantmentLevel(Enchantments.KNOCKBACK, stack);
        int fireAspect = EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, stack);
        int unbreaking = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack);
        int mending = EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack);

        if (vanishingCurse > 0) {
            list.add("Vanish" + vanishingCurse);
        }
        if (looting > 0) {
            list.add("Loot" + looting);
        }
        if (bindingCurse > 0) {
            list.add("Bindi" + bindingCurse);
        }
        if (sweeping > 0) {
            list.add("Swe" + sweeping);
        }
        if (sharpness > 0) {
            list.add("Shr" + sharpness);
        }
        if (knockback > 0) {
            list.add("Kno" + knockback);
        }
        if (fireAspect > 0) {
            list.add("Fir" + fireAspect);
        }
        if (unbreaking > 0) {
            list.add("Unb" + unbreaking);
        }
        if (mending > 0) {
            list.add("Men" + mending);
        }
    }

    private void handleToolEnchantments(ArrayList<String> list, ItemStack stack) {
        int unbreaking = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack);
        int mending = EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack);
        int vanishingCurse = EnchantmentHelper.getEnchantmentLevel(Enchantments.VANISHING_CURSE, stack);
        int bindingCurse = EnchantmentHelper.getEnchantmentLevel(Enchantments.BINDING_CURSE, stack);
        int efficiency = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack);
        int silkTouch = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack);
        int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);

        if (unbreaking > 0) {
            list.add("Unb" + unbreaking);
        }
        if (mending > 0) {
            list.add("Men" + mending);
        }
        if (vanishingCurse > 0) {
            list.add("Vanish" + vanishingCurse);
        }
        if (bindingCurse > 0) {
            list.add("Binding" + bindingCurse);
        }
        if (efficiency > 0) {
            list.add("Eff" + efficiency);
        }
        if (silkTouch > 0) {
            list.add("Sil" + silkTouch);
        }
        if (fortune > 0) {
            list.add("For" + fortune);
        }
    }
}