package hexlet.code.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Locale;

/**
 * Every knob the test suite exposes to the outside world, read once from environment variables.
 * Nothing here is hard-coded: the same jar has to run against the reference app and against every
 * broken fixture, and the only thing that changes between those runs is {@code APP_BASE_URL}.
 */
public final class TestSettings {
    private static final String DEFAULT_WINDOW_SIZE = "1440,900";
    private static final String DEFAULT_LOG_DIR = ".logs";
    private static final String DEFAULT_CHROME_BIN = "/usr/bin/chromium";
    private static final String DEFAULT_CHROMEDRIVER_BIN = "/usr/bin/chromedriver";

    private final String baseUrl;
    private final boolean headless;
    private final String windowSize;
    private final Duration pageLoadTimeout;
    private final Duration implicitWait;
    private final Duration explicitWait;
    private final Duration pollInterval;
    private final String logLevel;
    private final Path logDir;
    private final Path screenshotDir;
    private final String chromeBinary;
    private final String chromeDriverBinary;

    private TestSettings(Builder builder) {
        this.baseUrl = builder.baseUrl;
        this.headless = builder.headless;
        this.windowSize = builder.windowSize;
        this.pageLoadTimeout = builder.pageLoadTimeout;
        this.implicitWait = builder.implicitWait;
        this.explicitWait = builder.explicitWait;
        this.pollInterval = builder.pollInterval;
        this.logLevel = builder.logLevel;
        this.logDir = builder.logDir;
        this.screenshotDir = builder.screenshotDir;
        this.chromeBinary = builder.chromeBinary;
        this.chromeDriverBinary = builder.chromeDriverBinary;
    }

    public static TestSettings fromEnvironment() {
        var rawBaseUrl = readEnv("APP_BASE_URL", null);
        if (rawBaseUrl == null || rawBaseUrl.isBlank()) {
            throw new IllegalStateException(
                    "APP_BASE_URL is not set: tests need the address of a running kanban app");
        }

        var logDir = Path.of(readEnv("TEST_LOG_DIR", DEFAULT_LOG_DIR)).toAbsolutePath();
        var screenshotDir = logDir.resolve("screenshots").resolve(hostOf(withScheme(rawBaseUrl)));
        logDir.toFile().mkdirs();
        screenshotDir.toFile().mkdirs();

        return new Builder()
                .baseUrl(withScheme(rawBaseUrl))
                .headless(!"false".equalsIgnoreCase(readEnv("HEADLESS", "true")))
                .windowSize(readEnv("BROWSER_WINDOW_SIZE", DEFAULT_WINDOW_SIZE))
                .pageLoadTimeout(Duration.ofSeconds(readLong("PAGE_LOAD_TIMEOUT", 45)))
                .implicitWait(millisFromSeconds(readDouble("SELENIUM_IMPLICIT_WAIT", 0.2)))
                .explicitWait(Duration.ofSeconds(readLong("SELENIUM_DEFAULT_TIMEOUT", 8)))
                .pollInterval(millisFromSeconds(readDouble("SELENIUM_POLL_INTERVAL", 0.5)))
                .logLevel(readEnv("TEST_LOG_LEVEL", "INFO").toUpperCase(Locale.ROOT))
                .logDir(logDir)
                .screenshotDir(screenshotDir)
                .chromeBinary(readBinary("CHROME_BIN", DEFAULT_CHROME_BIN))
                .chromeDriverBinary(readBinary("CHROMEDRIVER_BIN", DEFAULT_CHROMEDRIVER_BIN))
                .build();
    }

    private static String withScheme(String url) {
        var trimmed = url.trim();
        return trimmed.startsWith("http://") || trimmed.startsWith("https://")
                ? trimmed
                : "http://" + trimmed;
    }

    private static String hostOf(String url) {
        try {
            var host = new URI(url).getHost();
            return host == null || host.isBlank() ? "app" : host;
        } catch (URISyntaxException e) {
            return "app";
        }
    }

    private static Duration millisFromSeconds(double seconds) {
        return Duration.ofMillis(Math.round(seconds * 1000));
    }

    private static String readEnv(String name, String fallback) {
        var value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }

    private static long readLong(String name, long fallback) {
        var value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : Long.parseLong(value.trim());
    }

    private static double readDouble(String name, double fallback) {
        var value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : Double.parseDouble(value.trim());
    }

    /**
     * The binary path is only handed to Selenium when it exists on disk; otherwise Selenium Manager
     * is left to resolve a driver on its own.
     */
    private static String readBinary(String name, String fallback) {
        var value = readEnv(name, fallback);
        return Path.of(value).toFile().exists() ? value : null;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public boolean isHeadless() {
        return headless;
    }

    public String getWindowSize() {
        return windowSize;
    }

    public Duration getPageLoadTimeout() {
        return pageLoadTimeout;
    }

    public Duration getImplicitWait() {
        return implicitWait;
    }

    public Duration getExplicitWait() {
        return explicitWait;
    }

    public Duration getPollInterval() {
        return pollInterval;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public Path getLogDir() {
        return logDir;
    }

    public Path getScreenshotDir() {
        return screenshotDir;
    }

    public String getChromeBinary() {
        return chromeBinary;
    }

    public String getChromeDriverBinary() {
        return chromeDriverBinary;
    }

    private static final class Builder {
        private String baseUrl;
        private boolean headless;
        private String windowSize;
        private Duration pageLoadTimeout;
        private Duration implicitWait;
        private Duration explicitWait;
        private Duration pollInterval;
        private String logLevel;
        private Path logDir;
        private Path screenshotDir;
        private String chromeBinary;
        private String chromeDriverBinary;

        private Builder baseUrl(String value) {
            this.baseUrl = value;
            return this;
        }

        private Builder headless(boolean value) {
            this.headless = value;
            return this;
        }

        private Builder windowSize(String value) {
            this.windowSize = value;
            return this;
        }

        private Builder pageLoadTimeout(Duration value) {
            this.pageLoadTimeout = value;
            return this;
        }

        private Builder implicitWait(Duration value) {
            this.implicitWait = value;
            return this;
        }

        private Builder explicitWait(Duration value) {
            this.explicitWait = value;
            return this;
        }

        private Builder pollInterval(Duration value) {
            this.pollInterval = value;
            return this;
        }

        private Builder logLevel(String value) {
            this.logLevel = value;
            return this;
        }

        private Builder logDir(Path value) {
            this.logDir = value;
            return this;
        }

        private Builder screenshotDir(Path value) {
            this.screenshotDir = value;
            return this;
        }

        private Builder chromeBinary(String value) {
            this.chromeBinary = value;
            return this;
        }

        private Builder chromeDriverBinary(String value) {
            this.chromeDriverBinary = value;
            return this;
        }

        private TestSettings build() {
            return new TestSettings(this);
        }
    }
}
