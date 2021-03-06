package test.auctionsniper.fakeserver;

import it.esteco.auctionsniper.adapters.xmpp.XMPPAuction;
import org.hamcrest.Matcher;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;

import static it.esteco.auctionsniper.adapters.xmpp.XMPPAuction.ITEM_ID_AS_LOGIN;
import static it.esteco.auctionsniper.adapters.xmpp.XMPPAuctionHouse.AUCTION_RESOURCE;
import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class FakeAuctionServer {

    public static final String XMPP_HOSTNAME = "localhost";
    public static final String XMPP_SERVICE_NAME = "broker";
    private static final String AUCTION_PASSWORD = "auction";

    private final String itemId;
    private final XMPPTCPConnection connection;
    private final SingleMessageListener messageListener = new SingleMessageListener();
    private Chat currentChat;

    public FakeAuctionServer(String itemId) {
        this.itemId = itemId;
        XMPPTCPConnectionConfiguration conf = XMPPTCPConnectionConfiguration.builder()
                .setHost(XMPP_HOSTNAME)
                .setServiceName(XMPP_SERVICE_NAME)
                .build();
        this.connection = new XMPPTCPConnection(conf);
    }

    public void startSellingItem() throws IOException, XMPPException, SmackException {
        connection.connect();
        connection.login(format(ITEM_ID_AS_LOGIN, itemId), AUCTION_PASSWORD, AUCTION_RESOURCE);
        ChatManager.getInstanceFor(connection).addChatListener((chat, b) -> {
            currentChat = chat;
            chat.addMessageListener(messageListener);
        });
    }

    public void reportPrice(int price, int increment, String bidder) {
        try {
            currentChat.sendMessage(format("SOLVersion: 1.1; Event: PRICE; CurrentPrice: %d; Increment: %d; Bidder: %s;", price, increment, bidder));
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    public void announceClosed() throws SmackException.NotConnectedException {
        currentChat.sendMessage("SOLVersion: 1.1; Event: CLOSE;");
    }

    public void sendInvalidMessageContaining(String brokenMessage) {
        try {
            currentChat.sendMessage(brokenMessage);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    public void hasReceivedJoinRequestFrom(String sniperId) throws InterruptedException {
        receivesAMessageMatching(sniperId, equalTo(XMPPAuction.JOIN_COMMAND_FORMAT));
    }

    public void hasReceivedBid(int bid, String sniperId) throws InterruptedException {
        receivesAMessageMatching(sniperId, equalTo(format(XMPPAuction.BID_COMMAND_FORMAT, bid)));
    }

    public void stop() {
        connection.disconnect();
    }

    public String getItemId() {
        return itemId;
    }

    private void receivesAMessageMatching(String sniperId, Matcher<String> messageMatcher) throws InterruptedException {
        messageListener.receivesAMessage(messageMatcher);
        assertThat(currentChat.getParticipant(), equalTo(sniperId));
    }
}
