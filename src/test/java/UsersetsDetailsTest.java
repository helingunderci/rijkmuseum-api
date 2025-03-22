import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UsersetsDetailsTest {

    private static final String BASE_URL = "https://www.rijksmuseum.nl/api";
    private static final String API_KEY = "0fiuZFh4";

    @Test
    public void testUsersetDetailsReturns200AndValidJson() {
        RestAssured.useRelaxedHTTPSValidation(); // To avoid SSL errors

        //Valid user set ID
        Response listResponse = given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .when()
                .get(BASE_URL + "/nl/usersets")
                .then()
                .statusCode(200)
                .extract().response();

        String validUsersetId = listResponse.jsonPath().getString("userSets[0].id");

        Response detailResponse = given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .when()
                .get(BASE_URL + "/nl/usersets/" + validUsersetId);

        detailResponse.then()
                .statusCode(200)
                .contentType("application/json")
                .body("userSet", notNullValue())
                .body("userSet.id", equalTo(validUsersetId))
                .body("userSet.links.web", notNullValue())
                .body("userSet.name", notNullValue())
                .body("userSet.count", greaterThanOrEqualTo(0));
    }
}
