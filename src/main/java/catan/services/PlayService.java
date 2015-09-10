package catan.services;

import catan.domain.exception.GameException;
import catan.domain.exception.PlayException;
import catan.domain.model.user.UserBean;

public interface PlayService {

    void buildRoad(UserBean user, String gameId, String edgeId) throws PlayException, GameException;
}
