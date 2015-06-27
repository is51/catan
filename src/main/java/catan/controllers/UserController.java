package catan.controllers;

import catan.exception.UserException;
import catan.services.UserService;
import catan.services.UserServiceImpl;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/user")
public class UserController {
    UserService userService = new UserServiceImpl();

    @POST
    @Path("login")
    @Produces({MediaType.APPLICATION_JSON})
    public String login(@FormParam("username") String username,
                        @FormParam("password") String password) {
        try {
            String token = userService.login(username, password);

            return "{\"token\": \"" + token + "\"}";
        } catch (UserException e) {
            throw new WebApplicationException(Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("{\"errorCode\": \"" + e.getErrorCode() + "\"}")
                    .build());
        }
    }

    @POST
    @Path("logout")
    @Produces({MediaType.APPLICATION_JSON})
    public void logout(@FormParam("username") String username,
                       @FormParam("token") String token) {
        userService.logout(username, token);
    }

    @POST
    @Path("register")
    @Produces({MediaType.APPLICATION_JSON})
    public void register(@FormParam("username") String username,
                         @FormParam("password") String password) {
        try {
            userService.register(username, password);
        } catch (UserException e) {
            throw new WebApplicationException(Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("{\"errorCode\": \"" + e.getErrorCode() + "\"}")
                    .build());
        }
    }
}
