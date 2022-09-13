package github.totorewa.rugserver.feature.player;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class FakeClientConnection extends ClientConnection {
    public FakeClientConnection() {
        super(NetworkSide.SERVERBOUND);
    }

    @Override
    public void disableAutoRead() {
    }

    @Override
    public void disconnect(Text disconnectReason) {
    }

    @Override
    public void handleDisconnection() {
        if (this.getDisconnectReason() != null) {
            this.getPacketListener().onDisconnected(this.getDisconnectReason());
        } else if (this.getPacketListener() != null) {
            this.getPacketListener().onDisconnected(new LiteralText("Disconnected"));
        }
    }
}
