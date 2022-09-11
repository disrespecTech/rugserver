package github.totorewa.rugserver.logging;

import com.google.common.collect.Lists;
import github.totorewa.rugserver.mixin.logging.PlayerListHeaderS2CPacketAccessor;
import github.totorewa.rugserver.util.message.Message;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.world.World;

import java.util.List;

public class FooterController {
    private static final List<FooterEntry> footers = Lists.newArrayList();

    public static FooterEntry add(String key, InfoLogger logger) {
        final FooterEntry entry = new FooterEntry(key, logger);
        footers.add(entry);
        return entry;
    }

    public static List<FooterEntry> getFooters() {
        return footers;
    }

    public static void tick(World world, long tick) {
        if (tick % 20 != 0) return;

        for (PlayerEntity player : world.playerEntities) {
            Message message = null;
            for (FooterEntry entry : footers) {
                if (entry.getFooter() == null) continue;
                if (!entry.logger.isLogging(player.getGameProfile().getName())) continue;
                if (message == null) message = entry.getFooter();
                else message.add("\n").add(entry.getFooter());
            }
            if (message == null) continue;
            PlayerListHeaderS2CPacket packet = new PlayerListHeaderS2CPacket(new LiteralText(""));
            ((PlayerListHeaderS2CPacketAccessor) packet).setFooter(message.toText());
            ((ServerPlayerEntity) player).networkHandler.sendPacket(packet);
        }
    }
}
