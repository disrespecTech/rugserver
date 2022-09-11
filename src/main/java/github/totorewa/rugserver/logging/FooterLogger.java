package github.totorewa.rugserver.logging;

public class FooterLogger extends InfoLogger {
    protected FooterEntry footer;

    protected FooterLogger(String name, LoggingTick ticker) {
        super(name, ticker);
    }

    @Override
    public void initialize() {
        super.initialize();
        footer = FooterController.add(name, this);
    }
}
