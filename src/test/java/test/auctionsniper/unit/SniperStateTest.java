package test.auctionsniper.unit;

import it.esteco.auction.sniper.Defect;
import it.esteco.auction.sniper.SniperState;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

public class SniperStateTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void loseIfAuctionClosedWhenJoining() throws Exception {
        assertThat(SniperState.JOINING.whenAuctionClosed(), equalTo(SniperState.LOST));
    }

    @Test
    public void loseIfAuctionClosedWhenBidding() throws Exception {
        assertThat(SniperState.BIDDING.whenAuctionClosed(), equalTo(SniperState.LOST));
    }

    @Test
    public void winIfAuctionClosedWhenWinning() throws Exception {
        assertThat(SniperState.WINNING.whenAuctionClosed(), equalTo(SniperState.WON));
    }

    @Test
    public void auctionShouldNotBeReClosedWhenLost() throws Exception {
        expectedException.expect(Defect.class);
        expectedException.expectMessage("Auction is already closed");

        SniperState.LOST.whenAuctionClosed();
    }

    @Test
    public void auctionShouldNotBeReClosedWhenWon() throws Exception {
        expectedException.expect(Defect.class);
        expectedException.expectMessage("Auction is already closed");

        SniperState.WON.whenAuctionClosed();
    }
}