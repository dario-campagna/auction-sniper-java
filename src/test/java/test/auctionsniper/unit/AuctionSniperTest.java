package test.auctionsniper.unit;

import it.esteco.auction.sniper.AuctionSniper;
import it.esteco.auction.sniper.SniperListener;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

public class AuctionSniperTest {

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    private final SniperListener sniperListener = context.mock(SniperListener.class);
    private final AuctionSniper sniper = new AuctionSniper(sniperListener);

    @Test
    public void reportLostWhenAuctionCloses() throws Exception {
        context.checking(new Expectations(){{
            oneOf(sniperListener).sniperLost();
        }});

        sniper.auctionClosed();
    }
}
