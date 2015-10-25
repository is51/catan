package catan.services;

import catan.domain.exception.GameException;
import catan.domain.exception.PlayException;
import catan.domain.model.user.UserBean;

public interface PlayService {

    void buildRoad(UserBean user, String gameId, String edgeId) throws PlayException, GameException;

    void buildSettlement(UserBean user, String gameId, String nodeId) throws PlayException, GameException;

    void buildCity(UserBean user, String gameId, String nodeId) throws PlayException, GameException;

    void endTurn(UserBean user, String gameId) throws PlayException, GameException;

}
