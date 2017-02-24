package it.esteco.auction.sniper.runner;

import it.esteco.auction.sniper.Main;
import it.esteco.auction.sniper.fake.server.FakeAuctionServer;

import static it.esteco.auction.sniper.MainWindow.STATUS_JOINING;
import static it.esteco.auction.sniper.MainWindow.STATUS_LOST;
import static it.esteco.auction.sniper.fake.server.FakeAuctionServer.XMPP_HOSTNAME;
import static it.esteco.auction.sniper.fake.server.FakeAuctionServer.XMPP_SERVICE_NAME;

public class ApplicationRunner {

    private static final String SNIPER_ID = "sniper";
    private static final String SNIPER_PASSWORD = "sniper";
    private AuctionSniperDriver driver;

    public void startBiddingIn(final FakeAuctionServer auction) {
        Thread thread = new Thread("Test Application") {
            @Override
            public void run() {
                try {
                    Main.main(XMPP_HOSTNAME, XMPP_SERVICE_NAME, SNIPER_ID, SNIPER_PASSWORD, auction.getItemId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
        driver = new AuctionSniperDriver(1000);
        driver.showsSniperStatus(STATUS_JOINING);
    }

    public void showsSniperHasLostAuction() {
        driver.showsSniperStatus(STATUS_LOST);
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }
}
