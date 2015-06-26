
package catan.controllers;

import catan.domain.LevelDetails;
import catan.domain.PlayerBean;
import catan.domain.PlayerDetails;

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
        List<PlayerBean> players = new ArrayList<PlayerBean>();
        players.add(new PlayerBean("Andrey1", "blue"));
        players.add(new PlayerBean("Syrovets", "yellow"));
        players.add(new PlayerBean("Bork", "green"));
        players.add(new PlayerBean("Yuriiii", "red"));

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
