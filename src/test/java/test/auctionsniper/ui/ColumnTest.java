package test.auctionsniper.ui;

import it.esteco.auctionsniper.SniperSnapshot;
import it.esteco.auctionsniper.SniperState;
import it.esteco.auctionsniper.ui.Column;
import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class ColumnTest {

    @Test
    public void valueInSnapshot() throws Exception {
        SniperSnapshot snapshot = new SniperSnapshot("item id", 111, 222, SniperState.BIDDING);

        assertThat(Column.ITEM_IDENTIFIER.valueIn(snapshot), equalTo("item id"));
        assertThat(Column.LAST_PRICE.valueIn(snapshot), equalTo(111));
        assertThat(Column.LAST_BID.valueIn(snapshot), equalTo(222));
        assertThat(Column.SNIPER_STATE.valueIn(snapshot), equalTo("Bidding"));
    }
}