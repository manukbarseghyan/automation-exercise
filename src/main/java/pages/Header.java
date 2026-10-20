package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

/**
 * Navigation bar shared by all pages. Link locators are scoped to .navbar-nav because the same
 * hrefs appear elsewhere on the page, for example the "View Cart" link in the add-to-cart modal.
 */
public class Header extends BasePage {

    private final By productsLink = By.cssSelector(".navbar-nav a[href='/products']");
    private final By cartLink = By.cssSelector(".navbar-nav a[href='/view_cart']");
    private final By signupLoginLink = By.cssSelector(".navbar-nav a[href='/login']");
    private final By deleteAccountLink = By.cssSelector(".navbar-nav a[href='/delete_account']");
    // The username sits in a nested <b>, so match on the normalized full text.
    private final By loggedInAsLink = By.xpath("//a[contains(normalize-space(), 'Logged in as')]");

    @Step("Open 'Products' from the header")
    public ProductsPage clickProducts() {
        click(productsLink);
        return new ProductsPage();
    }

    @Step("Open 'Cart' from the header")
    public CartPage clickCart() {
        click(cartLink);
        return new CartPage();
    }

    @Step("Open 'Signup / Login' from the header")
    public SignupLoginPage clickSignupLogin() {
        click(signupLoginLink);
        return new SignupLoginPage();
    }

    @Step("Click 'Delete Account' in the header")
    public AccountDeletedPage clickDeleteAccount() {
        click(deleteAccountLink);
        return new AccountDeletedPage();
    }

    @Step("Read 'Logged in as' text")
    public String getLoggedInAsText() {
        return getText(loggedInAsLink);
    }
}