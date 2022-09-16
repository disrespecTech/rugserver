package github.totorewa.rugserver.util.settings;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;

public enum OpEnableOption {
    FALSE("false", true, false),
    TRUE("true", false, false),
    OP("op", false, true);

    public final String key;
    private final boolean disabled;
    private final boolean opOnly;

    OpEnableOption(String key, boolean disabled, boolean opOnly) {
        this.key = key;
        this.disabled = disabled;
        this.opOnly = opOnly;
    }

    public boolean isEnabled(ServerPlayerEntity player) {
        return !disabled && (!opOnly || player.server.getPlayerManager().isOperator(player.getGameProfile()));
    }

    public boolean isEnabled(Entity entity) {
        return entity instanceof ServerPlayerEntity && isEnabled((ServerPlayerEntity) entity);
    }

    public boolean isEnabled(CommandSource source) {
        return isEnabled(source.getEntity());
    }

    public static OpEnableOption getByKey(String key) {
        for (OpEnableOption option : values()) {
            if (option.key.equalsIgnoreCase(key)) {
                return option;
            }
        }
        return null;
    }
}
