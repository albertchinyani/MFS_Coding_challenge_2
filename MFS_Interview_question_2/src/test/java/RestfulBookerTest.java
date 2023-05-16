import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.LinkedHashMap;


public class RestfulBookerTest extends BaseTest {
    String bookingUri=baseUri+properties.getProperty("bookingUri");


    @Test()
    public void ping() {
       Response response= given()
                .when()
                .get(baseUri+properties.getProperty("pingUri"))
                .then()
                .statusCode(201)
                .extract().response();
       if (response.statusCode()==201){        System.out.println("System  is up");}
       else { System.out.println("System  is down");}


    }

    @Test(priority =1 ,dataProvider = "bookingData")
    public void createBooking(LinkedHashMap<String, Object> bookingMap) {
        JSONObject booking = (JSONObject) JSONValue.parse(JSONValue.toJSONString(bookingMap));
        Response response = given()
                .contentType(ContentType.JSON)
                .body(booking)
                .when()
                .post(bookingUri)
                .then()
                .statusCode(200)
                .extract().response();

         bookingId = response.jsonPath().getString("bookingid");
        System.out.println("Booking created with ID: " + bookingId);
    }

    @Test(priority = 2)
    public void getBooking() {
       Response response= given()
                .contentType(ContentType.JSON)
                .when()
                .get(bookingUri+"/"+bookingId)
                .then()
                .statusCode(200)
                .extract().response();
       System.out.println("Booking fetched by getbooking(): " + response.asPrettyString());

    }

    @Test(priority = 3, dataProvider = "updateBookingData")
    public void updateBooking(LinkedHashMap<String, Object> updateBookingMap) {
        JSONObject updateBooking = (JSONObject) JSONValue.parse(JSONValue.toJSONString(updateBookingMap));




        String token = auth(properties.getProperty("username"), properties.getProperty("password"))
                .jsonPath().getString("token");
        //System.out.println("Token is: " + token);

       Response response= given()
                .contentType(ContentType.JSON)
                .header("Accept","application/json")
                .header("Cookie","token="+token)
                .body(updateBooking.toString())
                .when()
                .put(bookingUri+"/"+bookingId)
                .then()
                .statusCode(200)
                .extract().response();
        System.out.println("Booking modified updatebooking() with checkout date and additional data modified: " + response.asPrettyString());

    }


    public Response auth(String username, String password) {
        String requestBody = "{\n" +
                "    \"username\" : \"" + username + "\",\n" +
                "    \"password\" : \"" + password + "\"\n" +
                "}";


        return given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(baseUri+properties.getProperty("authUri"))
                .then()
                .statusCode(200)
                .extract().response();

    }

    //Data providers
    @DataProvider(name = "bookingData")
    public Object[][] getBookingData() throws IOException, org.json.simple.parser.ParseException {
        ObjectMapper objectMapper = new ObjectMapper();
        JSONArray bookingsJsonArray = getJsonData("src/main/resources/createBooking.json");
        Object[][] bookingData = new Object[bookingsJsonArray.size()][1];

        for (int i = 0; i < bookingsJsonArray.size(); i++) {
            JSONObject bookingJson = (JSONObject) bookingsJsonArray.get(i);
            Object bookingObject = objectMapper.readValue(bookingJson.toString(), Object.class);
            bookingData[i][0] = bookingObject;
        }

        return bookingData;
    }

    @DataProvider(name = "updateBookingData")
    public Object[][] getUpdateBookingData() throws IOException, org.json.simple.parser.ParseException {
        ObjectMapper objectMapper = new ObjectMapper();
        JSONArray bookingsJsonArray = getJsonData("src/main/resources/updateBooking.json");
        Object[][] updateBookingData = new Object[bookingsJsonArray.size()][1];

        for (int i = 0; i < bookingsJsonArray.size(); i++) {
            JSONObject bookingJson = (JSONObject) bookingsJsonArray.get(i);
            Object bookingObject = objectMapper.readValue(bookingJson.toString(), Object.class);
            updateBookingData[i][0] = bookingObject;
        }

        return updateBookingData;
    }



}