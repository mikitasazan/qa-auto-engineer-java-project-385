package hexlet.code.tests;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * The very first thing every other test assumes: the SPA actually mounted and painted its login
 * screen, instead of a blank page or a crashed React root.
 */
class ApplicationSmokeTest extends SeleniumTestBase {
    @Test
    void applicationRendersSignInScreen() {
        driver.get(settings.getBaseUrl());
        var wait = new WebDriverWait(driver, settings.getExplicitWait());
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[.=\"Sign in\"]")));
    }
}
