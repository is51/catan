package catan.controllers;

import catan.domain.model.game.GameBean;
import catan.domain.model.user.UserBean;
import catan.domain.transfer.output.GameDetails;
import catan.exception.AuthenticationException;
import catan.exception.GameException;
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

import static catan.services.impl.GameServiceImpl.ERROR_CODE_ERROR;

@RestController
@RequestMapping("/api/game")
public class GameController {

    GameService gameService;
    AuthenticationService authenticationService;

    @RequestMapping(value = "create",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public GameDetails createNewGame(@RequestParam(value = "token", required = false) String token,
                                     @RequestParam("privateGame") boolean privateGame) throws AuthenticationException, GameException {
        UserBean user = authenticationService.authenticateUserByToken(token);
        GameBean createdGame = gameService.createNewGame(user, privateGame);

        GameDetails gameDetails = new GameDetails();
        gameDetails.setGameId(createdGame.getGameId());
        gameDetails.setCreatorId(createdGame.getCreator().getId());
        gameDetails.setPrivateGame(createdGame.isPrivateGame());
        gameDetails.setDateCreated(createdGame.getDateCreated().getTime());

        return gameDetails;
    }

    @RequestMapping(value = "list/{gameListType}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<GameDetails> getListOfGames(@PathVariable(value = "gameListType") String gameListType,
                                            @RequestParam(value = "token", required = false) String token) throws AuthenticationException, GameException {

        List<GameBean> games;
        switch (GameListType.fromValue(gameListType)) {
            case CURRENT:
                UserBean user = authenticationService.authenticateUserByToken(token);
                games = gameService.getListOfGamesCreatedBy(user);
                break;
            case PUBLIC:
                games = gameService.getListOfAllPublicGames();
                break;
            case UNKNOWN:
            default:
                //TODO: throw something like page not found exception
                throw new GameException(ERROR_CODE_ERROR);
        }

        List<GameDetails> gamesToReturn = new ArrayList<GameDetails>();
        for (GameBean game : games) {
            GameDetails gameDetails = new GameDetails();
            gameDetails.setGameId(game.getGameId());
            gameDetails.setCreatorId(game.getCreator().getId());
            gameDetails.setPrivateGame(game.isPrivateGame());
            gameDetails.setDateCreated(game.getDateCreated().getTime());

            gamesToReturn.add(gameDetails);
        }

        return gamesToReturn;
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
