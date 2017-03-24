package test.auctionsniper.runner;

import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.*;
import com.objogate.wl.swing.gesture.GesturePerformer;
import it.esteco.auctionsniper.adapters.ui.MainWindow;

import javax.swing.*;
import javax.swing.table.JTableHeader;

import static com.objogate.wl.swing.matcher.IterableComponentsMatcher.matching;
import static com.objogate.wl.swing.matcher.JLabelTextMatcher.withLabelText;
import static it.esteco.auctionsniper.adapters.ui.MainWindow.MAIN_WINDOW_NAME;
import static java.lang.String.valueOf;

public class AuctionSniperDriver extends JFrameDriver {

    public AuctionSniperDriver(int timeoutMillis) {
        super(
                new GesturePerformer(),
                JFrameDriver.topLevelFrame(
                        named(MAIN_WINDOW_NAME),
                        showingOnScreen()),
                new AWTEventQueueProber(timeoutMillis, 100)
        );
    }

    public void hasColumnTitles() {
        JTableHeaderDriver headers = new JTableHeaderDriver(this, JTableHeader.class);
        headers.hasHeaders(matching(withLabelText("Item"), withLabelText("Last Price"),
                withLabelText("Last Bid"), withLabelText("State")));
    }

    public void startBiddingFor(String itemId, int stopPrice) {
        textField(MainWindow.NEW_ITEM_ID_NAME).replaceAllText(itemId);
        textField(MainWindow.NEW_ITEM_STOP_PRICE_NAME).replaceAllText(String.valueOf(stopPrice));
        bidButton().click();
    }

    public void showsSniperStatus(String itemId, int lastPrice, int lastBid, String statusText) {
        JTableDriver table = new JTableDriver(this);
        table.hasRow(
                matching(withLabelText(itemId), withLabelText(valueOf(lastPrice)),
                        withLabelText(valueOf(lastBid)), withLabelText(statusText)));
    }

    private JTextFieldDriver textField(String name) {
        JTextFieldDriver newItemId = new JTextFieldDriver(this, JTextField.class, named(name));
        newItemId.focusWithMouse();
        return newItemId;
    }

    private JButtonDriver bidButton() {
        return new JButtonDriver(this, JButton.class, named(MainWindow.JOIN_BUTTON_NAME));
    }
}
