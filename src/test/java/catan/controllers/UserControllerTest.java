package catan.controllers;

import com.jayway.restassured.http.ContentType;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


public class UserControllerTest {
    /*
private static Tomcat t;
private static final int TOMCAT_PORT = 8091;

@BeforeClass
public static void setUp() throws LifecycleException, ServletException, IOException {
   String currentDir = new File(".").getCanonicalPath();
   //String tomcatDir = currentDir + File.separatorChar + "tomcat";

   t = new Tomcat();
   t.setBaseDir(".");
   t.setPort(TOMCAT_PORT);
///There needs to be a symlink to the current dir named 'webapps'
t.addWebapp("/","src/main/webapp");
t.init();
t.start();
}

@AfterClass
public static void shutDownTomcat() throws LifecycleException {
   t.stop();
}

//@Test
    public void testUserFetchesSuccess() throws JSONException,
            URISyntaxException {
        Client client = new Client();
        WebResource webResource = client.resource("http://localhost:8091/");
        JSONObject json = webResource.path("/api/game/playerDetails").get(JSONObject.class);
        assertEquals("Tim", json.get("firstName"));
    }
       */

    @Test
    public void shouldReturnWeatherDataForWarsaw() {
        given().
                port(8091).
                header("Accept-Encoding", "application/json").
                parameters("username", "user1", "password", "123").
        when().
                post("/user/login").
        then().
                statusCode(200).
                contentType(ContentType.JSON).
                body("token", equalTo(""));
    }
}