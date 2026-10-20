package data;

import config.ConfigReader;
import io.qameta.allure.Step;
import models.User;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Creates and deletes accounts through the site's API, so tests that are not about
 * registration can start with a user without going through the signup UI.
 */
public final class UserApiClient {

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private static final int MAX_ATTEMPTS = 3;
    // Tolerates any whitespace around the colon, so the check does not depend on JSON formatting.
    private static final Pattern CREATED_RESPONSE = Pattern.compile("\"responseCode\"\\s*:\\s*201");

    private UserApiClient() {
    }

    @Step("Create account via API")
    public static void createAccount(User user) {
        Map<String, String> form = new LinkedHashMap<>();
        form.put("name", user.name());
        form.put("email", user.email());
        form.put("password", user.password());
        form.put("title", user.title());
        form.put("birth_date", user.birthDay());
        form.put("birth_month", user.birthMonth());
        form.put("birth_year", user.birthYear());
        form.put("firstname", user.firstName());
        form.put("lastname", user.lastName());
        form.put("company", user.company());
        form.put("address1", user.address());
        form.put("address2", user.address2());
        form.put("country", user.country());
        form.put("zipcode", user.zipcode());
        form.put("state", user.state());
        form.put("city", user.city());
        form.put("mobile_number", user.mobileNumber());

        String body = send("POST", "/api/createAccount", form);
        // The API returns HTTP 200 even on failure, so the outcome is read from the JSON body.
        if (!CREATED_RESPONSE.matcher(body).find()) {
            throw new IllegalStateException("Could not create test user " + user.email() + ": " + body);
        }
    }

    /**
     * Deletes the account if it still exists. Connection errors are logged, not thrown, and the
     * response body is not checked, because the account has usually been deleted through the UI already.
     */
    @Step("Delete account via API")
    public static void deleteAccountQuietly(User user) {
        try {
            send("DELETE", "/api/deleteAccount", Map.of("email", user.email(), "password", user.password()));
        } catch (RuntimeException e) {
            System.err.println("Cleanup failed for " + user.email() + ": " + e.getMessage());
        }
    }

    private static String send(String method, String path, Map<String, String> form) {
        String encoded = form.entrySet().stream()
                .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "="
                        + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
        HttpRequest request = HttpRequest.newBuilder(URI.create(ConfigReader.get("baseUrl") + path))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .method(method, HttpRequest.BodyPublishers.ofString(encoded))
                .build();
        for (int attempt = 1; ; attempt++) {
            try {
                return HTTP.send(request, HttpResponse.BodyHandlers.ofString()).body();
            } catch (ConnectException | HttpConnectTimeoutException e) {
                // The connection never opened, so the server received nothing and a retry cannot duplicate data.
                if (attempt == MAX_ATTEMPTS) {
                    throw new IllegalStateException(method + " " + path + " failed: could not connect after "
                            + MAX_ATTEMPTS + " attempts", e);
                }
                pause(attempt * 2000L, method, path);
            } catch (IOException e) {
                throw new IllegalStateException(method + " " + path + " failed", e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(method + " " + path + " was interrupted", e);
            }
        }
    }

    private static void pause(long millis, String method, String path) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(method + " " + path + " was interrupted", e);
        }
    }
}