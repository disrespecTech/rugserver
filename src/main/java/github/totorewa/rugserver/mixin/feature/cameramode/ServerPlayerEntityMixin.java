package github.totorewa.rugserver.mixin.feature.cameramode;

import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import github.totorewa.rugserver.RugSettings;
import github.totorewa.rugserver.fake.PlayerCameraHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.level.LevelInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.Set;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements PlayerCameraHandler {
    private static final long CAMERA_DAMAGE_COOLDOWN = 200;
    private static final String NBT_INCAMERA = "InCamera";
    private static final String NBT_SURVIVALX = "SurvivalX";
    private static final String NBT_SURVIVALY = "SurvivalY";
    private static final String NBT_SURVIVALZ = "SurvivalZ";
    private static final String NBT_SURVIVALYAW = "SurvivalYaw";
    private static final String NBT_SURVIVALPITCH = "SurvivalPitch";
    private static final String NBT_SURVIVALDIM = "SurvivalDim";
    private static final String NBT_SURVIVALEFFECTS = "SurvivalEffects";
    private Vec3d survivalPos;
    private float survivalYaw;
    private float survivalPitch;
    private int survivalDimension;
    private Set<StatusEffectInstance> survivalEffects = Sets.newHashSet();
    private long lastDamageTick;

    private boolean isInCameraMode;

    public ServerPlayerEntityMixin(World world, GameProfile profile) {
        super(world, profile);
    }

    @Shadow
    public ServerPlayNetworkHandler networkHandler;

    @Shadow
    @Final
    public MinecraftServer server;

    @Shadow
    @Final
    public ServerPlayerInteractionManager interactionManager;

    @Shadow
    public abstract ServerWorld getServerWorld();

    @Shadow
    public abstract void method_10763(Entity entity);

    @Inject(method = "damage", at = @At("HEAD"))
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (interactionManager.getGameMode() == LevelInfo.GameMode.SURVIVAL && amount > 0) {
            lastDamageTick = server.getTicks();
        }
    }

    @Inject(method = "copyFrom", at = @At("RETURN"))
    private void copyFrom(PlayerEntity player, boolean bl, CallbackInfo ci) {
        PlayerCameraHandler cameraHandler = (PlayerCameraHandler) player;
        survivalPos = cameraHandler.getSurvivalPos();
        survivalPitch = cameraHandler.getSurvivalPitch();
        survivalYaw = cameraHandler.getSurvivalYaw();
        survivalDimension = cameraHandler.getSurvivalDimension();
        survivalEffects = cameraHandler.getSurvivalEffects();
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCameraData(NbtCompound nbt, CallbackInfo ci) {
        if (isInCameraMode) {
            nbt.putBoolean(NBT_INCAMERA, true);
            nbt.putDouble(NBT_SURVIVALX, survivalPos.x);
            nbt.putDouble(NBT_SURVIVALY, survivalPos.y);
            nbt.putDouble(NBT_SURVIVALZ, survivalPos.z);
            nbt.putFloat(NBT_SURVIVALYAW, survivalYaw);
            nbt.putFloat(NBT_SURVIVALPITCH, survivalPitch);
            nbt.putShort(NBT_SURVIVALDIM, (short) survivalDimension);
            NbtList effects = new NbtList();
            for (StatusEffectInstance effect : survivalEffects) {
                effects.add(effect.toNbt(new NbtCompound()));
            }
            nbt.put(NBT_SURVIVALEFFECTS, effects);
        } else {
            nbt.putBoolean(NBT_INCAMERA, false);
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCameraData(NbtCompound nbt, CallbackInfo ci) {
        isInCameraMode = nbt.getBoolean(NBT_INCAMERA);
        if (isInCameraMode) {
            survivalPos = new Vec3d(
                    nbt.getDouble(NBT_SURVIVALX),
                    nbt.getDouble(NBT_SURVIVALY),
                    nbt.getDouble(NBT_SURVIVALZ));
            survivalYaw = nbt.getFloat(NBT_SURVIVALYAW);
            survivalPitch = nbt.getFloat(NBT_SURVIVALPITCH);
            survivalDimension = nbt.getShort(NBT_SURVIVALDIM);
            NbtList effects = nbt.getList(NBT_SURVIVALEFFECTS, 10);
            for (int i = 0; i < effects.size(); i++) {
                NbtCompound effect = effects.getCompound(i);
                if (!effect.isEmpty())
                    survivalEffects.add(StatusEffectInstance.fromNbt(effect));
            }
        }
    }

    @Override
    public String cameraModeBlockReason() {
        if (networkHandler == null || removed)
            return "player does not exist in world";
        if (getHealth() == 0.0f)
            return "player is dead";
        if (!RugSettings.cameraModeDisableDamageCooldown.isEnabled(this) && lastDamageTick != 0 &&
                lastDamageTick > (server.getTicks() - CAMERA_DAMAGE_COOLDOWN))
            return "player took damage too recently";
        return null;
    }

    @Override
    public boolean enterCameraMode() {
        if (isInCameraMode || !isPlayerConnected()) return false;
        survivalEffects.clear();
        survivalEffects.addAll(getStatusEffectInstances());
        survivalPos = getPos();
        survivalYaw = yaw;
        survivalPitch = pitch;
        survivalDimension = dimension;
        setGameMode(LevelInfo.GameMode.SPECTATOR);
        clearStatusEffects();
        addStatusEffect(new StatusEffectInstance(StatusEffect.NIGHTVISION.id, Short.MAX_VALUE, 0, true, false));
        isInCameraMode = true;
        return true;
    }

    @Override
    public boolean exitCameraMode() {
        if (!isInCameraMode || !isPlayerConnected()) return false;
        method_10763(this);
        startRiding(null);
        if (dimension != survivalDimension) {
            PlayerManager playerManager = server.getPlayerManager();
            ServerWorld currentWorld = getServerWorld();
            ServerWorld targetWorld = server.getWorld(survivalDimension);
            dimension = survivalDimension;
            networkHandler.sendPacket(new PlayerRespawnS2CPacket(dimension, currentWorld.getGlobalDifficulty(), currentWorld.getLevelProperties().getGeneratorType(), interactionManager.getGameMode()));
            currentWorld.method_3700(this);
            removed = false;
            refreshPositionAndAngles(survivalPos.x, survivalPos.y, survivalPos.z, survivalYaw, survivalPitch);
            if (isAlive()) {
                currentWorld.checkChunk(this, false);
                targetWorld.spawnEntity(this);
                targetWorld.checkChunk(this, false);
            }
            setWorld(targetWorld);
            playerManager.method_1986((ServerPlayerEntity) (Object) this, currentWorld);
            refreshPositionAfterTeleport(survivalPos.x, survivalPos.y, survivalPos.z);
            interactionManager.setWorld(targetWorld);
            playerManager.sendWorldInfo((ServerPlayerEntity) (Object) this, targetWorld);
            playerManager.method_2009((ServerPlayerEntity) (Object) this);
        } else {
            refreshPositionAfterTeleport(survivalPos.x, survivalPos.y, survivalPos.z);
        }
        setGameMode(LevelInfo.GameMode.SURVIVAL);
        velocityY = horizontalSpeed = fallDistance = 0;
        clearStatusEffects();
        for (StatusEffectInstance effect : survivalEffects) {
            addStatusEffect(effect);
        }
        isInCameraMode = false;
        return true;
    }

    @Override
    public boolean isInCameraMode() {
        return isInCameraMode;
    }

    @Override
    public Vec3d getSurvivalPos() {
        return survivalPos;
    }

    @Override
    public float getSurvivalPitch() {
        return survivalPitch;
    }

    @Override
    public float getSurvivalYaw() {
        return survivalYaw;
    }

    @Override
    public int getSurvivalDimension() {
        return survivalDimension;
    }

    @Override
    public Set<StatusEffectInstance> getSurvivalEffects() {
        return survivalEffects;
    }

    private boolean isPlayerConnected() {
        return networkHandler != null && isAlive();
    }
}
