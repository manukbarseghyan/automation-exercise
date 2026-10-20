package tests;

import base.BaseTest;
import data.TestDataFactory;
import data.UserApiClient;
import models.User;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pages.AccountCreatedPage;
import pages.AccountDeletedPage;
import pages.HomePage;
import pages.SignupLoginPage;
import pages.SignupPage;

import static io.qameta.allure.Allure.step;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class RegisterUserTest extends BaseTest {

    private User user;

    // Steps 1-2 (launch browser, navigate to URL) run in BaseTest.openApplication().
    @Test(description = "Test Case 1: Register User")
    public void registerUser() {
        User user = TestDataFactory.uniqueUser();
        this.user = user;
        HomePage homePage = new HomePage();
        step("3. Verify that home page is visible successfully", () ->
                assertTrue(homePage.isHomePageVisible(), "Home page should be visible after opening the base URL"));

        SignupLoginPage signupLoginPage = step("4. Click on 'Signup / Login' button", () ->
                homePage.header().clickSignupLogin());

        step("5. Verify 'New User Signup!' is visible", () ->
                assertTrue(signupLoginPage.isNewUserSignupVisible(), "'New User Signup!' should be visible"));

        // signup() fills both fields and submits, so steps 6 and 7 are one action.
        SignupPage signupPage = step("6-7. Enter name and email address, click 'Signup' button", () ->
                signupLoginPage.signup(user.name(), user.email()));

        step("8. Verify that 'ENTER ACCOUNT INFORMATION' is visible", () -> {
            assertTrue(signupPage.isEnterAccountInformationVisible(), "'ENTER ACCOUNT INFORMATION' should be visible");
            // The email field is prefilled and read-only, so it is verified rather than filled.
            assertEquals(signupPage.getPrefilledEmail(), user.email(),
                    "Email from the signup form should be carried over to the account form");
        });

        step("9. Fill details: Title, Name, Email, Password, Date of birth", () ->
                signupPage.fillAccountInformation(user));

        step("10. Select checkbox 'Sign up for our newsletter!'", signupPage::subscribeToNewsletter);

        step("11. Select checkbox 'Receive special offers from our partners!'", signupPage::acceptSpecialOffers);

        step("12. Fill details: First name, Last name, Company, Address, Address2, Country, State, City, Zipcode, "
                + "Mobile Number", () -> signupPage.fillAddressInformation(user));

        AccountCreatedPage accountCreatedPage = step("13. Click 'Create Account button'", signupPage::clickCreateAccount);

        step("14. Verify that 'ACCOUNT CREATED!' is visible", () ->
                assertEquals(accountCreatedPage.getHeadingText(), "ACCOUNT CREATED!",
                        "Account creation confirmation should be shown"));

        HomePage loggedInHomePage = step("15. Click 'Continue' button", accountCreatedPage::clickContinue);

        step("16. Verify that 'Logged in as username' is visible", () ->
                assertEquals(loggedInHomePage.header().getLoggedInAsText(), "Logged in as " + user.name(),
                        "Header should show the newly registered user"));

        AccountDeletedPage accountDeletedPage = step("17. Click 'Delete Account' button", () ->
                loggedInHomePage.header().clickDeleteAccount());

        step("18. Verify that 'ACCOUNT DELETED!' is visible and click 'Continue' button", () -> {
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