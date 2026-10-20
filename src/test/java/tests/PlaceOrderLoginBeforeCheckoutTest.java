package tests;

import base.BaseTest;
import data.TestDataFactory;
import data.UserApiClient;
import models.User;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pages.AccountDeletedPage;
import pages.CartPage;
import pages.CheckoutPage;
import pages.HomePage;
import pages.OrderPlacedPage;
import pages.PaymentPage;
import pages.SignupLoginPage;

import java.time.Year;
import java.util.List;

import static io.qameta.allure.Allure.step;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class PlaceOrderLoginBeforeCheckoutTest extends BaseTest {

    private User user;

    // Steps 1-2 (launch browser, navigate to URL) run in BaseTest.openApplication().
    @Test(description = "Test Case 16: Place Order: Login before Checkout")
    public void placeOrderLoginBeforeCheckout() {
        // Registration is not under test, so the account is created through the API.
        // Lambdas need an effectively final local; the field is kept for cleanup in deleteTestUser().
        User user = TestDataFactory.uniqueUser();
        this.user = user;
        step("Precondition: create account via API", () -> UserApiClient.createAccount(user));

        HomePage homePage = new HomePage();
        step("3. Verify that home page is visible successfully", () ->
                assertTrue(homePage.isHomePageVisible(), "Home page should be visible after opening the base URL"));

        SignupLoginPage signupLoginPage = step("4. Click 'Signup / Login' button", () ->
                homePage.header().clickSignupLogin());

        HomePage loggedInHomePage = step("5. Fill email, password and click 'Login' button", () ->
                signupLoginPage.login(user.email(), user.password()));

        step("6. Verify 'Logged in as username' at top", () ->
                assertEquals(loggedInHomePage.header().getLoggedInAsText(), "Logged in as " + user.name(),
                        "Header should show the logged-in user"));

        step("7. Add products to cart", () -> loggedInHomePage.addProductToCart(1).addProductToCart(2));

        CartPage cartPage = step("8. Click 'Cart' button", () -> loggedInHomePage.header().clickCart());

        List<String> cartProducts = step("9. Verify that cart page is displayed", () -> {
            assertTrue(cartPage.isCartPageVisible(), "Cart page should be displayed");
            List<String> products = cartPage.getProductNames();
            assertEquals(products.size(), 2, "Cart should contain the two added products, but has " + products);
            return products;
        });

        CheckoutPage checkoutPage = step("10. Click Proceed To Checkout", cartPage::proceedToCheckout);

        step("11. Verify Address Details and Review Your Order", () -> {
            assertTrue(checkoutPage.isAddressDetailsVisible(), "'Address Details' should be visible on checkout");
            assertTrue(checkoutPage.getDeliveryAddressText().contains(user.address()),
                    "Delivery address should contain '" + user.address() + "'");
            assertTrue(checkoutPage.isReviewYourOrderVisible(), "'Review Your Order' should be visible on checkout");
            assertEquals(checkoutPage.getOrderProductNames(), cartProducts,
                    "Order review should list the same products as the cart");
        });

        PaymentPage paymentPage = step("12. Enter description in comment text area and click 'Place Order'", () ->
                checkoutPage.enterComment("Automated order " + user.email()).clickPlaceOrder());

        step("13. Enter payment details: Name on Card, Card Number, CVC, Expiration date", () ->
                paymentPage.enterPaymentDetails(user.firstName() + " " + user.lastName(), "4111111111111111", "123",
                        "12", String.valueOf(Year.now().getValue() + 3)));

        OrderPlacedPage orderPlacedPage = step("14. Click 'Pay and Confirm Order' button",
                paymentPage::clickPayAndConfirmOrder);

        step("15. Verify success message 'Your order has been placed successfully!'", () -> {
            assertEquals(orderPlacedPage.getPaymentSuccessMessage(), "Your order has been placed successfully!",
                    "Payment page should show the order success message on submit");
            assertEquals(orderPlacedPage.getHeadingText(), "ORDER PLACED!", "Order confirmation page should be shown");
        });

        AccountDeletedPage accountDeletedPage = step("16. Click 'Delete Account' button", () ->
                orderPlacedPage.header().clickDeleteAccount());

        step("17. Verify 'ACCOUNT DELETED!' and click 'Continue' button", () -> {
            assertEquals(accountDeletedPage.getHeadingText(), "ACCOUNT DELETED!",
                    "Account deletion confirmation should be shown");
            accountDeletedPage.clickContinue();
        });
    }

    // Cleans up the account if the test failed before it was deleted through the UI.
    @AfterMethod(alwaysRun = true)
    public void deleteTestUser() {
        if (user != null) {
            UserApiClient.deleteAccountQuietly(user);
        }
    }
}