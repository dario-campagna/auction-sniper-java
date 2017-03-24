package test.auctionsniper.xmpp;

import it.esteco.auctionsniper.Auction;
import it.esteco.auctionsniper.AuctionEventListener;
import it.esteco.auctionsniper.xmpp.XMPPAuction;
import it.esteco.auctionsniper.ui.Main;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.auctionsniper.runner.ApplicationRunner;
import test.auctionsniper.fakeserver.FakeAuctionServer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class XMPPAuctionTest {

    private final FakeAuctionServer fakeAuctionServer = new FakeAuctionServer("item-54321");
    private XMPPTCPConnection connection;

    @Test
    public void receivesEventsFromAuctionServerAfterJoining() throws Exception {
        CountDownLatch auctionWasClosed = new CountDownLatch(1);

        fakeAuctionServer.startSellingItem();

        Auction auction = new XMPPAuction(connection, fakeAuctionServer.getItemId());
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
        };
    }

    @Before
    public void setUpConnection() throws Exception {
        XMPPTCPConnectionConfiguration conf = XMPPTCPConnectionConfiguration.builder()
                .setHost(FakeAuctionServer.XMPP_HOSTNAME)
                .setServiceName(fakeAuctionServer.XMPP_SERVICE_NAME)
                .setUsernameAndPassword(ApplicationRunner.SNIPER_ID, ApplicationRunner.SNIPER_PASSWORD)
                .setResource(Main.AUCTION_RESOURCE)
                .build();
        connection = new XMPPTCPConnection(conf);
        connection.connect();
        connection.login();
    }

    @After
    public void disconnect() throws Exception {
        connection.disconnect();
    }
}
