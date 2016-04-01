package catan.controllers;

import catan.domain.model.game.GameBean;
import catan.domain.model.user.UserBean;
import catan.domain.transfer.output.game.GameDetails;
import catan.domain.transfer.output.game.GameIdDetails;
import catan.domain.exception.AuthenticationException;
import catan.domain.exception.GameException;
import catan.domain.exception.WrongPathException;
import catan.services.AuthenticationService;
import catan.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/game")
public class GameController {

    GameService gameService;
    AuthenticationService authenticationService;

    @RequestMapping(value = "create",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public GameIdDetails createNewGame(@RequestParam(value = "token", required = false) String token,
                                       @RequestParam(value = "privateGame") boolean privateGame,
                                       @RequestParam(value = "targetVictoryPoints") String targetVictoryPoints,
                                       @RequestParam(value = "initialBuildingsSetId") String initialBuildingsSetId) throws AuthenticationException, GameException {
        UserBean user = authenticationService.authenticateUserByToken(token);
        GameBean createdGame = gameService.createNewGame(user, privateGame, targetVictoryPoints, initialBuildingsSetId);

        return new GameIdDetails(createdGame.getGameId());
    }

    @RequestMapping(value = "list/{gameListType}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<GameDetails> getListOfGames(@PathVariable(value = "gameListType") String gameListType,
                                            @RequestParam(value = "token", required = false) String token) throws AuthenticationException, GameException, WrongPathException {

        List<GameBean> games;
        switch (GameListType.fromValue(gameListType)) {
            case CURRENT:
                UserBean user = authenticationService.authenticateUserByToken(token);
                games = gameService.getListOfGamesWithJoinedUser(user);
                break;
            case PUBLIC:
                games = gameService.getListOfAllPublicGames();
                break;
            case UNKNOWN:
            default:
                throw new WrongPathException();
        }

        List<GameDetails> gamesToReturn = new ArrayList<GameDetails>();
        for (GameBean game : games) {
            gamesToReturn.add(new GameDetails(game, 0, false));
        }

        return gamesToReturn;
    }

    @RequestMapping(value = "join/public",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void joinPublicGame(@RequestParam(value = "token", required = false) String token,
                               @RequestParam("gameId") String gameId) throws AuthenticationException, GameException {
        UserBean user = authenticationService.authenticateUserByToken(token);

        gameService.joinGameByIdentifier(user, gameId, false);
    }

    @RequestMapping(value = "join/private",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public GameIdDetails joinPrivateGame(@RequestParam(value = "token", required = false) String token,
                                         @RequestParam(value = "privateCode", required = true) String privateCode) throws AuthenticationException, GameException {
        UserBean user = authenticationService.authenticateUserByToken(token);
        GameBean game = gameService.joinGameByIdentifier(user, privateCode, true);

        return new GameIdDetails(game.getGameId());
    }

    @RequestMapping(value = "details",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public GameDetails getGameDetails(@RequestParam(value = "token", required = false) String token,
                                      @RequestParam("gameId") String gameId) throws AuthenticationException, GameException {
        UserBean detailsRequester = authenticationService.authenticateUserByToken(token);
        GameBean game = gameService.getGameByGameIdWithJoinedUser(detailsRequester, gameId);

        return new GameDetails(game, detailsRequester.getId(), true);
    }

    @RequestMapping(value = "leave",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void leaveGame(@RequestParam(value = "token", required = false) String token,
                          @RequestParam("gameId") String gameId) throws AuthenticationException, GameException {
        UserBean user = authenticationService.authenticateUserByToken(token);
        gameService.leaveGame(user, gameId);
    }

    @RequestMapping(value = "cancel",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void cancelGame(@RequestParam(value = "token", required = false) String token,
                           @RequestParam("gameId") String gameId) throws AuthenticationException, GameException {
        UserBean user = authenticationService.authenticateUserByToken(token);
        gameService.cancelGame(user, gameId);
    }

    @RequestMapping(value = "ready",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void userReady(@RequestParam(value = "token", required = false) String token,
                          @RequestParam("gameId") String gameId
    ) throws AuthenticationException, GameException {
        UserBean user = authenticationService.authenticateUserByToken(token);
        gameService.updateGameUserStatus(user, gameId, true);
    }

    @RequestMapping(value = "not-ready",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void userNotReady(@RequestParam(value = "token", required = false) String token,
                             @RequestParam("gameId") String gameId) throws AuthenticationException, GameException {
        UserBean user = authenticationService.authenticateUserByToken(token);
        gameService.updateGameUserStatus(user, gameId, false);
    }

    @Autowired
    public void setGameService(GameService gameService) {
        this.gameService = gameService;
    }

    @Autowired
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    private enum GameListType {
        CURRENT("current"), PUBLIC("public"), UNKNOWN("");

        private String name;

        GameListType(String name) {
            this.name = name;
        }

        public static GameListType fromValue(String type) {
            for (GameListType value : values()) {
                if (value.name.equalsIgnoreCase(type)) {
                    return value;
                }
            }

            return UNKNOWN;
        }
    }
}
