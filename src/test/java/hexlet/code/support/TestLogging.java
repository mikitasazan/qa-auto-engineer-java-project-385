package hexlet.code.support;

import hexlet.code.config.TestSettings;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/** Wires the suite's logger to a file once, the first time any test needs it. */
public final class TestLogging {
    public static final String ROOT_LOGGER = "hexlet.kanban.ui";

    private static final AtomicBoolean READY = new AtomicBoolean(false);

    private TestLogging() {}

    public static void ensureConfigured(TestSettings settings) {
        if (!READY.compareAndSet(false, true)) {
            return;
        }

        var logger = Logger.getLogger(ROOT_LOGGER);
        logger.setLevel(Level.parse(settings.getLogLevel()));

        try {
            var handler =
                    new FileHandler(settings.getLogDir().resolve("ui-tests.log").toString(), true);
            handler.setFormatter(new SimpleFormatter());
            handler.setLevel(logger.getLevel());
            logger.addHandler(handler);
        } catch (IOException e) {
            // A missing log file must never fail a UI test; console logging still works.
        }
    }
}
