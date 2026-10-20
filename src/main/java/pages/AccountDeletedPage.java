package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class AccountDeletedPage extends BasePage {

    private final By accountDeletedHeading = By.cssSelector("h2[data-qa='account-deleted']");
    private final By continueButton = By.cssSelector("a[data-qa='continue-button']");

    @Step("Read 'Account Deleted' heading")
    public String getHeadingText() {
        return getText(accountDeletedHeading);
    }

    @Step("Click 'Continue' after account deletion")
    public HomePage clickContinue() {
        click(continueButton);
        return new HomePage();
    }
}