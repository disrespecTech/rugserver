package github.totorewa.rugserver.feature.player.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public enum PlayerActionType {
    ATTACK(PlayerAttackAction.class, "attack"),
    DROP(PlayerDropAction.class, "drop"),
    DROP_STACK(PlayerDropStackAction.class, "dropStack");

    public static final Map<String, PlayerActionType> nameMap;
    public static final Set<String> names;

    public final PlayerAction action;
    public final String actionName;

    PlayerActionType(Class<? extends PlayerAction> clazz, String actionName) {
        this.actionName = actionName;
        PlayerAction playerAction = null;
        try {
            playerAction = clazz.getConstructor().newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        action = playerAction;
    }

    static {
        nameMap = Arrays.stream(values()).collect(Collectors.toMap(k -> k.actionName, p -> p));
        names = nameMap.keySet();
    }
}
