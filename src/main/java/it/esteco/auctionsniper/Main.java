package it.esteco.auctionsniper;

import it.esteco.auctionsniper.domain.SniperLauncher;
import it.esteco.auctionsniper.domain.SniperPortfolio;
import it.esteco.auctionsniper.domain.AuctionHouse;
import it.esteco.auctionsniper.adapters.ui.MainWindow;
import it.esteco.auctionsniper.adapters.xmpp.XMPPAuctionHouse;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;

public class Main {

    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_SERVICE_NAME = 1;
    private static final int ARG_USERNAME = 2;
    private static final int ARG_PASSWORD = 3;

    private MainWindow ui;
    private final SniperPortfolio portfolio = new SniperPortfolio();

    public Main() throws Exception {
        startUserInterface();
    }

    public static void main(String... args) throws Exception {
        Main main = new Main();
        XMPPAuctionHouse auctionHouse = XMPPAuctionHouse.connect(args[ARG_HOSTNAME], args[ARG_SERVICE_NAME], args[ARG_USERNAME], args[ARG_PASSWORD]);
        main.disconnectWhenUICloses(auctionHouse);
        main.addUserRequestListenerFor(auctionHouse);
    }

    private void startUserInterface() throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait(() -> ui = new MainWindow(portfolio));
    }

    private void disconnectWhenUICloses(AuctionHouse auctionHouse) {
        ui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                auctionHouse.disconnect();
            }
        });
    }

    private void addUserRequestListenerFor(AuctionHouse auctionHouse) {
        ui.addUserRequestListener(new SniperLauncher(auctionHouse, portfolio));
    }

}
