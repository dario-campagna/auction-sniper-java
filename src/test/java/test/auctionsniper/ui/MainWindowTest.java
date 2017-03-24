package test.auctionsniper.ui;

import com.objogate.wl.swing.probe.ValueMatcherProbe;
import it.esteco.auctionsniper.ui.MainWindow;
import it.esteco.auctionsniper.ui.SniperPortfolio;
import org.junit.Test;
import test.auctionsniper.runner.AuctionSniperDriver;

import static org.hamcrest.core.IsEqual.equalTo;

public class MainWindowTest {

    private SniperPortfolio portfolio = new SniperPortfolio();
    private final MainWindow mainWindow = new MainWindow(portfolio);
    private final AuctionSniperDriver driver = new AuctionSniperDriver(100);

    @Test
    public void makesUserRequestWhenJoinButtonClicked() throws Exception {
        final ValueMatcherProbe<String> buttonProbe = new ValueMatcherProbe<>(equalTo("one item-i"), "join request");

        mainWindow.addUserRequestListener(itemId -> buttonProbe.setReceivedValue(itemId));

        driver.startBiddingFor("one item-i");
        driver.check(buttonProbe);
    }
}
