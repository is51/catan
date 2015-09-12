package catan.controllers;

import catan.domain.exception.AuthenticationException;
import catan.domain.exception.GameException;
import catan.domain.exception.PlayException;
import catan.domain.model.user.UserBean;
import catan.services.AuthenticationService;
import catan.services.PlayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @Autowired
    public void setPlayService(PlayService playService) {
        this.playService = playService;
    }

    @Autowired
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
}
