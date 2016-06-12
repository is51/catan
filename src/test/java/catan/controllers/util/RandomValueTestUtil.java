package catan.controllers.util;

import catan.domain.model.dashboard.types.HexType;
import catan.domain.model.game.types.DevelopmentCard;
import com.jayway.restassured.RestAssured;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import static com.jayway.restassured.RestAssured.given;

public class RandomValueTestUtil {

    public static final String ACCEPT_CONTENT_TYPE = "application/json";

    public static void setNextPrivateCode(String privateCode){
        given()
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("privateCode", privateCode)
                .when()
                .post("/api/management/random/set-next-private-code")
                .then()
                .statusCode(200);
    }

    public static void setNextMoveOrder(Integer moveOrder) {
        given()
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("moveOrder", moveOrder.toString())
                .when()
                .post("/api/management/random/set-next-move-order")
                .then()
                .statusCode(200);
    }

    public static void setNextHexType(int x, int y, String hexType) {
        given()
                .port(RestAssured.port)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("x", x, "y", y, "hexType", hexType)
                .when()
                .post("/api/management/random/set-next-hex-type")
                .then()
                .statusCode(200);
    }

    public static void setNextHexDiceNumber(int x, int y, Integer diceNumber) {
        given()
                .port(RestAssured.port)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("x", x, "y", y, "diceNumber", diceNumber)
                .when()
                .post("/api/management/random/set-next-hex-dice-number")
                .then()
                .statusCode(200);
    }

    public static void setNextDiceNumber(Integer diceNumber) {
        given()
                .port(RestAssured.port)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("diceNumber", diceNumber)
                .when()
                .post("/api/management/random/set-next-dice-number")
                .then()
                .statusCode(200);
    }

    public static void setNextDevelopmentCard(DevelopmentCard devCard) {
        given()
                .port(RestAssured.port)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("devCard", devCard.name())
                .when()
                .post("/api/management/random/set-next-development-card")
                .then()
                .statusCode(200);
    }

    public static void setNextStolenResource(HexType resource) {
        given()
                .port(RestAssured.port)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("resource", resource.name())
                .when()
                .post("/api/management/random/set-next-stolen-resource")
                .then()
                .statusCode(200);
    }

    public static void setNextOfferId(Integer offerId) {
        given()
                .port(RestAssured.port)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .parameters("offerId", offerId.toString())
                .when()
                .post("/api/management/random/set-next-offer-id")
                .then()
                .statusCode(200);
    }

    public static void resetNextRandomValues() {
        given()
                .port(RestAssured.port)
                .header("Accept", ACCEPT_CONTENT_TYPE)
                .when()
                .post("/api/management/random/reset-next-random-values")
                .then()
                .statusCode(200);
    }
}
