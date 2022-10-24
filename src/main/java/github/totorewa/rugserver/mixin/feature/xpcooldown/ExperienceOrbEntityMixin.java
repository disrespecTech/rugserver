package github.totorewa.rugserver.mixin.feature.xpcooldown;

import github.totorewa.rugserver.RugSettings;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ExperienceOrbEntity.class)
public class ExperienceOrbEntityMixin {
    @Redirect(method = "onPlayerCollision", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/ExperienceOrbEntity;pickupDelay:I", opcode = Opcodes.GETFIELD))
    private int alwaysZeroDelay(ExperienceOrbEntity xp) {
        return RugSettings.xpNoCooldown ? 0 : xp.pickupDelay;
    }

    @Redirect(method = "onPlayerCollision", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerEntity;experiencePickUpDelay:I", opcode = Opcodes.GETFIELD))
    private int alwaysZeroCooldown(PlayerEntity player) {
        return RugSettings.xpNoCooldown ? 0 : player.experiencePickUpDelay;
    }
}
