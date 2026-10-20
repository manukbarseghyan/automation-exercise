package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class AccountCreatedPage extends BasePage {

    private final By accountCreatedHeading = By.cssSelector("h2[data-qa='account-created']");
    private final By continueButton = By.cssSelector("a[data-qa='continue-button']");

    @Step("Read 'Account Created' heading")
    public String getHeadingText() {
        return getText(accountCreatedHeading);
    }

    @Step("Click 'Continue' after account creation")
    public HomePage clickContinue() {
        click(continueButton);
        return new HomePage();
    }
}