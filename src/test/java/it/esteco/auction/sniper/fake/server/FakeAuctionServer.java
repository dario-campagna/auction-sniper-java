package it.esteco.auction.sniper.fake.server;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;

public class FakeAuctionServer {

    public static final String XMPP_HOSTNAME = "localhost";
    public static final String AUCTION_RESOURCE = "Auction";
    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    private static final String AUCTION_PASSWORD = "auction";

    private final String itemId;
    private final XMPPTCPConnection connection;
    private final SingleMessageListener messageListener = new SingleMessageListener();
    private Chat currentChat;

    public FakeAuctionServer(String itemId) {
        this.itemId = itemId;
        XMPPTCPConnectionConfiguration conf = XMPPTCPConnectionConfiguration.builder()
                .setHost(XMPP_HOSTNAME)
                .setServiceName("broker")
                .build();
        this.connection = new XMPPTCPConnection(conf);
    }

    public void startSellingItem() throws IOException, XMPPException, SmackException {
        connection.connect();
        connection.login(String.format(ITEM_ID_AS_LOGIN, itemId), AUCTION_PASSWORD, AUCTION_RESOURCE);
        ChatManager.getInstanceFor(connection).addChatListener((chat, b) -> {
            currentChat = chat;
            chat.addMessageListener(messageListener);
        });
    }

    public void hasReceivedJoinRequestFromSniper() throws InterruptedException {
        messageListener.receivesAMessage();
    }

    public void announceClosed() throws SmackException.NotConnectedException {
        currentChat.sendMessage(new Message());
    }

    public void stop() {
        connection.disconnect();
    }

    public String getItemId() {
        return itemId;
    }
}
