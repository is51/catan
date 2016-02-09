package catan.localstart;

import catan.controllers.util.PlayTestUtil;

import static org.hamcrest.Matchers.is;


public class LocalStart extends PlayTestUtil {

    public static final String USER_NAME_1 = "1";
    public static final String USER_PASSWORD_1 = "1";
    public static final String USER_NAME_2 = "2";
    public static final String USER_PASSWORD_2 = "2";
    public static final String USER_NAME_3 = "3";
    public static final String USER_PASSWORD_3 = "3";

    public static void main(String[] args) {
        LocalStart ls = new LocalStart();

        ls.registerUser(USER_NAME_1, USER_PASSWORD_1);
        ls.registerUser(USER_NAME_2, USER_PASSWORD_2);
        ls.registerUser(USER_NAME_3, USER_PASSWORD_3);

        String userToken1 = ls.loginUser(USER_NAME_1, USER_PASSWORD_1);
        String userToken2 = ls.loginUser(USER_NAME_2, USER_PASSWORD_2);
        String userToken3 = ls.loginUser(USER_NAME_3, USER_PASSWORD_3);

        System.out.println("User 1 token: " + userToken1);
        System.out.println("User 2 token: " + userToken2);
        System.out.println("User 3 token: " + userToken3);

        int gameId = ls.createNewGame(userToken1, false, 12, 1).path("gameId");

        ls.joinPublicGame(userToken2, gameId);
        ls.joinPublicGame(userToken3, gameId);

        ls.setUserReady(userToken1, gameId);
        ls.setUserReady(userToken2, gameId);
        ls.setUserReady(userToken3, gameId);

        int moveOrderOfUser1 = ls.viewGame(userToken1, gameId).path("gameUsers[0].moveOrder");
        int moveOrderOfUser2 = ls.viewGame(userToken1, gameId).path("gameUsers[1].moveOrder");
        int moveOrderOfUser3 = ls.viewGame(userToken1, gameId).path("gameUsers[2].moveOrder");

        System.out.println("User 1 moves " + moveOrderOfUser1);
        System.out.println("User 2 moves " + moveOrderOfUser2);
        System.out.println("User 3 moves " + moveOrderOfUser3);
    }
}
