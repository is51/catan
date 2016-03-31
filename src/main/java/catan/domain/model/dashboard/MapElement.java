package catan.domain.model.dashboard;

import catan.domain.model.game.GameBean;

public interface MapElement {

    Integer getAbsoluteId();

    GameBean getGame();
}
