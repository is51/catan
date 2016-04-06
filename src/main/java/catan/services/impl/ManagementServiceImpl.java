package catan.services.impl;

import catan.dao.GameDao;
import catan.dao.UserDao;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.user.UserBean;
import catan.services.ManagementService;
import catan.services.ScheduledProcessor;
import catan.services.util.game.GameUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("managementService")
@Transactional
public class ManagementServiceImpl implements ManagementService {

    @Autowired
    private GameDao gameDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private GameUtil gameUtil;
    @Autowired
    private ScheduledProcessor scheduledProcessor;

    @Override
    public void startAutomatePlayerLifeCycle(String secretKey, String gameId, String userName) {
        try {
            UserBean user = userDao.getUserByUsername(userName);
            GameBean game = gameDao.getGameByGameId(Integer.valueOf(gameId));
            GameUserBean player = gameUtil.getGameUserJoinedToGame(user, game);

            scheduledProcessor.getAutomatedPlayers().add(player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopAutomatePlayerLifeCycle(String secretKey, String gameId, String userName) {
        try {
            UserBean user = userDao.getUserByUsername(userName);
            GameBean game = gameDao.getGameByGameId(Integer.valueOf(gameId));
            GameUserBean player = gameUtil.getGameUserJoinedToGame(user, game);

            scheduledProcessor.getAutomatedPlayers().remove(player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
