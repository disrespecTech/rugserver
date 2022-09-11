package github.totorewa.rugserver.mixin.logging;

import github.totorewa.rugserver.fake.IMobSpawnerHelper;
import net.minecraft.entity.MobSpawnerHelper;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MobSpawnerHelper.class)
public class MobSpawnerHelperMixin implements IMobSpawnerHelper {
    public int lastChunkCount = 1;

    @Inject(method = "tickSpawners", at = @At(value = "RETURN", ordinal = 2), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void captureChunkCount(
            ServerWorld world, boolean spawnAnimals, boolean spawnMonsters, boolean spawnSpecial,
            CallbackInfoReturnable<Integer> cir, int chunkCount) {
        lastChunkCount = chunkCount;
    }

    @Override
    public int getLastChunkCount() {
        return lastChunkCount;
    }
}
