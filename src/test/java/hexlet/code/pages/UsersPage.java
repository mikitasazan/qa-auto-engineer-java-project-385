package hexlet.code.pages;

import hexlet.code.config.TestSettings;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class UsersPage extends AbstractPage {
    private static final String ROUTE = "users";
    private static final String EMPTY_STATE = "Do you want to add one?";

    public UsersPage(WebDriver driver, TestSettings settings) {
        super(driver, settings);
    }

    public void open() {
        goTo(ROUTE);
    }

    public boolean isListVisible() {
        open();
        return isVisible("Email", "*");
    }

    public boolean isUserListed(String email) {
        open();
        return isVisible(email, "*");
    }

    public boolean createUser(String email, String firstName, String lastName) {
        open();
        clickByAriaLabel("Create");
        typeInto("input[name=\"email\"]", email);
        typeInto("input[name=\"firstName\"]", firstName);
        typeInto("input[name=\"lastName\"]", lastName);
        clickByAriaLabel("Save");
        return isUserListed(email);
    }

    public boolean renameUser(String email, String newFirstName) {
        open();
        clickVisibleText(email, "*");
        replaceFieldValue("input[name=\"firstName\"]", newFirstName);
        clickByAriaLabel("Save");
        return isUserListed(newFirstName);
    }

    public boolean removeUser(String email) {
        open();
        clickVisibleText(email, "*");
        clickByAriaLabel("Delete");
        open();
        return waitUntilInvisible(email, "*");
    }

    public boolean removeEveryUser() {
        open();
        var rowCount = driver.findElements(By.cssSelector("tbody tr")).size();
        if (rowCount == 0) {
            return true;
        }
        // Selecting a row can re-render the whole Datagrid, which would turn
        // WebElement references collected up front stale mid-loop; re-query
        // by index on every iteration, and retry the whole lookup-and-click
        // if a background refetch swaps the rows out from under it.
        for (int i = 0; i < rowCount; i++) {
            var index = i;
            withStaleRetry(
                    () -> {
                        var rows = driver.findElements(By.cssSelector("tbody tr"));
                        if (index >= rows.size()) {
                            return;
                        }
                        var checkbox =
                                rows.get(index)
                                        .findElement(By.cssSelector("input[type=\"checkbox\"]"));
                        if (!checkbox.isSelected()) {
                            checkbox.click();
                        }
                    });
        }
        clickByAriaLabel("Delete");
        open();
        return isVisible(EMPTY_STATE, "*");
    }

    /**
     * Selects the field's whole current value and replaces it. {@code WebElement#clear()} resets a
     * controlled React input without firing the key events React listens to, so the old value can
     * silently survive it; Home/Shift+End select the same way a person would, on every platform.
     */
    private void replaceFieldValue(String cssSelector, String value) {
        var field =
                wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(cssSelector)));
        field.sendKeys(Keys.HOME);
        field.sendKeys(Keys.chord(Keys.SHIFT, Keys.END));
        field.sendKeys(Keys.DELETE);
        field.sendKeys(value);
    }
}
