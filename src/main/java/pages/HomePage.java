package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Landing page. Scroll position and on-screen checks use JavaScript because WebDriver's
 * isDisplayed() does not report whether an element is inside the viewport.
 */
public class HomePage extends BasePage {

    private final By logo = By.cssSelector("img[alt='Website for automation practice']");
    // The Home link is highlighted only on the home page, which separates it from other pages with the same header.
    private final By activeHomeLink = By.cssSelector("a[href='/'][style*='orange']");
    // Only the active carousel slide is visible.
    private final By heroHeading = By.xpath(
            "//div[contains(@class,'item') and contains(@class,'active')]"
                    + "//h2[text()='Full-Fledged practice website for Automation Engineers']");
    private final By subscriptionHeading = By.xpath("//footer//h2[text()='Subscription']");
    // Added at runtime by the jquery.scrollUp plugin.
    private final By scrollUpArrow = By.id("scrollUp");
    private final By cartModal = By.id("cartModal");
    private final By continueShoppingButton = By.cssSelector("#cartModal button.close-modal");

    @Step("Check that the home page is visible")
    public boolean isHomePageVisible() {
        return isDisplayed(logo) && isDisplayed(activeHomeLink);
    }

    @Step("Add product #{productId} to the cart")
    public HomePage addProductToCart(int productId) {
        click(addToCartButton(productId));
        waitForVisible(cartModal);
        click(continueShoppingButton);
        // Wait for the modal to close, or it intercepts the next click.
        wait.until(ExpectedConditions.invisibilityOfElementLocated(cartModal));
        return this;
    }

    // Scoped to .productinfo: each product has a hidden duplicate button in its hover overlay.
    private By addToCartButton(int productId) {
        return By.cssSelector(".productinfo a.add-to-cart[data-product-id='" + productId + "']");
    }

    @Step("Scroll to the bottom of the page")
    public HomePage scrollToBottom() {
        executeScript("window.scrollTo(0, document.body.scrollHeight);");
        return this;
    }

    @Step("Check that 'SUBSCRIPTION' is visible on screen")
    public boolean isSubscriptionVisible() {
        return isDisplayed(subscriptionHeading) && isInViewport(subscriptionHeading);
    }

    // The arrow scrolls with an animation, so wait until the page reaches the top.
    @Step("Click the scroll-up arrow")
    public HomePage clickScrollUpArrow() {
        click(scrollUpArrow);
        wait.until(d -> isScrolledToTop());
        return this;
    }

    // No @Step: this is polled inside a wait and would flood the report.
    public boolean isScrolledToTop() {
        return ((Number) executeScript("return window.scrollY;")).longValue() == 0;
    }

    @Step("Check that the hero text is visible on screen")
    public boolean isHeroTextVisibleOnScreen() {
        return isDisplayed(heroHeading) && isInViewport(heroHeading);
    }

    // Guard in case the scroll has not taken effect yet. The site's CSS sets no smooth scrolling,
    // so this normally returns immediately.
    @Step("Wait until the page is scrolled away from the top")
    public HomePage waitUntilScrolledAwayFromTop() {
        wait.until(d -> !isScrolledToTop());
        return this;
    }

    // isDisplayed() is also true for elements scrolled out of view, so check the viewport separately.
    private boolean isInViewport(By locator) {
        WebElement element = waitForVisible(locator);
        return (Boolean) executeScript(
                "const r = arguments[0].getBoundingClientRect();"
                        + "return r.top >= 0 && r.bottom <= window.innerHeight;",
                element);
    }
}