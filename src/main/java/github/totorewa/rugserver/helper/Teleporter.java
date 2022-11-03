package github.totorewa.rugserver.helper;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public final class Teleporter {
    private Teleporter() {
    }

    public static void teleportPlayer(ServerPlayerEntity player, double x, double y, double z, int dimension) {
        teleportPlayer(player, x, y, z, dimension, player.yaw, player.pitch);
    }

    public static void teleportPlayer(ServerPlayerEntity player, double x, double y, double z, int dimension, float yaw, float pitch) {
        teleportPlayer(player.server.getWorld(dimension), player, x, y, z, yaw, pitch);
    }

    public static void teleportPlayer(ServerWorld world, ServerPlayerEntity player, double x, double y, double z, float yaw, float pitch) {
        player.method_10763(player);
        player.startRiding(null);
        if (player.dimension != world.dimension.getType()) {
            PlayerManager playerManager = player.server.getPlayerManager();
            ServerWorld currentWorld = player.getServerWorld();
            player.dimension = world.dimension.getType();
            player.networkHandler.sendPacket(new PlayerRespawnS2CPacket(player.dimension, currentWorld.getGlobalDifficulty(), currentWorld.getLevelProperties().getGeneratorType(), player.interactionManager.getGameMode()));
            currentWorld.method_3700(player);
            player.removed = false;
            player.refreshPositionAndAngles(x, y, z, yaw, pitch);
            if (player.isAlive()) {
                currentWorld.checkChunk(player, false);
                world.spawnEntity(player);
                world.checkChunk(player, false);
            }
            player.setWorld(world);
            playerManager.method_1986(player, currentWorld);
            player.refreshPositionAfterTeleport(x, y, z);
            player.interactionManager.setWorld(world);
            playerManager.sendWorldInfo(player, world);
            playerManager.method_2009(player);
        } else {
            player.yaw = yaw % 360.0f;
            player.pitch = pitch % 360.0f;
            player.refreshPositionAfterTeleport(x, y, z);
        }
        player.velocityY = player.horizontalSpeed = player.fallDistance = 0;
    }

    public static void teleportToSpawnPoint(ServerPlayerEntity player) {
        ServerWorld world = player.server.getWorld(0);
        BlockPos spawnPos = player.getSpawnPosition();
        if (spawnPos != null)
            spawnPos = PlayerEntity.findRespawnPosition(world, spawnPos, player.isSpawnForced());
        if (spawnPos == null) {
            spawnPos = world.getSpawnPos();
            player.setPlayerSpawn(null, false);
            int offsetRange = Math.max(5, player.server.getSpawnProtectionRadius() - 6);
            int max = MathHelper.floor(world.getWorldBorder().getDistanceInsideBorder(spawnPos.getX(), spawnPos.getZ()));
            if (max < offsetRange) {
                offsetRange = max;
            }
            if (max <= 1) {
                offsetRange = 1;
            }
            spawnPos = world.getTopPosition(
                    spawnPos.add(
                            world.random.nextInt(offsetRange * 2) - offsetRange,
                            0,
                            world.random.nextInt(offsetRange * 2) - offsetRange));
        }
        teleportPlayer(world, player, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 0.0f, 0.0f);
    }
}
