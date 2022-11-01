package github.totorewa.rugserver.feature.player.actions;

import github.totorewa.rugserver.feature.player.PlayerController;

public class PlayerDropAction implements PlayerAction {
    protected boolean dropStacks = false;

    @Override
    public void execute(PlayerController ctrl, ActionParameters parameters) {
        ctrl.player.dropSelectedItem(dropStacks);
    }

    @Override
    public boolean conflictsWith(PlayerActionType actionType) {
        return actionType == PlayerActionType.DROP_STACK;
    }
}
