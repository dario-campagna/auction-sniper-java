package it.esteco.auctionsniper.domain;

import it.esteco.auctionsniper.domain.SniperSnapshot;

public interface SniperListener {

    void sniperStateChanged(SniperSnapshot sniperSnapshot);

}
