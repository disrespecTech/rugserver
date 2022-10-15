package github.totorewa.rugserver.mixin.feature.spawnchunks;

import github.totorewa.rugserver.fake.IWorldSpawn;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class WorldMixin implements IWorldSpawn {
    @Unique
    private int spawnChunkDistanceInBlocks = 128;

    @Inject(method = "isChunkLoaded(II)Z", at = @At("HEAD"), cancellable = true)
    private void disableSpawnChunks(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        if (spawnChunkDistanceInBlocks == 0)
            cir.setReturnValue(false);
    }

    @ModifyConstant(method = "isChunkLoaded(II)Z", constant = @Constant(intValue = 128))
    private int overrideChunkSpawnDistance(int constant) {
        return spawnChunkDistanceInBlocks;
    }

    @Override
    public int getSpawnChunkDistanceInBlocks() {
        return spawnChunkDistanceInBlocks;
    }

    @Override
    public void setSpawnChunkDistanceInBlocks(int spawnChunkDistanceInBlocks) {
        this.spawnChunkDistanceInBlocks = spawnChunkDistanceInBlocks;
    }
}
