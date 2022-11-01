package github.totorewa.rugserver.feature.player.actions;

import github.totorewa.rugserver.feature.player.PlayerController;

public interface PlayerAction {
    default void setup(PlayerController player, ActionParameters parameters) {}
    void execute(PlayerController player, ActionParameters parameters);
    default void end(PlayerController player, ActionParameters parameters) {}
    default boolean conflictsWith(PlayerActionType actionType) {
        return false;
    }
}
