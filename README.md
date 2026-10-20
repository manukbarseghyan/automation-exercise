# Automation Exercise UI Tests

Selenium and TestNG Page Object Model framework for https://automationexercise.com.

## Prerequisites

- JDK 21
- Maven 3.8+
- Chrome, Edge or Firefox installed
- Internet access to automationexercise.com

Browser drivers are resolved automatically by Selenium Manager, which ships with `selenium-java`.

## Running tests

Run the full suite (defined in `testng.xml`):

```bash
mvn test -Denv=staging
```

`env` selects `src/test/resources/config-<env>.properties` and defaults to `staging`.

### Changing browser or headless mode

Any config key can be overridden with a system property of the same name:

```bash
mvn test -Dbrowser=firefox
mvn test -Dbrowser=edge
mvn test -DexplicitWaitSeconds=20
```

Supported browsers: `chrome`, `edge`, `firefox`.

### Running a single test class

```bash
mvn test -Dtest=SearchProductTest
```

## Results

| Output                      | Location                   |
|-----------------------------|----------------------------|
| TestNG / Surefire reports   | `target/surefire-reports/` |
| Screenshots of failed tests | `target/screenshots/`      |
| Allure raw results          | `target/allure-results/`   |

To view the Allure report (requires the [Allure CLI](https://allurereport.org/docs/install/)):

```bash
allure serve target/allure-results
```

Failure screenshots are also attached to the corresponding test in the Allure report.

## Project structure

```
src/main/java
  config/   ConfigReader - environment config with system-property overrides
  driver/   DriverManager - thread-local WebDriver lifecycle
  models/   User - test data model shared by pages and tests
  pages/    Page objects; locators and actions only, no assertions
src/test/java
  base/     BaseTest - opens the app, screenshots on failure, quits the driver
  data/     Unique test data and API helpers for test preconditions
  tests/    Test classes, one per test case
src/test/resources
  config-staging.properties
testng.xml  Suite definition run by Maven Surefire
```

## Automated test cases

| ID | Test case                                                           | Class                               |
|----|---------------------------------------------------------------------|-------------------------------------|
| 1  | Register User                                                       | `RegisterUserTest`                  |
| 9  | Search Product                                                      | `SearchProductTest`                 |
| 16 | Place Order: Login before Checkout                                  | `PlaceOrderLoginBeforeCheckoutTest` |
| 25 | Verify Scroll Up using 'Arrow' button and Scroll Down functionality | `ScrollUpWithArrowTest`             |

Each test creates its own unique data, so tests are independent and can run in any order.

## Architecture

- **Test layer** (`tests/`): scenarios and assertions, one class per test case. Each step of the test case is
  wrapped in `Allure.step("N. <step text from the test case>", ...)`, so the Allure report lists the steps exactly as
  the test case describes them. Steps 1-2 (launch browser, navigate to URL) run in `BaseTest`.
- **Page layer** (`pages/`): locators and actions for each page. No assertions.
- **Infrastructure layer** (`config/`, `driver/`, `base/`, `data/`): configuration, browser lifecycle, setup/teardown
  and test data.
