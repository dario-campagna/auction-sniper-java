package it.esteco.auction.sniper;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static java.lang.String.format;

public class Main implements SniperListener {

    public static final String AUCTION_RESOURCE = "Auction";
    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
    public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";

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
        disconnectWhenUICloses(connection);
        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        Chat chat = chatManager.createChat(
                auctionJID(itemId, connection),
                null);
        Auction auction = new Auction() {
            @Override
            public void bid(int amount) {
                try {
                    chat.sendMessage(format(BID_COMMAND_FORMAT, amount));
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }
        };
        chat.addMessageListener(new AuctionMessageTranslator(new AuctionSniper(auction, this)));
        this.notToBeGCd = chat;
        chat.sendMessage(JOIN_COMMAND_FORMAT);
    }

    private static XMPPTCPConnection connection(String hostname, String serviceName, String username, String password) throws IOException, XMPPException, SmackException {
        XMPPTCPConnectionConfiguration conf = XMPPTCPConnectionConfiguration.builder()
                .setHost(hostname)
                .setServiceName(serviceName)
                .setUsernameAndPassword(username, password)
                .setResource(AUCTION_RESOURCE)
                .build();
        XMPPTCPConnection connection = new XMPPTCPConnection(conf);
        connection.connect();
        connection.login();
        return connection;
    }

    private static String auctionJID(String itemId, XMPPTCPConnection connection) {
        return format(AUCTION_JID_FORMAT, itemId, connection.getServiceName());
    }

    private void disconnectWhenUICloses(XMPPTCPConnection connection) {
        ui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                connection.disconnect();
            }
        });
    }

    @Override
    public void sniperLost() {
        SwingUtilities.invokeLater(() -> ui.showStatus(MainWindow.STATUS_LOST));
    }

    @Override
    public void sniperBidding() {
        SwingUtilities.invokeLater(() -> ui.showStatus(MainWindow.STATUS_BIDDING));
    }
}
