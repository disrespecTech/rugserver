package github.totorewa.rugserver.mixin.feature.mobspawnnearplayer;

import github.totorewa.rugserver.RugSettings;
import github.totorewa.rugserver.fake.IMobCheckDespawn;
import net.minecraft.entity.MobSpawnerHelper;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MobSpawnerHelper.class)
public class MobSpawnerHelperMixin {
    @Redirect(method = "tickSpawners", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;canSpawn()Z"))
    private boolean checkMobInDistance(MobEntity mob) {
        if (!RugSettings.mobsOnlySpawnNearPlayers)
            return mob.canSpawn();
        if (!mob.canSpawn())
            return false;

        ((IMobCheckDespawn) mob).rug$checkDespawn();
        return !mob.removed;
    }
}
