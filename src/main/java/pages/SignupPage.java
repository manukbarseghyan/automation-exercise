package pages;

import io.qameta.allure.Step;
import models.User;
import org.openqa.selenium.By;

public class SignupPage extends BasePage {

    private final By enterAccountInformationHeading =
            By.xpath("//h2/b[normalize-space(.)='Enter Account Information']");

    private final By titleMr = By.id("id_gender1");
    private final By titleMrs = By.id("id_gender2");
    private final By nameInput = By.cssSelector("input[data-qa='name']");
    private final By emailInput = By.cssSelector("input[data-qa='email']");
    private final By passwordInput = By.cssSelector("input[data-qa='password']");
    private final By daySelect = By.cssSelector("select[data-qa='days']");
    private final By monthSelect = By.cssSelector("select[data-qa='months']");
    private final By yearSelect = By.cssSelector("select[data-qa='years']");
    private final By newsletterCheckbox = By.id("newsletter");
    private final By specialOffersCheckbox = By.id("optin");

    private final By firstNameInput = By.cssSelector("input[data-qa='first_name']");
    private final By lastNameInput = By.cssSelector("input[data-qa='last_name']");
    private final By companyInput = By.cssSelector("input[data-qa='company']");
    private final By addressInput = By.cssSelector("input[data-qa='address']");
    private final By address2Input = By.cssSelector("input[data-qa='address2']");
    private final By countrySelect = By.cssSelector("select[data-qa='country']");
    private final By stateInput = By.cssSelector("input[data-qa='state']");
    private final By cityInput = By.cssSelector("input[data-qa='city']");
    private final By zipcodeInput = By.cssSelector("input[data-qa='zipcode']");
    private final By mobileNumberInput = By.cssSelector("input[data-qa='mobile_number']");

    private final By createAccountButton = By.cssSelector("button[data-qa='create-account']");

    @Step("Check that 'Enter Account Information' is visible")
    public boolean isEnterAccountInformationVisible() {
        return isDisplayed(enterAccountInformationHeading);
    }

    @Step("Read prefilled email")
    public String getPrefilledEmail() {
        return waitForVisible(emailInput).getDomProperty("value");
    }

    // The email is prefilled from the previous step, so it is not filled here.
    @Step("Fill account information")
    public SignupPage fillAccountInformation(User user) {
        check("Mrs".equalsIgnoreCase(user.title()) ? titleMrs : titleMr);
        type(nameInput, user.name());
        type(passwordInput, user.password());
        selectByValue(daySelect, user.birthDay());
        selectByValue(monthSelect, user.birthMonth());
        selectByValue(yearSelect, user.birthYear());
        return this;
    }

    @Step("Subscribe to the newsletter")
    public SignupPage subscribeToNewsletter() {
        check(newsletterCheckbox);
        return this;
    }

    @Step("Accept special offers from partners")
    public SignupPage acceptSpecialOffers() {
        check(specialOffersCheckbox);
        return this;
    }

    @Step("Fill address information")
    public SignupPage fillAddressInformation(User user) {
        type(firstNameInput, user.firstName());
        type(lastNameInput, user.lastName());
        type(companyInput, user.company());
        type(addressInput, user.address());
        type(address2Input, user.address2());
        selectByVisibleText(countrySelect, user.country());
        type(stateInput, user.state());
        type(cityInput, user.city());
        type(zipcodeInput, user.zipcode());
        type(mobileNumberInput, user.mobileNumber());
        return this;
    }

    @Step("Click 'Create Account'")
    public AccountCreatedPage clickCreateAccount() {
        click(createAccountButton);
        return new AccountCreatedPage();
    }
}