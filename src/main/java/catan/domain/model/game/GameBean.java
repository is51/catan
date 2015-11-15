package catan.domain.model.game;

import catan.domain.model.dashboard.EdgeBean;
import catan.domain.model.dashboard.HexBean;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.game.types.GameStage;
import catan.domain.model.game.types.GameStatus;
import catan.domain.model.user.UserBean;
import catan.domain.transfer.output.dashboard.EdgeDetails;
import catan.domain.transfer.output.dashboard.HexDetails;
import catan.domain.transfer.output.dashboard.NodeDetails;
import catan.domain.transfer.output.game.GameUserDetails;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "GAME_STAGE", unique = false)
    private GameStage stage;

    @Column(name = "PREPARATION_CYCLE", unique = false, nullable = true)
    private Integer preparationCycle;

    @Column(name = "MIN_PLAYERS", unique = false, nullable = false)
    private int minPlayers;

    @Column(name = "MAX_PLAYERS", unique = false, nullable = false)
    private int maxPlayers;

    @Column(name = "TARGET_VICTORY_POINTS", unique = false, nullable = false)
    private int targetVictoryPoints;

    @Column(name = "INITIAL_BUILDINGS_SET", unique = false, nullable = false)
    private String initialBuildingsSet;

    //TODO: think about renaming of this field as it will be used only in preparation stage
    @Column(name = "CURRENT_CYCLE_BUILDING_NUMBER", unique = false, nullable = true)
    private Integer currentCycleBuildingNumber;

    @Column(name = "CURRENT_MOVE", unique = false, nullable = true)
    private Integer currentMove;

    @Column(name = "DICE_THROWN", unique = false, nullable = true)
    private Boolean diceThrown;

    @Column(name = "DICE_FIRST_VALUE", unique = false, nullable = true)
    private Integer diceFirstValue;

    @Column(name = "DICE_SECOND_VALUE", unique = false, nullable = true)
    private Integer diceSecondValue;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("colorId ASC")
    private Set<GameUserBean> gameUsers = new HashSet<GameUserBean>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(value = FetchMode.SELECT)
    @OrderBy("coordinates ASC")
    private Set<HexBean> hexes = new HashSet<HexBean>();

    //TODO: think about removal of this set as it may be redundant
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(value = FetchMode.SELECT)
    @OrderBy("id ASC")
    private Set<EdgeBean> edges = new HashSet<EdgeBean>();

    //TODO: think about removal of this set as it may be redundant
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(value = FetchMode.SELECT)
    @OrderBy("id ASC")
    private Set<NodeBean> nodes = new HashSet<NodeBean>();

    public GameBean() {
    }

    public GameBean(UserBean creator, Date dateCreated, GameStatus status, int minPlayers, int maxPlayers, int targetVictoryPoints, String initialBuildingsSet) {
        this.creator = creator;
        this.privateGame = false;
        this.dateCreated = dateCreated;
        this.status = status;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.targetVictoryPoints = targetVictoryPoints;
        this.initialBuildingsSet = initialBuildingsSet;
    }

    public GameBean(UserBean creator, String privateCode, Date dateCreated, GameStatus status, int minPlayers, int maxPlayers, int targetVictoryPoints, String initialBuildingsSet) {
        this.creator = creator;
        this.privateGame = true;
        this.privateCode = privateCode;
        this.dateCreated = dateCreated;
        this.status = status;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.targetVictoryPoints = targetVictoryPoints;
        this.initialBuildingsSet = initialBuildingsSet;
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

    public GameStage getStage() {
        return stage;
    }

    public void setStage(GameStage stage) {
        this.stage = stage;
    }

    public Integer getPreparationCycle() {
        return preparationCycle;
    }

    public void setPreparationCycle(Integer preparationCycle) {
        this.preparationCycle = preparationCycle;
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

    public String getInitialBuildingsSet() {
        return initialBuildingsSet;
    }

    public void setInitialBuildingsSet(String initialBuildingsSet) {
        this.initialBuildingsSet = initialBuildingsSet;
    }

    public Integer getCurrentCycleBuildingNumber() {
        return currentCycleBuildingNumber;
    }

    public void setCurrentCycleBuildingNumber(Integer currentCycleBuildingNumber) {
        this.currentCycleBuildingNumber = currentCycleBuildingNumber;
    }

    public Integer getCurrentMove() {
        return currentMove;
    }

    public void setCurrentMove(Integer currentMove) {
        this.currentMove = currentMove;
    }

    public Boolean isDiceThrown() {
        return diceThrown;
    }

    public void setDiceThrown(Boolean diceThrown) {
        this.diceThrown = diceThrown;
    }

    public Integer getDiceFirstValue() {
        return diceFirstValue;
    }

    public void setDiceFirstValue(Integer diceFirstValue) {
        this.diceFirstValue = diceFirstValue;
    }

    public Integer getDiceSecondValue() {
        return diceSecondValue;
    }

    public void setDiceSecondValue(Integer diceSecondValue) {
        this.diceSecondValue = diceSecondValue;
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

    public List<HexBean> fetchHexesWithCurrentDiceValue() {
        List<HexBean> hexesWithDiceNumber = new ArrayList<HexBean>();
        Integer diceSumValue = calculateDiceSumValue();
        if (diceSumValue != null) {
            for (HexBean hex : this.hexes) {
                if (diceSumValue.equals(hex.getDice())) {
                    hexesWithDiceNumber.add(hex);
                }
            }
        }

        return hexesWithDiceNumber;
    }

    public Integer calculateDiceSumValue() {
        if (this.diceFirstValue == null || this.diceSecondValue == null) {
            return null;
        }
        return this.diceFirstValue + this.diceSecondValue;
    }

    public List<GameUserDetails> getGameUserDetails(int detailsRequesterId) {
        List<GameUserDetails> gameUsers = new ArrayList<GameUserDetails>();

        for (GameUserBean gameUser : this.gameUsers) {
            gameUsers.add(new GameUserDetails(gameUser, detailsRequesterId, this.status));
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for (GameUserBean gameUser : gameUsers) {
            if (sb.length() > 2) {
                sb.append(",");
            }
            sb.append("\n\t\t\t");
            sb.append(gameUser.toString());
        }
        sb.append("\n" +
                "\t\t]");
        return "\n\tGame [ " +
                "\n" +
                "\t\tid: " + gameId +
                "\n" +
                "\t\tcreator: " + creator +
                "\n" +
                "\t\tprivateGame: " + privateGame + (privateGame ? (", privateCode: '" + privateCode + '\'') : "") +
                "\n" +
                "\t\tdateCreated: " + dateCreated +
                "\n" +
                "\t\tdateStarted: " + dateStarted +
                "\n" +
                "\t\tstatus: " + status +
                "\n" +
                "\t\tstage: " + stage +
                "\n" +
                "\t\tpreparationCycle: " + preparationCycle +
                "\n" +
                "\t\tminPlayers: " + minPlayers +
                "\n" +
                "\t\tmaxPlayers: " + maxPlayers +
                "\n" +
                "\t\ttargetVictoryPoints: " + targetVictoryPoints +
                "\n" +
                "\t\tinitialBuildingsSet: " + initialBuildingsSet +
                "\n" +
                "\t\tcurrentMove: " + currentMove +
                "\n" +
                "\t\tgameUsers: " + sb.toString() +
                "\n" +
                "\t]\n";
    }
}
