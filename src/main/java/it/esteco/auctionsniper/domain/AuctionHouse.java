package it.esteco.auctionsniper.domain;

public interface AuctionHouse {

    Auction auctionFor(Item item);

    void disconnect();
}
