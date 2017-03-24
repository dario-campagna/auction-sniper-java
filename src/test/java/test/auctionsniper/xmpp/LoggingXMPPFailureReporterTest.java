package test.auctionsniper.xmpp;


import it.esteco.auctionsniper.adapters.xmpp.LoggingXMPPFailureReporter;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LoggingXMPPFailureReporterTest {

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};

    private final Logger logger = context.mock(Logger.class);
    private final LoggingXMPPFailureReporter reporter = new LoggingXMPPFailureReporter(logger);

    @AfterClass
    public static void resetLogging() {
        LogManager.getLogManager().reset();
    }

    @Test
    public void writeMessageTranslationFailureToLog() throws Exception {
        context.checking(new Expectations(){{
            oneOf(logger).severe("<auction id> " +
                    "Could not translate message \"bad message\" " +
                    "because \"java.lang.Exception: bad\"");
        }});

        reporter.cannotTranslateMessage("auction id", "bad message", new Exception("bad"));
    }
}
