package catan.controllers;

import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.user.UserBean;
import catan.domain.transfer.output.GameDetails;
import catan.domain.transfer.output.GameUserDetails;
import catan.domain.transfer.output.UserDetails;
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

        return toGameDetails(createdGame);
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
                //TODO: throw something like page not found exception (as soon as BG-7 is fixed)
                throw new GameException(ERROR_CODE_ERROR);
        }

        List<GameDetails> gamesToReturn = new ArrayList<GameDetails>();
        for (GameBean game : games) {
            GameDetails gameDetails = toGameDetails(game);

            gamesToReturn.add(gameDetails);
        }

        return gamesToReturn;
    }

    @RequestMapping(value = "join/public",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void joinPublicGame(@RequestParam(value = "token", required = false) String token,
                               @RequestParam("gameId") String gameId) throws AuthenticationException, GameException {
        UserBean user = authenticationService.authenticateUserByToken(token);

        gameService.joinPublicGame(user, gameId);
    }

    @RequestMapping(value = "join/private",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void joinPrivateGame(@RequestParam(value = "token", required = false) String token,
                               @RequestParam("privateCode") String privateCode) throws AuthenticationException, GameException {
        UserBean user = authenticationService.authenticateUserByToken(token);

        gameService.joinPrivateGame(user, privateCode);
    }

    private GameDetails toGameDetails(GameBean game) {
        List<GameUserDetails> gameUsers = new ArrayList<GameUserDetails>();

        for (GameUserBean gameUser : game.getGameUsers()) {
            UserBean user = gameUser.getUser();

            UserDetails userDetails = new UserDetails(user.getId(), user.getUsername());
            GameUserDetails gameUserDetails = new GameUserDetails(userDetails, gameUser.getColorId());

            gameUsers.add(gameUserDetails);
        }

        String privateCode = null;
        if(game.isPrivateGame()){
            String originalIntegerCode = String.valueOf(game.getPrivateCode());
            if(originalIntegerCode.length() < 8){
                originalIntegerCode = "0" + originalIntegerCode;
            }

            char firstLetter = (char) (65 + Integer.parseInt(originalIntegerCode.substring(0, 2)));
            char secondLetter = (char) (65 + Integer.parseInt(originalIntegerCode.substring(2, 4)));
            privateCode = "" + firstLetter + secondLetter + originalIntegerCode.substring(4);
        }

        return new GameDetails(
                game.getGameId(),
                game.getCreator().getId(),
                game.isPrivateGame(),
                privateCode,
                game.getDateCreated().getTime(),
                game.getStatus().toString(),
                gameUsers);
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
