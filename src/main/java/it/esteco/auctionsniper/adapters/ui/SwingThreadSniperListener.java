package it.esteco.auctionsniper.adapters.ui;

import it.esteco.auctionsniper.domain.SniperListener;
import it.esteco.auctionsniper.domain.SniperSnapshot;

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
