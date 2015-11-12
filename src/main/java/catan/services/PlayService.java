package catan.services;

import catan.domain.exception.GameException;
import catan.domain.exception.PlayException;
import catan.domain.model.game.types.GameUserActionCode;
import catan.domain.model.user.UserBean;

import java.util.Map;

public interface PlayService {
    Map<String, String> processAction(GameUserActionCode action, UserBean user, String gameId) throws PlayException, GameException;

    Map<String, String> processAction(GameUserActionCode action, UserBean user, String gameId, Map<String, String> params) throws PlayException, GameException;
}
