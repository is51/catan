package catan.domain;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class PlayerDetails {
    public List<PlayerBean> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<PlayerBean> playerList) {
        this.playerList = playerList;
    }

    private List<PlayerBean> playerList;
}
