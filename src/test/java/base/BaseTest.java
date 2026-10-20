package base;

import config.ConfigReader;
import driver.DriverManager;
import io.qameta.allure.Allure;
import io.qameta.allure.AttachmentOptions;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Opens the application before each test, captures a screenshot on failure,
 * and always quits the browser afterwards.
 */
public abstract class BaseTest {

    private static final Path SCREENSHOT_DIR = Path.of("target", "screenshots");
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    // Steps 1-2 run in every test, so they appear in the Allure "Set up" section.
    @BeforeMethod
    public void openApplication() {
        WebDriver driver = Allure.step("1. Launch browser", DriverManager::getDriver);
        String baseUrl = ConfigReader.get("baseUrl");
        Allure.step("2. Navigate to url '" + baseUrl + "'", () -> driver.get(baseUrl));
    }

    // alwaysRun ensures the browser is closed even when setup fails.
    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        try {
            if (result.getStatus() == ITestResult.FAILURE && DriverManager.hasDriver()) {
                captureScreenshot(result.getMethod().getMethodName());
            }
        } finally {
            DriverManager.quitDriver();
        }
    }

    private void captureScreenshot(String testName) {
        try {
            byte[] png = ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BYTES);
            Files.createDirectories(SCREENSHOT_DIR);
            Files.write(SCREENSHOT_DIR.resolve(testName + "_" + LocalDateTime.now().format(TIMESTAMP) + ".png"), png);
            Allure.attachment("Screenshot on failure", "image/png", new ByteArrayInputStream(png),
                    AttachmentOptions.withFileExtension("png"));
        } catch (IOException | RuntimeException e) {
            // A screenshot problem must not hide the real test failure, so only log it.
            System.err.println("Could not capture screenshot for " + testName + ": " + e.getMessage());
        }
    }
}