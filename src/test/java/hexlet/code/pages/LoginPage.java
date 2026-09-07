package hexlet.code.pages;

import hexlet.code.config.TestSettings;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends AbstractPage {
    private static final String DASHBOARD_MARKER = "Lorem ipsum sic dolor amet...";

    public LoginPage(WebDriver driver, TestSettings settings) {
        super(driver, settings);
    }

    /**
     * Signs in and waits for the dashboard to actually render. Stopping right after the click would
     * pass on a build where the login form works but nothing is behind it — the very thing one of
     * the graded fixtures does.
     */
    public void signIn(String username, String password) {
        goTo("login");
        typeInto("input[name=\"username\"]", username);
        typeInto("input[name=\"password\"]", password);
        clickVisibleText("Sign in", "button");
        waitVisibleText(DASHBOARD_MARKER, "*");
    }

    public void signOut() {
        goTo("tasks");
        clickByAriaLabel("Profile");
        wait.until(
                        ExpectedConditions.elementToBeClickable(
                                By.xpath("//li[@role=\"menuitem\" and .=\"Logout\"]")))
                .click();
        waitVisibleText("Sign in", "button");
    }

    public boolean isOnDashboard() {
        return isVisible(DASHBOARD_MARKER, "*");
    }

    public boolean isOnSignInScreen() {
        return isVisible("Sign in", "button");
    }
}
