import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.*;

public class CollectionTest extends BaseTest{

    // Test that the API returns 200 OK and the collection is not empty
    @Test
    public void testApiReturnsSuccessResponse() {
        given()
                .baseUri(BASE_API_URL + "en/collection")
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("count", greaterThan(0));
    }

    // Test that the API response is in JSON format
    @Test
    public void testApiReturnsJsonFormat() {
        given()
                .baseUri(BASE_API_URL + "en/collection")
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
    public void testRetrieveCollectionWithCultureNl_WebUrlCheck() {
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

        System.out.println("Web URLs returned with culture=nl:");
        webUrls.forEach(System.out::println);
    }

    // Test that the returned web URLs contain '/en/' when culture=en
    @Test
    public void testRetrieveCollectionWithCultureEn() {
        Response response = given()
                .baseUri(BASE_API_URL + "en/collection")
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .when()
                .get();

        assertEquals(200, response.statusCode(), "Response status code is not 200!");

        List<String> webUrls = response.jsonPath().getList("artObjects.links.web");
        boolean allContainEn = webUrls.stream().allMatch(url -> url.contains("/en/"));
        assertTrue(allContainEn, "Some URLs do not contain '/en/' as expected for culture=en");

        System.out.println("Web URLs returned with culture=en:");
        webUrls.forEach(System.out::println);
    }

    // Test retrieving a collection with page size = 8
    @Test
    public void testRetrieveCollectionWithValidPsParameter() {
        given()
                .baseUri(BASE_API_URL + "en/collection")
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
                .baseUri(BASE_API_URL + "en/collection")
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

        System.out.println("Number of artworks returned: " + artObjects.size());
        System.out.println("First artwork title: " + artObjects.get(0).get("title"));
    }

    // Test that the facet "type" exists and is not empty
    @Test
    public void testRetrieveCollectionFacetTypeExistsAndNotEmpty() {
        Response response = given()
                .baseUri(BASE_API_URL + "en/collection")
                .queryParam("key", API_KEY)
                .queryParam("format", "json")
                .queryParam("s", "objecttype")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().response();

        List<Map<String, Object>> facets = response.jsonPath().getList("facets");
        List<String> actualTypes = new ArrayList<>();

        for (Map<String, Object> facet : facets) {
            if ("type".equals(facet.get("name"))) {
                List<Map<String, Object>> typeFacets = (List<Map<String, Object>>) facet.get("facets");
                for (Map<String, Object> typeFacet : typeFacets) {
                    actualTypes.add((String) typeFacet.get("key"));
                }
                break;
            }
        }

        System.out.println("Type facet list (unsorted):");
        actualTypes.forEach(System.out::println);

        assertNotNull(actualTypes, "'type' facet list is null");
        assertFalse(actualTypes.isEmpty(), "'type' facet list is empty");
    }

    // Test filtering the collection by involved maker: Rembrandt van Rijn
    @Test
    public void testRetrieveCollectionByInvolvedMaker() {
        Response response = given()
                .baseUri(BASE_API_URL + "en/collection")
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

        System.out.println("Found " + makers.size() + " artworks by Rembrandt van Rijn.");
    }
}
