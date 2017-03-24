package it.esteco.auctionsniper.domain;

import java.util.EventListener;

public interface UserRequestListener extends EventListener {

    void joinAuction(Item item);

}
