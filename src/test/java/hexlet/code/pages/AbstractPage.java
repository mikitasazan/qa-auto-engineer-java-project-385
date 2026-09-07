package hexlet.code.pages;

import hexlet.code.config.TestSettings;
import hexlet.code.support.Locators;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Shared plumbing for every Page Object.
 *
 * <p>The one rule that matters more than any locator: every method here that stands for "the user
 * saw X" waits for {@code visibilityOf}/{@code elementToBeClickable}, never bare {@code
 * presenceOf}. The fixtures this suite is graded against hide broken sections with CSS instead of
 * removing them from the DOM, so a presence-only check would report success on a section nobody can
 * actually see or use.
 */
abstract class AbstractPage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;
    private final String baseUrl;

    protected AbstractPage(WebDriver driver, TestSettings settings) {
        this.driver = driver;
        this.baseUrl = settings.getBaseUrl().replaceAll("/+$", "");
        this.wait = new WebDriverWait(driver, settings.getExplicitWait());
        this.wait.pollingEvery(settings.getPollInterval());
    }

    /** Navigates to {@code baseUrl/#/fragment}; an empty fragment goes to the root. */
    protected void goTo(String fragment) {
        var path = fragment == null ? "" : fragment.trim().replaceFirst("^/+", "");
        var url = path.isEmpty() ? baseUrl : baseUrl + "/#/" + path;
        driver.get(url);
    }

    protected WebElement waitVisibleText(String text, String tag) {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath(Locators.byExactText(tag, text))));
    }

    protected WebElement clickVisibleText(String text, String tag) {
        var element = waitVisibleText(text, tag);
        element.click();
        return element;
    }

    protected WebElement clickByAriaLabel(String ariaLabel) {
        var locator = By.cssSelector("[aria-label=\"" + ariaLabel + "\"]");
        var element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        element.click();
        return element;
    }

    protected WebElement typeInto(String cssSelector, String value) {
        var field =
                wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(cssSelector)));
        field.clear();
        field.sendKeys(value);
        return field;
    }

    /**
     * Material UI renders a combobox as a hidden {@code <input>} paired with a clickable sibling
     * carrying {@code role="combobox"}; opening the popup means clicking that sibling, then picking
     * the option by its visible text.
     */
    protected void chooseOption(String triggerSelector, String optionText) {
        var input =
                wait.until(
                        ExpectedConditions.presenceOfElementLocated(
                                By.cssSelector(triggerSelector)));

        var opener = input;
        if ("input".equalsIgnoreCase(input.getTagName())) {
            try {
                opener = input.findElement(By.xpath("./ancestor::*[1]//*[@role='combobox']"));
            } catch (NoSuchElementException e) {
                opener = input;
            }
        }

        var target = opener;
        wait.until(driver -> target.isDisplayed() && target.isEnabled());
        try {
            target.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", target);
        }

        wait.until(
                        ExpectedConditions.elementToBeClickable(
                                By.xpath(Locators.byExactText("li", optionText))))
                .click();
    }

    protected boolean isVisible(String text, String tag) {
        try {
            waitVisibleText(text, tag);
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    /**
     * Runs {@code action} again whenever a re-render invalidates the element it just looked up.
     * react-admin's list can refetch and swap its rows right after the initial paint, so a
     * lookup-then-act sequence has to be retried as a whole, not just the click at the end of it.
     */
    protected void withStaleRetry(Runnable action) {
        var deadline = System.nanoTime() + java.time.Duration.ofSeconds(3).toNanos();
        while (true) {
            try {
                action.run();
                return;
            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                if (System.nanoTime() > deadline) {
                    throw e;
                }
            }
        }
    }

    protected boolean waitUntilInvisible(String text, String tag) {
        try {
            return wait.until(
                    ExpectedConditions.invisibilityOfElementLocated(
                            By.xpath(Locators.byExactText(tag, text))));
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }
}
