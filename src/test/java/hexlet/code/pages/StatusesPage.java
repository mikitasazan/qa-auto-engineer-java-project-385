package hexlet.code.pages;

import hexlet.code.config.TestSettings;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class StatusesPage extends AbstractPage {
    private static final String ROUTE = "task_statuses";
    private static final String EMPTY_STATE = "Do you want to add one?";

    public StatusesPage(WebDriver driver, TestSettings settings) {
        super(driver, settings);
    }

    public void open() {
        goTo(ROUTE);
    }

    public boolean isListVisible() {
        open();
        return isVisible("Name", "*") && isVisible("Slug", "*");
    }

    public boolean isStatusListed(String name) {
        open();
        return isVisible(name, "*");
    }

    public boolean createStatus(String name, String slug) {
        open();
        clickByAriaLabel("Create");
        typeInto("input[name=\"name\"]", name);
        typeInto("input[name=\"slug\"]", slug);
        clickByAriaLabel("Save");
        return isStatusListed(name);
    }

    public boolean renameStatus(String currentName, String newName) {
        open();
        clickVisibleText(currentName, "*");
        replaceFieldValue("input[name=\"name\"]", newName);
        clickByAriaLabel("Save");
        return isStatusListed(newName);
    }

    public boolean removeStatus(String name) {
        open();
        clickVisibleText(name, "*");
        clickByAriaLabel("Delete");
        open();
        return waitUntilInvisible(name, "*");
    }

    public boolean removeEveryStatus() {
        open();
        try {
            withStaleRetry(
                    () ->
                            wait.until(
                                            ExpectedConditions.elementToBeClickable(
                                                    By.cssSelector(
                                                            "table thead tr th input[type=\"checkbox\"]")))
                                    .click());
        } catch (TimeoutException e) {
            return true;
        }
        clickByAriaLabel("Delete");
        open();
        return isVisible(EMPTY_STATE, "*");
    }

    private void replaceFieldValue(String cssSelector, String value) {
        var field =
                wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(cssSelector)));
        field.sendKeys(Keys.HOME);
        field.sendKeys(Keys.chord(Keys.SHIFT, Keys.END));
        field.sendKeys(Keys.DELETE);
        field.sendKeys(value);
    }
}
