package test.auctionsniper.runner;

import it.esteco.auctionsniper.Main;
import it.esteco.auctionsniper.adapters.ui.MainWindow;
import it.esteco.auctionsniper.adapters.ui.SnipersTableModel;
import it.esteco.auctionsniper.adapters.xmpp.XMPPAuctionHouse;
import it.esteco.auctionsniper.domain.SniperState;
import test.auctionsniper.fakeserver.FakeAuctionServer;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.containsString;

public class ApplicationRunner {

    public static final String SNIPER_ID = "sniper";
    public static final String SNIPER_XMPP_ID = SNIPER_ID + "@" + FakeAuctionServer.XMPP_SERVICE_NAME + "/" + XMPPAuctionHouse.AUCTION_RESOURCE;
    public static final String SNIPER_PASSWORD = "sniper";

    private AuctionLogDriver logDriver = new AuctionLogDriver();
    private AuctionSniperDriver driver;

    public void startBiddingIn(final FakeAuctionServer... auctions) {
        startSniper(auctions);
        for (FakeAuctionServer auction : auctions) {
            driver.startBiddingFor(auction.getItemId(), Integer.MAX_VALUE);
            driver.showsSniperStatus(auction.getItemId(), 0, 0, SnipersTableModel.textFor(SniperState.JOINING));
        }
    }

    public void startBiddingWithStopPrice(FakeAuctionServer auction, int stopPrice) {
        startSniper(new FakeAuctionServer[]{auction});
        driver.startBiddingFor(auction.getItemId(), stopPrice);
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }

    public void hasShownSniperIsBidding(FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, SnipersTableModel.textFor(SniperState.BIDDING));
    }

    public void showsSniperHasLostAuction(FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, SnipersTableModel.textFor(SniperState.LOST));
    }

    public void hasShownSniperIsWinning(FakeAuctionServer auction, int winningBid) {
        driver.showsSniperStatus(auction.getItemId(), winningBid, winningBid, SnipersTableModel.textFor(SniperState.WINNING));
    }

    public void hasShownSniperIsLosing(FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, SnipersTableModel.textFor(SniperState.LOSING));
    }

    public void showsSniperHasWonAuction(FakeAuctionServer auction, int lastPrice) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastPrice, SnipersTableModel.textFor(SniperState.WON));
    }

    public void showsSniperHasFailed(FakeAuctionServer auction) {
        driver.showsSniperStatus(auction.getItemId(), 0, 0, SnipersTableModel.textFor(SniperState.FAILED));
    }

    public void reportsInvalidMessage(FakeAuctionServer auction, String message) throws IOException {
        logDriver.hasEntry(containsString(message));
    }

    private void startSniper(final FakeAuctionServer[] auctions) {
        logDriver.clearLog();
        Thread thread = new Thread("Test Application") {
            @Override
            public void run() {
                try {
                    Main.main(arguments(auctions));
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
    }

    private String[] arguments(FakeAuctionServer... auctions) {
        String[] arguments = new String[auctions.length + 4];
        arguments[0] = FakeAuctionServer.XMPP_HOSTNAME;
        arguments[1] = FakeAuctionServer.XMPP_SERVICE_NAME;
        arguments[2] = SNIPER_ID;
        arguments[3] = SNIPER_PASSWORD;
        for (int i = 0; i < auctions.length; i++) {
            arguments[i + 4] = auctions[i].getItemId();
        }
        return arguments;
    }
}
