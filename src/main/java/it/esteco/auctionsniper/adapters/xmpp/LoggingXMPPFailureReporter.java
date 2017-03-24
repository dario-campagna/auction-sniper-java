package it.esteco.auctionsniper.adapters.xmpp;

import java.util.logging.Logger;

public class LoggingXMPPFailureReporter implements XMPPFailureReporter {

    private static final String LOG_MESSAGE_FORMAT = "<%s> " +
            "Could not translate message \"%s\" " +
            "because \"%s\"";
    private Logger logger;

    public LoggingXMPPFailureReporter(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void cannotTranslateMessage(String auctionId, String failedMessage, Exception exception) {
        logger.severe(String.format(LOG_MESSAGE_FORMAT, auctionId, failedMessage, exception));
    }
}
