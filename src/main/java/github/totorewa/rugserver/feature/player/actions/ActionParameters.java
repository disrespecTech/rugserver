package github.totorewa.rugserver.feature.player.actions;

public class ActionParameters {
    public final int interval;
    public final int limit;
    public final boolean continuous;

    public ActionParameters(int interval, int limit, boolean continuous) {
        this.interval = interval;
        this.limit = limit;
        this.continuous = continuous;
    }
}
