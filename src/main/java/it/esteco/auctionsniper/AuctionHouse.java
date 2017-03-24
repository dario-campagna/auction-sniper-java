package it.esteco.auctionsniper;

public interface AuctionHouse {

    Auction auctionFor(String itemId);

    void disconnect();
}
