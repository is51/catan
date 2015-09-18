package catan.services;

import catan.domain.exception.GameException;
import catan.domain.exception.PlayException;
import catan.domain.model.user.UserBean;

public interface PlayService {

    void endTurn(UserBean user, String gameId) throws PlayException, GameException;

}
