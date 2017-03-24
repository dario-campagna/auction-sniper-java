package it.esteco.auctionsniper.adapters.ui;

import it.esteco.auctionsniper.Announcer;
import it.esteco.auctionsniper.domain.Item;
import it.esteco.auctionsniper.domain.SniperPortfolio;
import it.esteco.auctionsniper.domain.UserRequestListener;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

public class MainWindow extends JFrame {

    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    public static final String APPLICATION_TITLE = "Auction Sniper";
    private static final String SNIPERS_TABLE_NAME = "Snipers Table";
    public static final String NEW_ITEM_ID_NAME = "item id";
    public static final String NEW_ITEM_STOP_PRICE_NAME = "stop price";
    public static final String JOIN_BUTTON_NAME = "join";

    private final Announcer<UserRequestListener> userRequests = Announcer.to(UserRequestListener.class);
    private JTextField itemIdField;
    private JFormattedTextField stopPriceField;

    public MainWindow(SniperPortfolio portfolio) {
        super(APPLICATION_TITLE);
        setName(MAIN_WINDOW_NAME);
        fillContentPane(makeSnipersTable(portfolio), makeControls());
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void fillContentPane(JTable snipersTable, JPanel controls) {
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(controls, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
    }

    private JPanel makeControls() {
        JPanel controls = new JPanel(new FlowLayout());

        itemIdField = new JTextField();
        itemIdField.setColumns(25);
        itemIdField.setName(MainWindow.NEW_ITEM_ID_NAME);
        controls.add(new JLabel("Item:"));
        controls.add(itemIdField);

        stopPriceField = new JFormattedTextField(numberFormatter());
        stopPriceField.setColumns(25);
        stopPriceField.setName(MainWindow.NEW_ITEM_STOP_PRICE_NAME);
        controls.add(new JLabel("Stop price:"));
        controls.add(stopPriceField);

        JButton joinAuctionButton = new JButton("Join Auction");
        joinAuctionButton.setName(MainWindow.JOIN_BUTTON_NAME);
        joinAuctionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userRequests.announce().joinAuction(new Item(itemId(), stopPrice()));
            }
        });
        controls.add(joinAuctionButton);

        return controls;
    }

    private String itemId() {
        return itemIdField.getText();
    }

    private int stopPrice() {
        return ((Number) stopPriceField.getValue()).intValue();
    }

    private NumberFormatter numberFormatter() {
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        return formatter;
    }

    private JTable makeSnipersTable(SniperPortfolio portfolio) {
        SnipersTableModel model = new SnipersTableModel();
        portfolio.addPortfolioListener(model);
        final JTable snipersTable = new JTable(model);
        snipersTable.setName(SNIPERS_TABLE_NAME);
        return snipersTable;
    }

    public void addUserRequestListener(UserRequestListener userRequestListener) {
        userRequests.addListener(userRequestListener);
    }
}
