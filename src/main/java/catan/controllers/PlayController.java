package catan.controllers;

import catan.domain.exception.AuthenticationException;
import catan.domain.exception.GameException;
import catan.domain.exception.PlayException;
import catan.domain.model.game.types.GameUserActionCode;
import catan.domain.model.user.UserBean;
import catan.domain.transfer.output.game.BoughtCardDetails;
import catan.domain.transfer.output.game.MonopolyCardUsageDetails;
import catan.domain.transfer.output.game.RoadBuildingCardUsageDetails;
import catan.services.AuthenticationService;
import catan.services.PlayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


@RestController
@RequestMapping("/api/play")
public class PlayController {

    PlayService playService;
    AuthenticationService authenticationService;

    @RequestMapping(value = "build/road",
            method = POST,
            produces = APPLICATION_JSON_VALUE)
    public void buildRoad(@RequestParam(value = "token", required = false) String token,
                          @RequestParam("gameId") String gameId,
                          @RequestParam("edgeId") String edgeId) throws AuthenticationException, GameException, PlayException {
        UserBean user = authenticationService.authenticateUserByToken(token);

        Map<String, String> params = new HashMap<String, String>();
        params.put("edgeId", edgeId);

        playService.processAction(GameUserActionCode.BUILD_ROAD, user, gameId, params);
    }

    @RequestMapping(value = "build/settlement",
            method = POST,
            produces = APPLICATION_JSON_VALUE)
    public void buildSettlement(@RequestParam(value = "token", required = false) String token,
                                @RequestParam("gameId") String gameId,
                                @RequestParam("nodeId") String nodeId) throws AuthenticationException, GameException, PlayException {
        UserBean user = authenticationService.authenticateUserByToken(token);

        Map<String, String> params = new HashMap<String, String>();
        params.put("nodeId", nodeId);

        playService.processAction(GameUserActionCode.BUILD_SETTLEMENT, user, gameId, params);
    }

    @RequestMapping(value = "build/city",
            method = POST,
            produces = APPLICATION_JSON_VALUE)
    public void buildCity(@RequestParam(value = "token", required = false) String token,
                          @RequestParam("gameId") String gameId,
                          @RequestParam("nodeId") String nodeId) throws AuthenticationException, GameException, PlayException {
        UserBean user = authenticationService.authenticateUserByToken(token);

        Map<String, String> params = new HashMap<String, String>();
        params.put("nodeId", nodeId);

        playService.processAction(GameUserActionCode.BUILD_CITY, user, gameId, params);
    }

    @RequestMapping(value = "end-turn",
            method = POST,
            produces = APPLICATION_JSON_VALUE)
    public void endTurn(@RequestParam(value = "token", required = false) String token,
                        @RequestParam("gameId") String gameId) throws AuthenticationException, GameException, PlayException {
        UserBean user = authenticationService.authenticateUserByToken(token);

        playService.processAction(GameUserActionCode.END_TURN, user, gameId);
    }

    @RequestMapping(value = "throw-dice",
            method = POST,
            produces = APPLICATION_JSON_VALUE)
    public void throwDice(@RequestParam(value = "token", required = false) String token,
                          @RequestParam("gameId") String gameId) throws AuthenticationException, GameException, PlayException {
        UserBean user = authenticationService.authenticateUserByToken(token);

        playService.processAction(GameUserActionCode.THROW_DICE, user, gameId);
    }

    @RequestMapping(value = "buy/card",
            method = POST,
            produces = APPLICATION_JSON_VALUE)
    public BoughtCardDetails buyCard(@RequestParam(value = "token", required = false) String token,
                                     @RequestParam("gameId") String gameId) throws AuthenticationException, GameException, PlayException {
        UserBean user = authenticationService.authenticateUserByToken(token);

        Map<String, String> returnedParams = playService.processAction(GameUserActionCode.BUY_CARD, user, gameId);
        String developmentCardCode = returnedParams.get("card");

        return new BoughtCardDetails(developmentCardCode);
    }

    @RequestMapping(value = "use-card/year-of-plenty",
            method = POST,
            produces = APPLICATION_JSON_VALUE)
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

    @RequestMapping(value = "use-card/monopoly",
            method = POST,
            produces = APPLICATION_JSON_VALUE)
    public MonopolyCardUsageDetails useCardMonopoly(@RequestParam(value = "token", required = false) String token,
                                                    @RequestParam("gameId") String gameId,
                                                    @RequestParam("resource") String resource) throws AuthenticationException, GameException, PlayException {
        UserBean user = authenticationService.authenticateUserByToken(token);

        Map<String, String> params = new HashMap<String, String>();
        params.put("resource", resource);

        Map<String, String> returnedParams = playService.processAction(GameUserActionCode.USE_CARD_MONOPOLY, user, gameId, params);
        Integer resourcesCount = Integer.parseInt(returnedParams.get("resourcesCount"));

        return new MonopolyCardUsageDetails(resourcesCount);
    }

    @RequestMapping(value = "use-card/road-building", method = POST, produces = APPLICATION_JSON_VALUE)
    public RoadBuildingCardUsageDetails useCardRoadBuilding(@RequestParam(value = "token", required = false) String token,
                                                            @RequestParam(value = "gameId", required = true) String gameId)
            throws AuthenticationException, GameException, PlayException {
        UserBean user = authenticationService.authenticateUserByToken(token);

        Map<String, String> returnedParams = playService.processAction(GameUserActionCode.USE_CARD_ROAD_BUILDING, user, gameId);
        Integer roadsCount = Integer.parseInt(returnedParams.get("roadsCount"));

        return new RoadBuildingCardUsageDetails(roadsCount);
    }

    @RequestMapping(value = "use-card/knight", method = POST, produces = APPLICATION_JSON_VALUE)
    public void useCardKnight(@RequestParam(value = "token", required = false) String token,
                              @RequestParam(value = "gameId", required = true) String gameId)
            throws AuthenticationException, GameException, PlayException {
        UserBean user = authenticationService.authenticateUserByToken(token);

        playService.processAction(GameUserActionCode.USE_CARD_KNIGHT, user, gameId);
    }

    @RequestMapping(value = "robbery/move-robber",
            method = POST,
            produces = APPLICATION_JSON_VALUE)
    public void moveRobber(@RequestParam(value = "token", required = false) String token,
                           @RequestParam("gameId") String gameId,
                           @RequestParam("hexId") String hexId) throws AuthenticationException, GameException, PlayException {
        UserBean user = authenticationService.authenticateUserByToken(token);

        Map<String, String> params = new HashMap<String, String>();
        params.put("hexId", hexId);

        playService.processAction(GameUserActionCode.MOVE_ROBBER, user, gameId, params);
    }

    @RequestMapping(value = "robbery/choose-player-to-rob",
            method = POST,
            produces = APPLICATION_JSON_VALUE)
    public void choosePlayerToRob(@RequestParam(value = "token", required = false) String token,
                                  @RequestParam("gameId") String gameId,
                                  @RequestParam("gameUserId") String gameUserId) throws AuthenticationException, GameException, PlayException {
        UserBean user = authenticationService.authenticateUserByToken(token);

        Map<String, String> params = new HashMap<String, String>();
        params.put("gameUserId", gameUserId);

        playService.processAction(GameUserActionCode.CHOOSE_PLAYER_TO_ROB, user, gameId, params);
    }

    @RequestMapping(value = "robbery/kick-off-resources",
            method = POST,
            produces = APPLICATION_JSON_VALUE)
    public void kickOffResources(@RequestParam(value = "token", required = false) String token,
                                 @RequestParam("gameId") String gameId,
                                 @RequestParam("brick") String brick,
                                 @RequestParam("wood") String wood,
                                 @RequestParam("sheep") String sheep,
                                 @RequestParam("wheat") String wheat,
                                 @RequestParam("stone") String stone) throws AuthenticationException, GameException, PlayException {
        UserBean user = authenticationService.authenticateUserByToken(token);

        Map<String, String> params = new HashMap<String, String>();
        params.put("brick", brick);
        params.put("wood", wood);
        params.put("sheep", sheep);
        params.put("wheat", wheat);
        params.put("stone", stone);

        playService.processAction(GameUserActionCode.KICK_OFF_RESOURCES, user, gameId, params);
    }

    @RequestMapping(value = "trade/port",
            method = POST,
            produces = APPLICATION_JSON_VALUE)
    public void tradeResourcesInPort(@RequestParam(value = "token", required = false) String token,
                                     @RequestParam("gameId") String gameId,
                                     @RequestParam("brick") String brick,
                                     @RequestParam("wood") String wood,
                                     @RequestParam("sheep") String sheep,
                                     @RequestParam("wheat") String wheat,
                                     @RequestParam("stone") String stone) throws AuthenticationException, GameException, PlayException {
        UserBean user = authenticationService.authenticateUserByToken(token);

        Map<String, String> params = new HashMap<String, String>();
        params.put("brick", brick);
        params.put("wood", wood);
        params.put("sheep", sheep);
        params.put("wheat", wheat);
        params.put("stone", stone);

        playService.processAction(GameUserActionCode.TRADE_PORT, user, gameId, params);
    }

    @RequestMapping(value = "trade/propose",
            method = POST,
            produces = APPLICATION_JSON_VALUE)
    public void proposeTrade(@RequestParam(value = "token", required = false) String token,
                             @RequestParam("gameId") String gameId,
                             @RequestParam("brick") String brick,
                             @RequestParam("wood") String wood,
                             @RequestParam("sheep") String sheep,
                             @RequestParam("wheat") String wheat,
                             @RequestParam("stone") String stone) throws AuthenticationException, GameException, PlayException {
        UserBean user = authenticationService.authenticateUserByToken(token);

        Map<String, String> params = new HashMap<String, String>();
        params.put("brick", brick);
        params.put("wood", wood);
        params.put("sheep", sheep);
        params.put("wheat", wheat);
        params.put("stone", stone);

        playService.processAction(GameUserActionCode.TRADE_PROPOSE, user, gameId, params);
    }

    @RequestMapping(value = "trade/reply/accept",
            method = POST,
            produces = APPLICATION_JSON_VALUE)
    public void acceptTradeProposition(@RequestParam(value = "token", required = false) String token,
                                       @RequestParam(value = "gameId", required = true) String gameId) throws AuthenticationException, GameException, PlayException {
        UserBean user = authenticationService.authenticateUserByToken(token);

        Map<String, String> params = new HashMap<String, String>();
        params.put("tradeReply", "accept");

        playService.processAction(GameUserActionCode.TRADE_REPLY, user, gameId, params);
    }

    @RequestMapping(value = "trade/reply/decline",
            method = POST,
            produces = APPLICATION_JSON_VALUE)
    public void declineTradeProposition(@RequestParam(value = "token", required = false) String token,
                                        @RequestParam(value = "gameId", required = true) String gameId) throws AuthenticationException, GameException, PlayException {
        UserBean user = authenticationService.authenticateUserByToken(token);

        Map<String, String> params = new HashMap<String, String>();
        params.put("tradeReply", "decline");

        playService.processAction(GameUserActionCode.TRADE_REPLY, user, gameId, params);
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
