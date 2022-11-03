package github.totorewa.rugserver.feature.player;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import github.totorewa.rugserver.RugSettings;
import github.totorewa.rugserver.feature.dropxp.OwnedExperienceOrbEntity;
import github.totorewa.rugserver.feature.player.actions.PlayerActionType;
import github.totorewa.rugserver.helper.ExperienceHelper;
import github.totorewa.rugserver.helper.RayTraceHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class PlayerController {
    private final Map<Integer, ActionAttachment> actionMap = Maps.newHashMap();
    private final Set<Augmentation> augmentations = Sets.newHashSet();
    public final ServerPlayerEntity player;
    public final boolean isFakePlayer;
    public final Random random = new Random();
    public int attackCooldown;
    public int miningCooldown;
    public int useCooldown;
    public int eatCooldown;
    public BlockPos miningPos;
    public int miningTick;

    public PlayerController(ServerPlayerEntity player) {
        this.player = player;
        isFakePlayer = player instanceof FakeServerPlayerEntity;
    }

    public void tick() {
        if (!player.isSpectator()) {
            List<Augmentation> removed = new ArrayList<>(augmentations.size());
            for (Augmentation aug : augmentations) {
                aug.tick(this);
                if (aug.isDisabled())
                    removed.add(aug);
            }
            removed.forEach(a -> {
                augmentations.remove(a);
                if (a instanceof ActionAttachment)
                    actionMap.remove(((ActionAttachment) a).actionType.ordinal(), a);
            });
        }
        tickCooldown();
    }

    public void addAugmentation(Augmentation aug) {
        augmentations.add(aug);
        if (aug instanceof ActionAttachment) {
            ActionAttachment attachment = (ActionAttachment) aug;
            ActionAttachment previous = actionMap.put(attachment.actionType.ordinal(), attachment);
            if (previous != null) previous.scheduleDisable();
        }
        for (ActionAttachment other : actionMap.values()) {
            if (aug.conflictsWith(other))
                other.scheduleDisable();
        }
    }

    public boolean removeAugmentationByName(String name) {
        boolean matched = false;
        for (Augmentation aug : augmentations) {
            if (aug.getName().equals(name)) {
                aug.scheduleDisable();
                matched = true;
            }
        }
        return matched;
    }

    public boolean mountNearbyVehicle() {
        Vec3d cameraPos = RayTraceHelper.getCameraPos(player);
        double reach = getMaxEntityReach();
        List<Entity> vehicles = player.world.getEntitiesIn(
                player, player.getBoundingBox().expand(reach, reach, reach),
                e -> (e instanceof MinecartEntity || e instanceof BoatEntity || e instanceof HorseBaseEntity ||
                        e instanceof PigEntity && ((PigEntity) e).isSaddled()) && e.isAlive() && e.rider == null);
        Entity closestVehicle = null;
        double distance = reach;
        double currentDistance;
        for (Entity vehicle : vehicles) {
            if ((currentDistance = RayTraceHelper.distanceBetween(vehicle.getPos(), cameraPos)) < distance) {
                distance = currentDistance;
                closestVehicle = vehicle;
            }
        }
        if (closestVehicle != null) {
            player.startRiding(closestVehicle);
            return true;
        }
        return false;
    }

    public boolean dismountVehicle() {
        if (player.hasVehicle()) {
            player.startRiding(null);
            return !player.hasVehicle();
        }
        return false;
    }

    public void sneak() {
        player.setSneaking(true);
    }

    public void unsneak() {
        player.setSneaking(false);
    }

    public void dropAll() {
        int i;
        for (i = 0; i < player.inventory.main.length; ++i) {
            if (player.inventory.main[i] == null) continue;
            player.dropStack(player.inventory.main[i], false, true);
            player.inventory.main[i] = null;
        }
        for (i = 0; i < player.inventory.armor.length; ++i) {
            if (player.inventory.armor[i] == null) continue;
            player.dropStack(player.inventory.armor[i], false, true);
            player.inventory.armor[i] = null;
        }
    }

    public void dropSlot(int slot, boolean dropStack) {
        if (player.inventory.getInvSize() < slot + 1) return;
        ItemStack stack = player.inventory.getInvStack(slot);
        if (dropStack || stack.count == 1) {
            player.dropStack(stack, false, true);
            player.inventory.setInvStack(slot, null);
        } else {
            ItemStack drop = stack.split(1);
            player.dropStack(drop, false, true);
        }
    }

    public void dumpExperience() {
        if (RugSettings.allowXpDumping == RugSettings.BotExperienceDropType.NONE) return;
        int xpToDrop;
        if (RugSettings.allowXpDumping == RugSettings.BotExperienceDropType.LIMITED) {
            xpToDrop = player.experienceLevel * 7;
            if (xpToDrop > 100) xpToDrop = 100;
        } else xpToDrop = ExperienceHelper.getExperience(player.experienceLevel, player.experienceProgress);
        player.decrementXp(player.experienceLevel + 1);

        int orbSize;
        for (int i = xpToDrop; i > 0; i -= orbSize) {
            orbSize = ExperienceOrbEntity.roundToOrbSize(i);
            player.world.spawnEntity(new OwnedExperienceOrbEntity(player.world, player.x, player.y, player.z, orbSize, player));
        }
    }

    public void swingHand() {
        player.swingHand();
        if (!isFakePlayer && player.networkHandler != null) {
            player.networkHandler.sendPacket(new EntityAnimationS2CPacket(player, 0));
        }
    }

    public void eat() {
        if (!isFakePlayer && player.networkHandler != null) {
            player.networkHandler.sendPacket(new EntityAnimationS2CPacket(player, 3));
        }
    }

    public boolean canEat() {
        return player.canConsume(false);
    }

    public void enterAttackCooldown() {
        // Client has a 10-tick delay between sending attack packets
        // Add extra tick to account for tickCooldown()
        attackCooldown = 11;
    }

    public void enterMiningCooldown() {
        miningCooldown = player.interactionManager.isCreative() ? 6 : 0;
    }

    public void enterUseCooldown() {
        useCooldown = 3;
    }

    public void enterEatCooldown(int interval) {
        eatCooldown = interval <= 1 ? random.nextInt(5) + 19 : interval;
    }

    public void tickCooldown() {
        if (miningCooldown > 0) {
            miningCooldown--;
        }
        if (attackCooldown > 0) {
            attackCooldown--;
        }
        if (useCooldown > 0) {
            useCooldown--;
        }
        if (eatCooldown > 0) {
            eatCooldown--;
        }
    }

    public float getMaxReach() {
        return player.interactionManager.isCreative() ? 5.0f : 4.5f;
    }

    public float getMaxEntityReach() {
        return 3.0f;
    }

    public void copyFrom(PlayerController other) {
        other.augmentations.forEach(this::addAugmentation);
        attackCooldown = other.attackCooldown;
    }
}
