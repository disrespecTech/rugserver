package me.totorewa.dissserver.helper;

import net.minecraft.network.play.server.S47PacketPlayerListHeaderFooter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;

import java.util.Locale;

public class TPSHelper {
    public static final double MAX_MSPT = 50.0D;

    public static void logTPS(MinecraftServer server) {
        IChatComponent message = getTPSMessage(server);
        S47PacketPlayerListHeaderFooter packet = new S47PacketPlayerListHeaderFooter(message);
        server.getConfigurationManager().sendPacketToAllPlayers(packet);
    }

    private static IChatComponent getTPSMessage(MinecraftServer server) {
        double mspt = MathHelper.average(server.tickTimeArray) * 1.0E-6D;
        double tps = 1000.0D / Math.max(50.0D, mspt);
        EnumChatFormatting heat = getColor(mspt);

        IChatComponent root = new ChatComponentText("");

        IChatComponent curr = new ChatComponentText("TPS: ");
        curr.getChatStyle().setColor(EnumChatFormatting.GRAY);
        root.appendSibling(curr);

        curr = new ChatComponentText(formatDouble(tps));
        curr.getChatStyle().setColor(heat);
        root.appendSibling(curr);

        curr = new ChatComponentText(" MSPT: ");
        curr.getChatStyle().setColor(EnumChatFormatting.GRAY);
        root.appendSibling(curr);

        curr = new ChatComponentText(formatDouble(mspt));
        curr.getChatStyle().setColor(heat);
        root.appendSibling(curr);

        return root;
    }

    private static EnumChatFormatting getColor(double value) {
        if (value > MAX_MSPT) return EnumChatFormatting.LIGHT_PURPLE;
        if (value > 0.8 * MAX_MSPT) return EnumChatFormatting.RED;
        if (value > 0.5 * MAX_MSPT) return EnumChatFormatting.YELLOW;
        if (value >= 0.0D) return EnumChatFormatting.DARK_GREEN;
        return EnumChatFormatting.GRAY;
    }

    private static String formatDouble(double d) {
        return String.format(Locale.US, "%.1f", d);
    }
}
