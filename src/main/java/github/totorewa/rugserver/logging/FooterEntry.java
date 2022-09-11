package github.totorewa.rugserver.logging;

import github.totorewa.rugserver.util.message.Message;

public class FooterEntry {
    public final String key;
    public final InfoLogger logger;
    private Message message;

    public FooterEntry(String key, InfoLogger logger) {
        this.key = key;
        this.logger = logger;
    }

    public void setFooter(Message message) {
        this.message = message;
    }

    public Message getFooter() {
        return message;
    }
}
