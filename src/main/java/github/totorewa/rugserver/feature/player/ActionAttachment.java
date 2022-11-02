package github.totorewa.rugserver.feature.player;

import github.totorewa.rugserver.feature.player.actions.ActionParameters;
import github.totorewa.rugserver.feature.player.actions.PlayerActionType;

public class ActionAttachment implements Augmentation {
    public final PlayerActionType actionType;
    public final ActionParameters parameters;

    private int tickCount = -1;
    private int runs;
    private boolean toBeRemoved;
    private boolean forceFinish;

    public ActionAttachment(PlayerActionType actionType, int interval, int limit, boolean continuous) {
        this.actionType = actionType;
        parameters = new ActionParameters(interval, limit, continuous);
    }

    public void tick(PlayerController controller) {
        if (tickCount == -1) {
            actionType.action.setup(controller, parameters);
            if (toBeRemoved || isLimitReached()) {
                actionType.action.end(controller, parameters);
                forceFinish = toBeRemoved;
            }
            tickCount++;
            return;
        }
        if (toBeRemoved || isLimitReached()) {
            actionType.action.end(controller, parameters);
            forceFinish = toBeRemoved;
        } else if (tickCount++ % parameters.interval == 0) {
            actionType.action.execute(controller, parameters);
            runs++;
        }
    }

    @Override
    public void scheduleDisable() {
        toBeRemoved = true;
    }

    @Override
    public boolean isDisabled() {
        return forceFinish || isLimitReached();
    }

    @Override
    public String getName() {
        return actionType.actionName;
    }

    @Override
    public boolean conflictsWith(Augmentation other) {
        return other instanceof ActionAttachment && actionType.action.conflictsWith(((ActionAttachment) other).actionType);
    }

    private boolean isLimitReached() {
        return parameters.limit > -1 && runs >= parameters.limit;
    }
}
