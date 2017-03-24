package it.esteco.auctionsniper.adapters.xmpp;

public interface XMPPFailureReporter {

    void cannotTranslateMessage(String auctionId, String failedMessage, Exception exception);

}
