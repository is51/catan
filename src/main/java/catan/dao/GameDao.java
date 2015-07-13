package catan.dao;

import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;

import java.util.List;

public interface GameDao {
    void addNewGame(GameBean newGame);

    void addNewGameUser(GameUserBean newGameUser);

    GameBean getGameByGameId(int gameId);

    List<GameBean> getGamesByCreatorId(int creatorId);

    List<GameBean> getAllNewPublicGames();

    List<Integer> getUsedActiveGamePrivateCodes();
}
