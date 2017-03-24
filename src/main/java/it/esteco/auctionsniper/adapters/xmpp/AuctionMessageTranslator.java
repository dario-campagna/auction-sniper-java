package it.esteco.auctionsniper.adapters.xmpp;

import it.esteco.auctionsniper.domain.AuctionEventListener;
import it.esteco.auctionsniper.domain.MissingValueException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.HashMap;
import java.util.Map;

import static it.esteco.auctionsniper.adapters.xmpp.AuctionMessageTranslator.AuctionEvent.EVENT_TYPE_CLOSE;
import static it.esteco.auctionsniper.adapters.xmpp.AuctionMessageTranslator.AuctionEvent.EVENT_TYPE_PRICE;

public class AuctionMessageTranslator implements ChatMessageListener {

    private final String sniperId;
    private final AuctionEventListener listener;

    public AuctionMessageTranslator(String sniperId, AuctionEventListener listener) {
        this.sniperId = sniperId;
        this.listener = listener;
    }

    @Override
    public void processMessage(Chat chat, Message message) {
        try {
            translate(message);
        } catch (Exception parseException) {
            listener.auctionFailed();
        }
    }

    private void translate(Message message) throws MissingValueException {
        AuctionEvent event = AuctionEvent.from(message.getBody());

        String eventType = event.type();
        if (EVENT_TYPE_CLOSE.equals(eventType)) {
            listener.auctionClosed();
        } else if (EVENT_TYPE_PRICE.equals(eventType)) {
            listener.currentPrice(event.currentPrice(), event.increment(), event.isFrom(sniperId));
        }
    }

    public static class AuctionEvent {

        public static final String EVENT_TYPE_CLOSE = "CLOSE";
        public static final String EVENT_TYPE_PRICE = "PRICE";

        private final Map<String, String> fields = new HashMap<>();

        static AuctionEvent from(String messageBody) {
            AuctionEvent event = new AuctionEvent();
            for (String field : fieldsIn(messageBody)) {
                event.addField(field);
            }
            return event;
        }

        private static String[] fieldsIn(String messageBody) {
            return messageBody.split(";");
        }

        public String type() throws MissingValueException {
            return get("Event");
        }

        public int currentPrice() throws MissingValueException {
            return getInt("CurrentPrice");
        }

        public int increment() throws MissingValueException {
            return getInt("Increment");
        }

        public AuctionEventListener.PriceSource isFrom(String sniperId) throws MissingValueException {
            return sniperId.equals(bidder()) ? AuctionEventListener.PriceSource.FromSniper : AuctionEventListener.PriceSource.FromOtherBidder;
        }

        private String bidder() throws MissingValueException {
            return get("Bidder");
        }

        private String get(String fieldName) throws MissingValueException {
            String value = fields.get(fieldName);
            if (null == value) {
                throw new MissingValueException(fieldName);
            }
            return value;
        }

        private int getInt(String fieldName) throws MissingValueException {
            return Integer.parseInt(get(fieldName));
        }

        private void addField(String field) {
            String[] pair = field.split(":");
            fields.put(pair[0].trim(), pair[1].trim());
        }
    }
}
