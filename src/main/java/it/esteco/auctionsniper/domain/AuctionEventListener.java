package it.esteco.auctionsniper.domain;

import java.util.EventListener;

public interface AuctionEventListener extends EventListener {

    enum PriceSource {
        FromSniper, FromOtherBidder
    }

    void auctionClosed();

    void currentPrice(int price, int increment, PriceSource fromOtherBidder);

    void auctionFailed();
}
