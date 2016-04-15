package catan.services.impl;

import catan.dao.GameDao;
import catan.domain.exception.GameException;
import catan.domain.exception.PlayException;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.user.UserBean;
import catan.domain.transfer.output.game.actions.ActionDetails;
import catan.domain.transfer.output.game.actions.AvailableActionsDetails;
import catan.services.AutomationProcessor;
import catan.services.PlayService;
import catan.services.util.game.GameUtil;
import catan.services.util.play.PlayUtil;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static catan.domain.model.game.types.GameUserActionCode.BUILD_CITY;
import static catan.domain.model.game.types.GameUserActionCode.BUILD_ROAD;
import static catan.domain.model.game.types.GameUserActionCode.BUILD_SETTLEMENT;
import static catan.domain.model.game.types.GameUserActionCode.BUY_CARD;
import static catan.domain.model.game.types.GameUserActionCode.CHOOSE_PLAYER_TO_ROB;
import static catan.domain.model.game.types.GameUserActionCode.END_TURN;
import static catan.domain.model.game.types.GameUserActionCode.KICK_OFF_RESOURCES;
import static catan.domain.model.game.types.GameUserActionCode.MOVE_ROBBER;
import static catan.domain.model.game.types.GameUserActionCode.THROW_DICE;
import static catan.domain.model.game.types.GameUserActionCode.TRADE_PORT;
import static catan.domain.model.game.types.GameUserActionCode.TRADE_REPLY;

@Service("scheduledProcessor")
@Transactional
public class AutomationProcessorImpl implements AutomationProcessor {
    private Logger log = LoggerFactory.getLogger(AutomationProcessor.class);

    private Map<GameUserBean, String> automatedPlayers = new HashMap<GameUserBean, String>();
    private Set<Integer> gamesWithCardsAreOver = new HashSet<Integer>();

    @Autowired
    private List<AbstractBot> bots = new ArrayList<AbstractBot>();

    @Override
    @Scheduled(fixedDelay = 6000)
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

