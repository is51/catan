package catan.controllers;

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
}