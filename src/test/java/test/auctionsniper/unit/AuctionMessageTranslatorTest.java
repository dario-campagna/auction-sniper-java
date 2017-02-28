package test.auctionsniper.unit;

import it.esteco.auction.sniper.AuctionEventListener;
import it.esteco.auction.sniper.AuctionMessageTranslator;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

public class AuctionMessageTranslatorTest {

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    private static final Chat UNUSED_CHAT = null;
    private final AuctionEventListener listener = context.mock(AuctionEventListener.class);
    private AuctionMessageTranslator translator = new AuctionMessageTranslator(listener);

    @Test
    public void notifiesAuctionClosedWhenCloseMessageReceived() throws Exception {
        context.checking(new Expectations(){{
            oneOf(listener).auctionClosed();
        }});

        Message message = new Message();
        message.setBody("SOLVersion 1.1; Event: CLOSE;");

        translator.processMessage(UNUSED_CHAT, message);
    }
}
