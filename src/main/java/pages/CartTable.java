package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Product table shared by the cart and checkout pages, which render the same #cart_info markup.
 */
class CartTable extends BasePage {

    private final By productNames = By.cssSelector("#cart_info tr[id^='product-'] .cart_description h4 a");

    // Waits for at least one row, so an empty cart makes this method time out.
    List<String> getProductNames() {
        return waitForAllVisible(productNames).stream().map(WebElement::getText).toList();
    }
}