package me.totorewa.dissserver.helper;

import me.totorewa.dissserver.util.message.Message;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.WorldServer;

public class MobcapHelper {
    public static Message[] createMessages(MinecraftServer server) {
        Message[] dims = new Message[server.worldServers.length];
        for (int i = 0; i < dims.length; i++) {
            WorldServer world = server.worldServers[i];
            Message message = null;
            for (EnumCreatureType creatureType : EnumCreatureType.values()) {
                if (message == null) message = new Message();
                else message.add("  ", Message.WHITE);
                int count = world.countEntities(creatureType.getCreatureClass());
                int cap = creatureType.getMaxNumberOfCreature()
                        * world.mobSpawner.lastEligibleChunkCount
                        / SpawnerAnimals.MOB_COUNT_DIV;
                message.add(String.format("%d", count), Message.RESET)
                        .add("/")
                        .add(String.format("%d", cap), getCreatureColor(creatureType));
            }
            dims[i] = message == null ? new Message() : message;
        }
        return dims;
    }

    private static int getCreatureColor(EnumCreatureType creatureType) {
        switch (creatureType) {
            case MONSTER:
                return Message.DARK_RED;
            case CREATURE:
                return Message.DARK_GREEN;
            case AMBIENT:
                return Message.DARK_GRAY;
            case WATER_CREATURE:
                return Message.DARK_BLUE;
        }
        return Message.WHITE;
    }
}
