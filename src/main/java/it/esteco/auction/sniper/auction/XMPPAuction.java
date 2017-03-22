package it.esteco.auction.sniper.auction;

import it.esteco.auction.sniper.Auction;
import it.esteco.auction.sniper.mainwindow.Main;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;

public class XMPPAuction implements Auction {
    private final Chat chat;

    public XMPPAuction(Chat chat) {
        this.chat = chat;
    }

    @Override
    public void join() {
        sendMessage(Main.JOIN_COMMAND_FORMAT);
    }

    @Override
    public void bid(int amount) {
        String message = String.format(Main.BID_COMMAND_FORMAT, amount);
        sendMessage(message);
    }

    private void sendMessage(String message) {
        try {
            chat.sendMessage(message);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }
}
