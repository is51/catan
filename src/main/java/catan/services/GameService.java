package catan.services;

import catan.domain.model.game.GameBean;
import catan.domain.model.user.UserBean;
import catan.exception.GameException;

import java.util.List;

public interface GameService {
    GameBean createNewGame(UserBean creator, boolean privateGame, String targetVictoryPoints) throws GameException;

    List<GameBean> getListOfGamesWithJoinedUser(UserBean user) throws GameException;

    List<GameBean> getListOfAllPublicGames();

    void joinGameByIdentifier(UserBean user, String gameIdentifier, boolean privateGame) throws GameException;

    GameBean getGameByGameIdWithJoinedUser(UserBean user, String gameId) throws GameException;

    void leaveGame(UserBean user, String gameId) throws GameException;

    void cancelGame(UserBean user, String gameId) throws GameException;
}
