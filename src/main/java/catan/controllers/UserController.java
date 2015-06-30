package catan.controllers;

import catan.domain.UserBean;
import catan.domain.transfer.output.ErrorDetails;
import catan.domain.transfer.output.SessionTokenDetails;
import catan.domain.transfer.output.UserDetails;
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
    public SessionTokenDetails login(@FormParam("username") String username,
                                     @FormParam("password") String password) {
        try {
            String token = userService.login(username, password);

            SessionTokenDetails sessionTokenDetails = new SessionTokenDetails();
            sessionTokenDetails.setToken(token);

            return sessionTokenDetails;
        } catch (UserException e) {
            throw webApplicationException(e);
        }
    }

    @POST
    @Path("logout")
    @Produces({MediaType.APPLICATION_JSON})
    public Response logout(@FormParam("token") String token) {
        try {
            userService.logout(token);
            return Response
                    .status(Response.Status.OK)
                    .entity("")
                    .build();
        } catch (UserException e) {
            throw webApplicationException(e);
        }
    }

    @POST
    @Path("register")
    @Produces({MediaType.APPLICATION_JSON})
    public Response register(@FormParam("username") String username,
                             @FormParam("password") String password) {
        try {
            userService.register(username, password);
            return Response
                    .status(Response.Status.OK)
                    .entity("")
                    .build();
        } catch (UserException e) {
            throw webApplicationException(e);
        }
    }

    @POST
    @Path("details")
    @Produces({MediaType.APPLICATION_JSON})
    public UserDetails getUserDetailsByToken(@FormParam("token") String token) {
        try {
            UserBean user = userService.getUserDetailsByToken(token);

            UserDetails userDetails = new UserDetails();
            userDetails.setUsername(user.getUsername());
            userDetails.setFirstName(user.getFirstName());
            userDetails.setLastName(user.getLastName());

            return userDetails;
        } catch (UserException e) {
            throw webApplicationException(e);
        }
    }


    private WebApplicationException webApplicationException(UserException e) {
        ErrorDetails details = new ErrorDetails(e.getErrorCode());
        Response.Status status = Response.Status.BAD_REQUEST;

        if (UserServiceImpl.ERROR_CODE_TOKEN_INVALID.equals(e.getErrorCode())) {
            details = null;
            status = Response.Status.FORBIDDEN;
        }

        return new WebApplicationException(Response
                .status(status)
                .entity(details)
                .build());
    }
}
