package catan.services.util.game;

import catan.dao.GameDao;
import catan.domain.exception.GameException;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.types.GameStatus;
import catan.domain.model.user.UserBean;
import catan.services.util.random.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static catan.services.impl.GameServiceImpl.ALREADY_JOINED_ERROR;
import static catan.services.impl.GameServiceImpl.ERROR_CODE_ERROR;
import static catan.services.impl.GameServiceImpl.INVALID_CODE_ERROR;
import static catan.services.impl.GameServiceImpl.MIN_TARGET_VICTORY_POINTS;

@Component
public class GameUtil {
    private Logger log = LoggerFactory.getLogger(GameUtil.class);

    private GameDao gameDao;
    private RandomUtil randomUtil;

    public int toValidVictoryPoints(String inputTargetVictoryPoints) throws GameException {
        int targetVictoryPoints;
        try {
            targetVictoryPoints = Integer.parseInt(inputTargetVictoryPoints);
        } catch (Exception e) {
            log.debug("Cannot create game with non-integer format of victory points");
            throw new GameException(ERROR_CODE_ERROR);
        }

        if (targetVictoryPoints < MIN_TARGET_VICTORY_POINTS) {
            log.debug("Cannot create game with less than " + MIN_TARGET_VICTORY_POINTS + " victory points");
            throw new GameException(ERROR_CODE_ERROR);
        }
        return targetVictoryPoints;
    }

    public GameBean getGameById(String gameIdString, String errorCodeToReturnIfNotFound) throws GameException {
        int gameId;
        try {
            gameId = Integer.parseInt(gameIdString);
        } catch (Exception e) {
            log.debug("Cannot convert gameId to integer value");
            throw new GameException(ERROR_CODE_ERROR);
        }

        GameBean game = gameDao.getGameByGameId(gameId);
        if (game == null) {
            log.debug("Game with such game id doesn't exists");
            throw new GameException(errorCodeToReturnIfNotFound);
        }

        return game;
    }

    public GameBean findPrivateGame(String privateCode) throws GameException {
        GameBean game = gameDao.getGameByPrivateCode(privateCode);
        if (game == null) {
            log.debug("Game with such private code doesn't exists");
            throw new GameException(INVALID_CODE_ERROR);
        }

        return game;
    }

    public GameBean findPublicGame(String gameIdentifier) throws GameException {
        GameBean game = getGameById(gameIdentifier, ERROR_CODE_ERROR);

        if (game.isPrivateGame()) {
            log.debug("Game with id '" + game.getGameId() + "' is private," +
                    " but join public game was initiated");
            throw new GameException(ERROR_CODE_ERROR);
        }

        return game;
    }

    public void addUserToGame(GameBean game, UserBean user) throws GameException {
        for (GameUserBean alreadyJoinedGameUser : game.getGameUsers()) {
            if (alreadyJoinedGameUser.getUser().getId() == user.getId()) {
                log.debug("User " + user + " already joined to this game");
                throw new GameException(ALREADY_JOINED_ERROR);
            }
        }

        List<Integer> usedColorCodes = new ArrayList<Integer>();
        for (GameUserBean gameUser : game.getGameUsers()) {
            usedColorCodes.add(gameUser.getColorId());
        }

        int colorId = 1;
        while (usedColorCodes.contains(colorId)) {
            colorId++;
        }

        GameUserBean newGameUser = new GameUserBean(user, colorId, game);
        game.getGameUsers().add(newGameUser);
    }


    public void startGame(GameBean game) {
        log.debug("Checking if game {} can be started (all players is ready)", game);

        if (game.getMinPlayers() > game.getGameUsers().size()) {
            log.info("There are not enough players to start game {}. " +
                            "Game will start when players count will be {}, current count is {}",
                    game, game.getMaxPlayers(), game.getGameUsers().size());
            return;
        }

        for (GameUserBean userBean : game.getGameUsers()) {
            if (!userBean.isReady()) {
                return;
            }
        }

        log.debug("All players are ready");
        log.debug("Starting game {}", game);

        randomUtil.populatePlayersMoveOrderRandomly(game.getGameUsers());

        game.setCurrentMove(1);
        game.setStatus(GameStatus.PLAYING);
        game.setDateStarted(new Date());

        gameDao.updateGame(game);
    }

    @Autowired
    public void setGameDao(GameDao gameDao) {
        this.gameDao = gameDao;
    }

    @Autowired
    public void setRandomUtil(RandomUtil randomUtil) {
        this.randomUtil = randomUtil;
    }
}
