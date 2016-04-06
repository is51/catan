package catan.controllers.management;

import catan.services.ManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/management/player/automate")
public class PlayerAutomationController {

    @Autowired
    private ManagementService managementService;

    @RequestMapping(value = "start",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void startAutomatePlayerLifeCycle(@RequestParam("secretKey") String secretKey,
                                       @RequestParam("gameId") String gameId,
                                       @RequestParam("userName") String userName) {
        //TODO: authenticate
        managementService.startAutomatePlayerLifeCycle(secretKey, gameId, userName);
    }

    @RequestMapping(value = "stop",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void stopAutomatePlayerLifeCycle(@RequestParam("secretKey") String secretKey,
                                       @RequestParam("gameId") String gameId,
                                       @RequestParam("userName") String userName) {
        //TODO: authenticate
        managementService.stopAutomatePlayerLifeCycle(secretKey, gameId, userName);
    }
}