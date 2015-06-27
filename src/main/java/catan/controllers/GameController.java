
package catan.controllers;

import catan.domain.transfer.LevelDetails;
import catan.domain.UserBean;
import catan.domain.transfer.PlayerDetails;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;


@Path("/game")
public class GameController {
    private Integer currentLevelId = 1;

    @GET
    @Path("levelDetails")
    @Produces({MediaType.APPLICATION_JSON})
    public LevelDetails getLevelDetails() {
        LevelDetails levelDetails = new LevelDetails();
        levelDetails.setId(currentLevelId++);
        levelDetails.setSize(19);

        return levelDetails;
    }

    @GET
    @Path("playerDetails")
    @Produces({MediaType.APPLICATION_JSON})
    public PlayerDetails getPlayerDetails() {
        List<UserBean> players = new ArrayList<UserBean>();
        players.add(new UserBean("Andrey1", "blue", "", ""));
        players.add(new UserBean("Syrovets", "yellow", "", ""));
        players.add(new UserBean("Bork", "green", "", ""));
        players.add(new UserBean("Yuriiii", "red", "", ""));

        PlayerDetails playerDetails = new PlayerDetails();
        playerDetails.setPlayerList(players);

        return playerDetails;
    }

    @GET
    @Path("error")
    @Produces({MediaType.APPLICATION_JSON})
    public String getError() {
        throw new WebApplicationException(Response.Status.BAD_REQUEST);
    }

}
