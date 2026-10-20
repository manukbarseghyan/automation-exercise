package pages;

import config.ConfigReader;
import driver.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Base class for page objects. Every interaction waits explicitly for the element state it needs.
 * Locators stay in the subclasses; assertions stay in the tests.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicitWaitSeconds")));
    }

    public Header header() {
        return new Header();
    }

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected List<WebElement> waitForAllVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    protected void waitForUrlContains(String fragment) {
        wait.until(ExpectedConditions.urlContains(fragment));
    }

    protected void click(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        // Scroll the element to the middle so a sticky header or footer does not cover it.
        executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        element.click();
    }

    protected void type(By locator, String text) {
        WebElement element = waitForVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected String getText(By locator) {
        return waitForVisible(locator).getText().trim();
    }

    /**
     * Returns false only when the element does not appear within the wait timeout.
     */
    protected boolean isDisplayed(By locator) {
        try {
            return waitForVisible(locator).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    protected void selectByValue(By locator, String value) {
        new Select(waitForVisible(locator)).selectByValue(value);
    }

    protected void selectByVisibleText(By locator, String text) {
        new Select(waitForVisible(locator)).selectByVisibleText(text);
    }

    /**
     * Ensures a checkbox is selected. Clicks only when it is not already selected.
     */
    protected void check(By locator) {
        WebElement checkbox = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        if (!checkbox.isSelected()) {
            click(locator);
        }
    }

    protected Object executeScript(String script, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(script, args);
    }
}