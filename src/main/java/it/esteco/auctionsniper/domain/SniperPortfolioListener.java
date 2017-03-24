package it.esteco.auctionsniper.domain;

import java.util.EventListener;

public interface SniperPortfolioListener extends EventListener {

    void sniperAdded(AuctionSniper sniper);

}
