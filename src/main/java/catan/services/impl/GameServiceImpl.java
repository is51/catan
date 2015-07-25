package catan.services.impl;

import catan.dao.GameDao;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameStatus;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.user.UserBean;
import catan.exception.GameException;
import catan.services.GameService;
import catan.services.PrivateCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    public static final int MIN_VICTORY_POINTS = 2;

    public static final String ERROR_CODE_ERROR = "ERROR";
    public static final String GAME_ALREADY_STARTED_ERROR = "GAME_ALREADY_STARTED";
    public static final String GAME_FINISHED_ERROR = "GAME_FINISHED";
    public static final String GAME_CANCELED_ERROR = "GAME_CANCELED";
    public static final String TOO_MANY_PLAYERS_ERROR = "TOO_MANY_PLAYERS";
    public static final String ALREADY_JOINED_ERROR = "ALREADY_JOINED";
    public static final String INVALID_CODE_ERROR = "INVALID_CODE";
    public static final String GAME_IS_NOT_FOUND_ERROR = "GAME_IS_NOT_FOUND";
    public static final String USER_IS_NOT_JOINED_ERROR = "USER_IS_NOT_JOINED";
    public static final String GAME_HAS_ALREADY_STARTED_ERROR = "GAME_HAS_ALREADY_STARTED";
    public static final String USER_IS_TEMPORARY_ERROR = "USER_IS_TEMPORARY";

    private GameDao gameDao;

    private PrivateCodeUtil privateCodeUtil = new PrivateCodeUtil();

    @Override
    synchronized public GameBean createNewGame(UserBean creator, boolean privateGame, String inputTargetVictoryPoints) throws GameException {
        log.debug(">> Creating new " + (privateGame ? "private" : "public") + " game, creator: " + creator + ", victory points: " + inputTargetVictoryPoints + " ...");
        if (creator == null) {
            log.debug("<< Cannot create new game due to creator is empty");
            throw new GameException(ERROR_CODE_ERROR);
        }

        int targetVictoryPoints;
        try {
            targetVictoryPoints = Integer.parseInt(inputTargetVictoryPoints);
        } catch (Exception e) {
            log.debug("<< Cannot create game with non-integer format of victory points");
            throw new GameException(ERROR_CODE_ERROR);
        }

        if (targetVictoryPoints < MIN_VICTORY_POINTS) {
            log.debug("<< Cannot create game with less than " + MIN_VICTORY_POINTS + " victory points");
            throw new GameException(ERROR_CODE_ERROR);
        }

        GameBean game = privateGame ? createPrivateGame(creator, targetVictoryPoints) : createPublicGame(creator, targetVictoryPoints);
        gameDao.addNewGame(game);

        addUserToGame(game, creator);

        log.debug("<< Game '" + game + "' successfully created with creator " + creator);

        return game;
    }

    @Override
    public List<GameBean> getListOfGamesWithJoinedUser(UserBean user) throws GameException {
        log.debug(">> Getting list of games joined by " + user + " ...");
        if (user == null) {
            log.debug("<< Cannot get list of games due to user is empty");
            throw new GameException(ERROR_CODE_ERROR);
        }

        List<GameBean> games = gameDao.getGamesWithJoinedUser(user.getId());

        log.debug("<< " + games.size() + " games joined by " + user + " successfully retrieved");

        return games;
    }

    @Override
    public List<GameBean> getListOfAllPublicGames() {
        log.debug(">> Getting list of all public games ...");

        List<GameBean> games = gameDao.getAllNewPublicGames();

        log.debug("<< " + games.size() + " games successfully retrieved");

        return games;
    }

    @Override
    synchronized public void joinGameByIdentifier(UserBean user, String gameIdentifier, boolean privateGame) throws GameException {
        log.debug(">> Join user " + user + " to "
                + (privateGame ? "private" : "public") + " game with "
                + (privateGame ? "privateCode" : "id") + " '" + gameIdentifier + "' ...");
        checkPreconditions(user, gameIdentifier);

        GameBean game = privateGame ? findPrivateGame(gameIdentifier) : findPublicGame(gameIdentifier);

        for (GameUserBean gameUser : game.getGameUsers()) {
            if (gameUser.getUser().getId() == user.getId()) {
                log.debug("<< User " + user + " already joined to this game");
                throw new GameException(ALREADY_JOINED_ERROR);
            }
        }

        if (!GameStatus.NEW.equals(game.getStatus())) {
            switch (game.getStatus()) {
                case PLAYING:
                    log.debug("<< Cannot join to game that is already started");
                    throw new GameException(GAME_ALREADY_STARTED_ERROR);
                case FINISHED:
                    log.debug("<< Cannot join to game that is finished");
                    throw new GameException(GAME_FINISHED_ERROR);
                case CANCELLED:
                    log.debug("<< Cannot join to game that is cancelled");
                    throw new GameException(GAME_CANCELED_ERROR);
                default:
                    log.debug("<< Invalid game status");
                    throw new GameException(ERROR_CODE_ERROR);
            }
        }

        if (game.getGameUsers().size() == game.getMaxUsers()) {
            log.debug("<< Number of players is already up to limit");
            throw new GameException(TOO_MANY_PLAYERS_ERROR);
        }

        addUserToGame(game, user);

        log.debug("<< User " + user + " successfully joined game " + game);
    }

    @Override
    public GameBean getGameByGameIdWithJoinedUser(UserBean user, String gameIdString) throws GameException {
        log.debug(">> Getting game by gameId '" + gameIdString + "' for user " + user + " ...");
        checkPreconditions(user, gameIdString);

        GameBean game = getGameBean(gameIdString, GAME_IS_NOT_FOUND_ERROR);

        if (GameStatus.CANCELLED.equals(game.getStatus())) {
            log.debug("<< Game details cannot be retrieved when game status is CANCELLED");
            throw new GameException(GAME_CANCELED_ERROR);
        }

        for (GameUserBean gameUser : game.getGameUsers()) {
            if (gameUser.getUser().getId() == user.getId()) {
                log.debug("<< Game " + game + " with joined user " + user + " successfully found ");
                return game;
            }
        }

        log.debug("<< User " + user + " is not joined to game " + game);
        throw new GameException(USER_IS_NOT_JOINED_ERROR);
    }

    @Override
    public void leaveGame(UserBean user, String gameIdString) throws GameException {
        log.debug(">> Leaving user " + user + " from game with gameId '" + gameIdString + "' ...");
        checkPreconditions(user, gameIdString);

        GameBean game = getGameBean(gameIdString, ERROR_CODE_ERROR);

        if (GameStatus.PLAYING.equals(game.getStatus())) {
            log.debug("<< Game already started");
            throw new GameException(GAME_HAS_ALREADY_STARTED_ERROR);
        }

        if (!GameStatus.NEW.equals(game.getStatus())) {
            log.debug("<< Game can be leaved only when it is in status NEW");
            throw new GameException(ERROR_CODE_ERROR);
        }

        if (user.getId() == game.getCreator().getId()) {
            log.debug("<< Creator can't leave his own game");
            throw new GameException(ERROR_CODE_ERROR);
        }

        Iterator<GameUserBean> it = game.getGameUsers().iterator();
        while (it.hasNext()) {
            GameUserBean gameUser = it.next();

            if (gameUser.getUser().getId() == user.getId()) {
                it.remove();
                gameDao.updateGame(game);
                log.debug("<< User " + user + " successfully left the game " + game);
                return;
            }
        }

        log.debug("<< User " + user + " is not joined to game " + game);
        throw new GameException(ERROR_CODE_ERROR);
    }

    @Override
    public void cancelGame(UserBean user, String gameIdString) throws GameException {
        log.debug(">> Canceling game with gameId '" + gameIdString + "' ...");
        checkPreconditions(user, gameIdString);

        GameBean game = getGameBean(gameIdString, ERROR_CODE_ERROR);

        if (!GameStatus.NEW.equals(game.getStatus())) {
            log.debug("<< Game can be cancelled only when it is in status NEW");
            throw new GameException(ERROR_CODE_ERROR);
        }

        if (user.getId() != game.getCreator().getId()) {
            log.debug("<< Only creator can cancel the game");
            throw new GameException(ERROR_CODE_ERROR);
        }

        game.getGameUsers().clear();
        game.setStatus(GameStatus.CANCELLED);
        gameDao.updateGame(game);

        log.debug("<< Game " + game + " successfully cancelled");
    }

    @Override
    public void readyForGame(UserBean user, String gameId, boolean readyForGame) throws GameException {
        log.debug(">> Setting status ready for user {} for game {}", user, gameId );
        checkPreconditions(user, gameId);

        GameBean game = getGameBean(gameId, ERROR_CODE_ERROR);

        if (!GameStatus.NEW.equals(game.getStatus())) {
            log.debug("<< User can set ready status only for game with NEW status, current status is {}", game.getStatus());
            throw new GameException(ERROR_CODE_ERROR);
        }

        GameUserBean gameUserBean = null;
        for (GameUserBean gameUser : game.getGameUsers()) {
            if (gameUser.getUser().equals(user)) {
                gameUserBean = gameUser;
            }
        }

        if (gameUserBean == null) {
            log.debug("<< User can set ready status only for joined game {}", game.getStatus());
            throw new GameException(ERROR_CODE_ERROR);
        }

        gameUserBean.setReady(readyForGame);

        gameDao.updateGameUserBean(gameUserBean);

        log.debug("<< User {} successfully updated status to ready for game {}", user, game);
    }

    private void checkPreconditions(UserBean user, String gameId) throws GameException {
        if (user == null) {
            log.debug("<< User should not be empty");
            throw new GameException(ERROR_CODE_ERROR);
        }

        if (gameId == null || gameId.trim().length() == 0) {
            log.debug("<< Cannot get game with empty gameId");
            throw new GameException(ERROR_CODE_ERROR);
        }
    }

    private GameBean getGameBean(String gameIdString, String errorCodeToReturn) throws GameException {
        int gameId;
        try {
            gameId = Integer.parseInt(gameIdString);
        } catch (Exception e) {
            log.debug("<< Cannot convert gameId to integer value");
            throw new GameException(ERROR_CODE_ERROR);
        }

        GameBean game = gameDao.getGameByGameId(gameId);
        if (game == null) {
            log.debug("<< Game with such game id doesn't exists");
            throw new GameException(errorCodeToReturn);
        }

        return game;
    }

    private GameBean createPrivateGame(UserBean creator, int targetVictoryPoints) {
        List<String> usedCodes = gameDao.getUsedActiveGamePrivateCodes();

        String randomPrivateCode = null;
        int numberOfDuplicates = 0;

        while (numberOfDuplicates != -1) {
            int numberOfDigits = START_NUMBER_OF_DIGITS_IN_PRIVATE_CODE + numberOfDuplicates / MAX_DUPLICATES_RATIO;
            randomPrivateCode = privateCodeUtil.generateRandomPrivateCode(numberOfDigits);

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
            log.debug("<< Temporary user cannot create new public games");
            throw new GameException(USER_IS_TEMPORARY_ERROR);
        }

        return new GameBean(
                creator,
                new Date(),
                GameStatus.NEW,
                MIN_USERS,
                MAX_USERS,
                targetVictoryPoints);
    }

    private GameBean findPrivateGame(String privateCode) throws GameException {
        GameBean game = gameDao.getGameByPrivateCode(privateCode);
        if (game == null) {
            log.debug("<< Game with such private code doesn't exists");
            throw new GameException(INVALID_CODE_ERROR);
        }

        return game;
    }

    private GameBean findPublicGame(String gameIdentifier) throws GameException {
        GameBean game = getGameBean(gameIdentifier, ERROR_CODE_ERROR);

        if (game.isPrivateGame()) {
            log.debug("<< Game with id '" + game.getGameId() + "' is private," +
                    " but join public game was initiated");
            throw new GameException(ERROR_CODE_ERROR);
        }

        return game;
    }

    private void addUserToGame(GameBean game, UserBean userBean) {
        List<Integer> usedColorCodes = new ArrayList<Integer>();
        for (GameUserBean gameUser : game.getGameUsers()) {
            usedColorCodes.add(gameUser.getColorId());
        }

        int colorId = 1;
        while (usedColorCodes.contains(colorId)) {
            colorId++;
        }

        GameUserBean newGameUser = new GameUserBean(userBean, colorId);
        gameDao.addNewGameUser(newGameUser);

        game.getGameUsers().add(newGameUser);
    }

    public PrivateCodeUtil getPrivateCodeUtil() {
        return privateCodeUtil;
    }

    @Autowired
    public void setGameDao(GameDao gameDao) {
        this.gameDao = gameDao;
    }
}
