package catan.controllers;

import com.sun.jersey.api.client.Client;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.ws.rs.core.Application;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static junit.framework.Assert.assertEquals;

public class UserControllerTest{

    private static Tomcat t;
    private static final int TOMCAT_PORT = 8091;

    @BeforeClass
    public static void setUp() throws LifecycleException, ServletException, IOException {
        String currentDir = new File(".").getCanonicalPath();
        //String tomcatDir = currentDir + File.separatorChar + "tomcat";

        t = new Tomcat();
        t.setBaseDir(".");
        t.setPort(TOMCAT_PORT);
    /* There needs to be a symlink to the current dir named 'webapps' */
        t.addWebapp("/", "src/main/webapp");
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

}