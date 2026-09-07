package hexlet.code.pages;

import hexlet.code.config.TestSettings;
import hexlet.code.support.Locators;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class TasksPage extends AbstractPage {
    private static final String ROUTE = "tasks";
    private static final String CARD_CLASS_MARKER = "MuiCard-root";

    public TasksPage(WebDriver driver, TestSettings settings) {
        super(driver, settings);
    }

    /**
     * Opening the board is only trusted once the "Create" action is actually clickable. That single
     * wait is what makes a hidden-board fixture fail loudly here, before any of the methods below
     * even try to act on it.
     */
    public void open() {
        goTo(ROUTE);
        wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("[aria-label=\"Create\"]")));
    }

    public boolean createTask(
            String title, String content, String assigneeEmail, String statusName) {
        open();
        clickByAriaLabel("Create");
        chooseOption("input[name=\"assignee_id\"]", assigneeEmail);
        typeInto("input[name=\"title\"]", title);
        if (content != null && !content.isBlank()) {
            typeInto("textarea[name=\"content\"]", content);
        }
        chooseOption("input[name=\"status_id\"]", statusName);
        clickByAriaLabel("Save");
        return isCardVisible(title);
    }

    public boolean isCardVisible(String title) {
        open();
        return isVisible(title, "*");
    }

    /** Checks a piece of text on whatever page is currently open (e.g. the task-show screen). */
    public boolean isDetailVisible(String text) {
        return isVisible(text, "*");
    }

    public boolean editTask(
            String currentTitle, String newTitle, String newContent, String newStatus) {
        open();
        openCardAction(currentTitle, "Edit");

        if (newTitle != null) {
            replaceFieldValue("input[name=\"title\"]", newTitle);
        }
        if (newContent != null) {
            replaceFieldValue("textarea[name=\"content\"]", newContent);
        }
        if (newStatus != null) {
            chooseOption("input[name=\"status_id\"]", newStatus);
        }

        clickByAriaLabel("Save");
        var expectedTitle = newTitle != null ? newTitle : currentTitle;
        wait.until(ExpectedConditions.urlContains("#/tasks"));
        return isCardVisible(expectedTitle);
    }

    public void openTaskDetails(String title) {
        open();
        openCardAction(title, "Show");
    }

    /** True when a visible card with this title sits inside the named column. */
    public boolean isCardInColumn(String title, String statusName) {
        open();
        var columnXpath =
                Locators.byExactText("*", statusName)
                        + "/following::div[@data-rfd-droppable-id][1]";
        try {
            var column =
                    wait.until(
                            ExpectedConditions.visibilityOfElementLocated(By.xpath(columnXpath)));
            column.findElement(By.xpath(Locators.relativeByExactText("*", title)));
            return true;
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }

    private String cardXpath(String title) {
        return Locators.byExactText("*", title)
                + "/ancestor::div[contains(concat(' ', normalize-space(@class), ' '), ' "
                + CARD_CLASS_MARKER
                + " ')]";
    }

    private void openCardAction(String title, String ariaLabel) {
        var cardLocator = By.xpath(cardXpath(title));
        wait.until(ExpectedConditions.visibilityOfElementLocated(cardLocator));
        var actionXpath = cardXpath(title) + "//*[@aria-label='" + ariaLabel + "']";
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(actionXpath))).click();
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
