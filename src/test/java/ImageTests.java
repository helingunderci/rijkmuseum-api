import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ImageTests extends BaseTest {

    private static final String BASE_URL = BASE_API_URL + "en/collection/{object-number}/tiles";

    //API should return 200 OK for a valid request
    @Test
    public void testRetrieveTilesWithValidApiKey() {
        given()
                .queryParam("key", API_KEY)
                .pathParam("object-number", "SK-C-5")
                .when()
                .get(BASE_URL)
                .then()
                .statusCode(200);
    }

    //API should return 401 Unauthorized if API Key is missing
    @Test
    public void testRetrieveTilesWithoutApiKey() {
        given()
                .pathParam("object-number", "SK-C-5")
                .when()
                .get(BASE_URL)
                .then()
                .statusCode(401); // 401 Unauthorized expected
    }

    //API should return 401 Unauthorized if API Key is invalid
    @Test
    public void testRetrieveTilesWithInvalidApiKey() {
        given()
                .queryParam("key", "aaaaaa")
                .pathParam("object-number", "AK-MAK-187")
                .when()
                .get(BASE_URL)
                .then()
                .statusCode(401); // 401 Unauthorized expected
    }

    //API should return 403 Forbidden or 404 Not Found for an invalid object ID
    @Test
    public void testRetrieveTilesWithInvalidObjectId() {
        given()
                .queryParam("key", API_KEY)
                .pathParam("object-number", "thisisinvalid") // Invalid object number
                .when()
                .get(BASE_URL)
                .then()
                .statusCode(anyOf(is(403), is(404)));
    }
}
