package catan.domain.model.game;

import catan.domain.model.dashboard.EdgeBean;
import catan.domain.model.dashboard.HexBean;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.game.types.GameStatus;
import catan.domain.model.user.UserBean;
import catan.domain.transfer.output.dashboard.EdgeDetails;
import catan.domain.transfer.output.dashboard.HexDetails;
import catan.domain.transfer.output.dashboard.NodeDetails;
import catan.domain.transfer.output.game.GameUserDetails;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE;

@Entity
@Table(name = "GAME")
public class GameBean {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "GAME_ID", unique = true, nullable = false)
    private int gameId;

    @ManyToOne
    @JoinColumn(name = "CREATOR_ID")
    private UserBean creator;

    @Column(name = "IS_PRIVATE", unique = false, nullable = false)
    private boolean privateGame;

    @Column(name = "PRIVATE_CODE", unique = false, nullable = true)
    private String privateCode;

    @Column(name = "DATE_CREATED", unique = false, nullable = false)
    private Date dateCreated;

    @Column(name = "DATE_STARTED", unique = false)
    private Date dateStarted;

    @Enumerated(EnumType.STRING)
    @Column(name = "GAME_STATUS", unique = false, nullable = false)
    private GameStatus status;

    @Column(name = "MIN_PLAYERS", unique = false, nullable = false)
    private int minPlayers;

    @Column(name = "MAX_PLAYERS", unique = false, nullable = false)
    private int maxPlayers;

    @Column(name = "TARGET_VICTORY_POINTS", unique = false, nullable = false)
    private int targetVictoryPoints;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GameUserBean> gameUsers = new HashSet<GameUserBean>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HexBean> hexes = new HashSet<HexBean>();

    //TODO: think about removal of this set as it may be redundant
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EdgeBean> edges = new HashSet<EdgeBean>();

    //TODO: think about removal of this set as it may be redundant
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<NodeBean> nodes = new HashSet<NodeBean>();

    public GameBean() {
    }

    public GameBean(UserBean creator, Date dateCreated, GameStatus status, int minPlayers, int maxPlayers, int targetVictoryPoints) {
        this.creator = creator;
        this.privateGame = false;
        this.dateCreated = dateCreated;
        this.status = status;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.targetVictoryPoints = targetVictoryPoints;
    }

    public GameBean(UserBean creator, String privateCode, Date dateCreated, GameStatus status, int minPlayers, int maxPlayers, int targetVictoryPoints) {
        this.creator = creator;
        this.privateGame = true;
        this.privateCode = privateCode;
        this.dateCreated = dateCreated;
        this.status = status;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.targetVictoryPoints = targetVictoryPoints;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }


    public UserBean getCreator() {
        return creator;
    }

    public void setCreator(UserBean creator) {
        this.creator = creator;
    }

    public boolean isPrivateGame() {
        return privateGame;
    }

    public void setPrivateGame(boolean privateGame) {
        this.privateGame = privateGame;
    }

    public String getPrivateCode() {
        return privateCode;
    }

    public void setPrivateCode(String privateCode) {
        this.privateCode = privateCode;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateStarted() {
        return dateStarted;
    }

    public void setDateStarted(Date dateStarted) {
        this.dateStarted = dateStarted;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Set<GameUserBean> getGameUsers() {
        return gameUsers;
    }

    public void setGameUsers(Set<GameUserBean> gameUsers) {
        this.gameUsers = gameUsers;
    }

    public int getTargetVictoryPoints() {
        return targetVictoryPoints;
    }

    public void setTargetVictoryPoints(int targetVictoryPoints) {
        this.targetVictoryPoints = targetVictoryPoints;
    }

    public Set<EdgeBean> getEdges() {
        return edges;
    }

    public void setEdges(Set<EdgeBean> edges) {
        this.edges = edges;
    }

    public Set<HexBean> getHexes() {
        return hexes;
    }

    public void setHexes(Set<HexBean> hexes) {
        this.hexes = hexes;
    }

    public Set<NodeBean> getNodes() {
        return nodes;
    }

    public void setNodes(Set<NodeBean> nodes) {
        this.nodes = nodes;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, SHORT_PREFIX_STYLE);
    }

    public List<GameUserDetails> getGameUserDetails() {
        List<GameUserDetails> gameUsers = new ArrayList<GameUserDetails>();

        for (GameUserBean gameUser : this.gameUsers) {
            gameUsers.add(new GameUserDetails(gameUser));
        }

        return gameUsers;
    }

    public List<HexDetails> getHexDetails() {
        List<HexDetails> hexesDetails = new ArrayList<HexDetails>();

        for (HexBean hex : this.hexes) {
            hexesDetails.add(new HexDetails(hex));
        }

        return hexesDetails;
    }

    public List<NodeDetails> getNodeDetails() {
        List<NodeDetails> nodesDetails = new ArrayList<NodeDetails>();

        for (NodeBean node : this.nodes) {
            nodesDetails.add(new NodeDetails(node));
        }

        return nodesDetails;
    }

    public List<EdgeDetails> getEdgeDetails() {
        List<EdgeDetails> edgesDetails = new ArrayList<EdgeDetails>();

        for (EdgeBean edge : this.edges) {
            edgesDetails.add(new EdgeDetails(edge));
        }

        return edgesDetails;
    }
}
