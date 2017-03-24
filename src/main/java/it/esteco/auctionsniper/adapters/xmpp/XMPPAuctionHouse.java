package it.esteco.auctionsniper.adapters.xmpp;

import it.esteco.auctionsniper.domain.Auction;
import it.esteco.auctionsniper.domain.AuctionHouse;
import it.esteco.auctionsniper.domain.Item;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

public class XMPPAuctionHouse implements AuctionHouse {

    public static final String AUCTION_RESOURCE = "Auction";

    private XMPPTCPConnection connection;

    public static XMPPAuctionHouse connect(String hostname, String serviceName, String username, String password) throws Exception {
        XMPPTCPConnectionConfiguration conf = XMPPTCPConnectionConfiguration.builder()
                .setHost(hostname)
                .setServiceName(serviceName)
                .setUsernameAndPassword(username, password)
                .setResource(AUCTION_RESOURCE)
                .build();
        XMPPTCPConnection connection = new XMPPTCPConnection(conf);
        connection.connect();
        connection.login();
        return new XMPPAuctionHouse(connection);
    }

    private XMPPAuctionHouse(XMPPTCPConnection connection) {
        this.connection = connection;
    }

    @Override
    public void disconnect() {
        connection.disconnect();
    }

    @Override
    public Auction auctionFor(Item item) {
        return new XMPPAuction(connection, item.identifier);
    }
}
