package test.auctionsniper.e2e;

import org.junit.After;
import org.junit.Test;
import test.auctionsniper.e2e.fakeserver.FakeAuctionServer;
import test.auctionsniper.e2e.runner.ApplicationRunner;

public class AuctionSniperEndToEndTest {

    private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
    private final ApplicationRunner application = new ApplicationRunner();

    @Test
    public void sniperJoinAuctionUntilAuctionCloses() throws Exception {
        auction.startSellingItem();
        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
        auction.announceClosed();
        application.showsSniperHasLostAuction();
    }

    @Test
    public void sniperMakesAHigherBidsButLoses() throws Exception {
        auction.startSellingItem();

        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

        auction.reportPrice(1000, 98, "other bidder");
        application.hasShownSniperIsBidding(auction, 1000, 1098);

        auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

        auction.announceClosed();
        application.showsSniperHasLostAuction();
    }

    @Test
    public void sniperWinsAnAuctionByBiddingHigher() throws Exception {
        auction.startSellingItem();

        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

        auction.reportPrice(1000, 98, "other bidder");
        application.hasShownSniperIsBidding(auction, 1000, 1098); //last price, last bid

        auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

        auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
        application.hasShownSniperIsWinning(1098); // winning bid

        auction.announceClosed();
        application.showsSniperHasWonAuction(1098); // last price
    }

    @Test
    public void sniperWinsAnAuctionAfterTwoBids() throws Exception {
        auction.startSellingItem();

        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

        auction.reportPrice(1100, 98, "other bidder");
        application.hasShownSniperIsBidding(auction, 1100, 1198);

        auction.hasReceivedBid(1198, ApplicationRunner.SNIPER_XMPP_ID);

        auction.reportPrice(1198, 100, ApplicationRunner.SNIPER_XMPP_ID);
        application.hasShownSniperIsWinning(1198);

        auction.reportPrice(1298, 102, "other bidder");
        application.hasShownSniperIsBidding(auction, 1298, 1400);

        auction.hasReceivedBid(1400, ApplicationRunner.SNIPER_XMPP_ID);

        auction.reportPrice(1400, 100, ApplicationRunner.SNIPER_XMPP_ID);
        application.hasShownSniperIsWinning(1400);

        auction.announceClosed();
        application.showsSniperHasWonAuction(1400);
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
