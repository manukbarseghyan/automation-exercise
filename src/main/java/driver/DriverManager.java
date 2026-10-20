package driver;

import config.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * Holds one WebDriver per thread so parallel tests never share a browser.
 */
public final class DriverManager {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private static final String HEADLESS_WINDOW_SIZE = "--window-size=1920,1080";

    // Ad and consent overlays intercept clicks. Mapping their hosts to localhost
    // keeps them from loading, which is more stable than closing them in every test.
    private static final String CHROMIUM_AD_HOST_RULES = "--host-resolver-rules="
            + "MAP *.googlesyndication.com 127.0.0.1,"
            + "MAP *.doubleclick.net 127.0.0.1,"
            + "MAP *.googleadservices.com 127.0.0.1,"
            + "MAP fundingchoicesmessages.google.com 127.0.0.1";

    // Firefox does not support host-resolver rules, and its DNS overrides take no wildcards.
    private static final String FIREFOX_AD_HOSTS = String.join(",",
            "pagead2.googlesyndication.com",
            "tpc.googlesyndication.com",
            "googleads.g.doubleclick.net",
            "securepubads.g.doubleclick.net",
            "stats.g.doubleclick.net",
            "ad.doubleclick.net",
            "www.googleadservices.com",
            "partner.googleadservices.com",
            "www.googletagservices.com",
            "fundingchoicesmessages.google.com");

    private DriverManager() {
    }

    public static WebDriver getDriver() {
        if (DRIVER.get() == null) {
            DRIVER.set(createDriver());
        }
        return DRIVER.get();
    }

    public static boolean hasDriver() {
        return DRIVER.get() != null;
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                // Pool threads are reused, so remove the reference to avoid handing out a closed session.
                DRIVER.remove();
            }
        }
    }

    private static WebDriver createDriver() {
        String browser = ConfigReader.get("browser").toLowerCase();
        boolean headless = ConfigReader.getBoolean("headless");

        WebDriver driver = switch (browser) {
            case "chrome" -> {
                ChromeOptions options = new ChromeOptions();
                options.addArguments(CHROMIUM_AD_HOST_RULES);
                applyChromiumHeadless(options, headless);
                yield new ChromeDriver(options);
            }
            case "edge" -> {
                EdgeOptions options = new EdgeOptions();
                options.addArguments(CHROMIUM_AD_HOST_RULES);
                applyChromiumHeadless(options, headless);
                yield new EdgeDriver(options);
            }
            case "firefox" -> {
                FirefoxOptions options = new FirefoxOptions();
                options.addPreference("network.dns.localDomains", FIREFOX_AD_HOSTS);
                options.addPreference("privacy.trackingprotection.enabled", true);
                options.addPreference("browser.contentblocking.category", "strict");
                if (headless) {
                    options.addArguments("-headless", "--width=1920", "--height=1080");
                }
                yield new FirefoxDriver(options);
            }
            default -> throw new IllegalArgumentException(
                    "Unsupported browser '" + browser + "'. Use chrome, edge or firefox.");
        };

        if (!headless) {
            driver.manage().window().maximize();
        }
        return driver;
    }

    private static void applyChromiumHeadless(ChromiumOptions<?> options, boolean headless) {
        if (headless) {
            options.addArguments("--headless", HEADLESS_WINDOW_SIZE);
        }
    }
}