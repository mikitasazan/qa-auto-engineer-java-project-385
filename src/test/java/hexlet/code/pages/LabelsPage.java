package hexlet.code.pages;

import hexlet.code.config.TestSettings;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LabelsPage extends AbstractPage {
    private static final String ROUTE = "labels";
    private static final String EMPTY_STATE = "Do you want to add one?";

    public LabelsPage(WebDriver driver, TestSettings settings) {
        super(driver, settings);
    }

    public void open() {
        goTo(ROUTE);
    }

    public boolean isTableVisible() {
        open();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        return true;
    }

    public boolean isLabelListed(String name) {
        open();
        return isVisible(name, "*");
    }

    public boolean createLabel(String name) {
        open();
        clickByAriaLabel("Create");
        typeInto("input[name=\"name\"]", name);
        clickByAriaLabel("Save");
        return isLabelListed(name);
    }

    public boolean renameLabel(String currentName, String newName) {
        open();
        clickVisibleText(currentName, "*");
        replaceFieldValue("input[name=\"name\"]", newName);
        clickByAriaLabel("Save");
        return isLabelListed(newName);
    }

    public boolean removeLabel(String name) {
        open();
        clickVisibleText(name, "*");
        clickByAriaLabel("Delete");
        open();
        return waitUntilInvisible(name, "*");
    }

    public boolean removeEveryLabel() {
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
