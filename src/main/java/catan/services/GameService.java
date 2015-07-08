package catan.services;

import catan.domain.model.game.GameBean;
import catan.domain.model.user.UserBean;
import catan.exception.GameException;

import java.util.List;

public interface GameService {
    GameBean createNewGame(UserBean creator, boolean privateGame) throws GameException;

    List<GameBean> getListOfGamesCreatedBy(UserBean creator) throws GameException;

    List<GameBean> getListOfAllPublicGames();
}
