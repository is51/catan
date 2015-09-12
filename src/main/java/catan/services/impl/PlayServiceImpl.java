package catan.services.impl;

import catan.dao.GameDao;
import catan.domain.exception.GameException;
import catan.domain.exception.PlayException;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.types.GameStatus;
import catan.domain.model.user.UserBean;
import catan.services.GameService;
import catan.services.PlayService;
import catan.services.util.game.GameUtil;
import catan.services.util.map.MapUtil;
import catan.services.util.random.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service("playService")
@Transactional
public class PlayServiceImpl implements PlayService {
    private Logger log = LoggerFactory.getLogger(PlayService.class);

    public static final String ERROR_CODE_ERROR = "ERROR";

    private GameDao gameDao;
    private GameUtil gameUtil;

    @Override
    public void endTurn(UserBean user, String gameIdString) throws PlayException, GameException {
        log.debug("User {} tries to end his turn of game id {}",
                user == null ? "<EMPTY>" : user.getUsername(), gameIdString);
        //TODO: move to common validation method
        if (user == null) {
            log.debug("User should not be empty");
            throw new PlayException(ERROR_CODE_ERROR);
        }

        GameBean game = gameUtil.getGameById(gameIdString, ERROR_CODE_ERROR);

        //TODO: move to util method and refactor all other places
        GameUserBean gameUserBean = null;
        for (GameUserBean gameUser : game.getGameUsers()) {
            if (gameUser.getUser().equals(user)) {
                gameUserBean = gameUser;
                break;
            }
        }

        if (gameUserBean == null) {
            log.debug("User is not joined to game with specified id %s", gameIdString);
            throw new PlayException(ERROR_CODE_ERROR);
        }

        if(!game.getCurrentMove().equals(gameUserBean.getMoveOrder())){
            log.debug("It is not current turn of user {}", user.getUsername());
            throw new PlayException(ERROR_CODE_ERROR);
        }

        //TODO: think about naming of method and namings of fields related to moveOrder and turn
        giveCurrentMoveToNextPlayer(game);

        gameDao.updateGame(game);
    }

    private void giveCurrentMoveToNextPlayer(GameBean game) {
        int nextMove = 1;
        if(!game.getCurrentMove().equals(game.getGameUsers().size())){
            nextMove = game.getCurrentMove() + 1;
        }

        game.setCurrentMove(nextMove);
    }

    @Autowired
    public void setGameDao(GameDao gameDao) {
        this.gameDao = gameDao;
    }

    @Autowired
    public void setGameUtil(GameUtil gameUtil) {
        this.gameUtil = gameUtil;
    }


}
