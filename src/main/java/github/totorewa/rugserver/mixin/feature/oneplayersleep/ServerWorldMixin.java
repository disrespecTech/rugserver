package github.totorewa.rugserver.mixin.feature.oneplayersleep;

import github.totorewa.rugserver.util.message.Message;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerWorldManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.SaveHandler;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.level.LevelProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {
    @Shadow
    private boolean ready;

    @Shadow public abstract MinecraftServer getServer();

    @Shadow public abstract PlayerWorldManager getPlayerWorldManager();

    private String sleepingPlayerName;

    protected ServerWorldMixin(SaveHandler handler, LevelProperties properties, Dimension dim, Profiler profiler, boolean isClient) {
        super(handler, properties, dim, profiler, isClient);
    }

    /**
     * @reason For one player sleep
     * @author Totorewa
     */
    @Overwrite
    public void method_3669() {
        this.ready = false;
        for (PlayerEntity player : playerEntities) {
            if (player.isSleeping()) {
                this.ready = true;
                return;
            }
        }
    }

    /**
     * @reason For one player sleep
     * @author Totorewa
     */
    @Overwrite
    public boolean isReady() {
        if (this.ready && !this.isClient) {
            for (PlayerEntity player : playerEntities) {
                if (player.isSleepingLongEnough()) {
                    sleepingPlayerName = player.getGameProfile().getName();
                    return true;
                }
            }
        }

        return false;
    }

    @Inject(method = "method_2141", at = @At("RETURN"))
    private void announceSleeper(CallbackInfo ci) {
        if (sleepingPlayerName != null && playerEntities.size() > 1) {
            Text message = new Message(sleepingPlayerName, Message.YELLOW)
                    .add(" went to sleep. Sweet Dreams!", Message.GOLD)
                    .toText();
            ChatMessageS2CPacket packet = new ChatMessageS2CPacket(message);
            for (PlayerEntity player : playerEntities) {
                if (((ServerPlayerEntity)player).networkHandler != null)
                    ((ServerPlayerEntity)player).networkHandler.sendPacket(packet);
            }
        }
    }
}
