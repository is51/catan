package catan.controllers;

import catan.domain.exception.AuthenticationException;
import catan.domain.exception.GameException;
import catan.domain.exception.PlayException;
import catan.domain.model.user.UserBean;
import catan.services.AuthenticationService;
import catan.services.PlayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


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

        playService.buildRoad(user, gameId, edgeId);
    }

    @RequestMapping(value = "build/settlement",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void buildSettlement(@RequestParam(value = "token", required = false) String token,
                          @RequestParam("gameId") String gameId,
                          @RequestParam("nodeId") String nodeId) throws AuthenticationException, GameException, PlayException {
        UserBean user = authenticationService.authenticateUserByToken(token);

        playService.buildSettlement(user, gameId, nodeId);
    }

    @RequestMapping(value = "end-turn",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void endTurn(@RequestParam(value = "token", required = false) String token,
                        @RequestParam("gameId") String gameId) throws AuthenticationException, GameException, PlayException {
        UserBean user = authenticationService.authenticateUserByToken(token);

        playService.endTurn(user, gameId);
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
