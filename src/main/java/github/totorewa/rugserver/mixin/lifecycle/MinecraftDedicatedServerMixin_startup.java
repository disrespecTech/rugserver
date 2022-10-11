package github.totorewa.rugserver.mixin.lifecycle;

import github.totorewa.rugserver.RugServerMod;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftDedicatedServer.class)
public class MinecraftDedicatedServerMixin_startup {
    @Inject(method = "setupServer", at = @At("TAIL"))
    private void afterSetup(CallbackInfoReturnable<Boolean> cir) {
        RugServerMod.mod.onServerSetup((MinecraftServer) (Object) this);
    }
}
