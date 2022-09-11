package github.totorewa.rugserver.mixin.feature.anticheat;

import net.minecraft.server.network.ServerPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {
    @ModifyConstant(method = "method_10764", constant = @Constant(floatValue = 0.7f))
    private float relaxBlockBreakTime(float constant) {
        return 0.001f;
    }
}
