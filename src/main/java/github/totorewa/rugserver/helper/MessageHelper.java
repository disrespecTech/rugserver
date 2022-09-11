package github.totorewa.rugserver.helper;

import github.totorewa.rugserver.util.message.Message;
import net.minecraft.entity.EntityCategory;

import java.util.Locale;

public class MessageHelper {
    public static int getHeatmapColor(double value, double maxValue) {
        if (value > maxValue) return Message.LIGHT_PURPLE;
        if (value > 0.8 * maxValue) return Message.RED;
        if (value > 0.5 * maxValue) return Message.YELLOW;
        if (value >= 0.0D) return Message.DARK_GREEN;
        return Message.GRAY;
    }

    public static String formatDouble(double d) {
        return String.format(Locale.US, "%.1f", d);
    }

    public static int getEntityCategoryColor(EntityCategory category) {
        switch (category) {
            case MONSTER:
                return Message.DARK_RED;
            case PASSIVE:
                return Message.DARK_GREEN;
            case AMBIENT:
                return Message.DARK_GRAY;
            case AQUATIC:
                return Message.DARK_BLUE;
        }
        return Message.WHITE;
    }
}
