import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UsersetsDetailsTest extends BaseTest {

    public static final String BASE_URL_USERSETS = BASE_API_URL + "nl/usersets";

    @Test
    public void testUsersetDetailsReturnsValidJson() {
        RestAssured.useRelaxedHTTPSValidation(); // To avoid SSL errors

        // Fetch a valid user set ID
        Response listResponse = given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .when()
                .get(BASE_URL_USERSETS)
                .then()
                .statusCode(200)
                .extract().response();

        String validUsersetId = listResponse.jsonPath().getString("userSets[0].id");

        // Validate the fetched user set ID
        Response detailResponse = given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .when()
                .get(BASE_URL_USERSETS + "/" + validUsersetId);

        detailResponse.then()
                .statusCode(200)
                .contentType("application/json")
                .body("userSet", notNullValue())
                .body("userSet.id", equalTo(validUsersetId))
                .body("userSet.links.web", notNullValue())
                .body("userSet.name", notNullValue())
                .body("userSet.count", greaterThanOrEqualTo(0));
    }

    //Invalid user set id
    @Test
    public void testUsersetDetailsWithInvalidIdReturnsError() {
        final String invalidUsersetId = "INVALID_ID_123456";

        given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .when()
                .get(BASE_URL_USERSETS + "/" + invalidUsersetId)
                .then()
                .statusCode(anyOf(is(403), is(404)));
    }

    //Call without API key
    @Test
    public void testUsersetDetailsWithoutApiKey() {
        final String randomUsersetId = "123"; // random ID

        given()
                .queryParam("format", "json")
                .when()
                .get(BASE_URL_USERSETS + "/" + randomUsersetId)
                .then()
                .statusCode(401);
    }

    //UsersetList is not empty check
    @Test
    public void testUsersetsListIsNotEmpty() {
        Response response = given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .when()
                .get(BASE_URL_USERSETS)
                .then()
                .statusCode(200)
                .extract().response();

        int usersetCount = response.jsonPath().getList("userSets").size();
        assertTrue(usersetCount > 0, "userSets list is empty");
    }
}
