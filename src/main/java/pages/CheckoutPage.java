package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

import java.util.List;

public class CheckoutPage extends BasePage {

    private final By addressDetailsHeading = By.xpath("//h2[@class='heading' and normalize-space(.)='Address Details']");
    private final By reviewYourOrderHeading = By.xpath("//h2[@class='heading' and normalize-space(.)='Review Your Order']");
    private final By deliveryAddress = By.id("address_delivery");
    private final By commentTextArea = By.cssSelector("textarea[name='message']");
    private final By placeOrderButton = By.cssSelector("a[href='/payment']");

    @Step("Check that 'Address Details' is visible")
    public boolean isAddressDetailsVisible() {
        return isDisplayed(addressDetailsHeading);
    }

    @Step("Check that 'Review Your Order' is visible")
    public boolean isReviewYourOrderVisible() {
        return isDisplayed(reviewYourOrderHeading);
    }

    @Step("Read delivery address")
    public String getDeliveryAddressText() {
        return getText(deliveryAddress);
    }

    @Step("Read product names in the order review")
    public List<String> getOrderProductNames() {
        return new CartTable().getProductNames();
    }

    @Step("Enter order comment '{comment}'")
    public CheckoutPage enterComment(String comment) {
        type(commentTextArea, comment);
        return this;
    }

    @Step("Click 'Place Order'")
    public PaymentPage clickPlaceOrder() {
        click(placeOrderButton);
        return new PaymentPage();
    }
}