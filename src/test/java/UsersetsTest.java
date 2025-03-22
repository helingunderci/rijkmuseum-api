import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UsersetsTest extends BaseTest {

    private String validUsersetId;

    @BeforeEach
    public void setup() {
        RestAssured.useRelaxedHTTPSValidation(); //To avoid SSL errors

        //Call UsersetAPI and get valid Userset ID
        Response listResponse = given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .when()
                .get(BASE_API_URL + "/nl/usersets")
                .then()
                .statusCode(200)
                .extract().response();

        //First UserID
        validUsersetId = listResponse.jsonPath().getString("userSets[0].id");
    }

    @Test
    public void testUsersetDetailsReturns200AndValidJson() {
        //If validUsersetId is null, the test should not fail
        if (validUsersetId == null || validUsersetId.isEmpty()) {
            throw new IllegalStateException("Could not get a valid Userset ID!");
        }

        Response response = given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .when()
                .get(BASE_API_URL + "/nl/usersets/" + validUsersetId);

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
    public void testUsersetDetailsReturnsValidJson() {
        if (validUsersetId == null || validUsersetId.isEmpty()) {
            throw new IllegalStateException("Geçerli bir Userset ID alınamadı!");
        }

        given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .when()
                .get(BASE_API_URL + "/nl/usersets/" + validUsersetId)
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("userSet", notNullValue())
                .body("userSet.id", equalTo(validUsersetId))
                .body("userSet.links.web", notNullValue())
                .body("userSet.name", notNullValue())
                .body("userSet.count", greaterThanOrEqualTo(0)); // Number of collections must be 0 or more
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
                .body("userSets", not(empty())) // User sets must not be empty
                .body("userSets[0].id", notNullValue()) // First userset ID must be filled
                .body("userSets[0].name", notNullValue()); // First userset name must be filled
    }

    @Test
    public void testPaginationReturnsDifferentResults() {
        Response firstPage = given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .queryParam("page", 0)
                .when()
                .get(BASE_API_URL + "/nl/usersets")
                .then()
                .extract().response();

        Response secondPage = given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .queryParam("page", 1)
                .when()
                .get(BASE_API_URL + "/nl/usersets")
                .then()
                .extract().response();

        String firstPageFirstItem = firstPage.jsonPath().getString("userSets[0].id");
        String secondPageFirstItem = secondPage.jsonPath().getString("userSets[0].id");

        assertNotEquals(firstPageFirstItem, secondPageFirstItem, "Pagination not working properly, same results!");
    }

    @Disabled //Bug reported
    @Test
    public void testPageSizeLimitExceeds10000() {
        int page = 200;   // A high page number
        int pageSize = 100; // Maximum supported pageSize
        int calculatedLimit = page * pageSize;

        if (calculatedLimit > 10_000) {
            given()
                    .queryParam("key", API_KEY)
                    .queryParam("format", "json")
                    .queryParam("page", page)
                    .queryParam("pageSize", pageSize)
                    .when()
                    .get(BASE_API_URL + "/nl/usersets")
                    .then()
                    .statusCode(400) // Beklenen: Bad Request veya ilgili hata kodu
                    .body("error", containsString("page * pageSize cannot exceed 10,000"));
        } else {
            System.out.println("Skipping test: page * pageSize does not exceed limit.");
        }
    }
}
