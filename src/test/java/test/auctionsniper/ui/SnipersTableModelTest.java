package test.auctionsniper.ui;

import it.esteco.auctionsniper.Auction;
import it.esteco.auctionsniper.AuctionSniper;
import it.esteco.auctionsniper.Defect;
import it.esteco.auctionsniper.SniperSnapshot;
import it.esteco.auctionsniper.ui.Column;
import it.esteco.auctionsniper.ui.SnipersTableModel;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class SnipersTableModelTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();
    private Auction auction = context.mock(Auction.class);
    private TableModelListener listener = context.mock(TableModelListener.class);
    private final SnipersTableModel model = new SnipersTableModel();

    @Before
    public void attachModelListener() throws Exception {
        model.addTableModelListener(listener);
    }

    @Test
    public void hasEnoughColumns() throws Exception {
        assertThat(model.getColumnCount(), equalTo(Column.values().length));
    }

    @Test
    public void setsUpColumnHeadings() throws Exception {
        for (Column column : Column.values()) {
            assertEquals(column.name, model.getColumnName(column.ordinal()));
        }
    }

    @Test
    public void notifiesListenersWhenAddingASniper() throws Exception {
        AuctionSniper sniper = new AuctionSniper("item123", auction);
        context.checking(new Expectations() {{
            ignoring(auction);
            oneOf(listener).tableChanged(with(anInsertionAtRow(0)));
        }});

        assertEquals(0, model.getRowCount());

        model.sniperAdded(sniper);

        assertEquals(1, model.getRowCount());
        assertRowMatchesSnapshot(0, sniper.getSnapshot());
    }

    @Test
    public void holdsSnipersInAdditionOrder() throws Exception {
        AuctionSniper sniper0 = new AuctionSniper("item-0", auction);
        AuctionSniper sniper1 = new AuctionSniper("item-1", auction);

        context.checking(new Expectations() {{
            ignoring(listener);
        }});

        model.sniperAdded(sniper0);
        model.sniperAdded(sniper1);

        assertEquals("item-0", cellValue(0, Column.ITEM_IDENTIFIER));
        assertEquals("item-1", cellValue(1, Column.ITEM_IDENTIFIER));
    }

    @Test
    public void setsSniperValuesInColumns() throws Exception {
        AuctionSniper sniper = new AuctionSniper("item-id", auction);
        SniperSnapshot bidding = sniper.getSnapshot().bidding(555, 666);

        context.checking(new Expectations() {{
            allowing(listener).tableChanged(with(anyInsertionEvent()));
            oneOf(listener).tableChanged(with(aChangeInRow(0)));
        }});

        model.sniperAdded(sniper);
        model.sniperStateChanged(bidding);

        assertRowMatchesSnapshot(0, bidding);
    }

    @Test
    public void updatesCorretRowForSniper() throws Exception {
        AuctionSniper sniper = new AuctionSniper("item-0", auction);
        AuctionSniper anotherSniper = new AuctionSniper("item-1", auction);
        SniperSnapshot lost = anotherSniper.getSnapshot().closed();

        context.checking(new Expectations() {{
            ignoring(listener);
        }});

        model.sniperAdded(sniper);
        model.sniperAdded(anotherSniper);

        model.sniperStateChanged(lost);

        assertRowMatchesSnapshot(1, lost);
    }

    @Test
    public void throwsDefectIfNoExistingSniperForAnUpdate() throws Exception {
        SniperSnapshot joining = SniperSnapshot.joining("item 0");

        expectedException.expect(Defect.class);
        expectedException.expectMessage("Cannot find match for " + joining);

        model.sniperStateChanged(joining);
    }

    private Matcher<TableModelEvent> anInsertionAtRow(int rowIndex) {
        return samePropertyValuesAs(new TableModelEvent(model, rowIndex, rowIndex, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }

    private Matcher<TableModelEvent> anyInsertionEvent() {
        return samePropertyValuesAs(new TableModelEvent(model, 0, 0, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }

    private void assertRowMatchesSnapshot(int rowIndex, SniperSnapshot snapshot) {
        assertEquals(snapshot.itemId, cellValue(rowIndex, Column.ITEM_IDENTIFIER));
        assertEquals(snapshot.lastPrice, cellValue(rowIndex, Column.LAST_PRICE));
        assertEquals(snapshot.lastBid, cellValue(rowIndex, Column.LAST_BID));
        assertEquals(SnipersTableModel.textFor(snapshot.state), cellValue(rowIndex, Column.SNIPER_STATE));
    }

    private Object cellValue(int rowIndex, Column column) {
        return model.getValueAt(rowIndex, column.ordinal());
    }

    private Matcher<TableModelEvent> aChangeInRow(int rowIndex) {
        return samePropertyValuesAs(new TableModelEvent(model, rowIndex));
    }
}