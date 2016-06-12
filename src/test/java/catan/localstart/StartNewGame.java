package catan.localstart;

import catan.controllers.ctf.Scenario;


public class StartNewGame {

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
                .setUserReady(USER_NAME_3);

        String userWithMoveOrder1 = scenario.getUserNamesByMoveOrder().get(1);
        String userWithMoveOrder2 = scenario.getUserNamesByMoveOrder().get(2);
        String userWithMoveOrder3 = scenario.getUserNamesByMoveOrder().get(3);

        System.out.println("First moves user " + userWithMoveOrder1);
        System.out.println("Second moves user " + userWithMoveOrder2);
        System.out.println("Third moves user " + userWithMoveOrder3);
    }
}
