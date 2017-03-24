package it.esteco.auctionsniper.domain;

public interface AuctionHouse {

    Auction auctionFor(String itemId);

    void disconnect();
}
