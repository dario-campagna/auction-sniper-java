package it.esteco.auctionsniper.domain;

import it.esteco.auctionsniper.domain.AuctionSniper;

import java.util.EventListener;

public interface SniperPortfolioListener extends EventListener {

    void sniperAdded(AuctionSniper sniper);

}
