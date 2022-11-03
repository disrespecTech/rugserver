package github.totorewa.rugserver.mixin.feature.setspawn;

import github.totorewa.rugserver.RugSettings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    @Shadow
    public abstract void setPlayerSpawn(BlockPos pos, boolean spawnForced);

    @Shadow
    public abstract void addMessage(Text text);

    @Shadow
    private BlockPos spawnPos;

    public PlayerEntityMixin(World world) {
        super(world);
    }

    @Inject(method = "attemptSleep", at = @At("RETURN"))
    private void setSpawnOnSleepAttempt(BlockPos pos, CallbackInfoReturnable<PlayerEntity.SleepStatus> cir) {
        if (RugSettings.setSpawnWithoutSleep && !world.isClient) {
            PlayerEntity.SleepStatus status = cir.getReturnValue();
            if ((status == PlayerEntity.SleepStatus.NOT_POSSBLE_NOW || status == PlayerEntity.SleepStatus.NOT_SAFE) &&
                    (spawnPos == null || !spawnPos.equals(pos))) {
                setPlayerSpawn(pos, false);
                addMessage(new LiteralText("Respawn point set"));
            }
        }
    }

}
