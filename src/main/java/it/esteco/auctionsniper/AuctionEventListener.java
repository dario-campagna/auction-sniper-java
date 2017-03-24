package it.esteco.auctionsniper;

import java.util.EventListener;

public interface AuctionEventListener extends EventListener {

    void auctionClosed();

    void currentPrice(int price, int increment, PriceSource fromOtherBidder);

    enum PriceSource {
        FromSniper, FromOtherBidder
    }
}
