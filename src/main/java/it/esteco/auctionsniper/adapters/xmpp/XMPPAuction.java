package it.esteco.auctionsniper.adapters.xmpp;

import it.esteco.auctionsniper.domain.Auction;
import it.esteco.auctionsniper.domain.AuctionEventListener;
import it.esteco.auctionsniper.Announcer;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;

import static java.lang.String.format;

public class XMPPAuction implements Auction {

    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
    public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";
    private static final String AUCTION_JID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + XMPPAuctionHouse.AUCTION_RESOURCE;

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
        sendMessage(JOIN_COMMAND_FORMAT);
    }

    @Override
    public void bid(int amount) {
        String message = String.format(BID_COMMAND_FORMAT, amount);
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
