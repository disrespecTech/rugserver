package github.totorewa.rugserver.feature.player.actions;

public class PlayerDropStackAction extends PlayerDropAction {
    public PlayerDropStackAction() {
        dropStacks = true;
    }

    @Override
    public boolean conflictsWith(PlayerActionType actionType) {
        return actionType == PlayerActionType.DROP;
    }
}
