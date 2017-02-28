package it.esteco.auction.sniper;

public class AuctionSniper implements AuctionEventListener {
    private Auction auction;
    private SniperListener listener;

    public AuctionSniper(Auction auction, SniperListener listener) {
        this.auction = auction;
        this.listener = listener;
    }

    @Override
    public void auctionClosed() {
        listener.sniperLost();
    }

    @Override
    public void currentPrice(int price, int increment) {
        auction.bid(price + increment);
        listener.sniperBidding();
    }
}
