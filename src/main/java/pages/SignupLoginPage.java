package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class SignupLoginPage extends BasePage {

    private final By loginEmailInput = By.cssSelector("input[data-qa='login-email']");
    private final By loginPasswordInput = By.cssSelector("input[data-qa='login-password']");
    private final By loginButton = By.cssSelector("button[data-qa='login-button']");

    private final By newUserSignupHeading =
            By.xpath("//div[@class='signup-form']/h2[normalize-space(.)='New User Signup!']");
    private final By signupNameInput = By.cssSelector("input[data-qa='signup-name']");
    private final By signupEmailInput = By.cssSelector("input[data-qa='signup-email']");
    private final By signupButton = By.cssSelector("button[data-qa='signup-button']");

    @Step("Check that 'New User Signup!' is visible")
    public boolean isNewUserSignupVisible() {
        return isDisplayed(newUserSignupHeading);
    }

    // Returns HomePage only for a successful login. A failed login keeps the user on this page,
    // so tests for invalid credentials should check the page state instead of calling this method.
    @Step("Log in as '{email}'")
    public HomePage login(String email, String password) {
        type(loginEmailInput, email);
        type(loginPasswordInput, password);
        click(loginButton);
        return new HomePage();
    }

    @Step("Start signup as '{name}' (email '{email}')")
    public SignupPage signup(String name, String email) {
        type(signupNameInput, name);
        type(signupEmailInput, email);
        click(signupButton);
        return new SignupPage();
    }
}