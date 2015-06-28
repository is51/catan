package catan.domain.transfer.output;

import catan.domain.UserBean;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class PlayerDetails {
    public List<UserBean> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<UserBean> playerList) {
        this.playerList = playerList;
    }

    private List<UserBean> playerList;
}
