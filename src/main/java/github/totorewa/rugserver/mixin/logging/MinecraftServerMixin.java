package github.totorewa.rugserver.mixin.logging;

import github.totorewa.rugserver.RugServerMod;
import github.totorewa.rugserver.RugSettings;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Inject(method = "saveWorlds", at = @At("TAIL"))
    private void onWorldSave(boolean silent, CallbackInfo ci) {
        if (RugSettings.persistLogSubscriptions)
            RugServerMod.mod.getLogSubscriptionPersistenceHandler().save();
    }
}
