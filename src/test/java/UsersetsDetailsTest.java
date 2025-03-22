import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UsersetsDetailsTest extends BaseTest {

    @Test
    public void testUsersetDetailsReturnsValidJson() {
        RestAssured.useRelaxedHTTPSValidation(); // To avoid SSL errors

        //Valid user set ID
        Response listResponse = given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .when()
                .get(BASE_API_URL + "/nl/usersets")
                .then()
                .statusCode(200)
                .extract().response();

        String validUsersetId = listResponse.jsonPath().getString("userSets[0].id");

        Response detailResponse = given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .when()
                .get(BASE_API_URL + "/nl/usersets/" + validUsersetId);

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
