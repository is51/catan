package catan.controllers;

import catan.domain.transfer.ErrorDetails;
import catan.domain.transfer.SessionToken;
import catan.exception.UserException;
import catan.services.UserService;
import catan.services.UserServiceImpl;

import javax.ws.rs.Consumes;
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
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public SessionToken login(@FormParam("username") String username,
                              @FormParam("password") String password) {
        try {
            String token = userService.login(username, password);

            SessionToken sessionToken = new SessionToken();
            sessionToken.setToken(token);

            return sessionToken;
        } catch (UserException e) {
            throw webApplicationException(e);
        }
    }

    @POST
    @Path("logout")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public void logout(@FormParam("token") String token) {
        try {
            userService.logout(token);
        } catch (UserException e) {
            throw webApplicationException(e);
        }
    }

    @POST
    @Path("register")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public void register(@FormParam("username") String username,
                         @FormParam("password") String password) {
        try {
            userService.register(username, password);
        } catch (UserException e) {
            throw webApplicationException(e);
        }
    }


    private WebApplicationException webApplicationException(UserException e) {
        ErrorDetails details = new ErrorDetails(e.getErrorCode());

        return new WebApplicationException(Response
                .status(Response.Status.BAD_REQUEST)
                .entity(details)
                .build());
    }
}
