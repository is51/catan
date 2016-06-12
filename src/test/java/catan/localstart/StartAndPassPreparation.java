package catan.localstart;

import catan.controllers.ctf.Scenario;
import catan.controllers.util.PlayTestUtil;


public class StartAndPassPreparation {

    public static final String USER_NAME_1 = "1";
    public static final String USER_PASSWORD_1 = "1";
    public static final String USER_NAME_2 = "2";
    public static final String USER_PASSWORD_2 = "2";
    public static final String USER_NAME_3 = "3";
    public static final String USER_PASSWORD_3 = "3";

    public static void main(String[] args) {
        Scenario scenario = new Scenario()
                .registerUser(USER_NAME_1, USER_PASSWORD_1)
                .registerUser(USER_NAME_2, USER_PASSWORD_2)
                .registerUser(USER_NAME_3, USER_PASSWORD_3)

                .loginUser(USER_NAME_1, USER_PASSWORD_1)
                .loginUser(USER_NAME_2, USER_PASSWORD_2)
                .loginUser(USER_NAME_3, USER_PASSWORD_3)

                .createNewPublicGameByUser(USER_NAME_1)
                .joinPublicGame(USER_NAME_2)
                .joinPublicGame(USER_NAME_3)

                .setUserReady(USER_NAME_1)
                .setUserReady(USER_NAME_2)
                .setUserReady(USER_NAME_3)

                .BUILD_SETTLEMENT(1).atNode(0, 0, "topLeft")
                .BUILD_ROAD(1).atEdge(0, 0, "topLeft")
                .END_TURN(1)

                .BUILD_SETTLEMENT(2).atNode(0, 0, "topRight")
                .BUILD_ROAD(2).atEdge(0, 0, "right")
                .END_TURN(2)

                .BUILD_SETTLEMENT(3).atNode(0, 0, "bottom")
                .BUILD_ROAD(3).atEdge(0, 0, "bottomLeft")
                .END_TURN(3)

                .BUILD_SETTLEMENT(3).atNode(0, -2, "topLeft")
                .BUILD_ROAD(3).atEdge(0, -2, "topLeft")
                .END_TURN(3)

                .BUILD_SETTLEMENT(2).atNode(0, -2, "topRight")
                .BUILD_ROAD(2).atEdge(0, -2, "right")
                .END_TURN(2)

                .BUILD_SETTLEMENT(1).atNode(0, -2, "bottom")
                .BUILD_ROAD(1).atEdge(0, -2, "bottomLeft")
                .END_TURN(1);

        String userWithMoveOrder1 = scenario.getUserNamesByMoveOrder().get(1);
        String userWithMoveOrder2 = scenario.getUserNamesByMoveOrder().get(2);
        String userWithMoveOrder3 = scenario.getUserNamesByMoveOrder().get(3);

        System.out.println("First moves user " + userWithMoveOrder1);
        System.out.println("Second moves user " + userWithMoveOrder2);
        System.out.println("Third moves user " + userWithMoveOrder3);
    }
}
