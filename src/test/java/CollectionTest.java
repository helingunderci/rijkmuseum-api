import io.restassured.response.Response;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.*;

public class CollectionTest extends BaseTest{

    private static final String COLLECTION_URL = BASE_API_URL + "en/collection";

    // Test that the API returns 200 OK and the collection is not empty
    @Test
    public void shouldReturnSuccessStatusAndNonEmptyCollection() {
        sendGetRequestToCollection()
                .then()
                .statusCode(200)
                // Assert that the "count" field in the response body is greater than 0
                .body("count", greaterThan(0));
    }

    // Utility method to send a GET request to the collection endpoint
    private Response sendGetRequestToCollection() {
        return given()
                .baseUri(COLLECTION_URL)
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .when()
                .get();
    }

    // Test that the API response is in JSON format
    @Test
    public void testApiReturnsJsonFormat() {
        given()
                .baseUri(COLLECTION_URL)
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .when()
                .get()
                .then()
                .statusCode(200)
                .contentType("application/json");
    }

    // Test that the returned web URLs contain '/nl/' when culture=nl
    @Test
    public void testRetrieveCollectionWithCultureNl() {
        Response response = given()
                .baseUri(BASE_API_URL + "nl/collection")
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .when()
                .get();

        assertEquals(200, response.statusCode(), "Response status code is not 200!");

        List<String> webUrls = response.jsonPath().getList("artObjects.links.web");
        boolean allContainNl = webUrls.stream().allMatch(url -> url.contains("/nl/"));
        assertTrue(allContainNl, "Some URLs do not contain '/nl/' as expected for culture=nl");
    }

    // Test that the returned web URLs contain '/en/' when culture=en
    @Test
    public void testRetrieveCollectionWithCultureEn() {
        Response response = given()
                .baseUri(COLLECTION_URL)
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .when()
                .get();

        assertEquals(200, response.statusCode(), "Response status code is not 200!");

        List<String> webUrls = response.jsonPath().getList("artObjects.links.web");
        boolean allContainEn = webUrls.stream().allMatch(url -> url.contains("/en/"));
        assertTrue(allContainEn, "Some URLs do not contain '/en/' as expected for culture=en");

        webUrls.forEach(System.out::println);
    }

    // Test retrieving a collection with page size = 8
    @Test
    public void testRetrieveCollectionWithValidPsParameter() {
        given()
                .baseUri(COLLECTION_URL)
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .queryParam("ps", "8")
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("artObjects.size()", equalTo(8));
    }

    // Test sorting the collection by relevance
    @Test
    public void testRetrieveCollectionWithSortingByRelevance() {
        Response response = given()
                .baseUri(COLLECTION_URL)
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .queryParam("s", "relevance")
                .when()
                .get();

        response.prettyPrint();
        assertEquals(200, response.getStatusCode(), "Expected status code 200");
        List<Map<String, Object>> artObjects = response.jsonPath().getList("artObjects");
        assertNotNull(artObjects, "artObjects list is null");
        assertFalse(artObjects.isEmpty(), "artObjects list is empty");
    }

    // Test filtering the collection by involved maker: Rembrandt van Rijn
    @Test
    public void testRetrieveCollectionByInvolvedMaker() {
        Response response = given()
                .baseUri(COLLECTION_URL)
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .queryParam("involvedMaker", "Rembrandt van Rijn")
                .when()
                .get();

        assertEquals(200, response.statusCode(), "Expected HTTP status 200");

        List<String> makers = response.jsonPath().getList("artObjects.principalOrFirstMaker");
        assertNotNull(makers);
        assertFalse(makers.isEmpty(), "No artworks found for Rembrandt");

        boolean allByRembrandt = makers.stream().allMatch(maker -> maker.equals("Rembrandt van Rijn"));
        assertTrue(allByRembrandt, "Not all artworks are by Rembrandt van Rijn");
    }

    @Test
    public void testCollectionWithImageOnlyFilter() {
        Response response = given()
                .baseUri(COLLECTION_URL)
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .queryParam("imgonly", "true")
                .when()
                .get();

        assertEquals(200, response.getStatusCode());

        List<Map<String, Object>> artObjects = response.jsonPath().getList("artObjects");
        for (Map<String, Object> art : artObjects) {
            assertNotNull(art.get("webImage"), "Found artwork without image although imgonly=true");
        }
    }

    @Test
    public void testPaginationWithPageParam() {
        Response response = given()
                .baseUri(COLLECTION_URL)
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .queryParam("ps", 5)
                .queryParam("p", 2)
                .when()
                .get();

        assertEquals(200, response.statusCode());
        assertEquals(5, response.jsonPath().getList("artObjects").size());
    }

    @Disabled ("Bug reported")
    @Test
    public void testCollectionWithInvalidPsParameter() {
        given()
                .baseUri(COLLECTION_URL)
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .queryParam("ps", "-5")
                .when()
                .get()
                .then()
                .statusCode(400);
    }
}
