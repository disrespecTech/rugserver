package github.totorewa.rugserver.mixin.feature.noendcredits;

import com.mojang.authlib.GameProfile;
import github.totorewa.rugserver.RugSettings;
import github.totorewa.rugserver.helper.Teleporter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    public ServerPlayerEntityMixin(World world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(
            method = "teleportToDimension",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;removeEntity(Lnet/minecraft/entity/Entity;)V",
                    shift = At.Shift.AFTER),
            cancellable = true)
    private void preventEndCreditPacket(int dimensionId, CallbackInfo ci) {
        if (RugSettings.noEndCredits) {
            ci.cancel();
            Teleporter.teleportToSpawnPoint((ServerPlayerEntity) (Object) this);
        }
    }
}
