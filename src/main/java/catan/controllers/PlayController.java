package catan.controllers;

import catan.domain.exception.AuthenticationException;
import catan.domain.exception.GameException;
import catan.domain.exception.PlayException;
import catan.domain.model.game.types.GameUserActionCode;
import catan.domain.model.user.UserBean;
import catan.domain.transfer.output.game.BoughtCardDetails;
import catan.services.AuthenticationService;
import catan.services.PlayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/play")
public class PlayController {

    PlayService playService;
    AuthenticationService authenticationService;

    @RequestMapping(value = "build/road",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void buildRoad(@RequestParam(value = "token", required = false) String token,
                          @RequestParam("gameId") String gameId,
                          @RequestParam("edgeId") String edgeId) throws AuthenticationException, GameException, PlayException {
        UserBean user = authenticationService.authenticateUserByToken(token);

        Map<String, String> params = new HashMap<String, String>();
        params.put("edgeId", edgeId);

        playService.processAction(GameUserActionCode.BUILD_ROAD, user, gameId, params);
    }

    @RequestMapping(value = "build/settlement",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void buildSettlement(@RequestParam(value = "token", required = false) String token,
                                @RequestParam("gameId") String gameId,
                                @RequestParam("nodeId") String nodeId) throws AuthenticationException, GameException, PlayException {
        UserBean user = authenticationService.authenticateUserByToken(token);

        Map<String, String> params = new HashMap<String, String>();
        params.put("nodeId", nodeId);

        playService.processAction(GameUserActionCode.BUILD_SETTLEMENT, user, gameId, params);
    }

    @RequestMapping(value = "build/city",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void buildCity(@RequestParam(value = "token", required = false) String token,
                          @RequestParam("gameId") String gameId,
                          @RequestParam("nodeId") String nodeId) throws AuthenticationException, GameException, PlayException {
        UserBean user = authenticationService.authenticateUserByToken(token);

        Map<String, String> params = new HashMap<String, String>();
        params.put("nodeId", nodeId);

        playService.processAction(GameUserActionCode.BUILD_CITY, user, gameId, params);
    }

    @RequestMapping(value = "end-turn",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void endTurn(@RequestParam(value = "token", required = false) String token,
                        @RequestParam("gameId") String gameId) throws AuthenticationException, GameException, PlayException {
        UserBean user = authenticationService.authenticateUserByToken(token);

        playService.processAction(GameUserActionCode.END_TURN, user, gameId);
    }

    @RequestMapping(value = "throw-dice",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void throwDice(@RequestParam(value = "token", required = false) String token,
                          @RequestParam("gameId") String gameId) throws AuthenticationException, GameException, PlayException {
        UserBean user = authenticationService.authenticateUserByToken(token);

        playService.processAction(GameUserActionCode.THROW_DICE, user, gameId);
    }

    @RequestMapping(value = "buy/card",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BoughtCardDetails buyCard(@RequestParam(value = "token", required = false) String token,
                                     @RequestParam("gameId") String gameId) throws AuthenticationException, GameException, PlayException {
        UserBean user = authenticationService.authenticateUserByToken(token);

        Map<String, String> returnedParams = playService.processAction(GameUserActionCode.BUY_CARD, user, gameId);
        String developmentCardCode = returnedParams.get("card");

        return new BoughtCardDetails(developmentCardCode);
    }

    @RequestMapping(value = "use-card/year-of-plenty",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void useCardYearOfPlenty(@RequestParam(value = "token", required = false) String token,
                                    @RequestParam("gameId") String gameId,
                                    @RequestParam("firstResource") String firstResource,
                                    @RequestParam("secondResource") String secondResource) throws AuthenticationException, GameException, PlayException {
        UserBean user = authenticationService.authenticateUserByToken(token);

        Map<String, String> params = new HashMap<String, String>();
        params.put("firstResource", firstResource);
        params.put("secondResource", secondResource);

        playService.processAction(GameUserActionCode.USE_CARD_YEAR_OF_PLENTY, user, gameId, params);
    }

    @Autowired
    public void setPlayService(PlayService playService) {
        this.playService = playService;
    }

    @Autowired
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
}
