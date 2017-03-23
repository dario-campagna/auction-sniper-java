package test.auctionsniper.e2e.runner;

import it.esteco.auction.sniper.SniperState;
import it.esteco.auction.sniper.mainwindow.Main;
import it.esteco.auction.sniper.mainwindow.MainWindow;
import it.esteco.auction.sniper.mainwindow.SnipersTableModel;
import test.auctionsniper.e2e.fakeserver.FakeAuctionServer;

public class ApplicationRunner {

    public static final String STATUS_JOINING = "Joining";
    public static final String STATUS_BIDDING = "Bidding";
    public static final String STATUS_LOST = "Lost";
    public static final String STATUS_WINNING = "Winning";
    public static final String STATUS_WON = "Won";

    private static final String SNIPER_ID = "sniper";
    public static final String SNIPER_XMPP_ID = SNIPER_ID + "@" + FakeAuctionServer.XMPP_SERVICE_NAME + "/" + Main.AUCTION_RESOURCE;
    private static final String SNIPER_PASSWORD = "sniper";
    private AuctionSniperDriver driver;
    private String itemId;

    public void startBiddingIn(final FakeAuctionServer auction) {
        itemId = auction.getItemId();
        Thread thread = new Thread("Test Application") {
            @Override
            public void run() {
                try {
                    Main.main(FakeAuctionServer.XMPP_HOSTNAME, FakeAuctionServer.XMPP_SERVICE_NAME, SNIPER_ID, SNIPER_PASSWORD, auction.getItemId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
        driver = new AuctionSniperDriver(1000);
        driver.hasTitle(MainWindow.APPLICATION_TITLE);
        driver.hasColumnTitles();
        driver.showsSniperStatus(SnipersTableModel.textFor(SniperState.JOINING));
    }

    public void hasShownSniperIsBidding() {
        driver.showsSniperStatus(SnipersTableModel.textFor(SniperState.BIDDING));
    }

    public void hasShownSniperIsBidding(int lastPrice, int lastBid) {
        driver.showsSniperStatus(itemId, lastPrice, lastBid, SnipersTableModel.textFor(SniperState.BIDDING));
    }

    public void showsSniperHasLostAuction() {
        driver.showsSniperStatus(SnipersTableModel.textFor(SniperState.LOST));
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }

    public void hasShownSniperIsWinning() {
        driver.showsSniperStatus(SnipersTableModel.textFor(SniperState.WINNING));
    }

    public void hasShownSniperIsWinning(int winningBid) {
        driver.showsSniperStatus(itemId, winningBid, winningBid, SnipersTableModel.textFor(SniperState.WINNING));
    }

    public void showsSniperHasWonAuction() {
        driver.showsSniperStatus(SnipersTableModel.textFor(SniperState.WON));
    }

    public void showsSniperHasWonAuction(int lastPrice) {
        driver.showsSniperStatus(itemId, lastPrice, lastPrice, SnipersTableModel.textFor(SniperState.WON));
    }
}
