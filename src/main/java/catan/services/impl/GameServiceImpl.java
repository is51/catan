package catan.services.impl;

import catan.dao.GameDao;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.types.GameStatus;
import catan.domain.model.user.UserBean;
import catan.domain.exception.GameException;
import catan.services.GameService;
import catan.services.util.game.GameUtil;
import catan.services.util.random.RandomUtil;
import catan.services.util.map.MapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service("gameService")
@Transactional
public class GameServiceImpl implements GameService {
    private Logger log = LoggerFactory.getLogger(GameService.class);
    public static final int MAX_DUPLICATES_RATIO = 5;
    public static final int START_NUMBER_OF_DIGITS_IN_PRIVATE_CODE = 4;
    public static final int MIN_USERS = 3;
    public static final int MAX_USERS = 4;
    public static final int MIN_TARGET_VICTORY_POINTS = 2;
    public static final int ROUND_MAP_SIZE = 2;

    public static final String ERROR_CODE_ERROR = "ERROR";
    public static final String TOO_MANY_PLAYERS_ERROR = "TOO_MANY_PLAYERS";
    public static final String ALREADY_JOINED_ERROR = "ALREADY_JOINED";
    public static final String INVALID_CODE_ERROR = "INVALID_CODE";
    public static final String GAME_IS_NOT_FOUND_ERROR = "GAME_IS_NOT_FOUND";
    public static final String USER_IS_NOT_JOINED_ERROR = "USER_IS_NOT_JOINED";
    public static final String GUEST_NOT_PERMITTED_ERROR = "GUEST_NOT_PERMITTED";

    private GameDao gameDao;
    private GameUtil gameUtil;
    private RandomUtil randomUtil;
    private MapUtil mapUtil;

    @Override
    synchronized public GameBean createNewGame(UserBean creator, boolean privateGame, String inputTargetVictoryPoints) throws GameException {
        log.debug("Creating new " + (privateGame ? "private" : "public") + " game, creator: " + creator + ", victory points: " + inputTargetVictoryPoints + " ...");

        validateUserNotEmpty(creator);

        int targetVictoryPoints = gameUtil.toValidVictoryPoints(inputTargetVictoryPoints);
        GameBean game = privateGame
                ? createPrivateGame(creator, targetVictoryPoints)
                : createPublicGame(creator, targetVictoryPoints);

        gameUtil.addUserToGame(game, creator);
        mapUtil.generateNewRoundGameMap(game, ROUND_MAP_SIZE);
        gameDao.addNewGame(game);

        log.debug("New game successfully created, {}", game);
        return game;
    }

    @Override
    public List<GameBean> getListOfGamesWithJoinedUser(UserBean user) throws GameException {
        log.debug("Getting list of games joined by " + user + " ...");

        validateUserNotEmpty(user);
        List<GameBean> games = gameDao.getGamesWithJoinedUser(user.getId());

        log.debug(games.size() + " games joined by " + user + " successfully retrieved");
        return games;
    }

    @Override
    public List<GameBean> getListOfAllPublicGames() {
        log.debug("Getting list of all public games ...");

        List<GameBean> games = gameDao.getAllNewPublicGames();

        log.debug("" + games.size() + " games successfully retrieved");
        return games;
    }

    @Override
    synchronized public GameBean joinGameByIdentifier(UserBean user, String gameId, boolean privateGame) throws GameException {
        log.debug("Join " + user + " to "
                + (privateGame ? "private" : "public") + " game with "
                + (privateGame ? "privateCode" : "id") + " '" + gameId + "' ...");

        validateUserNotEmpty(user);
        validateGameIdNotEmpty(gameId);

        GameBean game = privateGame
                ? gameUtil.findPrivateGame(gameId)
                : gameUtil.findPublicGame(gameId);

        validateGameStatusIsNew(game);
        validateMaxPlayersLimitNotReached(game);

        gameUtil.addUserToGame(game, user);
        gameDao.updateGame(game);

        log.debug(user + " successfully joined game " + game);
        return game;
    }

    @Override
    public GameBean getGameByGameIdWithJoinedUser(UserBean user, String gameId) throws GameException {
        log.debug("Getting game by gameId '" + gameId + "' for " + user + " ...");

        validateUserNotEmpty(user);
        validateGameIdNotEmpty(gameId);

        GameBean game = gameUtil.getGameById(gameId, GAME_IS_NOT_FOUND_ERROR);

        validateGameStatusIsNotCancelled(game);

        for (GameUserBean gameUser : game.getGameUsers()) {
            if (gameUser.getUser().getId() == user.getId()) {
                log.debug("Successfully found game that is joined by user specified, " + game);
                return game;
            }
        }

        log.debug(user + " is not joined to " + game);
        throw new GameException(USER_IS_NOT_JOINED_ERROR);
    }

    @Override
    public void leaveGame(UserBean user, String gameId) throws GameException {
        log.debug("Leaving " + user + " from game with gameId '" + gameId + "' ...");

        validateUserNotEmpty(user);
        validateGameIdNotEmpty(gameId);

        GameBean game = gameUtil.getGameById(gameId, ERROR_CODE_ERROR);

        validateGameStatusIsNew(game);
        validateUserIsNotCreator(user, game);

        Iterator<GameUserBean> it = game.getGameUsers().iterator();
        while (it.hasNext()) {
            GameUserBean gameUser = it.next();

            if (gameUser.getUser().getId() == user.getId()) {
                it.remove();
                gameDao.updateGame(game);
                log.debug(user + " successfully left the " + game);
                return;
            }
        }

        log.debug( user + " is not joined to " + game);
        throw new GameException(ERROR_CODE_ERROR);
    }

    @Override
    public void cancelGame(UserBean user, String gameId) throws GameException {
        log.debug("Canceling game with gameId '" + gameId + "' ...");

        validateUserNotEmpty(user);
        validateGameIdNotEmpty(gameId);

        GameBean game = gameUtil.getGameById(gameId, ERROR_CODE_ERROR);

        validateGameStatusIsNew(game);
        validateUserIsCreator(user, game);

        game.getGameUsers().clear();
        game.setStatus(GameStatus.CANCELLED);
        gameDao.updateGame(game);

        log.debug("Successfully cancelled " + game);
    }

    @Override
    synchronized public void updateGameUserStatus(UserBean user, String gameId, boolean readyForGame) throws GameException {
        log.debug("Setting status ready for user {} for game {}", user, gameId);

        validateUserNotEmpty(user);
        validateGameIdNotEmpty(gameId);

        GameBean game = gameUtil.getGameById(gameId, ERROR_CODE_ERROR);

        validateGameStatusIsNew(game);

        GameUserBean gameUserBean = null;
        for (GameUserBean gameUser : game.getGameUsers()) {
            if (gameUser.getUser().equals(user)) {
                gameUserBean = gameUser;
                break;
            }
        }

        //TODO: Think about throwing exception in case when gameUser not found.
        if (gameUserBean == null) {
            log.debug("User can set ready status only for joined game {}", game.getStatus());
            throw new GameException(ERROR_CODE_ERROR);
        }

        if (gameUserBean.isReady() == readyForGame) {
            log.debug("{} already set ready status for game , skipping, {}", user, game);
            return;
        }

        gameUserBean.setReady(readyForGame);
        gameDao.updateGameUser(gameUserBean);
        gameUtil.startGame(game);

        log.debug("{} successfully updated status to ready for {}", user, game);
    }

    private void validateUserNotEmpty(UserBean user) throws GameException {
        if (user == null) {
            log.debug("Cannot proceed due to user is empty");
            throw new GameException(ERROR_CODE_ERROR);
        }
    }

    private void validateGameIdNotEmpty(String gameId) throws GameException {
        if (gameId == null || gameId.trim().length() == 0) {
            log.debug("Cannot get game with empty gameId");
            throw new GameException(ERROR_CODE_ERROR);
        }
    }

    private void validateMaxPlayersLimitNotReached(GameBean game) throws GameException {
        if (game.getGameUsers().size() == game.getMaxPlayers()) {
            log.debug("Number of players is already up to limit");
            throw new GameException(TOO_MANY_PLAYERS_ERROR);
        }
    }

    private void validateGameStatusIsNew(GameBean game) throws GameException {
        if (!GameStatus.NEW.equals(game.getStatus())) {
            switch (game.getStatus()) {
                case PLAYING:
                    log.debug("Cannot proceed doe to game is already started");
                    throw new GameException(ERROR_CODE_ERROR);
                case FINISHED:
                    log.debug("Cannot proceed doe to game is finished");
                    throw new GameException(ERROR_CODE_ERROR);
                case CANCELLED:
                    log.debug("Cannot proceed doe to game is cancelled");
                    throw new GameException(ERROR_CODE_ERROR);
                default:
                    log.debug("Invalid game status");
                    throw new GameException(ERROR_CODE_ERROR);
            }
        }
    }

    private void validateGameStatusIsNotCancelled(GameBean game) throws GameException {
        if (GameStatus.CANCELLED.equals(game.getStatus())) {
            log.debug("Game details cannot be retrieved when game status is CANCELLED");
            throw new GameException(ERROR_CODE_ERROR);
        }
    }

    private void validateUserIsNotCreator(UserBean user, GameBean game) throws GameException {
        if (user.getId() == game.getCreator().getId()) {
            log.debug("Cannot proceed as user is creator");
            throw new GameException(ERROR_CODE_ERROR);
        }
    }

    private void validateUserIsCreator(UserBean user, GameBean game) throws GameException {
        if (user.getId() != game.getCreator().getId()) {
            log.debug("Cannot proceed as user is not a creator");
            throw new GameException(ERROR_CODE_ERROR);
        }
    }

    private GameBean createPrivateGame(UserBean creator, int targetVictoryPoints) {
        List<String> usedCodes = gameDao.getUsedActiveGamePrivateCodes();

        String randomPrivateCode = null;
        int numberOfDuplicates = 0;

        while (numberOfDuplicates != -1) {
            int numberOfDigits = START_NUMBER_OF_DIGITS_IN_PRIVATE_CODE + numberOfDuplicates / MAX_DUPLICATES_RATIO;
            randomPrivateCode = randomUtil.generateRandomPrivateCode(numberOfDigits);

            if (usedCodes.contains(randomPrivateCode)) {
                numberOfDuplicates++;
            } else {
                numberOfDuplicates = -1;
            }
        }

        return new GameBean(
                creator,
                randomPrivateCode,
                new Date(),
                GameStatus.NEW,
                MIN_USERS,
                MAX_USERS,
                targetVictoryPoints);
    }

    private GameBean createPublicGame(UserBean creator, int targetVictoryPoints) throws GameException {
        if (creator.isGuest()) {
            log.debug("Guest user cannot create new public games");
            throw new GameException(GUEST_NOT_PERMITTED_ERROR);
        }

        return new GameBean(
                creator,
                new Date(),
                GameStatus.NEW,
                MIN_USERS,
                MAX_USERS,
                targetVictoryPoints);
    }

    @Autowired
    public void setGameDao(GameDao gameDao) {
        this.gameDao = gameDao;
    }

    @Autowired
    public void setGameUtil(GameUtil gameUtil) {
        this.gameUtil = gameUtil;
    }

    @Autowired
    public void setRandomUtil(RandomUtil randomUtil) {
        this.randomUtil = randomUtil;
    }

    @Autowired
    public void setMapUtil(MapUtil mapUtil) {
        this.mapUtil = mapUtil;
    }
}
