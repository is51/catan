package catan.services.impl;

import catan.dao.GameDao;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameStatus;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.user.UserBean;
import catan.exception.GameException;
import catan.services.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service("gameService")
@Transactional
public class GameServiceImpl implements GameService {
    private Logger log = LoggerFactory.getLogger(GameService.class);
    public static final String ERROR_CODE_ERROR = "ERROR";

    GameDao gameDao;

    @Override
    public GameBean createNewGame(UserBean creator, boolean privateGame) throws GameException {
        log.debug(">> Creating new " + (privateGame ? "private" : "public") + " game, creator: " + creator + " ...");
        if (creator == null) {
            log.debug("<< Cannot create new game due to creator is empty");
            throw new GameException(ERROR_CODE_ERROR);
        }

        GameBean game = new GameBean(creator, privateGame, new Date(), GameStatus.NEW);
        gameDao.addNewGame(game);

        addUserToGame(game, creator);

        log.debug("<< Game with id '" + game.getGameId() + "' successfully created with creator " + creator);

        return game;
    }

    @Override
    public List<GameBean> getListOfGamesCreatedBy(UserBean creator) throws GameException {
        log.debug(">> Getting list of games created by " + creator + " ...");
        if (creator == null) {
            log.debug("<< Cannot get list of games due to creator is empty");
            throw new GameException(ERROR_CODE_ERROR);
        }

        List<GameBean> games = gameDao.getGamesByCreatorId(creator.getId());

        log.debug("<< " + games.size() + " games created by " + creator + " successfully retrieved");

        return games;
    }

    @Override
    public List<GameBean> getListOfAllPublicGames() {
        log.debug(">> Getting list of all public games ...");

        List<GameBean> games = gameDao.getPublicGames();

        log.debug("<< " + games.size() + " games successfully retrieved");

        return games;
    }

    private void addUserToGame(GameBean game, UserBean userBean) {
        int numberOfUsers = game.getGameUsers().size();
        int colorId = numberOfUsers + 1;

        GameUserBean gameUserBean = new GameUserBean(userBean, colorId);

        game.getGameUsers().add(gameUserBean);
    }

    @Autowired
    public void setGameDao(GameDao gameDao) {
        this.gameDao = gameDao;
    }
}
