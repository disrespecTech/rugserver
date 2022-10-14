package github.totorewa.rugserver.helper;

import github.totorewa.rugserver.RugServerMod;
import github.totorewa.rugserver.fake.ITickRate;
import github.totorewa.rugserver.util.message.Message;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

public class TickHelper {
    public static Text tryAdjustTickRate(int targetRate) {
        MinecraftServer server = RugServerMod.mod.server;
        if (server == null) return Message.createComponent("Could not adjust tick rate", Message.RED);

        if (targetRate < 1) return Message.createComponent("Tick rate must be 1 or greater", Message.RED);

        ITickRate tickRateProvider = (ITickRate) server;
        tickRateProvider.setTickRate(targetRate);
        return null;
    }

    public static int getCurrentTickRate() {
        MinecraftServer server = RugServerMod.mod.server;
        return server == null ? 20 : ((ITickRate) server).getTickRate();
    }
}
