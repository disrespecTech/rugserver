package github.totorewa.rugserver.mixin.feature.player;

import com.mojang.authlib.GameProfile;
import github.totorewa.rugserver.fake.IPlayerControllerAccessor;
import github.totorewa.rugserver.feature.player.PlayerController;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements IPlayerControllerAccessor {
    @Unique
    private PlayerController rug$controller;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstruct(MinecraftServer server, ServerWorld world, GameProfile profile, ServerPlayerInteractionManager interactionManager, CallbackInfo ci) {
        rug$controller = new PlayerController((ServerPlayerEntity) (Object) this);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        rug$controller.tick();
    }

    @Inject(method = "copyFrom", at = @At("HEAD"))
    private void onCopyFrom(PlayerEntity player, boolean isAlive, CallbackInfo ci) {
        if (isAlive) rug$controller.copyFrom(((IPlayerControllerAccessor) player).getPlayerController());
    }

    @Override
    public PlayerController getPlayerController() {
        return rug$controller;
    }
}
