import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ObjectDetailsTest {

    private static final String BASE_URL = "https://www.rijksmuseum.nl/api/nl/collection";
    private static final String API_KEY = "0fiuZFh4"; // A valid API key should be provided here

    // ✅ 1. Test retrieving object details with a valid object ID
    @Test
    public void testRetrieveObjectDetailsWithValidId() {
        given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .when()
                .get(BASE_URL + "/SK-C-5") // Valid object ID
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("artObject.objectNumber", equalTo("SK-C-5"))
                .body("artObject.title", equalTo("De Nachtwacht"))
                .body("artObject.principalOrFirstMaker", equalTo("Rembrandt van Rijn"))
                .body("artObject.webImage.url", notNullValue()); // Image URL must be present
    }

    // ✅ 2. Test that API should return 404 for an invalid object ID
    @Test
    public void testRetrieveObjectDetailsWithInvalidId() {
        given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .when()
                .get(BASE_URL + "/INVALID_ID") // Invalid object ID
                .then()
                .statusCode(404); // Should return 404 Not Found
    }

    // ✅ 3. Test that API should return 401 Unauthorized when API Key is missing
    @Test
    public void testRetrieveObjectDetailsWithoutApiKey() {
        given()
                .queryParam("format", "json") // Request without API key
                .when()
                .get(BASE_URL + "/SK-C-5")
                .then()
                .statusCode(401); // Should return 401 Unauthorized
    }

    // ✅ 4. Test invalid format usage, expecting 400 Bad Request
    @Test
    public void testRetrieveObjectDetailsWithInvalidFormat() {
        given()
                .queryParam("key", API_KEY)
                .queryParam("format", "invalidFormat") // Unsupported format
                .when()
                .get(BASE_URL + "/SK-C-5")
                .then()
                .statusCode(400); // Should return 400 Bad Request
    }

    // ✅ 5. Test response time (should be less than 2 seconds)
    @Test
    public void testResponseTimeForObjectDetails() {
        given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .when()
                .get(BASE_URL + "/SK-C-5")
                .then()
                .statusCode(200)
                .time(lessThan(2000L)); // Response should be within 2 seconds
    }

    // ✅ 6. Test that the image URL is not empty
    @Test
    public void testWebImageIsPresent() {
        given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .when()
                .get(BASE_URL + "/SK-C-5")
                .then()
                .statusCode(200)
                .body("artObject.webImage.url", notNullValue()); // Image URL must not be null
    }

    // ✅ 7. Test the validity of the date information in object details
    @Test
    public void testObjectHasValidDate() {
        given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .when()
                .get(BASE_URL + "/SK-C-5")
                .then()
                .statusCode(200)
                .body("artObject.dating.presentingDate", equalTo("1642")) // Expected year
                .body("artObject.dating.period", equalTo(17)); // Should belong to the 17th century
    }
}
