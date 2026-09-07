package hexlet.code.support;

import hexlet.code.config.TestSettings;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Builds the one WebDriver flavour this suite needs: headless Chrome running against the
 * chromium/chromedriver baked into the grading image.
 *
 * <p>The driver executable is passed explicitly when {@code CHROMEDRIVER_BIN} points at a real
 * file. Selenium Manager's own resolution looks for google-chrome and reaches out to the network
 * for a matching build, which is both unnecessary (the image ships a matching driver already) and
 * unsafe to rely on in a locked-down CI image.
 */
public final class BrowserFactory {
    private BrowserFactory() {}

    public static WebDriver launch(TestSettings settings) {
        var options = new ChromeOptions();
        if (settings.isHeadless()) {
            options.addArguments("--headless=new");
        }
        options.addArguments(
                "--disable-gpu",
                "--disable-dev-shm-usage",
                "--no-sandbox",
                "--window-size=" + settings.getWindowSize());

        if (settings.getChromeBinary() != null) {
            options.setBinary(settings.getChromeBinary());
        }

        var driver = createDriver(settings, options);
        var timeouts = driver.manage().timeouts();
        timeouts.pageLoadTimeout(settings.getPageLoadTimeout());
        timeouts.implicitlyWait(settings.getImplicitWait());
        return driver;
    }

    private static WebDriver createDriver(TestSettings settings, ChromeOptions options) {
        var driverPath = settings.getChromeDriverBinary();
        if (driverPath == null) {
            return new ChromeDriver(options);
        }
        var service =
                new ChromeDriverService.Builder()
                        .usingDriverExecutable(new java.io.File(driverPath))
                        .build();
        return new ChromeDriver(service, options);
    }
}
