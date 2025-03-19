import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class RijksmuseumApiTest {

    private static final String BASE_URL = "https://www.rijksmuseum.nl/api/en/collection";
    private static final String API_KEY = "0fiuZFh4";

    @Test
    public void testRetrieveCollections() {
        RestAssured.baseURI = BASE_URL;

        given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("artObjects", notNullValue())
                .body("artObjects.size()", greaterThan(0));
    }

    @Test
    public void testRetrieveObjectDetails() {
        RestAssured.baseURI = BASE_URL;

        Response response = given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .when()
                .get();

        String objectId = response.jsonPath().getString("artObjects[0].objectNumber");

        given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .when()
                .get("/" + objectId)
                .then()
                .statusCode(200)
                .body("artObject.objectNumber", equalTo(objectId));
    }
}
