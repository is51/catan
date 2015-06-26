package catan.controllers;

import catan.services.UserService;
import catan.services.UserServiceImpl;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/user")
public class UserController {
    UserService userService = new UserServiceImpl();

    @POST
    @Path("login")
    @Produces({MediaType.APPLICATION_JSON})
    public String login(@FormParam("login") String login,
                        @FormParam("password") String password) {
        return "{token:" + userService.login(login, password) + "}";
    }

    @POST
    @Path("logout")
    @Produces({MediaType.APPLICATION_JSON})
    public void logout(@FormParam("login") String login,
                       @FormParam("token") String token) {
        userService.logout(login, token);
    }

    @POST
    @Path("register")
    @Produces({MediaType.APPLICATION_JSON})
    public void register(@FormParam("login") String login,
                       @FormParam("password") String password) {
        userService.register(login, password);
    }
}
