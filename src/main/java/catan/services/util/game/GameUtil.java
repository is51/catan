package catan.services.util.game;

import catan.dao.GameDao;
import catan.domain.exception.GameException;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.types.GameStage;
import catan.domain.model.game.types.GameStatus;
import catan.domain.model.game.types.GameUserActionCode;
import catan.domain.model.user.UserBean;
import catan.services.util.play.PlayUtil;
import catan.services.util.random.RandomUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static catan.services.impl.GameServiceImpl.ALREADY_JOINED_ERROR;
import static catan.services.impl.GameServiceImpl.ERROR_CODE_ERROR;
import static catan.services.impl.GameServiceImpl.INVALID_CODE_ERROR;
import static catan.services.impl.GameServiceImpl.MIN_TARGET_VICTORY_POINTS;

@Component
public class GameUtil {
    private Logger log = LoggerFactory.getLogger(GameUtil.class);

    private GameDao gameDao;
    private RandomUtil randomUtil;
    private PlayUtil playUtil;

    private static final Gson GSON = new Gson();
    private static final Map<Integer, List<List<GameUserActionCode>>> initialBuildingsSetsMap = new HashMap<Integer, List<List<GameUserActionCode>>>();

    static {
        initialBuildingsSetsMap.put(1, Arrays.asList(
                Arrays.asList(GameUserActionCode.BUILD_SETTLEMENT, GameUserActionCode.BUILD_ROAD),
                Arrays.asList(GameUserActionCode.BUILD_SETTLEMENT, GameUserActionCode.BUILD_ROAD)));
        initialBuildingsSetsMap.put(2, Arrays.asList(
                Arrays.asList(GameUserActionCode.BUILD_SETTLEMENT, GameUserActionCode.BUILD_ROAD),
                Arrays.asList(GameUserActionCode.BUILD_CITY, GameUserActionCode.BUILD_ROAD),
                Arrays.asList(GameUserActionCode.BUILD_SETTLEMENT, GameUserActionCode.BUILD_ROAD)));
    }

    public int toValidVictoryPoints(String inputTargetVictoryPoints) throws GameException {
        int targetVictoryPoints;
        try {
            targetVictoryPoints = Integer.parseInt(inputTargetVictoryPoints);
        } catch (Exception e) {
            log.error("Cannot create game with non-integer format of victory points");
            throw new GameException(ERROR_CODE_ERROR);
        }

        if (targetVictoryPoints < MIN_TARGET_VICTORY_POINTS) {
            log.error("Cannot create game with less than " + MIN_TARGET_VICTORY_POINTS + " victory points");
            throw new GameException(ERROR_CODE_ERROR);
        }
        return targetVictoryPoints;
    }

    public String toValidInitialBuildingsSet(String inputInitialBuildingsSetId) throws GameException {
        int initialBuildingsSetId;
        try {
            initialBuildingsSetId = Integer.parseInt(inputInitialBuildingsSetId);
        } catch (Exception e) {
            log.error("Cannot create game with non-integer format of InitialBuildingsSetId");
            throw new GameException(ERROR_CODE_ERROR);
        }

        List<List<GameUserActionCode>> buildingsSet = initialBuildingsSetsMap.get(initialBuildingsSetId);
        if (buildingsSet == null) {
            log.error("Cannot create game with unknown initialBuildingsSetId");
            throw new GameException(ERROR_CODE_ERROR);
        }

        return GSON.toJson(buildingsSet, new TypeToken<List<List<GameUserActionCode>>>() {
        }.getType());
    }

    public GameBean getGameById(String gameIdString, String errorCodeToReturnIfNotFound) throws GameException {
        int gameId;
        try {
            gameId = Integer.parseInt(gameIdString);
        } catch (Exception e) {
            log.error("Cannot convert gameId to integer value");
            throw new GameException(ERROR_CODE_ERROR);
        }

        GameBean game = gameDao.getGameByGameId(gameId);
        if (game == null) {
            log.error("Game with such game id doesn't exists");
            throw new GameException(errorCodeToReturnIfNotFound);
        }

        return game;
    }

    public GameUserBean getGameUserJoinedToGame(UserBean user, String gameId) throws GameException {
        GameBean game = getGameById(gameId, ERROR_CODE_ERROR);

        return  getGameUserJoinedToGame(user, game);
    }

    public GameUserBean getGameUserJoinedToGame(UserBean user, GameBean game) throws GameException {
        for (GameUserBean gameUser : game.getGameUsers()) {
            if (gameUser.getUser().equals(user)) {
                return gameUser;
            }
        }

        log.error("User is not joined to game {}", game.getGameId());
        throw new GameException(ERROR_CODE_ERROR);
    }

    public GameBean findPrivateGame(String privateCode) throws GameException {
        GameBean game = gameDao.getGameByPrivateCode(privateCode);
        if (game == null) {
            log.error("Game with such private code doesn't exists");
            throw new GameException(INVALID_CODE_ERROR);
        }

        return game;
    }

    public GameBean findPublicGame(String gameIdentifier) throws GameException {
        GameBean game = getGameById(gameIdentifier, ERROR_CODE_ERROR);

        if (game.isPrivateGame()) {
            log.error("Game with id '" + game.getGameId() + "' is private," +
                    " but join public game was initiated");
            throw new GameException(ERROR_CODE_ERROR);
        }

        return game;
    }

    public void addUserToGame(GameBean game, UserBean user) throws GameException {
        for (GameUserBean alreadyJoinedGameUser : game.getGameUsers()) {
            if (alreadyJoinedGameUser.getUser().getId() == user.getId()) {
                log.error("User " + user + " already joined to this game");
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


    public boolean enoughPlayersToStartGame(GameBean game) {
        if (game.getMinPlayers() > game.getGameUsers().size()) {
            log.info("There are not enough players to start game {}. Game will start when players count will be {}, current count is {}",
                    game.getGameId(), game.getMaxPlayers(), game.getGameUsers().size());
            return false;
        }

        log.info("Enough players to start game");
        return true;
    }

    public boolean allPlayersAreReady(GameBean game) {
        log.info("Checking if all joined players are READY");

        for (GameUserBean userBean : game.getGameUsers()) {
            if (!userBean.isReady()) {
                log.info("Cannot start game as not all joined players are ready, players: {}", game.getGameUsers());
                return false;
            }
        }

        log.info("All players are READY");
        return true;
    }

    public void startGame(GameBean game) throws GameException {
        log.info("Starting game ", game.getGameId());

        populatePlayersMoveOrderRandomly(game.getGameUsers());

        game.setCurrentMove(1);
        game.setStatus(GameStatus.PLAYING);
        game.setStage(GameStage.PREPARATION);
        game.setPreparationCycle(1);
        game.setCurrentCycleBuildingNumber(1);
        game.setDateStarted(new Date());
        playUtil.updateAvailableActionsForAllUsers(game);

        gameDao.updateGame(game);

        log.info("Game successfully started: {}", game);
    }

    private void populatePlayersMoveOrderRandomly(Set<GameUserBean> players) {
        List<Integer> moveOrderSequence = new ArrayList<Integer>();
        for (int i = 1; i <= players.size(); i++) {
            moveOrderSequence.add(i);
        }
        log.debug("Possible move order sequence: {} ", moveOrderSequence);

        for (GameUserBean gameUser : players) {
            Integer moveOrder = randomUtil.pullRandomMoveOrder(moveOrderSequence);
            log.debug("Random move order calculated is: {}", moveOrder);

            gameUser.setMoveOrder(moveOrder);
            log.debug("GameUser (id: {}, username: {}) moves: {}", gameUser.getGameUserId(), gameUser.getUser().getUsername(), gameUser.getMoveOrder());
        }
    }


    @Autowired
    public void setGameDao(GameDao gameDao) {
        this.gameDao = gameDao;
    }

    @Autowired
    public void setRandomUtil(RandomUtil randomUtil) {
        this.randomUtil = randomUtil;
    }

    @Autowired
    public void setPlayUtil(PlayUtil playUtil) {
        this.playUtil = playUtil;
    }
}
