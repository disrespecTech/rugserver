package github.totorewa.rugserver.feature.player;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.LiteralText;

public class FakeServerPlayNetworkHandler extends ServerPlayNetworkHandler {
    public FakeServerPlayNetworkHandler(MinecraftServer server, ClientConnection connection, ServerPlayerEntity player) {
        super(server, connection, player);
    }

    @Override
    public void sendPacket(Packet packet) {
    }

    @Override
    public void disconnect(String reason) {
        this.onDisconnected(new LiteralText(reason));
    }
}
