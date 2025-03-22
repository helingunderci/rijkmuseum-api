import io.github.cdimascio.dotenv.Dotenv;

public class BaseTest {
    private static final Dotenv dotenv = Dotenv.load();

    public static final String BASE_API_URL = dotenv.get("BASE_API_URL");
    public static final String API_KEY = dotenv.get("API_KEY");

}
