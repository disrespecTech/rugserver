package me.totorewa.dissserver.helper;

import me.totorewa.dissserver.util.message.Message;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;

import java.util.Locale;

public class TPSHelper {
    public static final double MAX_MSPT = 50.0D;

    public static Message createMessage(MinecraftServer server) {
        double mspt = MathHelper.average(server.tickTimeArray) * 1.0E-6D;
        double tps = 1000.0D / Math.max(50.0D, mspt);
        int heatStyle = getColor(mspt);

        return new Message("TPS: ", Message.GRAY)
                .add(formatDouble(tps), heatStyle)
                .add(" MSPT: ", Message.GRAY)
                .add(formatDouble(mspt), heatStyle);
    }

    private static int getColor(double value) {
        if (value > MAX_MSPT) return Message.LIGHT_PURPLE;
        if (value > 0.8 * MAX_MSPT) return Message.RED;
        if (value > 0.5 * MAX_MSPT) return Message.YELLOW;
        if (value >= 0.0D) return Message.DARK_GREEN;
        return Message.GRAY;
    }

    private static String formatDouble(double d) {
        return String.format(Locale.US, "%.1f", d);
    }
}
