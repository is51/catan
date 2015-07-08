package catan.dao;

import catan.domain.model.game.GameBean;

import java.util.List;

public interface GameDao {
    void addNewGame(GameBean newGame);

    GameBean getGameByGameId(int gameId);

    List<GameBean> getGamesByCreatorId(int creatorId);

    List<GameBean> getPublicGames();
}
