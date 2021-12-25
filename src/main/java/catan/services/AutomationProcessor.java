package catan.services;

import catan.domain.model.game.GameUserBean;

import java.util.List;
import java.util.Map;

public interface AutomationProcessor {
    void monitorNewGames();

    void monitorPlayerAction();

    Map<GameUserBean, String> getAutomatedPlayers();

    List<String> getAvailableBotNames();
}
