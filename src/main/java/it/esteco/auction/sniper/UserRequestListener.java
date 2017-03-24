package it.esteco.auction.sniper;

import java.util.EventListener;

public interface UserRequestListener extends EventListener {

    void joinAuction(String itemId);

}
