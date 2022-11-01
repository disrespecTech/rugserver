package github.totorewa.rugserver.feature.player;

import github.totorewa.rugserver.feature.player.actions.ActionParameters;
import github.totorewa.rugserver.feature.player.actions.PlayerActionType;

public class ActionAttachment {
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

    public void remove() {
        toBeRemoved = true;
    }

    public boolean isFinished() {
        return forceFinish || isLimitReached();
    }

    private boolean isLimitReached() {
        return parameters.limit > -1 && runs >= parameters.limit;
    }
}
