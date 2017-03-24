package it.esteco.auctionsniper.domain;

import java.util.ArrayList;
import java.util.Collection;

public class SniperPortfolio implements SniperCollector {

    private Collection<AuctionSniper> snipers = new ArrayList<>();
    private SniperPortfolioListener listener;

    @Override
    public void addSniper(AuctionSniper sniper) {
        snipers.add(sniper);
        listener.sniperAdded(sniper);
    }

    public void addPortfolioListener(SniperPortfolioListener listener) {
        this.listener = listener;
    }
}
