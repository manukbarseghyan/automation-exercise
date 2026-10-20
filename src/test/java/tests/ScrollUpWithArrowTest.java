package tests;

import base.BaseTest;
import org.testng.annotations.Test;
import pages.HomePage;

import static io.qameta.allure.Allure.step;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class ScrollUpWithArrowTest extends BaseTest {

    // Steps 1-2 (launch browser, navigate to URL) run in BaseTest.openApplication().
    @Test(description = "Test Case 25: Verify Scroll Up using 'Arrow' button and Scroll Down functionality")
    public void scrollUpWithArrowAndScrollDown() {
        HomePage homePage = new HomePage();
        step("3. Verify that home page is visible successfully", () ->
                assertTrue(homePage.isHomePageVisible(), "Home page should be visible after opening the base URL"));

        step("4. Scroll down page to bottom", () -> {
            homePage.scrollToBottom();
            homePage.waitUntilScrolledAwayFromTop();
            assertFalse(homePage.isScrolledToTop(), "Page should be scrolled away from the top");
        });

        step("5. Verify 'SUBSCRIPTION' is visible", () ->
                assertTrue(homePage.isSubscriptionVisible(), "'SUBSCRIPTION' should be visible at the bottom of the page"));

        step("6. Click on arrow at bottom right side to move upward", homePage::clickScrollUpArrow);

        step("7. Verify that page is scrolled up and 'Full-Fledged practice website for Automation Engineers' "
                + "text is visible on screen", () -> {
            assertTrue(homePage.isScrolledToTop(), "Page should be scrolled back to the top");
            assertTrue(homePage.isHeroTextVisibleOnScreen(),
                    "'Full-Fledged practice website for Automation Engineers' should be visible on screen");
        });
    }
}