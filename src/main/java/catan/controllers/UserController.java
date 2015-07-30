package catan.controllers;

import catan.domain.model.user.UserBean;
import catan.domain.transfer.output.SessionTokenDetails;
import catan.domain.transfer.output.UserDetails;
import catan.exception.AuthenticationException;
import catan.exception.UserException;
import catan.services.AuthenticationService;
import catan.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    UserService userService;
    AuthenticationService authenticationService;

    @RequestMapping(value = "login",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public SessionTokenDetails login(@RequestParam("username") String username,
                                     @RequestParam("password") String password) throws UserException {
        String token = userService.loginUser(username, password);

        return new SessionTokenDetails(token);
    }

    @RequestMapping(value = "logout",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void logout(@RequestParam(value = "token", required = false) String token) throws UserException {
        userService.logout(token);
    }

    @RequestMapping(value = "register",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void register(@RequestParam("username") String username,
                         @RequestParam("password") String password) throws UserException {
        userService.registerUser(username, password);
    }

    @RequestMapping(value = "register/guest",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public SessionTokenDetails addGuestUser(@RequestParam("username") String username) throws UserException {
        userService.registerGuest(username);
        String token = userService.loginGuest(username);

        return new SessionTokenDetails(token);
    }

    @RequestMapping(value = "details",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDetails getUserDetailsByToken(@RequestParam(value = "token", required = false) String token) throws AuthenticationException {
        UserBean user = authenticationService.authenticateUserByToken(token);

        return new UserDetails(user);
    }

    public UserService getUserService() {
        return userService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
}
