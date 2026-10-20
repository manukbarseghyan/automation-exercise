package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.List;

public class ProductsPage extends BasePage {

    // Shows "All Products" before a search and "Searched Products" after it. The site's CSS
    // uppercases h2.title and getText() returns the rendered text, so compare ignoring case.
    private final By listHeading = By.cssSelector(".features_items h2.title");
    private final By searchInput = By.id("search_product");
    private final By searchButton = By.id("submit_search");
    // Matches only the product name; the price is in a separate <h2>.
    private final By productNames = By.cssSelector(".features_items .productinfo p");

    @Step("Check that the 'All Products' page is visible")
    public boolean isAllProductsPageVisible() {
        try {
            waitForUrlContains("/products");
            return "All Products".equalsIgnoreCase(getText(listHeading));
        } catch (TimeoutException e) {
            return false;
        }
    }

    @Step("Search for '{productName}'")
    public ProductsPage searchFor(String productName) {
        type(searchInput, productName);
        click(searchButton);
        // The search reloads the page, so wait for the new URL before reading the list.
        waitForUrlContains("search=");
        return new ProductsPage();
    }

    @Step("Check that 'Searched Products' is visible")
    public boolean isSearchedProductsVisible() {
        try {
            return "Searched Products".equalsIgnoreCase(getText(listHeading));
        } catch (TimeoutException e) {
            return false;
        }
    }

    @Step("Read visible product names")
    public List<String> getVisibleProductNames() {
        return waitForAllVisible(productNames).stream().map(WebElement::getText).toList();
    }
}