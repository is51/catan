package catan.services;

import catan.domain.model.game.GameBean;
import catan.domain.model.user.UserBean;
import catan.domain.exception.GameException;

import java.util.List;

public interface GameService {
    GameBean createNewGame(UserBean creator, boolean privateGame, String targetVictoryPoints, String preparationBuildingsSet) throws GameException;

    List<GameBean> getListOfGamesWithJoinedUser(UserBean user) throws GameException;

    List<GameBean> getListOfAllPublicGames();

    GameBean joinGameByIdentifier(UserBean user, String gameIdentifier, boolean privateGame) throws GameException;

    GameBean getGameByGameIdWithJoinedUser(UserBean user, String gameId) throws GameException;

    void leaveGame(UserBean user, String gameId) throws GameException;

    void cancelGame(UserBean user, String gameId) throws GameException;

    void updateGameUserStatus(UserBean user, String gameId, boolean readyForGame) throws GameException;
}
