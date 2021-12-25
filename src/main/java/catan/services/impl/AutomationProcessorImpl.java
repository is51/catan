package catan.services.impl;

import catan.dao.GameDao;
import catan.dao.UserDao;
import catan.domain.exception.GameException;
import catan.domain.exception.PlayException;
import catan.domain.exception.UserException;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.types.GameStatus;
import catan.domain.model.user.UserBean;
import catan.services.AutomationProcessor;
import catan.services.GameService;
import catan.services.UserService;
import catan.services.impl.bots.AbstractBot;
import catan.services.util.game.GameUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("scheduledProcessor")
@Transactional
public class AutomationProcessorImpl implements AutomationProcessor {
    private Logger log = LoggerFactory.getLogger(AutomationProcessor.class);

    private Map<GameUserBean, String> automatedPlayers = new HashMap<GameUserBean, String>();
    private Set<Integer> gamesWithCardsAreOver = new HashSet<Integer>();

    @Autowired
    private List<AbstractBot> bots = new ArrayList<AbstractBot>();

    @Autowired
    public UserService userService;

    @Autowired
    public GameService gameService;
    @Autowired
    private GameDao gameDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private GameUtil gameUtil;

    private List<String> playerThatMayBeAutomated = new ArrayList<>();


    @PostConstruct
    public void registerBots(){
        try {
            userService.registerUser("bot1", "12345");
            userService.loginUser("bot1", "12345");
            playerThatMayBeAutomated.add("bot1");

            userService.registerUser("bot2", "12345");
            userService.loginUser("bot2", "12345");
            playerThatMayBeAutomated.add("bot2");
        } catch (UserException e) {
            log.error("Failed to register bot", e);
        }

    }


    @Scheduled(fixedDelay = 5000)
    public void monitorNewGames() {
        for (String playerBot : playerThatMayBeAutomated) {
            try {
                for (GameBean game : gameService.getListOfAllPublicGames()) {
                    if (game.getStatus() != GameStatus.NEW) {
                    } else {
                        boolean alreadyJoined = false;
                        for (GameUserBean gameUser : game.getGameUsers()) {
                            if (gameUser.getUser().getUsername().equalsIgnoreCase(playerBot)) {
                                alreadyJoined = true;
                                break;
                            }
                        }

                        if(!alreadyJoined){
                            UserBean user = userDao.getUserByUsername(playerBot);
                            if (user != null) {
                                gameService.joinGameByIdentifier(user, String.valueOf(game.getGameId()), false);

                                GameUserBean player = gameUtil.getGameUserJoinedToGame(user, game);
                                if (player != null) {
                                    automatedPlayers.put(player, "SMART_BOT");
                                }

                                gameService.updateGameUserStatus(user, String.valueOf(game.getGameId()), true);


                            }
                        }
                    }
                }
            } catch (GameException e) {
                log.error("failed to register at game", e);
            }
        }
    }

    @Override
    @Scheduled(fixedDelay = 2000)
    public void monitorPlayerAction() {
        for (GameUserBean oldStatePlayer : automatedPlayers.keySet()) {
            try {
                String playersBotTypeName = automatedPlayers.get(oldStatePlayer).toUpperCase();

                for (AbstractBot bot : bots) {
                    if(bot.getBotName().equals(playersBotTypeName)){
                        boolean cardsAreOver = gamesWithCardsAreOver.contains(oldStatePlayer.getGame().getGameId());
                        bot.automatePlayersActions(oldStatePlayer, cardsAreOver);
                    }
                }
            } catch (Exception e) {
                if(e instanceof PlayException && ((PlayException)e).getErrorCode().equals("CARDS_ARE_OVER")){
                    gamesWithCardsAreOver.add(oldStatePlayer.getGame().getGameId());
                }
                log.error("Automate action was not performed for player " + oldStatePlayer.getUser().getUsername(), e);
            }
        }
    }

    @Override
    public Map<GameUserBean, String> getAutomatedPlayers() {
        return automatedPlayers;
    }

    @Override
    public List<String> getAvailableBotNames() {
        List<String> botNames = new ArrayList<String>();
        for (AbstractBot bot : bots) {
            botNames.add(bot.getBotName());
        }

        return botNames;
    }
}

