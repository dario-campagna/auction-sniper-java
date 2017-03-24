package it.esteco.auctionsniper.ui;

import it.esteco.auctionsniper.Auction;
import it.esteco.auctionsniper.AuctionHouse;
import it.esteco.auctionsniper.AuctionSniper;
import it.esteco.auctionsniper.SniperSnapshot;
import it.esteco.auctionsniper.xmpp.XMPPAuctionHouse;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

public class Main {

    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_SERVICE_NAME = 1;
    private static final int ARG_USERNAME = 2;
    private static final int ARG_PASSWORD = 3;

    private final SnipersTableModel snipers = new SnipersTableModel();
    private MainWindow ui;
    @SuppressWarnings("unused")
    private Collection<Auction> notToBeGCd = new ArrayList<Auction>();

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
        SwingUtilities.invokeAndWait(() -> ui = new MainWindow(snipers));
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
        ui.addUserRequestListener(itemId -> {
            snipers.addSniper(SniperSnapshot.joining(itemId));
            Auction auction = auctionHouse.auctionFor(itemId);
            notToBeGCd.add(auction);
            auction.addAuctionEventListener(new AuctionSniper(itemId, auction, new SwingThreadSniperListener(snipers)));
            auction.join();
        });
    }

}
