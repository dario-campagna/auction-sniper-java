package it.esteco.auctionsniper.domain;

import it.esteco.auctionsniper.Announcer;

import java.util.ArrayList;
import java.util.Collection;

public class SniperPortfolio implements SniperCollector {

    private Collection<AuctionSniper> snipers = new ArrayList<>();
    private Announcer<SniperPortfolioListener> listeners = Announcer.to(SniperPortfolioListener.class);

    @Override
    public void addSniper(AuctionSniper sniper) {
        snipers.add(sniper);
        listeners.announce().sniperAdded(sniper);
    }

    public void addPortfolioListener(SniperPortfolioListener listener) {
        listeners.addListener(listener);
    }
}
