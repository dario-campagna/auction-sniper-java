package test.auctionsniper.ui;

import com.objogate.wl.swing.probe.ValueMatcherProbe;
import it.esteco.auctionsniper.adapters.ui.MainWindow;
import it.esteco.auctionsniper.domain.Item;
import it.esteco.auctionsniper.domain.SniperPortfolio;
import org.junit.Test;
import test.auctionsniper.runner.AuctionSniperDriver;

import static org.hamcrest.core.IsEqual.equalTo;

public class MainWindowTest {

    private SniperPortfolio portfolio = new SniperPortfolio();
    private final MainWindow mainWindow = new MainWindow(portfolio);
    private final AuctionSniperDriver driver = new AuctionSniperDriver(100);

    @Test
    public void makesUserRequestWhenJoinButtonClicked() throws Exception {
        final ValueMatcherProbe<Item> itemProbe = new ValueMatcherProbe<>(equalTo(new Item("one item-i", 789)), "item request");

        mainWindow.addUserRequestListener(item -> itemProbe.setReceivedValue(item));

        driver.startBiddingFor("one item-i", 789);
        driver.check(itemProbe);
    }
}
