package it.esteco.auction.sniper;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Main {

    public static final String AUCTION_RESOURCE = "Auction";
    public static final String ITEM_ID_AS_LOGIN = "auction-%s";

    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_SERVICE_NAME = 1;
    private static final int ARG_USERNAME = 2;
    private static final int ARG_PASSWORD = 3;
    private static final int ARG_ITEM_ID = 4;
    private static final String AUCTION_JID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

    private MainWindow ui;
    @SuppressWarnings("unused")
    private Chat notToBeGCd;

    public Main() throws Exception {
        startUserInterface();
    }

    public static void main(String... args) throws Exception {
        Main main = new Main();
        main.joinAuction(
                connection(args[ARG_HOSTNAME], args[ARG_SERVICE_NAME], args[ARG_USERNAME], args[ARG_PASSWORD]),
                args[ARG_ITEM_ID]);
    }

    private void startUserInterface() throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait(() -> ui = new MainWindow());
    }

    private void joinAuction(XMPPTCPConnection connection, String itemId) throws SmackException.NotConnectedException {
        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        Chat chat = chatManager.createChat(
                auctionJID(itemId, connection),
                (chat1, message) -> SwingUtilities.invokeLater(() -> ui.showStatus(MainWindow.STATUS_LOST)));
        this.notToBeGCd = chat;
        chat.sendMessage(new Message());
    }

    private static XMPPTCPConnection connection(String hostname, String serviceName, String username, String password) throws IOException, XMPPException, SmackException {
        XMPPTCPConnectionConfiguration conf = XMPPTCPConnectionConfiguration.builder()
                .setHost(hostname)
                .setServiceName(serviceName)
                .setUsernameAndPassword(username, password)
                .build();
        XMPPTCPConnection connection = new XMPPTCPConnection(conf);
        connection.connect();
        connection.login();
        return connection;
    }

    private static String auctionJID(String itemId, XMPPTCPConnection connection) {
        return String.format(AUCTION_JID_FORMAT, itemId, connection.getServiceName());
    }
}