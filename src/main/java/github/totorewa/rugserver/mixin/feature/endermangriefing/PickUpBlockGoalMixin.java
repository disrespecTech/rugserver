package github.totorewa.rugserver.mixin.feature.endermangriefing;

import github.totorewa.rugserver.RugSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.entity.mob.EndermanEntity$PickUpBlockGoal")
public class PickUpBlockGoalMixin {
    @Inject(method = "canStart", at = @At("HEAD"), cancellable = true)
    private void disablePickup(CallbackInfoReturnable<Boolean> cir) {
        if (RugSettings.endermanNoGriefing)
            cir.setReturnValue(false);
    }
}
