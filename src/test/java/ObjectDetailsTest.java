import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class ObjectDetailsTest extends BaseTest {

    private static final String BASE_URL = BASE_API_URL + "nl/collection";
    //Test retrieving object details with a valid object ID
    @Test
    public void testRetrieveObjectDetailsWithValidId() {
        Response response = given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .when()
                .pathParam("objectNumber", "SK-C-5")
                .get(BASE_URL + "/{objectNumber}");

        response.then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("artObject.objectNumber", equalTo("SK-C-5"))
                .body("artObject.title", equalTo("De Nachtwacht"))
                .body("artObject.principalOrFirstMaker", equalTo("Rembrandt van Rijn"))
                .body("artObject.webImage.url", notNullValue());
    }

    // Test that API should return 404 for an invalid object ID
    @Disabled ("Bug reported for this")
    @Test
    public void testRetrieveObjectDetailsWithInvalidId() {
        given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .when()
                .get(BASE_URL + "/aaaAAAaaa123") // Invalid object ID
                .then()
                .statusCode(404); // Should return 404 Not Found
    }

    // Test that API should return 401 Unauthorized when API Key is missing
    @Test
    public void testRetrieveObjectDetailsWithoutApiKey() {
        given()
                .queryParam("format", "json") // Request without API key
                .when()
                .pathParam("objectNumber", "SK-C-5")
                .get(BASE_URL + "/{objectNumber}")
                .then()
                .statusCode(401); // Should return 401 Unauthorized
    }

    //Test invalid format usage
    @Test
    public void testRetrieveObjectDetailsWithInvalidFormat() {
        given()
                .queryParam("key", API_KEY)
                .queryParam("format", "invalidFormat") // Unsupported format
                .when()
                .get(BASE_URL + "/SK-C-5")
                .then()
                .statusCode(404);
    }

    //Test that the image URL is not empty
    @Test
    public void testArtObjectsHaveWebImageFieldPresent() {
        Response response = given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .when()
                .get(BASE_URL);

        assertEquals(200, response.getStatusCode(), "Status code is not 200");

        List<Map<String, Object>> artObjects = response.jsonPath().getList("artObjects");

        boolean allHaveWebImageField = artObjects.stream()
                .allMatch(obj -> ((Map<String, Object>) obj).containsKey("webImage"));

        assertTrue(allHaveWebImageField, "Some artworks are missing the 'webImage' field");
    }
}