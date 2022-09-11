package github.totorewa.rugserver.feature.carefulbreak;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class CarefulBreakManager {
    public static final CarefulBreakManager INSTANCE = new CarefulBreakManager();

    private boolean isCarefulBreaking;
    private ServerPlayerEntity breakingPlayer;
    private BlockPos breakingPos;

    private CarefulBreakManager() {
    }

    public boolean shouldCarefulBreak() {
        return isCarefulBreaking && breakingPlayer != null && breakingPos != null;
    }

    public void carefulBreak(ServerPlayerEntity player, BlockPos pos) {
        isCarefulBreaking = true;
        breakingPlayer = player;
        breakingPos = pos;
    }

    public void clear() {
        isCarefulBreaking = false;
        breakingPlayer = null;
        breakingPos = null;
    }

    public ServerPlayerEntity getBreakingPlayer() {
        return breakingPlayer;
    }

    public boolean isBrokenBlock(BlockPos pos) {
        return shouldCarefulBreak() && pos.equals(breakingPos);
    }
}
