package it.esteco.auction.sniper;

public class AuctionSniper implements AuctionEventListener {
    private SniperListener listener;

    public AuctionSniper(SniperListener listener) {
        this.listener = listener;
    }

    @Override
    public void auctionClosed() {
        listener.sniperLost();
    }

    @Override
    public void currentPrice(int price, int increment) {

    }
}
