package test.auctionsniper.integration;

import com.objogate.wl.swing.probe.ValueMatcherProbe;
import it.esteco.auction.sniper.mainwindow.MainWindow;
import it.esteco.auction.sniper.mainwindow.SnipersTableModel;
import org.junit.Test;
import test.auctionsniper.AuctionSniperDriver;

import static org.hamcrest.core.IsEqual.equalTo;

public class MainWindowTest {

    private final SnipersTableModel tableModel = new SnipersTableModel();
    private final MainWindow mainWindow = new MainWindow(tableModel);
    private final AuctionSniperDriver driver = new AuctionSniperDriver(100);

    @Test
    public void makesUserRequestWhenJoinButtonClicked() throws Exception {
        final ValueMatcherProbe<String> buttonProbe = new ValueMatcherProbe<>(equalTo("one item-i"), "join request");

        mainWindow.addUserRequestListener(itemId -> buttonProbe.setReceivedValue(itemId));

        driver.startBiddingFor("one item-i");
        driver.check(buttonProbe);
    }
}
