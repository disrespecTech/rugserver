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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlayerController {
    private Map<Integer, ActionAttachment> attachmentMap = Maps.newHashMap();
    private Set<ActionAttachment> attachments = Sets.newHashSet();
    public final ServerPlayerEntity player;
    public int attackCooldown;
    public int miningCooldown;
    public BlockPos miningPos;
    public int miningTick;

    public PlayerController(ServerPlayerEntity player) {
        this.player = player;
    }

    public void tick() {
        if (player.isSpectator()) return;
        List<ActionAttachment> removed = new ArrayList<>(attachments.size());
        for (ActionAttachment attachment : attachments) {
            attachment.tick(this);
            if (attachment.isFinished())
                removed.add(attachment);
        }
        removed.forEach(a -> {
            attachments.remove(a);
            attachmentMap.remove(a.actionType.ordinal(), a);
        });
    }

    public void addAttachment(ActionAttachment attachment) {
        ActionAttachment previous = attachmentMap.put(attachment.actionType.ordinal(), attachment);
        attachments.add(attachment);
        if (previous != null) previous.remove();
        for (ActionAttachment other : attachments) {
            if (attachment.actionType.action.conflictsWith(other.actionType))
                other.remove();
        }
    }

    public boolean removeAttachment(PlayerActionType type) {
        ActionAttachment attachment = attachmentMap.remove(type.ordinal());
        if (attachment != null) {
            attachment.remove();
            return true;
        }
        return false;
    }

    public boolean mountNearbyVehicle() {
        Vec3d cameraPos = RayTraceHelper.getCameraPos(player);
        double reach = getMaxReach();
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
        if (RugSettings.botsCanDropXp == RugSettings.BotExperienceDropType.NONE) return;
        int xpToDrop;
        if (RugSettings.botsCanDropXp == RugSettings.BotExperienceDropType.LIMITED) {
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

    public void enterAttackCooldown() {
        // Client has a 10-tick delay between sending attack packets
        attackCooldown = 10;
    }

    public void enterMiningCooldown() {
        miningCooldown = player.interactionManager.isCreative() ? 5 : 0;
    }

    public float getMaxReach() {
        return player.interactionManager.isCreative() ? 5.0f : 4.5f;
    }

    public void copyFrom(PlayerController other) {
        other.attachments.forEach(this::addAttachment);
        attackCooldown = other.attackCooldown;
    }
}
