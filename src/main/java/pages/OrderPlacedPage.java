package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class OrderPlacedPage extends BasePage {

    private final By orderPlacedHeading = By.cssSelector("h2[data-qa='order-placed']");

    private final String successMessageKey;

    // Package-private: created only by PaymentPage, which knows the storage key.
    OrderPlacedPage(String successMessageKey) {
        this.successMessageKey = successMessageKey;
    }

    @Step("Read 'Order Placed' heading")
    public String getHeadingText() {
        return getText(orderPlacedHeading);
    }

    /**
     * Returns the message stored by {@link PaymentPage#clickPayAndConfirmOrder()}, or an empty string if none was stored.
     */
    @Step("Read payment success message")
    public String getPaymentSuccessMessage() {
        // Waiting for the heading confirms the form was submitted, so the submit listener has already run.
        waitForVisible(orderPlacedHeading);
        Object message = executeScript("return sessionStorage.getItem(arguments[0]);", successMessageKey);
        return message == null ? "" : message.toString();
    }
}