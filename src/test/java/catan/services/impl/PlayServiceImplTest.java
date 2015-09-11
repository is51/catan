package catan.services.impl;

import catan.dao.GameDao;
import catan.domain.exception.GameException;
import catan.domain.exception.PlayException;
import catan.domain.model.dashboard.Coordinates;
import catan.domain.model.dashboard.EdgeBean;
import catan.domain.model.dashboard.HexBean;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.dashboard.types.EdgeOrientationType;
import catan.domain.model.dashboard.types.HexType;
import catan.domain.model.dashboard.types.NodeOrientationType;
import catan.domain.model.dashboard.types.NodePortType;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.types.GameStatus;
import catan.domain.model.user.UserBean;
import catan.services.util.game.GameUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlayServiceImplTest {
    public static final String USER_NAME1 = "userName1";
    public static final String USER_NAME2 = "userName2";
    public static final String USER_NAME3 = "userName3";
    public static final String USER_NAME4 = "userName4";
    public static final String PASSWORD1 = "12345";
    public static final String PASSWORD2 = "67890";
    public static final String PASSWORD3 = "67890";
    public static final String PASSWORD4 = "67890";

    @Mock
    private GameDao gameDao;
    @InjectMocks
    private PlayServiceImpl playService;
    @InjectMocks
    private GameUtil gameUtil;
    private GameBean game;
    private UserBean user1;
    private UserBean user2;

    @Before
    public void setUp() {
        playService.setGameUtil(gameUtil);
        buildClearTriangleMap();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void shouldFailWhenBuildRoadNotNearOwnNeighbourCityOrRoad() throws GameException, PlayException {
        try {
            // WHEN
            when(gameDao.getGameByGameId(1)).thenReturn(game);
            playService.buildRoad(user1, "1", "7");
            fail("playService with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    private void buildClearTriangleMap() {
        // GIVEN
        game = new GameBean();

        user1 = new UserBean(USER_NAME1, PASSWORD1, false);
        user2 = new UserBean(USER_NAME2, PASSWORD2, false);
        UserBean user3 = new UserBean(USER_NAME3, PASSWORD3, false);
        UserBean user4 = new UserBean(USER_NAME4, PASSWORD4, false);

        GameUserBean gameUser1 = new GameUserBean(user1, 1, game);
        GameUserBean gameUser2 = new GameUserBean(user2, 2, game);
        GameUserBean gameUser3 = new GameUserBean(user3, 3, game);
        GameUserBean gameUser4 = new GameUserBean(user4, 4, game);

        HexBean hex_0_0 = new HexBean(game, new Coordinates(0, 0), HexType.BRICK, 2, false); //top-left
        HexBean hex_1_0 = new HexBean(game, new Coordinates(1, 0), HexType.WOOD, 10, false); //top-right
        HexBean hex_0_1 = new HexBean(game, new Coordinates(0, 1), HexType.STONE, 11, true); //bottom-central

        NodeBean node_1_1 = new NodeBean(game, NodePortType.ANY);
        NodeBean node_1_2 = new NodeBean(game, NodePortType.NONE);
        NodeBean node_1_3 = new NodeBean(game, NodePortType.NONE);
        NodeBean node_1_4 = new NodeBean(game, NodePortType.NONE);
        NodeBean node_1_5 = new NodeBean(game, NodePortType.NONE);
        NodeBean node_1_6 = new NodeBean(game, NodePortType.NONE);
        NodeBean node_2_1 = new NodeBean(game, NodePortType.NONE);
        NodeBean node_2_2 = new NodeBean(game, NodePortType.NONE);
        NodeBean node_2_3 = new NodeBean(game, NodePortType.STONE);
        NodeBean node_2_4 = new NodeBean(game, NodePortType.NONE);
        NodeBean node_2_5 = new NodeBean(game, NodePortType.NONE);
        NodeBean node_2_6 = new NodeBean(game, NodePortType.NONE);
        NodeBean node_3_1 = new NodeBean(game, NodePortType.NONE);
        NodeBean node_3_2 = new NodeBean(game, NodePortType.NONE);
        NodeBean node_3_3 = new NodeBean(game, NodePortType.NONE);
        NodeBean node_3_4 = new NodeBean(game, NodePortType.NONE);
        NodeBean node_3_5 = new NodeBean(game, NodePortType.WOOD);
        NodeBean node_3_6 = new NodeBean(game, NodePortType.NONE);

        EdgeBean edge_1_1 = new EdgeBean(game);
        EdgeBean edge_1_2 = new EdgeBean(game);
        EdgeBean edge_1_3 = new EdgeBean(game);
        EdgeBean edge_1_4 = new EdgeBean(game);
        EdgeBean edge_1_5 = new EdgeBean(game);
        EdgeBean edge_1_6 = new EdgeBean(game);
        EdgeBean edge_2_1 = new EdgeBean(game);
        EdgeBean edge_2_2 = new EdgeBean(game);
        EdgeBean edge_2_3 = new EdgeBean(game);
        EdgeBean edge_2_4 = new EdgeBean(game);
        EdgeBean edge_2_5 = new EdgeBean(game);
        EdgeBean edge_2_6 = new EdgeBean(game);
        EdgeBean edge_3_1 = new EdgeBean(game);
        EdgeBean edge_3_2 = new EdgeBean(game);
        EdgeBean edge_3_3 = new EdgeBean(game);
        EdgeBean edge_3_4 = new EdgeBean(game);
        EdgeBean edge_3_5 = new EdgeBean(game);
        EdgeBean edge_3_6 = new EdgeBean(game);

        user1.setId(1);
        user2.setId(2);
        user3.setId(3);
        user4.setId(4);

        gameUser1.setGameUserId(1);
        gameUser1.setMoveOrder(1);
        gameUser1.setReady(true);

        gameUser2.setGameUserId(2);
        gameUser2.setMoveOrder(2);
        gameUser2.setReady(true);

        gameUser3.setGameUserId(3);
        gameUser3.setMoveOrder(3);
        gameUser3.setReady(true);

        gameUser4.setGameUserId(4);
        gameUser4.setMoveOrder(4);
        gameUser4.setReady(true);

        //
        //   / \ / \
        //  |0,0|1,0|
        //   \ / \ /
        //    |0,1|
        //     \ /
        //
        //       2
        //    1 / \ 3
        //     |   |
        //    6 \ / 4
        //       5
        //
        //     1/ \2
        //    6|   |3
        //     5\ /4
        //

        hex_0_0.setId(1);
        hex_0_0.getNodes().setTopLeft(node_1_1);
        hex_0_0.getNodes().setTop(node_1_2);
        hex_0_0.getNodes().setTopRight(node_1_3);
        hex_0_0.getNodes().setBottomRight(node_1_4);
        hex_0_0.getNodes().setBottom(node_1_5);
        hex_0_0.getNodes().setBottomLeft(node_1_6);
        hex_0_0.getEdges().setTopLeft(edge_1_1);
        hex_0_0.getEdges().setTopRight(edge_1_2);
        hex_0_0.getEdges().setRight(edge_1_3);
        hex_0_0.getEdges().setBottomRight(edge_1_4);
        hex_0_0.getEdges().setBottomLeft(edge_1_5);
        hex_0_0.getEdges().setLeft(edge_1_6);
        hex_1_0.setId(2);
        hex_1_0.getNodes().setTopLeft(node_2_1);
        hex_1_0.getNodes().setTop(node_2_2);
        hex_1_0.getNodes().setTopRight(node_2_3);
        hex_1_0.getNodes().setBottomRight(node_2_4);
        hex_1_0.getNodes().setBottom(node_2_5);
        hex_1_0.getNodes().setBottomLeft(node_2_6);
        hex_1_0.getEdges().setTopLeft(edge_2_1);
        hex_1_0.getEdges().setTopRight(edge_2_2);
        hex_1_0.getEdges().setRight(edge_2_3);
        hex_1_0.getEdges().setBottomRight(edge_2_4);
        hex_1_0.getEdges().setBottomLeft(edge_2_5);
        hex_1_0.getEdges().setLeft(edge_2_6);
        hex_0_1.setId(3);
        hex_0_1.getNodes().setTopLeft(node_3_1);
        hex_0_1.getNodes().setTop(node_3_2);
        hex_0_1.getNodes().setTopRight(node_3_3);
        hex_0_1.getNodes().setBottomRight(node_3_4);
        hex_0_1.getNodes().setBottom(node_3_5);
        hex_0_1.getNodes().setBottomLeft(node_3_6);
        hex_0_1.getEdges().setTopLeft(edge_3_1);
        hex_0_1.getEdges().setTopRight(edge_3_2);
        hex_0_1.getEdges().setRight(edge_3_3);
        hex_0_1.getEdges().setBottomRight(edge_3_4);
        hex_0_1.getEdges().setBottomLeft(edge_3_5);
        hex_0_1.getEdges().setLeft(edge_3_6);

        // Nodes of Hex 0,0
        node_1_1.setId(1);
        node_1_1.setOrientation(NodeOrientationType.SINGLE_TOP);
        node_1_1.getHexes().setBottomRight(hex_0_0);
        node_1_1.getEdges().setTopRight(edge_1_1);
        node_1_1.getEdges().setBottom(edge_1_6);

        node_1_2.setId(2);
        node_1_2.setOrientation(NodeOrientationType.SINGLE_BOTTOM);
        node_1_2.getHexes().setBottom(hex_0_0);
        node_1_2.getEdges().setBottomLeft(edge_1_1);
        node_1_2.getEdges().setBottomRight(edge_1_2);

        node_1_3.setId(3);
        node_1_3.setOrientation(NodeOrientationType.SINGLE_TOP);
        node_1_3.getHexes().setBottomLeft(hex_0_0);
        node_1_3.getHexes().setBottomRight(hex_1_0);
        node_1_3.getEdges().setTopLeft(edge_1_2);
        node_1_3.getEdges().setBottom(edge_1_3);

        node_1_4.setId(4);
        node_1_4.setOrientation(NodeOrientationType.SINGLE_BOTTOM);
        node_1_4.getHexes().setTopLeft(hex_0_0);
        node_1_4.getHexes().setTopRight(hex_1_0);
        node_1_4.getHexes().setBottom(hex_0_1);
        node_1_4.getEdges().setTop(edge_1_3);
        node_1_4.getEdges().setBottomLeft(edge_1_4);

        node_1_5.setId(5);
        node_1_5.setOrientation(NodeOrientationType.SINGLE_TOP);
        node_1_5.getHexes().setTop(hex_0_0);
        node_1_5.getHexes().setBottomRight(hex_0_1);
        node_1_5.getEdges().setTopRight(edge_1_4);
        node_1_5.getEdges().setTopLeft(edge_1_5);

        node_1_6.setId(6);
        node_1_6.setOrientation(NodeOrientationType.SINGLE_BOTTOM);
        node_1_6.getHexes().setTopRight(hex_0_0);
        node_1_6.getEdges().setBottomRight(edge_1_5);
        node_1_6.getEdges().setTop(edge_1_6);

        // Nodes of Hex 1,0
        node_2_1.setId(7);
        node_2_1.setOrientation(NodeOrientationType.SINGLE_TOP);
        node_2_1.getHexes().setBottomRight(hex_1_0);
        node_2_1.getHexes().setBottomLeft(hex_0_1);
        node_2_1.getEdges().setTopRight(edge_2_1);
        node_2_1.getEdges().setBottom(edge_2_6);

        node_2_2.setId(8);
        node_2_2.setOrientation(NodeOrientationType.SINGLE_BOTTOM);
        node_2_2.getHexes().setBottom(hex_1_0);
        node_2_2.getEdges().setBottomLeft(edge_2_1);
        node_2_2.getEdges().setBottomRight(edge_2_2);

        node_2_3.setId(9);
        node_2_3.setOrientation(NodeOrientationType.SINGLE_TOP);
        node_2_3.getHexes().setBottomLeft(hex_1_0);
        node_2_3.getEdges().setTopLeft(edge_2_2);
        node_2_3.getEdges().setBottom(edge_2_3);

        node_2_4.setId(10);
        node_2_4.setOrientation(NodeOrientationType.SINGLE_BOTTOM);
        node_2_4.getHexes().setTopLeft(hex_1_0);
        node_2_4.getEdges().setTop(edge_2_3);
        node_2_4.getEdges().setBottomLeft(edge_2_4);

        node_2_5.setId(11);
        node_2_5.setOrientation(NodeOrientationType.SINGLE_TOP);
        node_2_5.getHexes().setTop(hex_1_0);
        node_2_5.getHexes().setBottomLeft(hex_0_1);
        node_2_5.getEdges().setTopRight(edge_2_4);
        node_2_5.getEdges().setTopLeft(edge_2_5);

        node_2_6.setId(12);
        node_2_6.setOrientation(NodeOrientationType.SINGLE_BOTTOM);
        node_2_6.getHexes().setTopRight(hex_1_0);
        node_2_6.getHexes().setTopLeft(hex_0_0);
        node_2_6.getHexes().setBottom(hex_0_1);
        node_2_6.getHexes().setTopRight(hex_1_0);
        node_2_6.getEdges().setBottomRight(edge_2_5);
        node_2_6.getEdges().setTop(edge_2_6);

        // Nodes of Hex 0,1
        node_3_1.setId(13);
        node_3_1.setOrientation(NodeOrientationType.SINGLE_TOP);
        node_3_1.getHexes().setBottomRight(hex_0_1);
        node_3_1.getHexes().setTop(hex_0_0);
        node_3_1.getEdges().setTopRight(edge_3_1);
        node_3_1.getEdges().setBottom(edge_3_6);

        node_3_2.setId(14);
        node_3_2.setOrientation(NodeOrientationType.SINGLE_BOTTOM);
        node_3_2.getHexes().setBottom(hex_0_1);
        node_3_2.getHexes().setTopLeft(hex_0_0);
        node_3_2.getHexes().setTopRight(hex_1_0);
        node_3_2.getEdges().setBottomLeft(edge_3_1);
        node_3_2.getEdges().setBottomRight(edge_3_2);

        node_3_3.setId(15);
        node_3_3.setOrientation(NodeOrientationType.SINGLE_TOP);
        node_3_3.getHexes().setBottomLeft(hex_0_1);
        node_3_3.getHexes().setTop(hex_1_0);
        node_3_3.getEdges().setTopLeft(edge_3_2);
        node_3_3.getEdges().setBottom(edge_3_3);

        node_3_4.setId(16);
        node_3_4.setOrientation(NodeOrientationType.SINGLE_BOTTOM);
        node_3_4.getHexes().setTopLeft(hex_0_1);
        node_3_4.getEdges().setTop(edge_3_3);
        node_3_4.getEdges().setBottomLeft(edge_3_4);

        node_3_5.setId(17);
        node_3_5.setOrientation(NodeOrientationType.SINGLE_TOP);
        node_3_5.getHexes().setTop(hex_0_1);
        node_3_5.getEdges().setTopRight(edge_3_4);
        node_3_5.getEdges().setTopLeft(edge_3_5);

        node_3_6.setId(18);
        node_3_6.setOrientation(NodeOrientationType.SINGLE_BOTTOM);
        node_3_6.getHexes().setTopRight(hex_0_1);
        node_3_6.getEdges().setBottomRight(edge_3_5);
        node_3_6.getEdges().setTop(edge_3_6);

        // Edges of Hex 0,0
        edge_1_1.setId(1);
        edge_1_1.setOrientation(EdgeOrientationType.BOTTOM_LEFT);
        edge_1_1.getHexes().setBottomRight(hex_0_0);
        edge_1_1.getNodes().setBottomLeft(node_1_1);
        edge_1_1.getNodes().setTopRight(node_1_2);

        edge_1_2.setId(2);
        edge_1_2.setOrientation(EdgeOrientationType.BOTTOM_RIGHT);
        edge_1_2.getHexes().setBottomLeft(hex_0_0);
        edge_1_2.getNodes().setTopLeft(node_1_2);
        edge_1_2.getNodes().setBottomRight(node_1_3);

        edge_1_3.setId(3);
        edge_1_3.setOrientation(EdgeOrientationType.VERTICAL);
        edge_1_3.getHexes().setLeft(hex_0_0);
        edge_1_3.getHexes().setRight(hex_1_0);
        edge_1_3.getNodes().setTop(node_1_3);
        edge_1_3.getNodes().setBottom(node_1_4);

        edge_1_4.setId(4);
        edge_1_4.setOrientation(EdgeOrientationType.BOTTOM_LEFT);
        edge_1_4.getHexes().setTopLeft(hex_0_0);
        edge_1_4.getHexes().setBottomRight(hex_0_1);
        edge_1_4.getNodes().setTopRight(node_1_4);
        edge_1_4.getNodes().setBottomLeft(node_1_5);

        edge_1_5.setId(5);
        edge_1_5.setOrientation(EdgeOrientationType.BOTTOM_RIGHT);
        edge_1_5.getHexes().setTopRight(hex_0_0);
        edge_1_5.getNodes().setBottomRight(node_1_5);
        edge_1_5.getNodes().setTopLeft(node_1_6);

        edge_1_6.setId(6);
        edge_1_6.setOrientation(EdgeOrientationType.VERTICAL);
        edge_1_6.getHexes().setRight(hex_0_0);
        edge_1_6.getNodes().setBottom(node_1_6);
        edge_1_6.getNodes().setTop(node_1_1);

        // Edges of Hex 1,0
        edge_2_1.setId(7);
        edge_2_1.setOrientation(EdgeOrientationType.BOTTOM_LEFT);
        edge_2_1.getHexes().setBottomRight(hex_1_0);
        edge_2_1.getNodes().setBottomLeft(node_2_1);
        edge_2_1.getNodes().setTopRight(node_2_2);

        edge_2_2.setId(8);
        edge_2_2.setOrientation(EdgeOrientationType.BOTTOM_RIGHT);
        edge_2_2.getHexes().setBottomLeft(hex_1_0);
        edge_2_2.getNodes().setTopLeft(node_2_2);
        edge_2_2.getNodes().setBottomRight(node_2_3);

        edge_2_3.setId(9);
        edge_2_3.setOrientation(EdgeOrientationType.VERTICAL);
        edge_2_3.getHexes().setLeft(hex_1_0);
        edge_2_3.getNodes().setTop(node_2_3);
        edge_2_3.getNodes().setBottom(node_2_4);

        edge_2_4.setId(10);
        edge_2_4.setOrientation(EdgeOrientationType.BOTTOM_LEFT);
        edge_2_4.getHexes().setTopLeft(hex_1_0);
        edge_2_4.getNodes().setTopRight(node_2_4);
        edge_2_4.getNodes().setBottomLeft(node_2_5);

        edge_2_5.setId(11);
        edge_2_5.setOrientation(EdgeOrientationType.BOTTOM_RIGHT);
        edge_2_5.getHexes().setTopRight(hex_1_0);
        edge_2_5.getHexes().setBottomLeft(hex_0_1);
        edge_2_5.getNodes().setBottomRight(node_2_5);
        edge_2_5.getNodes().setTopLeft(node_2_6);

        edge_2_6.setId(12);
        edge_2_6.setOrientation(EdgeOrientationType.VERTICAL);
        edge_2_6.getHexes().setRight(hex_1_0);
        edge_2_3.getHexes().setLeft(hex_0_0);
        edge_2_6.getNodes().setBottom(node_2_6);
        edge_2_6.getNodes().setTop(node_2_1);

        // Edges of Hex 0,1
        edge_3_1.setId(13);
        edge_3_1.setOrientation(EdgeOrientationType.BOTTOM_LEFT);
        edge_3_1.getHexes().setTopLeft(hex_0_0);
        edge_3_1.getHexes().setBottomRight(hex_0_1);
        edge_3_1.getNodes().setBottomLeft(node_2_1);
        edge_3_1.getNodes().setTopRight(node_2_2);

        edge_3_2.setId(14);
        edge_3_2.setOrientation(EdgeOrientationType.BOTTOM_RIGHT);
        edge_3_2.getHexes().setTopRight(hex_1_0);
        edge_3_2.getHexes().setBottomLeft(hex_0_1);
        edge_3_2.getNodes().setTopLeft(node_2_2);
        edge_3_2.getNodes().setBottomRight(node_2_3);

        edge_3_3.setId(15);
        edge_3_3.setOrientation(EdgeOrientationType.VERTICAL);
        edge_3_3.getHexes().setLeft(hex_0_1);
        edge_3_3.getNodes().setTop(node_2_3);
        edge_3_3.getNodes().setBottom(node_2_4);

        edge_3_4.setId(16);
        edge_3_4.setOrientation(EdgeOrientationType.BOTTOM_LEFT);
        edge_3_4.getHexes().setTopLeft(hex_0_1);
        edge_3_4.getNodes().setTopRight(node_2_4);
        edge_3_4.getNodes().setBottomLeft(node_2_5);

        edge_3_5.setId(17);
        edge_3_5.setOrientation(EdgeOrientationType.BOTTOM_RIGHT);
        edge_3_5.getHexes().setTopRight(hex_0_1);
        edge_3_5.getNodes().setBottomRight(node_2_5);
        edge_3_5.getNodes().setTopLeft(node_2_6);

        edge_3_6.setId(18);
        edge_3_6.setOrientation(EdgeOrientationType.VERTICAL);
        edge_3_6.getHexes().setRight(hex_0_1);
        edge_3_6.getNodes().setBottom(node_2_6);
        edge_3_6.getNodes().setTop(node_2_1);

        game.setGameId(1);
        game.setCreator(user1);
        game.setStatus(GameStatus.PLAYING);
        game.setCurrentMove(1);
        game.setDateCreated(new Date());
        game.setDateStarted(new Date());
        game.setGameUsers(new HashSet<GameUserBean>(Arrays.asList(gameUser1, gameUser2, gameUser3, gameUser4)));
        game.setMinPlayers(3);
        game.setMinPlayers(4);
        game.setPrivateGame(false);
        game.setTargetVictoryPoints(12);
        game.getHexes().addAll(Arrays.asList(
                hex_0_0, hex_1_0, hex_0_1));
        game.getNodes().addAll(Arrays.asList(
                node_1_1, node_1_2, node_1_3, node_1_4, node_1_5, node_1_6,
                node_2_1, node_2_2, node_2_3, node_2_4, node_2_5, node_2_6,
                node_3_1, node_3_2, node_3_3, node_3_4, node_3_5, node_3_6));
        game.getEdges().addAll(Arrays.asList(
                edge_1_1, edge_1_2, edge_1_3, edge_1_4, edge_1_5, edge_1_6,
                edge_2_1, edge_2_2, edge_2_3, edge_2_4, edge_2_5, edge_2_6,
                edge_3_1, edge_3_2, edge_3_3, edge_3_4, edge_3_5, edge_3_6));
    }
}