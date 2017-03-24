package it.esteco.auctionsniper.adapters.xmpp;

import it.esteco.auctionsniper.domain.Auction;
import it.esteco.auctionsniper.domain.AuctionHouse;
import it.esteco.auctionsniper.domain.Item;
import org.apache.commons.io.FilenameUtils;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class XMPPAuctionHouse implements AuctionHouse {

    public static final String AUCTION_RESOURCE = "Auction";
    public static final String LOG_FILE_NAME = "auction-sniper-log";
    private static final String LOGGER_NAME = "XMPP_FAILURE_REPORTER";

    private XMPPTCPConnection connection;
    private final LoggingXMPPFailureReporter failureReporter;

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

    @Override
    public void disconnect() {
        connection.disconnect();
    }

    @Override
    public Auction auctionFor(Item item) {
        return new XMPPAuction(connection, item.identifier, failureReporter);
    }

    private XMPPAuctionHouse(XMPPTCPConnection connection) throws XMPPAuctionException {
        this.connection = connection;
        this.failureReporter = new LoggingXMPPFailureReporter(makeLogger());
    }

    private Logger makeLogger() throws XMPPAuctionException {
        Logger logger = Logger.getLogger(LOGGER_NAME);
        logger.setUseParentHandlers(false);
        logger.addHandler(simpleFileHandler());
        return logger;
    }

    private Handler simpleFileHandler() throws XMPPAuctionException {
        try {
            FileHandler handler = new FileHandler(LOG_FILE_NAME);
            handler.setFormatter(new SimpleFormatter());
            return handler;
        } catch (IOException e) {
            throw new XMPPAuctionException("Could not create logger FileHandler " + FilenameUtils.getFullPath(LOG_FILE_NAME), e);
        }
    }
}
