package test.auctionsniper;

import it.esteco.auctionsniper.domain.*;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.States;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static it.esteco.auctionsniper.domain.AuctionEventListener.PriceSource;
import static org.hamcrest.core.IsEqual.equalTo;

public class AuctionSniperTest {

    private final String ITEM_ID = "item_id";
    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();
    private final Auction auction = context.mock(Auction.class);
    private final SniperListener sniperListener = context.mock(SniperListener.class);
    private final States sniperState = context.states("sniper");
    private final AuctionSniper sniper = new AuctionSniper(new Item(ITEM_ID, 1234), auction);

    @Test
    public void reportsLostIfAuctionClosesImmediately() throws Exception {
        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.LOST)));
        }});

        sniper.auctionClosed();
    }

    @Test
    public void reportsLostIfAuctionClosesWhenBidding() throws Exception {
        final int price = 123;
        final int increment = 45;
        final int bid = price + increment;

        allowingSniperBidding();
        context.checking(new Expectations() {{
            ignoring(auction);
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, price, bid, SniperState.LOST));
            when(sniperState.is("bidding"));
        }});

        sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
        sniper.auctionClosed();
    }

    @Test
    public void bidsHigherAndReportsBiddingWhenNewPriceArrives() throws Exception {
        final int price = 1001;
        final int increment = 25;
        final int bid = price + increment;

        context.checking(new Expectations() {{
            oneOf(auction).bid(price + increment);
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, price, bid, SniperState.BIDDING));
        }});

        sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
    }

    @Test
    public void reportsWinningWhenCurrentPriceComesFromSniper() throws Exception {
        ignoringAuction();
        allowingSniperBidding();
        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 135, 135, SniperState.WINNING));
            when(sniperState.is("bidding"));
        }});

        sniper.currentPrice(123, 12, PriceSource.FromOtherBidder);
        sniper.currentPrice(135, 45, PriceSource.FromSniper);
    }

    @Test
    public void reportsWinningIfCurrentPriceComesFromSniperWhenBidding() throws Exception {
        ignoringAuction();
        allowingSniperBidding();
        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 168, 168, SniperState.WINNING));
            when(sniperState.is("bidding"));
        }});

        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        sniper.currentPrice(168, 45, PriceSource.FromSniper);
    }

    @Test
    public void reportsBiddingIfCurrentPriceComesFromOtherBidderWhenWinning() throws Exception {
        ignoringAuction();
        context.checking(new Expectations() {{
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.WINNING)));
            then(sniperState.is("winning"));
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 168, 213, SniperState.BIDDING));
            when(sniperState.is("winning"));
        }});

        sniper.currentPrice(123, 45, PriceSource.FromSniper);
        sniper.currentPrice(168, 45, PriceSource.FromOtherBidder);
    }

    @Test
    public void reportsWonIfAuctionClosesWhenWinning() throws Exception {
        ignoringAuction();
        allowingSniperBidding();
        context.checking(new Expectations() {{
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.WINNING)));
            when(sniperState.is("bidding"));
            then(sniperState.is("winning"));
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 123, 123, SniperState.WON));
            when(sniperState.is("winning"));
        }});

        sniper.currentPrice(78, 45, PriceSource.FromOtherBidder);
        sniper.currentPrice(123, 45, PriceSource.FromSniper);
        sniper.auctionClosed();
    }

    @Test
    public void doesNotBidAndReportsLosingIfSubsequentPriceIsAboveStopPrice() throws Exception {
        allowingSniperBidding();
        context.checking(new Expectations(){{
            int bid = 123 + 45;
            allowing(auction).bid(bid);
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 2345, bid, SniperState.LOSING));
            when(sniperState.is("bidding"));
        }});

        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        sniper.currentPrice(2345, 25, PriceSource.FromOtherBidder);
    }

    @Test
    public void doesNotBidAndReportsLosingIfFirstPriceIsAboveStopPrice() throws Exception {
        context.checking(new Expectations(){{
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 1300, 0, SniperState.LOSING));
        }});

        sniper.currentPrice(1300, 100, PriceSource.FromOtherBidder);
    }

    @Test
    public void reportsLostIfAuctionClosesWhenLosing() throws Exception {
        context.checking(new Expectations(){{
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.LOSING)));
            then(sniperState.is("losing"));
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 1300, 0, SniperState.LOST));
            when(sniperState.is("losing"));
        }});

        sniper.currentPrice(1300, 100, PriceSource.FromOtherBidder);
        sniper.auctionClosed();
    }

    @Test
    public void continuesToBeLosingOnceStopPriceHasBeenReached() throws Exception {
        allowingSniperBidding();
        context.checking(new Expectations(){{
            int bid = 900 + 100;
            allowing(auction).bid(bid);
            oneOf(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 1300, 1000, SniperState.LOSING));
            when(sniperState.is("bidding"));
            then(sniperState.is("losing"));
            oneOf(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 1400, 1000, SniperState.LOSING));
            when(sniperState.is("losing"));
        }});

        sniper.currentPrice(900, 100, PriceSource.FromOtherBidder);
        sniper.currentPrice(1300, 100, PriceSource.FromOtherBidder);
        sniper.currentPrice(1400, 90, PriceSource.FromOtherBidder);
    }

    @Test
    public void doesNotBidAndReportsLosingIfPriceAfterWinningIsAboveStopPrice() throws Exception {
        allowingSniperBidding();
        context.checking(new Expectations(){{
            allowing(auction).bid(1000);
            allowing(sniperListener).sniperStateChanged(with(new SniperSnapshot(ITEM_ID, 1000, 1000, SniperState.WINNING)));
            when(sniperState.is("bidding"));
            then(sniperState.is("winning"));
            oneOf(sniperListener).sniperStateChanged(with(new SniperSnapshot(ITEM_ID, 1200, 1000, SniperState.LOSING)));
            when(sniperState.is("winning"));
        }});
        sniper.currentPrice(900, 100, PriceSource.FromOtherBidder);
        sniper.currentPrice(1000, 90, PriceSource.FromSniper);
        sniper.currentPrice(1200, 90, PriceSource.FromOtherBidder);
    }

    @Test
    public void reportsFailedIfAuctionFailsWhenBidding() throws Exception {
        ignoringAuction();
        allowingSniperBidding();

        expectsSniperToFailWhenItIs("bidding");

        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        sniper.auctionFailed();
    }

    @Before
    public void attachListener() throws Exception {
        sniper.addSniperListener(sniperListener);
    }

    private void ignoringAuction() {
        context.checking(new Expectations(){{
            ignoring(auction);
        }});
    }

    private void allowingSniperBidding() {
        context.checking(new Expectations(){{
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.BIDDING)));
            then(sniperState.is("bidding"));
        }});
    }

    private void expectsSniperToFailWhenItIs(final String state) {
        context.checking(new Expectations(){{
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 0, 0, SniperState.FAILED));
            when(sniperState.is(state));
        }});
    }

    private Matcher<SniperSnapshot> aSniperThatIs(SniperState state) {
        return new FeatureMatcher<SniperSnapshot, SniperState>(equalTo(state), "sniper that is ", "was") {
            @Override
            protected SniperState featureValueOf(SniperSnapshot actual) {
                return actual.state;
            }
        };
    }
}
