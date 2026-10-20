package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;

import java.util.List;

public class CartPage extends BasePage {

    private final By activeBreadcrumb = By.cssSelector(".breadcrumb li.active");
    private final By proceedToCheckoutButton = By.cssSelector("a.check_out");

    @Step("Check that the cart page is visible")
    public boolean isCartPageVisible() {
        try {
            waitForUrlContains("/view_cart");
            return "Shopping Cart".equals(getText(activeBreadcrumb));
        } catch (TimeoutException e) {
            return false;
        }
    }

    @Step("Read product names in the cart")
    public List<String> getProductNames() {
        return new CartTable().getProductNames();
    }

    // Requires a logged-in user: guests get a login modal instead of the checkout page.
    @Step("Click 'Proceed To Checkout'")
    public CheckoutPage proceedToCheckout() {
        click(proceedToCheckoutButton);
        return new CheckoutPage();
    }
}