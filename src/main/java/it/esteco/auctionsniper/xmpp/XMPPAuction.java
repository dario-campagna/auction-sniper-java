package it.esteco.auctionsniper.xmpp;

import it.esteco.auctionsniper.Auction;
import it.esteco.auctionsniper.AuctionEventListener;
import it.esteco.auctionsniper.ui.Announcer;
import it.esteco.auctionsniper.ui.Main;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;

import static java.lang.String.format;

public class XMPPAuction implements Auction {

    private static final String AUCTION_JID_FORMAT = Main.ITEM_ID_AS_LOGIN + "@%s/" + Main.AUCTION_RESOURCE;

    private Announcer<AuctionEventListener> auctionEventListeners = Announcer.to(AuctionEventListener.class);
    private final Chat chat;

    public XMPPAuction(XMPPConnection connection, String itemId) {
        chat = ChatManager.getInstanceFor(connection).createChat(
                auctionJID(itemId, connection),
                new AuctionMessageTranslator(connection.getUser(), auctionEventListeners.announce()));
    }

    @Override
    public void addAuctionEventListener(AuctionEventListener listener) {
        auctionEventListeners.addListener(listener);
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

    private static String auctionJID(String itemId, XMPPConnection connection) {
        return format(AUCTION_JID_FORMAT, itemId, connection.getServiceName());
    }

    private void sendMessage(String message) {
        try {
            chat.sendMessage(message);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }
}
