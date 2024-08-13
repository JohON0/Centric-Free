package pa.centric.client.modules.impl.combat;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.item.minecart.TNTMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.*;
import net.minecraft.potion.Effects;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.client.modules.settings.imp.BooleanOption;
import pa.centric.client.modules.settings.imp.ModeSetting;
import pa.centric.client.modules.settings.imp.MultiBoxSetting;
import pa.centric.client.modules.settings.imp.SliderSetting;
import pa.centric.util.world.InventoryUtil;
import pa.centric.util.world.WorldUtil;
import pa.centric.events.Event;
import pa.centric.events.impl.player.EventUpdate;


@ModuleAnnotation(name = "AutoTotem", category = Type.Combat)
public class AutoTotemFunction
        extends Function {
    private final SliderSetting health = new SliderSetting("Здоровье", 3.5f, 1.0f, 20.0f, 0.05f);
    private final BooleanOption swapBack = new BooleanOption("Возвращать предмет", true);
    private final BooleanOption noBallSwitch = new BooleanOption("Не брать если шар в руке", false);
    private final MultiBoxSetting mode = new MultiBoxSetting("Срабатывать", new BooleanOption("Золотые сердца", true), new BooleanOption("Кристаллы", true), new BooleanOption("Обсидиан", false), new BooleanOption("Обсидиан", false), new BooleanOption("Падение", true), new BooleanOption("Кристалл в руке", true), new BooleanOption("Здоровье на элитре", true));
    private final SliderSetting radiusExplosion = new SliderSetting("Дистанция до кристала", 6.0f, 1.0f, 8.0f, 1.0f).setVisible(() -> this.mode.get(1));
    private final SliderSetting radiusObs = new SliderSetting("Дистанция до обсидиана", 6.0f, 1.0f, 8.0f, 1.0f).setVisible(() -> this.mode.get(2));
    private final SliderSetting radiusAnch = new SliderSetting("Дистанция до якоря", 6.0f, 1.0f, 8.0f, 1.0f).setVisible(() -> this.mode.get(2));
    private final SliderSetting HPElytra = new SliderSetting("Брать на элитре", 6.0f, 1.0f, 20.0f, 0.005f).setVisible(() -> this.mode.get("Здоровье на элитре"));
    private final SliderSetting DistanceFall = new SliderSetting("Дистанция падения", 20.0f, 3.0f, 50.0f, 0.005f).setVisible(() -> this.mode.get("Падение"));
    int oldItem = -1;

    public AutoTotemFunction() {
        this.addSettings(this.mode, this.health, this.swapBack, this.noBallSwitch, this.radiusExplosion, radiusAnch, this.HPElytra, this.DistanceFall);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventUpdate) {
            this.handleEventUpdate((EventUpdate)event);
        }
    }

    private void handleEventUpdate(EventUpdate event) {
        boolean totemInHand;
        int slot = InventoryUtil.getItemSlot(Items.TOTEM_OF_UNDYING);
        boolean handNotNull = !(AutoTotemFunction.mc.player.getHeldItemOffhand().getItem() instanceof AirItem);
        boolean bl = totemInHand = AutoTotemFunction.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING || AutoTotemFunction.mc.player.getHeldItemMainhand().getItem() == Items.TOTEM_OF_UNDYING;
        if (this.condition()) {
            if (slot >= 0 && !totemInHand) {
                AutoTotemFunction.mc.playerController.windowClick(0, slot, 40, ClickType.SWAP, AutoTotemFunction.mc.player);
                if (handNotNull && this.oldItem == -1) {
                    this.oldItem = slot;
                }
            }
        } else if (this.oldItem != -1 && this.swapBack.get()) {
            AutoTotemFunction.mc.playerController.windowClick(0, this.oldItem, 40, ClickType.SWAP, AutoTotemFunction.mc.player);
            this.oldItem = -1;
        }
    }

    private boolean condition() {
        float absorption;
        float f = absorption = this.mode.get(0) && AutoTotemFunction.mc.player.isPotionActive(Effects.ABSORPTION) ? AutoTotemFunction.mc.player.getAbsorptionAmount() : 0.0f;
        if (AutoTotemFunction.mc.player.getHealth() + absorption <= this.health.getValue().floatValue()) {
            return true;
        }
        if (!this.isBall()) {
            if (this.checkCrystal()) {
                return true;
            }
            if (this.checkObsidian()) {
                return true;
            }
            if (this.checkAnchor()) {
                return true;
            }
            if (this.checkPlayerItemCrystal()) {
                return true;
            }
        }
        if (this.checkHPElytra()) {
            return true;
        }
        return this.checkFall();
    }

    private boolean checkHPElytra() {
        if (!this.mode.get("Здоровье на элитре")) {
            return false;
        }
        return AutoTotemFunction.mc.player.inventory.armorInventory.get(2).getItem() == Items.ELYTRA && AutoTotemFunction.mc.player.getHealth() <= this.HPElytra.getValue().floatValue();
    }

    private boolean checkPlayerItemCrystal() {
        if (!this.mode.get("Кристалл в руке")) {
            return false;
        }
        for (PlayerEntity playerEntity : AutoTotemFunction.mc.world.getPlayers()) {
            if (AutoTotemFunction.mc.player == playerEntity || playerEntity.getHeldItemOffhand().getItem() != Items.END_CRYSTAL && playerEntity.getHeldItemMainhand().getItem() != Items.END_CRYSTAL || !(AutoTotemFunction.mc.player.getDistance(playerEntity) < 6.0f)) continue;
            return true;
        }
        return false;
    }

    private boolean checkFall() {
        if (!this.mode.get(4)) {
            return false;
        }
        return AutoTotemFunction.mc.player.fallDistance > this.DistanceFall.getValue().floatValue();
    }

    private boolean isBall() {
        if (this.mode.get(3) && AutoTotemFunction.mc.player.fallDistance > 5.0f) {
            return false;
        }
        return this.noBallSwitch.get() && AutoTotemFunction.mc.player.getHeldItemOffhand().getItem() instanceof SkullItem;
    }

    private boolean checkObsidian() {
        if (!this.mode.get(2)) {
            return false;
        }
        return WorldUtil.TotemUtil.getBlock(this.radiusObs.getValue().floatValue(), Blocks.OBSIDIAN) != null;
    }

    private boolean checkAnchor() {
        if (!this.mode.get(3)) {
            return false;
        }
        return WorldUtil.TotemUtil.getBlock(this.radiusAnch.getValue().floatValue(), Blocks.RESPAWN_ANCHOR) != null;
    }

    private boolean checkCrystal() {
        if (!this.mode.get(1)) {
            return false;
        }
        for (Entity entity : AutoTotemFunction.mc.world.getAllEntities()) {
            if (entity instanceof EnderCrystalEntity && AutoTotemFunction.mc.player.getDistance(entity) <= this.radiusExplosion.getValue().floatValue()) {
                return true;
            }
            if (!(entity instanceof TNTEntity) && !(entity instanceof TNTMinecartEntity) || !(AutoTotemFunction.mc.player.getDistance(entity) <= this.radiusExplosion.getValue().floatValue())) continue;
            return true;
        }
        return false;
    }

    private void reset() {
        this.oldItem = -1;
    }

    @Override
    protected void onEnable() {
        this.reset();
        super.onEnable();
    }

    @Override
    protected void onDisable() {
        this.reset();
        super.onDisable();
    }
}

