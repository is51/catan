package catan.services;

import catan.domain.model.game.GameUserBean;

import java.util.Set;

public interface ScheduledProcessor {
    void monitorPlayerAction();

    Set<GameUserBean> getAutomatedPlayers();
}
