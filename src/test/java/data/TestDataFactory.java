package data;

import models.User;

import java.util.UUID;

/**
 * Creates test users with unique names and emails so runs do not depend on existing accounts.
 */
public final class TestDataFactory {

    private TestDataFactory() {
    }

    public static User uniqueUser() {
        String id = UUID.randomUUID().toString().substring(0, 8);
        return new User(
                "Mr", "QA User " + id, "qa.user." + id + "@example.com", "Passw0rd!" + id,
                "17", "10", "1990",
                "John", "Doe", "IDT QA", "1 Test Street", "Apt 2",
                "Canada", "Ontario", "Toronto", "M5V 2T6", "+14165550123");
    }
}