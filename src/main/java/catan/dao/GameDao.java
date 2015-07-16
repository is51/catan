package catan.dao;

import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;

import java.util.List;

public interface GameDao {
    void addNewGame(GameBean newGame);

    void addNewGameUser(GameUserBean newGameUser);

    GameBean getGameByGameId(int gameId);

    GameBean getGameByPrivateCode(String privateCode);

    List<GameBean> getGamesByCreatorId(int creatorId);

    List<GameBean> getGamesWithJoinedUser(int userId);

    List<GameBean> getAllNewPublicGames();

    List<String> getUsedActiveGamePrivateCodes();

    void updateGame(GameBean game);
}
