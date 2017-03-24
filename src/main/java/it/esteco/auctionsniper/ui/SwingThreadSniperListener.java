package it.esteco.auctionsniper.ui;

import it.esteco.auctionsniper.SniperListener;
import it.esteco.auctionsniper.SniperSnapshot;

import javax.swing.*;

public class SwingThreadSniperListener implements SniperListener {

    private SniperListener sniperListener;

    public SwingThreadSniperListener(SniperListener sniperListener) {
        this.sniperListener = sniperListener;
    }

    @Override
    public void sniperStateChanged(SniperSnapshot sniperSnapshot) {
        SwingUtilities.invokeLater(() -> sniperListener.sniperStateChanged(sniperSnapshot));
    }

}
