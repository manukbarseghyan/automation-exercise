package tests;

import base.BaseTest;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.ProductsPage;

import java.util.List;
import java.util.Locale;

import static io.qameta.allure.Allure.step;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class SearchProductTest extends BaseTest {

    // "Jeans" appears in every matching product name, so the name check below is reliable.
    private static final String SEARCH_TERM = "Jeans";

    // Steps 1-2 (launch browser, navigate to URL) run in BaseTest.openApplication().
    @Test(description = "Test Case 9: Search Product")
    public void searchProduct() {
        HomePage homePage = new HomePage();
        step("3. Verify that home page is visible successfully", () ->
                assertTrue(homePage.isHomePageVisible(), "Home page should be visible after opening the base URL"));

        ProductsPage productsPage = step("4. Click on 'Products' button", () -> homePage.header().clickProducts());

        step("5. Verify user is navigated to ALL PRODUCTS page successfully", () ->
                assertTrue(productsPage.isAllProductsPageVisible(), "User should be on the ALL PRODUCTS page"));

        // Typing and clicking search are one page action, so they share a single step.
        ProductsPage searchResultsPage = step("6. Enter product name in search input and click search button", () ->
                productsPage.searchFor(SEARCH_TERM));

        step("7. Verify 'SEARCHED PRODUCTS' is visible", () ->
                assertTrue(searchResultsPage.isSearchedProductsVisible(), "'SEARCHED PRODUCTS' heading should be visible"));

        step("8. Verify all the products related to search are visible", () -> {
            List<String> results = searchResultsPage.getVisibleProductNames();
            assertFalse(results.isEmpty(), "Search for '" + SEARCH_TERM + "' should return at least one product");
            List<String> unrelated = results.stream()
                    .filter(name -> !name.toLowerCase(Locale.ROOT).contains(SEARCH_TERM.toLowerCase(Locale.ROOT)))
                    .toList();
            assertTrue(unrelated.isEmpty(),
                    "All results should relate to '" + SEARCH_TERM + "', but found: " + unrelated);
        });
    }
}