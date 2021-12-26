package catan.controllers.management;

import catan.domain.exception.AuthenticationException;
import catan.services.AuthenticationService;
import catan.services.AutomationProcessor;
import catan.services.ManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/management/player/automate")
public class PlayerAutomationController {

    @Autowired
    private ManagementService managementService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private AutomationProcessor automationProcessor;

    @RequestMapping(value = "available-bots",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getListOfAvailableBots() {
        return automationProcessor.getAvailableBotNames();
    }

    @RequestMapping(value = "start",
                    method = RequestMethod.POST,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public void startAutomatePlayerLifeCycle(@RequestParam("secretKey") String secretKey,
                                             @RequestParam("gameId") String gameId,
                                             @RequestParam("userName") String userName,
                                             @RequestParam("botName") String botName) throws AuthenticationException {
        authenticationService.authenticateAdminBySecretKey(secretKey);
        managementService.startAutomatePlayerLifeCycle(gameId, userName, botName);
    }

    @RequestMapping(value = "stop",
                    method = RequestMethod.POST,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public void stopAutomatePlayerLifeCycle(@RequestParam("secretKey") String secretKey,
                                            @RequestParam("gameId") String gameId,
                                            @RequestParam("userName") String userName) throws AuthenticationException {
        authenticationService.authenticateAdminBySecretKey(secretKey);
        managementService.stopAutomatePlayerLifeCycle(gameId, userName);
    }
}