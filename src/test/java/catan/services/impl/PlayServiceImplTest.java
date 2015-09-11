package catan.services.impl;

import catan.dao.GameDao;
import catan.domain.exception.GameException;
import catan.domain.exception.PlayException;
import catan.domain.model.dashboard.Coordinates;
import catan.domain.model.dashboard.HexBean;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.dashboard.types.HexType;
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
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
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

    @Before
    public void setUp() {
        playService.setGameUtil(gameUtil);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void createNewPrivateGameSuccessful() throws GameException, PlayException {
        // GIVEN
        UserBean user1 = new UserBean(USER_NAME1, PASSWORD1, false);
        user1.setId(1);
        UserBean user2 = new UserBean(USER_NAME2, PASSWORD2, false);
        user2.setId(2);
        UserBean user3 = new UserBean(USER_NAME3, PASSWORD3, false);
        user3.setId(3);
        UserBean user4 = new UserBean(USER_NAME4, PASSWORD4, false);
        user4.setId(4);

        GameBean game = new GameBean();

        GameUserBean gameUser1 = new GameUserBean(user1, 1, game);
        gameUser1.setGameUserId(1);
        GameUserBean gameUser2 = new GameUserBean(user2, 2, game);
        gameUser2.setGameUserId(2);
        GameUserBean gameUser3 = new GameUserBean(user3, 3, game);
        gameUser3.setGameUserId(3);
        GameUserBean gameUser4 = new GameUserBean(user4, 4, game);
        gameUser4.setGameUserId(4);

        //
        //   / \ / \
        //  |0,0|1,0|
        //   \ / \ /
        //    |0,1|
        //     \ /
        //

        HexBean hex_0_0 = new HexBean(game, new Coordinates(0, 0), HexType.BRICK, 2, false); //top-left
        hex_0_0.setId(1);
        HexBean hex_1_0 = new HexBean(game, new Coordinates(1, 0), HexType.WOOD, 10, false); //top-right
        hex_1_0.setId(2);
        HexBean hex_0_1 = new HexBean(game, new Coordinates(0, 1), HexType.STONE, 11, true); //bottom-central
        hex_0_1.setId(3);

        //       2
        //     1/ \3
        //     |   |
        //     6\ /4
        //       5
        //

        NodeBean node_1_1 = new NodeBean(game, NodePortType.ANY);
        node_1_1.setId(1);
        NodeBean node_1_2 = new NodeBean(game, NodePortType.NONE);
        node_1_1.setId(1);
        NodeBean node_1_3 = new NodeBean(game, NodePortType.NONE);
        node_1_1.setId(1);
        NodeBean node_1_4 = new NodeBean(game, NodePortType.NONE);
        node_1_1.setId(1);
        NodeBean node_1_5 = new NodeBean(game, NodePortType.NONE);
        node_1_1.setId(1);
        NodeBean node_1_6 = new NodeBean(game, NodePortType.NONE);
        node_1_1.setId(1);

        NodeBean node_2_1 = new NodeBean(game, NodePortType.NONE);
        node_1_1.setId(1);
        NodeBean node_2_2 = new NodeBean(game, NodePortType.NONE);
        node_1_1.setId(1);
        NodeBean node_2_3 = new NodeBean(game, NodePortType.BRICK);
        node_1_1.setId(1);
        NodeBean node_2_4 = new NodeBean(game, NodePortType.NONE);
        node_1_1.setId(1);
        NodeBean node_2_5 = new NodeBean(game, NodePortType.NONE);
        node_1_1.setId(1);
        NodeBean node_2_6 = new NodeBean(game, NodePortType.NONE);
        node_1_1.setId(1);

        NodeBean node_3_1 = new NodeBean(game, NodePortType.NONE);
        node_1_1.setId(1);
        NodeBean node_3_2 = new NodeBean(game, NodePortType.NONE);
        node_1_1.setId(1);
        NodeBean node_3_3 = new NodeBean(game, NodePortType.NONE);
        node_1_1.setId(1);
        NodeBean node_3_4 = new NodeBean(game, NodePortType.NONE);
        node_1_1.setId(1);
        NodeBean node_3_5 = new NodeBean(game, NodePortType.WOOD);
        node_3_5.setId(1);
        NodeBean node_3_6 = new NodeBean(game, NodePortType.NONE);
        node_3_6.setId(1);

        Set<NodeBean> nodes = new HashSet<NodeBean>();

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


        when(gameDao.getGameByGameId(1)).thenReturn(game);

        // WHEN
        playService.buildRoad(user1, "1", "7");

        // THEN
        assertNotNull(game);
    }
}