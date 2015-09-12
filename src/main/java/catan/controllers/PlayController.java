package catan.controllers;

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



    @Autowired
    public void setPlayService(PlayService playService) {
        this.playService = playService;
    }

    @Autowired
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
}
