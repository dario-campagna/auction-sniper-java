package it.esteco.auctionsniper.ui;

import it.esteco.auctionsniper.AuctionSniper;
import it.esteco.auctionsniper.PortfolioListener;
import it.esteco.auctionsniper.SniperCollector;

import java.util.ArrayList;
import java.util.Collection;

public class SniperPortfolio implements SniperCollector {

    private Collection<AuctionSniper> snipers = new ArrayList<>();
    private PortfolioListener listener;

    @Override
    public void addSniper(AuctionSniper sniper) {
        snipers.add(sniper);
        listener.sniperAdded(sniper);
    }

    public void addPortfolioListener(PortfolioListener listener) {
        this.listener = listener;
    }
}
