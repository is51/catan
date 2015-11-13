package catan.localstart;

import catan.controllers.ctf.Scenario;
import catan.controllers.util.PlayTestUtil;


public class StartAndPreparation extends PlayTestUtil {

    public static final String USER_NAME_1 = "1";
    public static final String USER_PASSWORD_1 = "1";
    public static final String USER_NAME_2 = "2";
    public static final String USER_PASSWORD_2 = "2";
    public static final String USER_NAME_3 = "3";
    public static final String USER_PASSWORD_3 = "3";

    public static void main(String[] args) {
        StartAndPreparation instance = new StartAndPreparation();

        instance.registerUser(USER_NAME_1, USER_PASSWORD_1);
        instance.registerUser(USER_NAME_2, USER_PASSWORD_2);
        instance.registerUser(USER_NAME_3, USER_PASSWORD_3);

        Scenario scenario = new Scenario();

        scenario
                .loginUser(USER_NAME_1, USER_PASSWORD_1)
                .loginUser(USER_NAME_2, USER_PASSWORD_2)
                .loginUser(USER_NAME_3, USER_PASSWORD_3)

                .createNewPublicGameByUser(USER_NAME_1)
                .joinPublicGame(USER_NAME_2)
                .joinPublicGame(USER_NAME_3)

                .setUserReady(USER_NAME_1)
                .setUserReady(USER_NAME_2)
                .setUserReady(USER_NAME_3)

                .buildSettlement(1).atNode(0, 0, "topLeft")
                .buildRoad(1).atEdge(0, 0, "topLeft")
                .endTurn(1)

                .buildSettlement(2).atNode(0, 0, "topRight")
                .buildRoad(2).atEdge(0, 0, "right")
                .endTurn(2)

                .buildSettlement(3).atNode(0, 0, "bottom")
                .buildRoad(3).atEdge(0, 0, "bottomLeft")
                .endTurn(3)

                .buildSettlement(3).atNode(0, -2, "topLeft")
                .buildRoad(3).atEdge(0, -2, "topLeft")
                .endTurn(3)

                .buildSettlement(2).atNode(0, -2, "topRight")
                .buildRoad(2).atEdge(0, -2, "right")
                .endTurn(2)

                .buildSettlement(1).atNode(0, -2, "bottom")
                .buildRoad(1).atEdge(0, -2, "bottomLeft")
                .endTurn(1);
    }
}
