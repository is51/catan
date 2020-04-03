package catan.services.impl;

import catan.domain.exception.PlayException;
import catan.domain.model.game.GameUserBean;
import catan.services.AutomationProcessor;
import catan.services.impl.bots.AbstractBot;
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

@Service("scheduledProcessor")
@Transactional
public class AutomationProcessorImpl implements AutomationProcessor {
    private Logger log = LoggerFactory.getLogger(AutomationProcessor.class);

    private Map<GameUserBean, String> automatedPlayers = new HashMap<GameUserBean, String>();
    private Set<Integer> gamesWithCardsAreOver = new HashSet<Integer>();

    @Autowired
    private List<AbstractBot> bots = new ArrayList<AbstractBot>();

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

