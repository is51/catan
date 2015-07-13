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

    public static final int MIN_USERS = 3;
    public static final int MAX_USERS = 4;

    public static final String ERROR_CODE_ERROR = "ERROR";
    public static final String GAME_ALREADY_STARTED_ERROR = "GAME_ALREADY_STARTED";
    public static final String GAME_FINISHED_ERROR = "GAME_FINISHED";
    public static final String GAME_CANCELED_ERROR = "GAME_CANCELED";
    public static final String TOO_MANY_PLAYERS_ERROR = "TOO_MANY_PLAYERS";
    public static final String ALREADY_JOINED_ERROR = "ALREADY_JOINED";

    GameDao gameDao;

    @Override
    synchronized public GameBean createNewGame(UserBean creator, boolean privateGame) throws GameException {
        log.debug(">> Creating new " + (privateGame ? "private" : "public") + " game, creator: " + creator + " ...");
        if (creator == null) {
            log.debug("<< Cannot create new game due to creator is empty");
            throw new GameException(ERROR_CODE_ERROR);
        }

        GameBean game = new GameBean(creator, privateGame, new Date(), GameStatus.NEW, MIN_USERS, MAX_USERS);
        if(privateGame){
            List<Integer> usedCodes = gameDao.getUsedActiveGamePrivateCodes();
            int randomPrivateCode = -1;
            while (randomPrivateCode < 0 || usedCodes.contains(randomPrivateCode)){
                randomPrivateCode = (int)(Math.random() * 25) * 1000000 + (int)(Math.random() * 25) * 10000 + (int)(Math.random() * 9999);
            }

            game.setPrivateCode(randomPrivateCode);
        }

        gameDao.addNewGame(game);

        addUserToGame(game, creator);

        log.debug("<< Game '" + game + "' successfully created with creator " + creator);

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

        List<GameBean> games = gameDao.getAllNewPublicGames();

        log.debug("<< " + games.size() + " games successfully retrieved");

        return games;
    }

    @Override
    synchronized public void joinPublicGame(UserBean user, String gameIdString) throws GameException {
        log.debug(">> Join user " + user + " to game with id '" + gameIdString + "' ...");
        if (user == null) {
            log.debug("<< Cannot join empty user to game");
            throw new GameException(ERROR_CODE_ERROR);
        }

        if (gameIdString == null || gameIdString.trim().length() == 0) {
            log.debug("<< Cannot join user to game with empty game id");
            throw new GameException(ERROR_CODE_ERROR);
        }

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
            throw new GameException(ERROR_CODE_ERROR);
        }

        if(game.isPrivateGame()){
            log.debug("<< Game with id '" + game.getGameId() + "' is private," +
                    " but under this method user is allowed to join only public games");
            throw new GameException(ERROR_CODE_ERROR);
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

        if(game.getGameUsers().size() == game.getMaxUsers()){
            log.debug("<< Number of players is already up to limit");
            throw new GameException(TOO_MANY_PLAYERS_ERROR);
        }

        if(game.getCreator().getId() == user.getId()){
            log.debug("<< Creator is not allowed to join game, as he is already joined");
            throw new GameException(ALREADY_JOINED_ERROR);
        }

        for(GameUserBean gameUser : game.getGameUsers()){
            if(gameUser.getUser().getId() == user.getId()){
                log.debug("<< User " + user + " already joined to this game");
                throw new GameException(ALREADY_JOINED_ERROR);
            }
        }

        addUserToGame(game, user);

        log.debug("<< User " + user + " successfully joined game " + game);
    }

    @Override
    public void joinPrivateGame(UserBean user, String privateCode) {
        //TODO: implement
    }

    private void addUserToGame(GameBean game, UserBean userBean) {
        int numberOfUsers = game.getGameUsers().size();
        int colorId = numberOfUsers + 1;

        GameUserBean gameUserBean = new GameUserBean(userBean, game, colorId);
        gameDao.addNewGameUser(gameUserBean);

        game.getGameUsers().add(gameUserBean);
    }

    @Autowired
    public void setGameDao(GameDao gameDao) {
        this.gameDao = gameDao;
    }
}
