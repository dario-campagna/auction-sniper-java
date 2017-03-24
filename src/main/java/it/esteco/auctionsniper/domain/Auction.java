package it.esteco.auctionsniper.domain;

public interface Auction {
    void join();

    void bid(int amount);

    void addAuctionEventListener(AuctionEventListener listener);
}
