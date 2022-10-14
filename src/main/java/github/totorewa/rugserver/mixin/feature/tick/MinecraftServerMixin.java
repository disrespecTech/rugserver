package github.totorewa.rugserver.mixin.feature.tick;

import github.totorewa.rugserver.fake.ITickRate;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements ITickRate {
    @Unique
    private long tickRateMs = 50L;
    @Unique
    private int tickRate = 20;

    @ModifyConstant(method = "run", constant = @Constant(longValue = 50L))
    private long overrideTickRate(long tickRate) {
        return tickRateMs;
    }


    @Override
    public int getTickRate() {
        return tickRate;
    }

    @Override
    public int getTickSpeed() {
        return (int) tickRateMs;
    }

    @Override
    public void setTickRate(int tickRate) {
        this.tickRate = tickRate;
        tickRateMs = Math.max(MathHelper.floor(1000f / tickRate), 1L);
    }
}
