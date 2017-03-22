package it.esteco.auction.sniper;

public class AuctionSniper implements AuctionEventListener {

    private Auction auction;
    private SniperListener listener;
    private boolean isWinning = false;

    public AuctionSniper(Auction auction, SniperListener listener) {
        this.auction = auction;
        this.listener = listener;
    }

    @Override
    public void auctionClosed() {
        if (isWinning) {
            listener.sniperWon();
        } else {
            listener.sniperLost();
        }
    }

    @Override
    public void currentPrice(int price, int increment, PriceSource priceSource) {
        isWinning = priceSource == PriceSource.FromSniper;
        if (isWinning) {
            listener.sniperWinning();
        } else {
            auction.bid(price + increment);
            listener.sniperBidding();
        }
    }
}
