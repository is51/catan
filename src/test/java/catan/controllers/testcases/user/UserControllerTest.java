package catan.controllers.testcases.user;

import catan.config.ApplicationConfig;
import catan.controllers.ctf.Scenario;
import catan.controllers.util.FunctionalTestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationConfig.class)
@WebIntegrationTest("server.port:8091")
public class UserControllerTest extends FunctionalTestUtil {
    // TODO: these tests need to be refactored: create methods like "register", "login", ... which will be returning "Response" objects. And use them.


    public static final int SERVER_PORT = 8091;

    public static final String USER_NAME_1 = "user1_UserTest";
    public static final String USER_PASSWORD_1 = "password1";
    public static final String USER_NAME_2 = "user2_UserTest";
    public static final String USER_PASSWORD_2 = "password2";
    public static final String USER_NAME_3 = "user3_UserTest";
    public static final String USER_PASSWORD_3 = "password3";

    public static final String USER_NAME_GUEST_1 = "userGuest1_UserTest";
    public static final String USER_NAME_GUEST_2 = "userGuest2_UserTest";


    private static boolean initialized = false;

    @Before
    public void setup() {
        if (!initialized) {
            new Scenario();     //setup RestAssured port and host
            initialized = true;
        }
    }

    @Test
    public void shouldSuccessfullyRegisterNewUserAndFailRegistrationWithHttpCode400WhenSuchUserAlreadyExists() {
        //Registering new User
        given().
                port(SERVER_PORT).
                header("Accept-Encoding", "application/json").
                parameters("username", USER_NAME_1 + FunctionalTestUtil.GLOBAL_UNIQUE_USERNAME_SUFFIX, "password", USER_PASSWORD_1).
                when().
                post("/api/user/register").
                then().
                statusCode(200).
                body(isEmptyString());

        String userToken = loginUser(USER_NAME_1, USER_PASSWORD_1);

        getUserDetails(userToken)
                .then()
                .statusCode(200)
                .body("guest", equalTo(false));

        //Registering user with existing username
        given().
                port(SERVER_PORT).
                header("Accept-Encoding", "application/json").
                parameters("username", USER_NAME_1 + FunctionalTestUtil.GLOBAL_UNIQUE_USERNAME_SUFFIX, "password", USER_PASSWORD_1).
                when().
                post("/api/user/register").
                then().
                statusCode(400).
                contentType("application/json").
                body("errorCode", equalTo("USERNAME_ALREADY_EXISTS"));
    }

    @Test
    public void shouldFailWithUserNotFoundErrorWithHttpCode400WhenLoginWithWrongPassword() {
        given().
                port(SERVER_PORT).
                header("Accept-Encoding", "application/json").
                parameters("username", USER_NAME_1, "password", "00000").
                when().
                post("/api/user/login").
                then().
                statusCode(400).
                contentType("application/json").
                body("errorCode", equalTo("INCORRECT_LOGIN_PASSWORD"));
    }

    @Test
    public void shouldFailWithCommonErrorWithHttpCode400WhenLoginWithIncorrectParameters() {
        given().
                port(SERVER_PORT).
                header("Accept-Encoding", "application/json").
                parameters("username", "", "password", "").
                when().
                post("/api/user/login").
                then().
                statusCode(400).
                contentType("application/json").
                body("errorCode", equalTo("ERROR"));
    }

    @Test
    public void shouldReturnSessionTokenWithHttpCode200AndReturnUserDetailsWithHttpCode200WhenRegisterLoginAndGetDetails() {
        //Registering new User
        given().
                port(SERVER_PORT).
                header("Accept-Encoding", "application/json").
                parameters("username", USER_NAME_2, "password", USER_PASSWORD_2).
                when().
                post("/api/user/register").
                then().
                statusCode(200).
                body(isEmptyString());

        //Login user and extract token from response
        String token =
                given().
                        port(8091).
                        header("Accept-Encoding", "application/json").
                        parameters("username", USER_NAME_2, "password", USER_PASSWORD_2).
                        when().
                        post("/api/user/login").
                        then().
                        statusCode(200).
                        contentType("application/json").
                        extract()
                        .path("token");

        //Get details by extracted token
        given().
                port(SERVER_PORT).
                header("Accept-Encoding", "application/json").
                parameters("token", token).
                when().
                post("/api/user/details").
                then().
                statusCode(200).
                contentType("application/json").
                body("username", equalTo(USER_NAME_2));
    }

    @Test
    public void shouldFailWithForbiddenResponseBodyWithHttpCode403WhenTryingToGetDetailsForInvalidToken() {
        given().
                port(SERVER_PORT).
                header("Accept-Encoding", "application/json").
                parameters("token", "12345").
                when().
                post("/api/user/details").
                then().
                statusCode(403);
    }

    @Test
    public void shouldLogoutSuccessfullyWithHttpCode200WhenAnyTokenIsPassed() {
        given().
                port(SERVER_PORT).
                header("Accept-Encoding", "application/json").
                parameters("token", "12345").
                when().
                post("/api/user/logout").
                then().
                statusCode(200).
                body(isEmptyString());
    }

    // end of code which needs to be refactored (see the first line of the file)



    @Test
    public void should_successfully_register_and_login_guest() {
        String userToken = registerAndLoginGuest(USER_NAME_GUEST_1)
                .then()
                .statusCode(200)
                .body("token", not(isEmptyOrNullString()))
                .extract()
                .path("token");

        getUserDetails(userToken)
                .then()
                .statusCode(200)
                .body("guest", equalTo(true));

    }

    @Test
    public void should_fail_when_register_and_login_guest_if_username_is_taken_by_other_guest() {
        registerAndLoginGuest(USER_NAME_GUEST_2);

        registerAndLoginGuest(USER_NAME_GUEST_2)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("USERNAME_ALREADY_EXISTS"));
    }

    @Test
    public void should_fail_when_register_and_login_guest_if_username_is_taken_by_regular_user() {
        registerUser(USER_NAME_3, USER_PASSWORD_3);

        registerAndLoginGuest(USER_NAME_3)
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("USERNAME_ALREADY_EXISTS"));
    }

    @Test
    public void should_fail_when_register_and_login_guest_if_username_is_empty() {
        registerAndLoginGuest("")
                .then()
                .statusCode(400)
                .body("errorCode", equalTo("ERROR"));
    }

}