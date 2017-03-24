package test.auctionsniper.ui;

import it.esteco.auctionsniper.domain.*;
import org.hamcrest.CustomMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.States;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

public class SniperLauncherTest {

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();


    private final States auctionState = context.states("auction state").startsAs("not joined");
    private final AuctionHouse auctionHouse = context.mock(AuctionHouse.class);
    private final SniperCollector sniperCollector = context.mock(SniperCollector.class);
    private final Auction auction = context.mock(Auction.class);
    private final SniperLauncher launcher = new SniperLauncher(auctionHouse, sniperCollector);

    @Test
    public void addsNewSniperToCollectorAndThenJoinsAuction() throws Exception {
        final Item item = new Item("item 123", 1);
        context.checking(new Expectations(){{
            allowing(auctionHouse).auctionFor(item);
            will(returnValue(auction));
            oneOf(auction).addAuctionEventListener(with(sniperForItem(item)));
            when(auctionState.is("not joined"));
            oneOf(sniperCollector).addSniper(with(sniperForItem(item)));
            when(auctionState.is("not joined"));
            oneOf(auction).join();
            then(auctionState.is("joined"));
        }});

        launcher.joinAuction(item);
    }

    private Matcher<AuctionSniper> sniperForItem(Item item) {
        return new CustomMatcher<AuctionSniper>("Sniper for item") {
            @Override
            public boolean matches(Object o) {
                if (o instanceof AuctionSniper) {
                    AuctionSniper actual = (AuctionSniper) o;
                    return actual.getSnapshot().isForSameIteamAs(SniperSnapshot.joining(item.identifier));
                }
                return false;
            }
        };
    }
}