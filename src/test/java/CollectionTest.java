import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CollectionTest {

    private static final String BASE_URL = "https://www.rijksmuseum.nl/api/en/collection";
    private static final String API_KEY = "0fiuZFh4";

    //API returns 200 OK and contains artworks (non-empty collection)
    @Test
    public void testApiReturnsSuccessResponse() {
        given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .when()
                .get(BASE_URL)
                .then()
                .statusCode(200)
                .body("count", greaterThan(0)); // Check that the collection is not empty
    }

    //Test if the API response is in JSON format
    @Test
    public void testApiReturnsJsonFormat() {
        given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .when()
                .get(BASE_URL)
                .then()
                .statusCode(200)
                .contentType("application/json"); // Is the response in JSON format?
    }

    //Test collection retrieval using culture = 'nl'
    @Test
    public void testRetrieveCollectionWithValidCultureNL() {
        given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .queryParam("culture", "nl")
                .when()
                .get(BASE_URL)
                .then()
                .statusCode(200)
                .body("artObjects.size()", greaterThan(0)); //Are there any objects in the collection?
    }

    //Test collection retrieval using culture = 'en'
    @Test
    public void testRetrieveCollectionWithValidCultureEN() {
        given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .queryParam("culture", "en")
                .when()
                .get(BASE_URL)
                .then()
                .statusCode(200)
                .body("artObjects.size()", greaterThan(0)); // Are there any objects in the collection?
    }

    //Test collection retrieval with page size = 8
    @Test
    public void testRetrieveCollectionWithValidPsParameter() {
        given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .queryParam("ps", "8")
                .when()
                .get(BASE_URL)
                .then()
                .statusCode(200)
                .body("artObjects.size()", equalTo(8)); // 5 objects should return
    }

    //Test sorting collection by 'relevance'
    @Test
    public void testRetrieveCollectionWithSortingByRelevance() {
        given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .queryParam("s", "relevance") // Sort by relevans
                .when()
                .get(BASE_URL)
                .then()
                .statusCode(200)
                .body("artObjects.size()", greaterThan(0)); // List should not be empty
    }

    //Test sorting collection by 'objecttype'
    @Test
    public void testRetrieveCollectionWithSortingByObjectType() {
        given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .queryParam("s", "objecttype") // Sorting by object type
                .when()
                .get(BASE_URL)
                .then()
                .statusCode(200)
                .body("artObjects.size()", greaterThan(0)); // List should be emty
    }

    //Test filtering the collection by involved artist: Rembrandt van Rijn
    @Test
    public void testRetrieveCollectionByInvolvedMaker() {
        given()
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .queryParam("involvedMaker", "Rembrandt van Rijn") // Artist filter
                .when()
                .get(BASE_URL)
                .then()
                .statusCode(200)
                .body("artObjects.size()", greaterThan(0))
                .body("artObjects[0].principalOrFirstMaker", equalTo("Rembrandt van Rijn")); // is first object Rembrant?
    }
}
