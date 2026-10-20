package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class PaymentPage extends BasePage {

    private static final String SUCCESS_MESSAGE_KEY = "capturedPaymentSuccessMessage";

    private final By nameOnCardInput = By.cssSelector("input[data-qa='name-on-card']");
    private final By cardNumberInput = By.cssSelector("input[data-qa='card-number']");
    private final By cvcInput = By.cssSelector("input[data-qa='cvc']");
    private final By expiryMonthInput = By.cssSelector("input[data-qa='expiry-month']");
    private final By expiryYearInput = By.cssSelector("input[data-qa='expiry-year']");
    private final By payAndConfirmButton = By.cssSelector("button[data-qa='pay-button']");

    @Step("Enter payment details for '{nameOnCard}'")
    public PaymentPage enterPaymentDetails(String nameOnCard, String cardNumber, String cvc,
                                           String expiryMonth, String expiryYear) {
        type(nameOnCardInput, nameOnCard);
        type(cardNumberInput, cardNumber);
        type(cvcInput, cvc);
        type(expiryMonthInput, expiryMonth);
        type(expiryYearInput, expiryYear);
        return this;
    }

    /**
     * The success message is visible only while the form submits, then the browser navigates
     * to the order-placed page, so waiting for the message in the DOM is racy. Instead, a submit
     * listener copies the message into sessionStorage, which survives same-origin navigation.
     * This works only if the site's own submit handler, registered at page load, runs before this
     * listener and un-hides the message. If that changes, getPaymentSuccessMessage() returns "".
     */
    @Step("Click 'Pay and Confirm Order'")
    public OrderPlacedPage clickPayAndConfirmOrder() {
        executeScript(
                "sessionStorage.removeItem(arguments[0]);"
                        + "document.getElementById('payment-form').addEventListener('submit', () => {"
                        + "  const msg = document.getElementById('success_message');"
                        + "  if (msg && !msg.classList.contains('hide')) {"
                        + "    sessionStorage.setItem(arguments[0], msg.textContent.trim());"
                        + "  }"
                        + "});",
                SUCCESS_MESSAGE_KEY);
        click(payAndConfirmButton);
        return new OrderPlacedPage(SUCCESS_MESSAGE_KEY);
    }
}