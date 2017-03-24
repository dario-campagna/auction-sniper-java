package test.auctionsniper.xmpp;

import it.esteco.auctionsniper.adapters.xmpp.XMPPAuctionHouse;
import it.esteco.auctionsniper.domain.Auction;
import it.esteco.auctionsniper.domain.AuctionEventListener;
import it.esteco.auctionsniper.domain.Item;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.auctionsniper.fakeserver.FakeAuctionServer;
import test.auctionsniper.runner.ApplicationRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class XMPPAuctionHouseTest {

    private final FakeAuctionServer fakeAuctionServer = new FakeAuctionServer("item-54321");
    private XMPPAuctionHouse auctionHouse;

    @Test
    public void receivesEventsFromAuctionServerAfterJoining() throws Exception {
        CountDownLatch auctionWasClosed = new CountDownLatch(1);

        fakeAuctionServer.startSellingItem();

        Auction auction = auctionHouse.auctionFor(new Item(fakeAuctionServer.getItemId(), 1));
        auction.addAuctionEventListener(auctionClosedListener(auctionWasClosed));

        auction.join();
        fakeAuctionServer.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
        fakeAuctionServer.announceClosed();

        assertTrue("should have been closed", auctionWasClosed.await(2, TimeUnit.SECONDS));
    }

    private AuctionEventListener auctionClosedListener(CountDownLatch auctionWasClosed) {
        return new AuctionEventListener() {
            @Override
            public void auctionClosed() {
                auctionWasClosed.countDown();
            }

            @Override
            public void currentPrice(int price, int increment, PriceSource fromOtherBidder) {
                // not implemented
            }

            @Override
            public void auctionFailed() {
                // not implemented
            }
        };
    }

    @Before
    public void setUp() throws Exception {
        auctionHouse = XMPPAuctionHouse.connect(FakeAuctionServer.XMPP_HOSTNAME, FakeAuctionServer.XMPP_SERVICE_NAME, ApplicationRunner.SNIPER_ID, ApplicationRunner.SNIPER_PASSWORD);
    }

    @After
    public void disconnect() throws Exception {
        auctionHouse.disconnect();
    }
}
