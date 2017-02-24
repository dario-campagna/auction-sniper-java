package it.esteco.auction.sniper;

import it.esteco.auction.sniper.fake.server.FakeAuctionServer;
import it.esteco.auction.sniper.runner.ApplicationRunner;
import org.junit.After;
import org.junit.Test;

public class AuctionSniperEndToEndTest {

    private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
    private final ApplicationRunner application = new ApplicationRunner();

    @Test
    public void sniperJoinAuctionUntilAuctionCloses() throws Exception {
        auction.startSellingItem();
        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFromSniper();
        auction.announceClosed();
        application.showsSniperHasLostAuction();
    }

    @After
    public void stopAuction() throws Exception {
        auction.stop();
    }

    @After
    public void stopApplication() {
        application.stop();
    }
}
