package catan.controllers;

import catan.domain.model.UserBean;
import catan.domain.transfer.output.ErrorDetails;
import catan.domain.transfer.output.SessionTokenDetails;
import catan.domain.transfer.output.UserDetails;
import catan.exception.UserException;
import catan.services.UserService;
import catan.services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@ControllerAdvice

@RequestMapping("/api/user")
public class UserController {

    UserService userService = new UserServiceImpl();

    @RequestMapping(value = "login",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public SessionTokenDetails login(@RequestParam("username") String username,
                                     @RequestParam("password") String password) throws UserException {
        String token = userService.login(username, password);

        SessionTokenDetails sessionTokenDetails = new SessionTokenDetails();
        sessionTokenDetails.setToken(token);

        return sessionTokenDetails;
    }

    @RequestMapping(value = "logout",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void logout(@RequestParam("token") String token) throws UserException {
        userService.logout(token);
    }

    @RequestMapping(value = "register",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void register(@RequestParam("username") String username,
                         @RequestParam("password") String password) throws UserException {
        userService.register(username, password);
    }

    @RequestMapping(value = "details",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDetails getUserDetailsByToken(@RequestParam("token") String token) throws UserException {
        UserBean user = userService.getUserDetailsByToken(token);

        UserDetails userDetails = new UserDetails();
        userDetails.setUsername(user.getUsername());

        return userDetails;
    }

    public UserService getUserService() {
        return userService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
