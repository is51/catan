package catan.services.impl;

import catan.dao.GameDao;
import catan.dao.UserDao;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.user.UserBean;
import catan.services.ManagementService;
import catan.services.AutomationProcessor;
import catan.services.util.game.GameUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service("managementService")
@Transactional
public class ManagementServiceImpl implements ManagementService {
    private Logger log = LoggerFactory.getLogger(ManagementService.class);

    @Autowired
    private GameDao gameDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private GameUtil gameUtil;
    @Autowired
    private AutomationProcessor automationProcessor;

    @Override
    public void startAutomatePlayerLifeCycle(String gameId, String userName, String botName) {
        log.info("Automating player {} with {} for game {}", userName, botName, gameId);

        if(StringUtils.isEmpty(gameId)){
            log.info("Parameter 'gameId' not provided");
            return;
        }
        if(StringUtils.isEmpty(userName)){
            log.info("Parameter 'userName' not provided");
            return;
        }
        if(StringUtils.isEmpty(botName)){
            log.info("Parameter 'gameId' not provided");
            return;
        }
        try {
            if(!automationProcessor.getAvailableBotNames().contains(botName)){
                log.info("Bot '{}' not found", botName);
                return;
            }

            UserBean user = userDao.getUserByUsername(userName);
            if(user == null){
                log.info("User '{}' not found", userName);
                return;
            }

            GameBean game = gameDao.getGameByGameId(Integer.valueOf(gameId));
            if(game == null){
                log.info("Game '{}' not found", gameId);
                return;
            }

            GameUserBean player = gameUtil.getGameUserJoinedToGame(user, game);
            if(player == null){
                log.info("Player '{}' for game {} not found", userName, gameId);
                return;
            }

            automationProcessor.getAutomatedPlayers().put(player, botName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopAutomatePlayerLifeCycle(String gameId, String userName) {
        log.info("Stop automating player {} with {} for game {}", userName, gameId);

        if(StringUtils.isEmpty(gameId)){
            log.info("Parameter 'gameId' not provided");
            return;
        }
        if(StringUtils.isEmpty(userName)){
            log.info("Parameter 'userName' not provided");
            return;
        }

        try {
            UserBean user = userDao.getUserByUsername(userName);
            if(user == null){
                log.info("User '{}' not found", userName);
                return;
            }

            GameBean game = gameDao.getGameByGameId(Integer.valueOf(gameId));
            if(game == null){
                log.info("Game '{}' not found", gameId);
                return;
            }

            GameUserBean player = gameUtil.getGameUserJoinedToGame(user, game);
            if(player == null){
                log.info("Player '{}' for game {} not found", userName, gameId);
                return;
            }

            automationProcessor.getAutomatedPlayers().remove(player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
