import io.restassured.RestAssured;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class BaseTest {
    String bookingId;
    protected static Properties properties = loadProperties();
    String baseUri = properties.getProperty("baseUri");

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = baseUri;
    }

    // Method to load properties file
    protected static Properties loadProperties() {
        Properties apiproperties = new Properties();

        try {
            FileInputStream inputStream = new FileInputStream("src/main/resources/test.properties");
            apiproperties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return apiproperties;
    }

    // Method to load JSON data
    protected static JSONArray getJsonData(String fileName) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader(fileName);
        JSONArray jsonArray = (JSONArray) jsonParser.parse(reader);
        reader.close();
        return jsonArray;
    }

    @AfterClass
    public void tearDown() {
        // We do nothing for now, as no WebDriver has been spun up.
    }
}
