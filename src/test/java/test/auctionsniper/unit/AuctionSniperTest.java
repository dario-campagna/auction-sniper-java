package test.auctionsniper.unit;

import it.esteco.auction.sniper.Auction;
import it.esteco.auction.sniper.AuctionSniper;
import it.esteco.auction.sniper.SniperListener;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

public class AuctionSniperTest {

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    private final Auction auction = context.mock(Auction.class);
    private final SniperListener sniperListener = context.mock(SniperListener.class);
    private final AuctionSniper sniper = new AuctionSniper(auction, sniperListener);

    @Test
    public void reportLostWhenAuctionCloses() throws Exception {
        context.checking(new Expectations(){{
            atLeast(1).of(sniperListener).sniperLost();
        }});

        sniper.auctionClosed();
    }

    @Test
    public void bidsHigherAndReportsBiddingWhenNewPriceArrives() throws Exception {
        final int price = 1001;
        final int increment = 25;
        context.checking(new Expectations(){{
            oneOf(auction).bid(price+increment);
            atLeast(1).of(sniperListener).sniperBidding();
        }});

        sniper.currentPrice(price, increment);
    }
}
