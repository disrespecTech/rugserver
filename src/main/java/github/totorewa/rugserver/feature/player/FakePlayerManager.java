package github.totorewa.rugserver.feature.player;

import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.UserCache;
import net.minecraft.world.level.LevelInfo;

import java.util.UUID;
import java.util.stream.Collectors;

public class FakePlayerManager {
    private final PlayerManager playerManager;
    public final MinecraftServer server;

    public FakePlayerManager(MinecraftServer server) {
        this.server = server;
        playerManager = server.getPlayerManager();
    }

    public Iterable<FakeServerPlayerEntity> getFakePlayers() {
        return server.getPlayerManager().getPlayers().stream()
                .filter(p -> p instanceof FakeServerPlayerEntity)
                .map(p -> (FakeServerPlayerEntity) p)
                .collect(Collectors.toList());
    }

    public FakeServerPlayerEntity getFakePlayerByName(String username) {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(username);
        return player instanceof FakeServerPlayerEntity ? (FakeServerPlayerEntity) player : null;
    }

    public boolean spawnFake(final String name, final ServerPlayerEntity spawner) {
        // Get profile by username
        GameProfile profile = server.getUserCache().findByName(name);
        if (profile == null) {
            final GameProfile[] repoOutput = new GameProfile[]{null};
            server.getGameProfileRepo().findProfilesByNames(new String[]{name}, Agent.MINECRAFT, new ProfileLookupCallback() {
                @Override
                public void onProfileLookupSucceeded(GameProfile profile) {
                    repoOutput[0] = profile;
                }

                @Override
                public void onProfileLookupFailed(GameProfile profile, Exception exception) {
                    UUID id = PlayerEntity.getOfflinePlayerUuid(name);
                    repoOutput[0] = new GameProfile(id, name);
                }
            });
            profile = repoOutput[0];
            if (profile == null) return false;
        }
        SkullBlockEntity.method_8997(profile); // Load skin texture

        ServerWorld world = spawner.getServerWorld();
        ServerPlayerInteractionManager interactionManager = new ServerPlayerInteractionManager(world);

        FakeServerPlayerEntity player = new FakeServerPlayerEntity(server, world, profile, interactionManager, false);
        player.setStartingPosition(spawner.x, spawner.y, spawner.z, spawner.yaw, spawner.pitch);
        playerManager.onPlayerConnect(new FakeClientConnection(), player);

        // Fix wrong dimension
        if (player.dimension != spawner.dimension) {
            ServerWorld otherWorld = server.getWorld(player.dimension);
            player.dimension = spawner.dimension;
            otherWorld.method_3700(player);
            player.removed = false;
            world.spawnEntity(player);
            player.setWorld(world);
            playerManager.method_1986(player, otherWorld);
            player.networkHandler.requestTeleport(spawner.x, spawner.y, spawner.z, spawner.yaw, spawner.pitch);
            player.interactionManager.setWorld(world);
        } else {
            player.networkHandler.requestTeleport(spawner.x, spawner.y, spawner.z, spawner.yaw, spawner.pitch);
        }

        spawnPlayerIntoWorld(player, spawner.interactionManager.getGameMode());
        return true;
    }

    public boolean spawnShadow(final ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        ServerPlayerInteractionManager interactionManager = new ServerPlayerInteractionManager(world);
        GameProfile profile = player.getGameProfile();
        player.networkHandler.disconnect("You logged in from another location");
        SkullBlockEntity.method_8997(profile); // Load skin texture

        FakeServerPlayerEntity shadow = new FakeServerPlayerEntity(server, world, profile, interactionManager, true);
        shadow.setStartingPosition(player.x, player.y, player.z, player.yaw, player.pitch);
        playerManager.onPlayerConnect(new FakeClientConnection(), shadow);
        spawnPlayerIntoWorld(shadow, player.interactionManager.getGameMode());
        return true;
    }

    private void spawnPlayerIntoWorld(FakeServerPlayerEntity player, LevelInfo.GameMode gameMode) {
        player.removed = false;
        player.stepHeight = 0.6f;
        player.setHealth(20.0f);
        player.updateVelocity(0.0f, 0.0f, 0.0f);
        player.abilities.allowFlying = !server.getDefaultGameMode().isSurvivalLike();
        player.interactionManager.setGameMode(gameMode);
        playerManager.sendToDimension(new EntitySetHeadYawS2CPacket(player, (byte) (player.headYaw * 256.0f / 360.0f)), player.dimension);
        playerManager.sendToDimension(new EntityPositionS2CPacket(player), player.dimension);
        playerManager.method_2009(player);
        player.getDataTracker().setProperty(10, (byte) 0x7f);
    }

    public boolean killFake(String name) {
        FakeServerPlayerEntity player = getFakePlayerByName(name.toLowerCase());
        if (player == null) return false;
        player.logout();
        return true;
    }

    private static FakePlayerManager instance;

    public static FakePlayerManager getInstance(MinecraftServer server) {
        if (instance == null || instance.server != server)
            instance = new FakePlayerManager(server);

        return instance;
    }
}
