package github.totorewa.rugserver.feature.player;

import com.mojang.authlib.GameProfile;
import github.totorewa.rugserver.util.message.Message;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class FakeServerPlayerEntity extends ServerPlayerEntity {
    private double startingX;
    private double startingY;
    private double startingZ;
    private float startingYaw;
    private float startingPitch;
    private final boolean isShadow;

    public FakeServerPlayerEntity(MinecraftServer server, ServerWorld world, GameProfile profile, ServerPlayerInteractionManager interactionManager, boolean isShadow) {
        super(server, world, profile, interactionManager);
        this.isShadow = isShadow;
    }

    public void setStartingPosition(double x, double y, double z, float yaw, float pitch) {
        startingX = x;
        startingY = y;
        startingZ = z;
        startingYaw = yaw;
        startingPitch = pitch;
    }

    public void moveToStartingPosition() {
        refreshPositionAndAngles(startingX, startingY, startingZ, startingYaw, startingPitch);
    }

    @Override
    public void tick() {
        super.tick();
        tickPlayer();
    }

    @Override
    public void kill() {
        logout();
    }

    @Override
    public void onKilled(DamageSource source) {
        super.onKilled(source);
        setHealth(20);
        logout();
    }

    public void logout() {
        networkHandler.onDisconnected(new LiteralText("Bot killed"));
    }

    @Override
    public Text getDisplayName() {
        return new Message(isShadow ? "[Shadow] " : "[Bot] ", Message.ITALIC | (isShadow ? Message.DARK_GRAY : Message.GRAY))
                .add(getGameProfile().getName(), Message.GRAY)
                .toText();
    }
}
