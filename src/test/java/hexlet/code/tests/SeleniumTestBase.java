package hexlet.code.tests;

import hexlet.code.config.TestSettings;
import hexlet.code.support.BrowserFactory;
import hexlet.code.support.TestLogging;
import java.nio.file.Files;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * Common lifecycle for every UI test: a fresh browser per test method (so one test's data can never
 * leak into the next, since the app keeps its records only in memory) plus a screenshot on failure
 * to make CI red builds diagnosable without re-running anything.
 */
public abstract class SeleniumTestBase {
    protected WebDriver driver;
    protected TestSettings settings;

    @RegisterExtension
    final TestWatcher captureOnFailure =
            new TestWatcher() {
                @Override
                public void testFailed(ExtensionContext context, Throwable cause) {
                    saveScreenshot(context.getDisplayName());
                }
            };

    @BeforeEach
    void launchBrowser() {
        settings = TestSettings.fromEnvironment();
        TestLogging.ensureConfigured(settings);
        driver = BrowserFactory.launch(settings);
        startFromCleanState();
    }

    @AfterEach
    void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void startFromCleanState() {
        driver.get(settings.getBaseUrl());
        driver.manage().deleteAllCookies();
        try {
            ((JavascriptExecutor) driver)
                    .executeScript("window.localStorage.clear(); window.sessionStorage.clear();");
        } catch (Exception e) {
            // Storage may be unavailable before the app has painted anything; harmless.
        }
    }

    private void saveScreenshot(String testName) {
        if (!(driver instanceof TakesScreenshot)) {
            return;
        }
        var safeName = testName.replaceAll("[^a-zA-Z0-9._-]", "_");
        var target = settings.getScreenshotDir().resolve(safeName + ".png");
        try {
            Files.write(target, ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
        } catch (Exception e) {
            // A failed screenshot must never mask the original test failure.
        }
    }
}
