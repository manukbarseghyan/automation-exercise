package models;

/**
 * Test user data used by the signup and checkout flows.
 * Lives in src/main so page objects can use it without test-scope dependencies.
 * <p>
 * Birth fields hold the option values of the signup form:
 * day "1"-"31", month "1"-"12", year such as "1990".
 */
public record User(
        String title,          // Radio value: "Mr" or "Mrs"
        String name,           // Full name shown on the signup form
        String email,
        String password,       // Fake test value, not a real credential
        String birthDay,
        String birthMonth,
        String birthYear,
        String firstName,      // Address fields below
        String lastName,
        String company,
        String address,
        String address2,
        String country,        // Must match a Country dropdown option text exactly
        String state,
        String city,
        String zipcode,
        String mobileNumber) {
}