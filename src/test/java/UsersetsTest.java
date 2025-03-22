import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class UsersetsTest extends BaseTest {

    private static final String USERSET_ENDPOINT = BASE_API_URL + "/nl/usersets";
    private String validUsersetId;

    @BeforeEach
    public void setup() {
        RestAssured.useRelaxedHTTPSValidation(); // To avoid SSL errors
        validUsersetId = fetchValidUsersetId();
    }

    private String fetchValidUsersetId() {
        Response response = given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .when()
                .get(USERSET_ENDPOINT)
                .then()
                .statusCode(200)
                .extract().response();
        return response.jsonPath().getString("userSets[0].id");
    }

    @Test
    public void testUsersetDetailsReturnsValidJson() {
        // Ensure validUsersetId is not null or empty
        assertNotNull(validUsersetId, "Could not get a valid Userset ID!");

        Response response = given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .when()
                .get(BASE_API_URL + "/nl/usersets/" + validUsersetId);

        // Validate the response fields
        response.then()
                .statusCode(200)
                .contentType("application/json")
                .body("userSet", notNullValue())
                .body("userSet.id", equalTo(validUsersetId))
                .body("userSet.links.web", notNullValue())
                .body("userSet.name", notNullValue())
                .body("userSet.count", greaterThanOrEqualTo(0)); // Collection count must be 0 or more
    }

    @Test
    public void testUsersetDetailsWithInvalidApiKey() {
        given()
                .queryParam("key", "invalid_api_key") // Geçersiz API key
                .queryParam("format", "json")
                .when()
                .get(BASE_API_URL + "/nl/usersets/" + validUsersetId)
                .then()
                .statusCode(401);
    }

    @Test
    public void testUsersetListReturnsValidResponse() {
        given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .when()
                .get(BASE_API_URL + "/nl/usersets")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("userSets", not(empty())) // Ensure user sets array is not empty
                .body("userSets[0].id", notNullValue()) // Validate the first user's ID is present
                .body("userSets[0].name", notNullValue()); // Validate the first user's name is present
    }

    @Test
    public void testPaginationReturnsDifferentResults() {
        // Fetch the first page of user sets
        Response firstPage = given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .queryParam("page", 0)
                .when()
                .get(BASE_API_URL + "/nl/usersets")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract().response();

        // Fetch the second page of user sets
        Response secondPage = given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .queryParam("page", 1)
                .when()
                .get(BASE_API_URL + "/nl/usersets")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract().response();

        // Extract the first item's ID from each page
        String firstPageFirstItem = firstPage.jsonPath().getString("userSets[0].id");
        String secondPageFirstItem = secondPage.jsonPath().getString("userSets[0].id");

        // Assert that the first items on each page are different
        if (firstPageFirstItem != null && secondPageFirstItem != null) {
            assertNotEquals(firstPageFirstItem, secondPageFirstItem, "Pagination is not working properly, same results!");
        } else {
            fail("Pagination test failed - insufficient data on one or both pages");
        }
    }

    @Disabled("Bug reported, test temporarily disabled")
    @Test
    public void testPageSizeLimitExceeds10000() {
        int page = 200;   // A high page number
        int pageSize = 100; // Maximum supported page size
        int calculatedLimit = page * pageSize;

        // Skip the test if calculated limit does not exceed 10,000
        assumeTrue(calculatedLimit > 10_000, "Test is skipped because the calculated limit does not exceed 10,000");

        // Perform the API request and validate the response when the limit exceeds 10,000
        given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .queryParam("page", page)
                .queryParam("pageSize", pageSize)
                .when()
                .get(BASE_API_URL + "/nl/usersets")
                .then()
                .statusCode(400) // Expected: Bad Request or relevant error code
                .body("error", containsString("page * pageSize cannot exceed 10,000"));
    }
}
