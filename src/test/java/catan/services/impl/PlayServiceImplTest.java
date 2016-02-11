package catan.services.impl;

import catan.dao.GameDao;
import catan.domain.exception.GameException;
import catan.domain.exception.PlayException;
import catan.domain.model.dashboard.Building;
import catan.domain.model.dashboard.Coordinates;
import catan.domain.model.dashboard.EdgeBean;
import catan.domain.model.dashboard.HexBean;
import catan.domain.model.dashboard.NodeBean;
import catan.domain.model.dashboard.types.EdgeBuiltType;
import catan.domain.model.dashboard.types.EdgeOrientationType;
import catan.domain.model.dashboard.types.HexType;
import catan.domain.model.dashboard.types.NodeBuiltType;
import catan.domain.model.dashboard.types.NodeOrientationType;
import catan.domain.model.dashboard.types.NodePortType;
import catan.domain.model.game.Achievements;
import catan.domain.model.game.DevelopmentCards;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.Resources;
import catan.domain.model.game.actions.Action;
import catan.domain.model.game.actions.AvailableActions;
import catan.domain.model.game.types.GameStage;
import catan.domain.model.game.types.GameStatus;
import catan.domain.model.game.types.GameUserActionCode;
import catan.domain.model.user.UserBean;
import catan.services.util.game.GameUtil;
import catan.services.util.play.BuildUtil;
import catan.services.util.play.CardUtil;
import catan.services.util.play.MainStageUtil;
import catan.services.util.play.PlayUtil;
import catan.services.util.play.PreparationStageUtil;
import catan.services.util.random.RandomUtil;
import catan.services.util.random.RandomValueGeneratorMock;
import com.google.gson.Gson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
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
    @InjectMocks
    private RandomUtil randomUtil;
    @InjectMocks
    private PlayUtil playUtil;
    @InjectMocks
    private BuildUtil buildUtil;
    @InjectMocks
    private CardUtil cardUtil;
    @InjectMocks
    private PreparationStageUtil preparationStageUtil;
    @InjectMocks
    private MainStageUtil mainStageUtil;

    private GameBean game;
    private HexBean hex_0_0;
    private HexBean hex_1_0;
    private HexBean hex_0_1;
    private GameUserBean gameUser1;
    private GameUserBean gameUser2;
    private GameUserBean gameUser3;
    private GameUserBean gameUser4;

    private RandomValueGeneratorMock rvg = new RandomValueGeneratorMock();

    private static final Gson GSON = new Gson();


    @Before
    public void setUp() throws GameException {
        RandomUtil randomUtil = new RandomUtil();
        randomUtil.setRvg(rvg);

        buildUtil.setGameUtil(gameUtil);

        playService.setRandomUtil(randomUtil);
        playService.setGameUtil(gameUtil);
        playService.setPlayUtil(playUtil);
        playService.setBuildUtil(buildUtil);
        playService.setCardUtil(cardUtil);
        playService.setPreparationStageUtil(preparationStageUtil);
        playService.setMainStageUtil(mainStageUtil);

        playUtil.setMainStageUtil(mainStageUtil);
        playUtil.setPreparationStageUtil(preparationStageUtil);

        cardUtil.setRandomUtil(randomUtil);

        buildClearTriangleMapAndSetAlreadyPlayingGame();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void shouldChangeCurrentMoveFromFirstPlayerToSecondWhenFirstPlayerEndsHisTurnCorrectly() throws GameException, PlayException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        playUtil.updateAvailableActionsForAllUsers(game);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        // WHEN
        playService.processAction(GameUserActionCode.END_TURN, gameUser1.getUser(), "1", new HashMap<String, String>());

        // THEN
        assertNotNull(game);
        assertNotNull(game.getCurrentMove());
        assertEquals(game.getCurrentMove().intValue(), gameUser2.getMoveOrder());
    }

    @Test
    public void shouldFailWhenFirstPlayerTriesToEndTurnWhenCurrentMoveBelongsToSecondPlayer() {
        try {
            //GIVEN
            game.setCurrentMove(gameUser2.getMoveOrder());
            playUtil.updateAvailableActionsForAllUsers(game);
            when(gameDao.getGameByGameId(1)).thenReturn(game);

            // WHEN
            playService.processAction(GameUserActionCode.END_TURN, gameUser1.getUser(), "1", new HashMap<String, String>());
            fail("playException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldFailWhenEndTurnOfNotPlayingGame() {
        try {
            //GIVEN
            game.setStatus(GameStatus.NEW);
            when(gameDao.getGameByGameId(1)).thenReturn(game);

            // WHEN
            playService.processAction(GameUserActionCode.END_TURN, gameUser1.getUser(), "1", new HashMap<String, String>());
            fail("playException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (GameException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldPassWhenBuildingRoadNearOwnNeighbourCity() throws GameException, PlayException {
        //GIVEN
        hex_0_0.getNodes().getTopRight().setBuilding(new Building<NodeBuiltType>(NodeBuiltType.SETTLEMENT, gameUser1));
        game.setCurrentCycleBuildingNumber(2);
        playUtil.updateAvailableActionsForAllUsers(game);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        // WHEN
        Map<String, String> params = new HashMap<String, String>();
        params.put("edgeId", "7");

        playService.processAction(GameUserActionCode.BUILD_ROAD, gameUser1.getUser(), "1", params);

        // THEN
        assertNotNull(game);
        assertNotNull(hex_1_0);
        assertNotNull(hex_1_0.getEdges().getTopLeft());
        assertNotNull(hex_1_0.getEdges().getTopLeft().getBuilding());
        assertEquals(hex_1_0.getEdges().getTopLeft().getBuilding().getBuilt(), EdgeBuiltType.ROAD);
        assertEquals(hex_1_0.getEdges().getTopLeft().getBuilding().getBuildingOwner(), gameUser1);
    }

    @Test
    public void shouldPassWhenBuildingRoadNearOwnNeighbourRoad() throws GameException, PlayException {
        //GIVEN
        hex_0_0.getEdges().getTopRight().setBuilding(new Building<EdgeBuiltType>(EdgeBuiltType.ROAD, gameUser1));
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        gameUser1.setResources(new Resources(1, 1, 0, 0, 0));
        playUtil.updateAvailableActionsForAllUsers(game);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        // WHEN

        Map<String, String> params = new HashMap<String, String>();
        params.put("edgeId", "7");

        playService.processAction(GameUserActionCode.BUILD_ROAD, gameUser1.getUser(), "1", params);

        // THEN
        assertNotNull(game);
        assertNotNull(hex_1_0);
        assertNotNull(hex_1_0.getEdges().getTopLeft());
        assertNotNull(hex_1_0.getEdges().getTopLeft().getBuilding());
        assertEquals(hex_1_0.getEdges().getTopLeft().getBuilding().getBuilt(), EdgeBuiltType.ROAD);
        assertEquals(hex_1_0.getEdges().getTopLeft().getBuilding().getBuildingOwner(), gameUser1);
    }

    @Test
    public void shouldFailWhenPassedEdgeIdIsWrong() {
        try {
            // WHEN
            when(gameDao.getGameByGameId(1)).thenReturn(game);

            Map<String, String> params = new HashMap<String, String>();
            params.put("edgeId", "16");

            playService.processAction(GameUserActionCode.BUILD_ROAD, gameUser1.getUser(), "1", params);
            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldFailWhenBuildRoadNotNearOwnNeighbourCityOrRoad() throws GameException {
        //GIVEN
        game.setCurrentCycleBuildingNumber(2);
        playUtil.updateAvailableActionsForAllUsers(game);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        try {
            // WHEN

            Map<String, String> params = new HashMap<String, String>();
            params.put("edgeId", "7");

            playService.processAction(GameUserActionCode.BUILD_ROAD, gameUser1.getUser(), "1", params);
            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown, but was thrown: " + e);
        }
    }

    @Test
    public void shouldPassWhenBuildRoadNearOwnNeighbourRoadAndNearNotOwnNeighbourCityFromOtherSide() throws GameException, PlayException {
        //GIVEN
        hex_1_0.getEdges().getTopRight().setBuilding(new Building<EdgeBuiltType>(EdgeBuiltType.ROAD, gameUser1));
        hex_0_0.getNodes().getTopRight().setBuilding(new Building<NodeBuiltType>(NodeBuiltType.SETTLEMENT, gameUser2));
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        gameUser1.setResources(new Resources(1, 1, 0, 0, 0));
        playUtil.updateAvailableActionsForAllUsers(game);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        // WHEN

        Map<String, String> params = new HashMap<String, String>();
        params.put("edgeId", "7");

        playService.processAction(GameUserActionCode.BUILD_ROAD, gameUser1.getUser(), "1", params);

        // THEN
        assertNotNull(game);
        assertNotNull(hex_1_0);
        assertNotNull(hex_1_0.getEdges().getTopLeft());
        assertNotNull(hex_1_0.getEdges().getTopLeft().getBuilding());
        assertEquals(hex_1_0.getEdges().getTopLeft().getBuilding().getBuilt(), EdgeBuiltType.ROAD);
        assertEquals(hex_1_0.getEdges().getTopLeft().getBuilding().getBuildingOwner(), gameUser1);
    }

    @Test
    public void shouldFailWhenBuildRoadNearOwnNeighbourRoadButOverNotOwnNeighbourCity() throws GameException {
        //GIVEN
        hex_1_0.getEdges().getTopRight().setBuilding(new Building<EdgeBuiltType>(EdgeBuiltType.ROAD, gameUser1));
        hex_1_0.getNodes().getTop().setBuilding(new Building<NodeBuiltType>(NodeBuiltType.SETTLEMENT, gameUser2));
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        gameUser1.setResources(new Resources(1, 1, 0, 0, 0));
        playUtil.updateAvailableActionsForAllUsers(game);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        try {
            // WHEN

            Map<String, String> params = new HashMap<String, String>();
            params.put("edgeId", "7");

            playService.processAction(GameUserActionCode.BUILD_ROAD, gameUser1.getUser(), "1", params);
            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown, but was thrown: " + e);
        }
    }

    @Test
    public void shouldFailWhenBuildRoadOnExistingRoad() throws GameException {
        //GIVEN
        hex_1_0.getEdges().getTopLeft().setBuilding(new Building<EdgeBuiltType>(EdgeBuiltType.ROAD, gameUser1));
        game.setCurrentCycleBuildingNumber(2);
        playUtil.updateAvailableActionsForAllUsers(game);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        try {
            // WHEN

            Map<String, String> params = new HashMap<String, String>();
            params.put("edgeId", "7");

            playService.processAction(GameUserActionCode.BUILD_ROAD, gameUser1.getUser(), "1", params);
            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown, but was thrown: " + e);
        }
    }

    @Test
    public void shouldFailWhenBuildRoadAndGameIsNotPlaying() {
        try {
            // WHEN
            game.setStatus(GameStatus.NEW);

            when(gameDao.getGameByGameId(1)).thenReturn(game);

            Map<String, String> params = new HashMap<String, String>();
            params.put("edgeId", "7");

            playService.processAction(GameUserActionCode.BUILD_ROAD, gameUser1.getUser(), "1", params);
            fail("GameException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (GameException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldFailWhenBuildSettlementAndPassedNodeIdIsWrong() {
        try {
            // WHEN
            when(gameDao.getGameByGameId(1)).thenReturn(game);

            Map<String, String> params = new HashMap<String, String>();
            params.put("nodeId", "14");

            playService.processAction(GameUserActionCode.BUILD_SETTLEMENT, gameUser1.getUser(), "1", params);
            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldFailWhenBuildingSettlementOnAnotherSettlement() {
        try {
            // WHEN
            hex_0_0.getNodes().getTopRight().setBuilding(new Building<NodeBuiltType>(NodeBuiltType.SETTLEMENT, gameUser1));

            when(gameDao.getGameByGameId(1)).thenReturn(game);

            Map<String, String> params = new HashMap<String, String>();
            params.put("nodeId", "3");

            playService.processAction(GameUserActionCode.BUILD_SETTLEMENT, gameUser1.getUser(), "1", params);
            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown, but was thrown: " + e);
        }
    }

    @Test
    public void shouldFailWhenBuildingSettlementTooCloseToOtherSettlements() {
        try {
            // WHEN
            hex_0_0.getNodes().getTopRight().setBuilding(new Building<NodeBuiltType>(NodeBuiltType.SETTLEMENT, gameUser1));

            when(gameDao.getGameByGameId(1)).thenReturn(game);

            Map<String, String> params = new HashMap<String, String>();
            params.put("nodeId", "4");

            playService.processAction(GameUserActionCode.BUILD_SETTLEMENT, gameUser1.getUser(), "1", params);
            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown, but was thrown: " + e);
        }
    }

    @Test
    public void shouldPassWhenBuildingSettlementOnTheWayOfOtherPlayerButNearOwnNeighbourRoad() throws GameException, PlayException {
        // GIVEN
        hex_0_0.getEdges().getTopRight().setBuilding(new Building<EdgeBuiltType>(EdgeBuiltType.ROAD, gameUser2));
        hex_0_0.getEdges().getRight().setBuilding(new Building<EdgeBuiltType>(EdgeBuiltType.ROAD, gameUser2));
        hex_1_0.getEdges().getTopLeft().setBuilding(new Building<EdgeBuiltType>(EdgeBuiltType.ROAD, gameUser1));

        when(gameDao.getGameByGameId(1)).thenReturn(game);

        // WHEN

        Map<String, String> params = new HashMap<String, String>();
        params.put("nodeId", "3");

        playService.processAction(GameUserActionCode.BUILD_SETTLEMENT, gameUser1.getUser(), "1", params);

        // THEN
        assertNotNull(game);
        assertNotNull(hex_0_0);
        assertNotNull(hex_0_0.getNodes().getTopRight());
        assertNotNull(hex_0_0.getNodes().getTopRight().getBuilding());
        assertEquals(hex_0_0.getNodes().getTopRight().getBuilding().getBuilt(), NodeBuiltType.SETTLEMENT);
        assertEquals(hex_0_0.getNodes().getTopRight().getBuilding().getBuildingOwner(), gameUser1);
    }

    @Test
    public void shouldFailWhenBuildSettlementAndGameIsNotPlaying() {
        try {
            // WHEN
            game.setStatus(GameStatus.NEW);

            when(gameDao.getGameByGameId(1)).thenReturn(game);

            Map<String, String> params = new HashMap<String, String>();
            params.put("nodeId", "3");

            playService.processAction(GameUserActionCode.BUILD_SETTLEMENT, gameUser1.getUser(), "1", params);
            fail("GameException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (GameException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldPassWhenBuildingSettlementInPreparationStage() throws GameException, PlayException {
        // WHEN
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("nodeId", "3");

        playService.processAction(GameUserActionCode.BUILD_SETTLEMENT, gameUser1.getUser(), "1", params);

        // THEN
        assertNotNull(game);
        assertNotNull(hex_0_0);
        assertNotNull(hex_0_0.getNodes().getTopRight());
        assertNotNull(hex_0_0.getNodes().getTopRight().getBuilding());
        assertEquals(hex_0_0.getNodes().getTopRight().getBuilding().getBuilt(), NodeBuiltType.SETTLEMENT);
        assertEquals(hex_0_0.getNodes().getTopRight().getBuilding().getBuildingOwner(), gameUser1);
    }

    @Test
    public void shouldFailWhenBuildSettlementByNonActivePlayer() {
        try {
            //GIVEN
            game.setCurrentMove(gameUser2.getMoveOrder());
            playUtil.updateAvailableActionsForAllUsers(game);
            when(gameDao.getGameByGameId(1)).thenReturn(game);

            // WHEN

            Map<String, String> params = new HashMap<String, String>();
            params.put("nodeId", "3");

            playService.processAction(GameUserActionCode.BUILD_SETTLEMENT, gameUser1.getUser(), "1", params);
            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldPassWhenBuildingSettlementInMainStage() throws GameException, PlayException {
        //GIVEN
        hex_0_0.getEdges().getTopRight().setBuilding(new Building<EdgeBuiltType>(EdgeBuiltType.ROAD, gameUser1));
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        // WHEN

        Map<String, String> params = new HashMap<String, String>();
        params.put("nodeId", "3");

        playService.processAction(GameUserActionCode.BUILD_SETTLEMENT, gameUser1.getUser(), "1", params);

        // THEN
        assertNotNull(game);
        assertNotNull(hex_0_0);
        assertNotNull(hex_0_0.getNodes().getTopRight());
        assertNotNull(hex_0_0.getNodes().getTopRight().getBuilding());
        assertEquals(hex_0_0.getNodes().getTopRight().getBuilding().getBuilt(), NodeBuiltType.SETTLEMENT);
        assertEquals(hex_0_0.getNodes().getTopRight().getBuilding().getBuildingOwner(), gameUser1);
    }

    @Test
    public void shouldFailWhenBuildingSettlementNotNearOwnNeighbourInMainStage() {
        try {
            // WHEN
            game.setStage(GameStage.MAIN);
            when(gameDao.getGameByGameId(1)).thenReturn(game);

            Map<String, String> params = new HashMap<String, String>();
            params.put("nodeId", "3");

            playService.processAction(GameUserActionCode.BUILD_SETTLEMENT, gameUser1.getUser(), "1", params);
            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldFailWhenBuildRoadByNonActivePlayer() {
        try {
            //GIVEN
            game.setCurrentMove(gameUser2.getMoveOrder());
            playUtil.updateAvailableActionsForAllUsers(game);
            when(gameDao.getGameByGameId(1)).thenReturn(game);

            // WHEN

            Map<String, String> params = new HashMap<String, String>();
            params.put("edgeId", "7");

            playService.processAction(GameUserActionCode.BUILD_ROAD, gameUser1.getUser(), "1", params);
            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldFailWhenBuildSettlementIfActionIsNotAllowed() throws GameException {
        //GIVEN
        game.setCurrentCycleBuildingNumber(2);
        playUtil.updateAvailableActionsForAllUsers(game);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        try {
            // WHEN

            Map<String, String> params = new HashMap<String, String>();
            params.put("nodeId", "3");

            playService.processAction(GameUserActionCode.BUILD_SETTLEMENT, gameUser1.getUser(), "1", params);
            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldFailWhenBuildRoadIfActionIsNotAllowed() {
        try {
            //WHEN
            when(gameDao.getGameByGameId(1)).thenReturn(game);
            Map<String, String> params = new HashMap<String, String>();
            params.put("edgeId", "7");

            playService.processAction(GameUserActionCode.BUILD_ROAD, gameUser1.getUser(), "1", params);
            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldFailWhenEndTurnIfActionIsNotAllowed() {
        try {
            //WHEN
            when(gameDao.getGameByGameId(1)).thenReturn(game);
            Map<String, String> params = new HashMap<String, String>();
            params.put("edgeId", "7");

            playService.processAction(GameUserActionCode.BUILD_ROAD, gameUser1.getUser(), "1", params);
            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldPassWhenBuildCityInPreparationStage() throws GameException, PlayException {
        // WHEN
        allowUserToBuildCity(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);
        Map<String, String> params = new HashMap<String, String>();
        params.put("nodeId", "3");

        playService.processAction(GameUserActionCode.BUILD_CITY, gameUser1.getUser(), "1", params);

        // THEN
        assertNotNull(game);
        assertNotNull(hex_0_0);
        assertNotNull(hex_0_0.getNodes().getTopRight());
        assertNotNull(hex_0_0.getNodes().getTopRight().getBuilding());
        assertEquals(hex_0_0.getNodes().getTopRight().getBuilding().getBuilt(), NodeBuiltType.CITY);
        assertEquals(hex_0_0.getNodes().getTopRight().getBuilding().getBuildingOwner(), gameUser1);
    }

    @Test
    public void shouldFailWhenBuildCityAndPassedNodeIdIsWrong() {
        try {
            // WHEN
            when(gameDao.getGameByGameId(1)).thenReturn(game);
            Map<String, String> params = new HashMap<String, String>();
            params.put("nodeId", "14");

            playService.processAction(GameUserActionCode.BUILD_CITY, gameUser1.getUser(), "1", params);
            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldFailWhenBuildingCityOnAnotherCity() {
        try {
            // WHEN
            hex_0_0.getNodes().getTopRight().setBuilding(new Building<NodeBuiltType>(NodeBuiltType.CITY, gameUser1));
            allowUserToBuildCity(gameUser1);

            when(gameDao.getGameByGameId(1)).thenReturn(game);
            Map<String, String> params = new HashMap<String, String>();
            params.put("nodeId", "3");

            playService.processAction(GameUserActionCode.BUILD_CITY, gameUser1.getUser(), "1", params);
            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown, but was thrown: " + e);
        }
    }

    @Test
    public void shouldFailWhenBuildingCityOnSettlementInPreparationStage() {
        try {
            // WHEN
            hex_0_0.getNodes().getTopRight().setBuilding(new Building<NodeBuiltType>(NodeBuiltType.SETTLEMENT, gameUser1));
            allowUserToBuildCity(gameUser1);

            when(gameDao.getGameByGameId(1)).thenReturn(game);
            Map<String, String> params = new HashMap<String, String>();
            params.put("nodeId", "3");

            playService.processAction(GameUserActionCode.BUILD_CITY, gameUser1.getUser(), "1", params);
            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown, but was thrown: " + e);
        }
    }

    @Test
    public void shouldFailWhenBuildingCityTooCloseToOtherSettlements() {
        try {
            // WHEN
            hex_0_0.getNodes().getTopRight().setBuilding(new Building<NodeBuiltType>(NodeBuiltType.SETTLEMENT, gameUser1));
            allowUserToBuildCity(gameUser1);

            when(gameDao.getGameByGameId(1)).thenReturn(game);
            Map<String, String> params = new HashMap<String, String>();
            params.put("nodeId", "4");

            playService.processAction(GameUserActionCode.BUILD_CITY, gameUser1.getUser(), "1", params);
            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown, but was thrown: " + e);
        }
    }

    @Test
    public void shouldFailWhenBuildingCityTooCloseToOtherCities() {
        try {
            // WHEN
            hex_0_0.getNodes().getTopRight().setBuilding(new Building<NodeBuiltType>(NodeBuiltType.CITY, gameUser1));
            allowUserToBuildCity(gameUser1);

            when(gameDao.getGameByGameId(1)).thenReturn(game);
            Map<String, String> params = new HashMap<String, String>();
            params.put("nodeId", "4");

            playService.processAction(GameUserActionCode.BUILD_CITY, gameUser1.getUser(), "1", params);
            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown, but was thrown: " + e);
        }
    }

    @Test
    public void shouldFailWhenBuildCityAndGameIsNotPlaying() {
        try {
            // WHEN
            game.setStatus(GameStatus.NEW);

            when(gameDao.getGameByGameId(1)).thenReturn(game);
            Map<String, String> params = new HashMap<String, String>();
            params.put("nodeId", "3");

            playService.processAction(GameUserActionCode.BUILD_CITY, gameUser1.getUser(), "1", params);
            fail("GameException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (GameException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldFailWhenBuildCityByNonActivePlayer() {
        try {
            //GIVEN
            game.setCurrentMove(gameUser2.getMoveOrder());
            playUtil.updateAvailableActionsForAllUsers(game);
            when(gameDao.getGameByGameId(1)).thenReturn(game);

            // WHEN
            Map<String, String> params = new HashMap<String, String>();
            params.put("nodeId", "3");

            playService.processAction(GameUserActionCode.BUILD_CITY, gameUser1.getUser(), "1", params);
            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldFailWhenBuildCityIfActionIsNotAllowed() throws GameException {
        //GIVEN
        game.setCurrentCycleBuildingNumber(2);
        playUtil.updateAvailableActionsForAllUsers(game);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        try {
            // WHEN
            Map<String, String> params = new HashMap<String, String>();
            params.put("nodeId", "3");

            playService.processAction(GameUserActionCode.BUILD_CITY, gameUser1.getUser(), "1", params);
            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldPassWhenBuildCityInMainStage() throws PlayException, GameException {
        // WHEN
        hex_0_0.getNodes().getTopRight().setBuilding(new Building<NodeBuiltType>(NodeBuiltType.SETTLEMENT, gameUser1));
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        allowUserToBuildCity(gameUser1);

        when(gameDao.getGameByGameId(1)).thenReturn(game);
        Map<String, String> params = new HashMap<String, String>();
        params.put("nodeId", "3");

        playService.processAction(GameUserActionCode.BUILD_CITY, gameUser1.getUser(), "1", params);

        // THEN
        assertNotNull(game);
        assertNotNull(hex_0_0);
        assertNotNull(hex_0_0.getNodes().getTopRight());
        assertNotNull(hex_0_0.getNodes().getTopRight().getBuilding());
        assertEquals(hex_0_0.getNodes().getTopRight().getBuilding().getBuilt(), NodeBuiltType.CITY);
        assertEquals(hex_0_0.getNodes().getTopRight().getBuilding().getBuildingOwner(), gameUser1);
    }

    @Test
    public void shouldFailWhenBuildCityOnNodeWithoutBuildingsInMainStage() throws GameException {
        //GIVEN
        game.setStage(GameStage.MAIN);
        allowUserToBuildCity(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        try {
            // WHEN
            Map<String, String> params = new HashMap<String, String>();
            params.put("nodeId", "3");

            playService.processAction(GameUserActionCode.BUILD_CITY, gameUser1.getUser(), "1", params);
            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldPassWhenDisplayVictoryPointsEqualsToTargetAfterSomeAction() throws GameException, PlayException {
        //GIVEN
        hex_0_0.getEdges().getTopRight().setBuilding(new Building<EdgeBuiltType>(EdgeBuiltType.ROAD, gameUser1));
        game.setStage(GameStage.MAIN);
        game.setTargetVictoryPoints(3);
        gameUser1.getBuildingsCount().setSettlements(2);

        //WHEN
        when(gameDao.getGameByGameId(1)).thenReturn(game);
        Map<String, String> params = new HashMap<String, String>();
        params.put("nodeId", "3");

        playService.processAction(GameUserActionCode.BUILD_SETTLEMENT, gameUser1.getUser(), "1", params);

        //THEN
        assertNotNull(game);
        assertEquals(3, gameUser1.getAchievements().getDisplayVictoryPoints());
        assertEquals(GameStatus.FINISHED, game.getStatus());
    }

    @Test
    public void shouldPassWhenRealVictoryPointsEqualsToTargetAfterSomeAction() throws GameException, PlayException {
        //GIVEN
        hex_0_0.getEdges().getTopRight().setBuilding(new Building<EdgeBuiltType>(EdgeBuiltType.ROAD, gameUser1));
        game.setStage(GameStage.MAIN);
        game.setTargetVictoryPoints(4);
        gameUser1.getBuildingsCount().setSettlements(2);
        gameUser1.setDevelopmentCards(new DevelopmentCards(0, 1, 0, 0, 0));

        //WHEN
        when(gameDao.getGameByGameId(1)).thenReturn(game);
        Map<String, String> params = new HashMap<String, String>();
        params.put("nodeId", "3");

        playService.processAction(GameUserActionCode.BUILD_SETTLEMENT, gameUser1.getUser(), "1", params);

        //THEN
        assertNotNull(game);
        assertEquals(3, gameUser1.getAchievements().getDisplayVictoryPoints());
        assertEquals(GameStatus.FINISHED, game.getStatus());
    }

    
    @Test
    public void shouldUpdateVictoryPointsOnBuildCity() throws Exception {
        allowUserToBuildCity(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("nodeId", "3");

        playService.processAction(GameUserActionCode.BUILD_CITY, gameUser1.getUser(), "1", params);

        assertEquals(2, gameUser1.getAchievements().getDisplayVictoryPoints());
    }

    @Test
    public void shouldUpdateVictoryPointsOnBuildSettlement() throws Exception {
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("nodeId", "3");

        playService.processAction(GameUserActionCode.BUILD_SETTLEMENT, gameUser1.getUser(), "1", params);

        assertEquals(1, gameUser1.getAchievements().getDisplayVictoryPoints());
    }

    @Test
    public void shouldUpdateVictoryPointsOnMultipleBuildingsInPreparationStage() throws Exception {
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("nodeId", "3");

        playService.processAction(GameUserActionCode.BUILD_SETTLEMENT, gameUser1.getUser(), "1", params);

        allowUserToBuildSettlement(gameUser1);
        params = new HashMap<String, String>();
        params.put("nodeId", "5");

        playService.processAction(GameUserActionCode.BUILD_SETTLEMENT, gameUser1.getUser(), "1", params);

        allowUserToBuildSettlement(gameUser1);
        params = new HashMap<String, String>();
        params.put("nodeId", "1");

        playService.processAction(GameUserActionCode.BUILD_SETTLEMENT, gameUser1.getUser(), "1", params);

        allowUserToBuildCity(gameUser1);
        params = new HashMap<String, String>();
        params.put("nodeId", "9");

        playService.processAction(GameUserActionCode.BUILD_CITY, gameUser1.getUser(), "1", params);

        assertEquals(5, gameUser1.getAchievements().getDisplayVictoryPoints());
    }

    @Test
    public void shouldUpdateVictoryPointsOnMultipleBuildingsInMainStage() throws Exception {
        //GIVEN
        hex_0_0.getEdges().getTopRight().setBuilding(new Building<EdgeBuiltType>(EdgeBuiltType.ROAD, gameUser1));
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        // WHEN
        assertEquals(0, gameUser1.getAchievements().getDisplayVictoryPoints());

        allowUserToBuildSettlement(gameUser1);
        Map<String, String> params = new HashMap<String, String>();
        params.put("nodeId", "3");

        playService.processAction(GameUserActionCode.BUILD_SETTLEMENT, gameUser1.getUser(), "1", params);

        assertEquals(1, gameUser1.getAchievements().getDisplayVictoryPoints());

        allowUserToBuildCity(gameUser1);
        params = new HashMap<String, String>();
        params.put("nodeId", "3");

        playService.processAction(GameUserActionCode.BUILD_CITY, gameUser1.getUser(), "1", params);

        assertEquals(2, gameUser1.getAchievements().getDisplayVictoryPoints());
    }

    @Test
    public void shouldFailWhenBuildCityInMainStageBeforeDiceThrown() throws PlayException, GameException{
        // WHEN
        hex_0_0.getNodes().getTopRight().setBuilding(new Building<NodeBuiltType>(NodeBuiltType.SETTLEMENT, gameUser1));
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(false);
        allowUserToThrowDice(gameUser1);

        when(gameDao.getGameByGameId(1)).thenReturn(game);
        Map<String, String> params = new HashMap<String, String>();
        params.put("nodeId", "3");

        try {
            playService.processAction(GameUserActionCode.BUILD_CITY, gameUser1.getUser(), "1", params);
            fail("Exception should be thrown should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldPassWhenBuildCityInMainStageAfterDiceThrown() throws PlayException, GameException{
        // WHEN
        hex_0_0.getNodes().getTopRight().setBuilding(new Building<NodeBuiltType>(NodeBuiltType.SETTLEMENT, gameUser1));
        gameUser1.setResources(new Resources(0, 0, 0, 2, 3));
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(false);
        allowUserToThrowDice(gameUser1);

        when(gameDao.getGameByGameId(1)).thenReturn(game);
        Map<String, String> params = new HashMap<String, String>();
        params.put("nodeId", "3");

        //Should generate dices values 2 & 6
        rvg.setNextGeneratedValue(0.3);
        rvg.setNextGeneratedValue(0.9);

        playService.processAction(GameUserActionCode.THROW_DICE, gameUser1.getUser(), "1");
        playService.processAction(GameUserActionCode.BUILD_CITY, gameUser1.getUser(), "1", params);

        // THEN
        assertNotNull(game);
        assertNotNull(hex_0_0);
        assertNotNull(hex_0_0.getNodes().getTopRight());
        assertNotNull(hex_0_0.getNodes().getTopRight().getBuilding());
        assertEquals(hex_0_0.getNodes().getTopRight().getBuilding().getBuilt(), NodeBuiltType.CITY);
        assertEquals(hex_0_0.getNodes().getTopRight().getBuilding().getBuildingOwner(), gameUser1);
    }


    @Test
    public void shouldPassWhenBuildingSettlementInMainStageOnlyAfterDiceThrown() throws GameException, PlayException {
        //GIVEN
        hex_0_0.getEdges().getTopRight().setBuilding(new Building<EdgeBuiltType>(EdgeBuiltType.ROAD, gameUser1));
        gameUser1.setResources(new Resources(1, 1, 1, 1, 0));
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(false);
        allowUserToThrowDice(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("nodeId", "3");

        try {
            playService.processAction(GameUserActionCode.BUILD_SETTLEMENT, gameUser1.getUser(), "1", params);
            fail("Exception should be thrown should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }

        //Should generate dices values 2 & 6
        rvg.setNextGeneratedValue(0.3);
        rvg.setNextGeneratedValue(0.9);

        playService.processAction(GameUserActionCode.THROW_DICE, gameUser1.getUser(), "1");


        // WHEN
        playService.processAction(GameUserActionCode.BUILD_SETTLEMENT, gameUser1.getUser(), "1", params);

        // THEN
        assertNotNull(game);
        assertNotNull(hex_0_0);
        assertNotNull(hex_0_0.getNodes().getTopRight());
        assertNotNull(hex_0_0.getNodes().getTopRight().getBuilding());
        assertEquals(hex_0_0.getNodes().getTopRight().getBuilding().getBuilt(), NodeBuiltType.SETTLEMENT);
        assertEquals(hex_0_0.getNodes().getTopRight().getBuilding().getBuildingOwner(), gameUser1);
    }

    @Test
    public void shouldPassWhenBuildingRoadNearOwnNeighbourRoadOnlyAfterDiceThrown() throws GameException, PlayException {
        //GIVEN
        hex_0_0.getEdges().getTopRight().setBuilding(new Building<EdgeBuiltType>(EdgeBuiltType.ROAD, gameUser1));
        gameUser1.setResources(new Resources(1, 1, 0, 0, 0));
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(false);
        allowUserToThrowDice(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("edgeId", "7");

        try {
            playService.processAction(GameUserActionCode.BUILD_ROAD, gameUser1.getUser(), "1", params);
            fail("Exception should be thrown should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }

        //Should generate dices values 2 & 6
        rvg.setNextGeneratedValue(0.3);
        rvg.setNextGeneratedValue(0.9);

        playService.processAction(GameUserActionCode.THROW_DICE, gameUser1.getUser(), "1");

        // WHEN
        playService.processAction(GameUserActionCode.BUILD_ROAD, gameUser1.getUser(), "1", params);

        // THEN
        assertNotNull(game);
        assertNotNull(hex_1_0);
        assertNotNull(hex_1_0.getEdges().getTopLeft());
        assertNotNull(hex_1_0.getEdges().getTopLeft().getBuilding());
        assertEquals(hex_1_0.getEdges().getTopLeft().getBuilding().getBuilt(), EdgeBuiltType.ROAD);
        assertEquals(hex_1_0.getEdges().getTopLeft().getBuilding().getBuildingOwner(), gameUser1);
    }

    @Test
    public void shouldAllowEndTurnOnlyAfterDiceThrown() throws GameException, PlayException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(false);
        allowUserToThrowDice(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        try {
            playService.processAction(GameUserActionCode.END_TURN, gameUser1.getUser(), "1");
            fail("Exception should be thrown should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }

        //Should generate dices values 2 & 6
        rvg.setNextGeneratedValue(0.3);
        rvg.setNextGeneratedValue(0.9);

        playService.processAction(GameUserActionCode.THROW_DICE, gameUser1.getUser(), "1");

        // WHEN
        playService.processAction(GameUserActionCode.END_TURN, gameUser1.getUser(), "1");

        // THEN
        assertNotNull(game);
        assertNotNull(game.getCurrentMove());
        assertEquals(game.getCurrentMove().intValue(), gameUser2.getMoveOrder());
    }

    @Test
    public void shouldFailWhenThrowDiceAfterItIsAlreadyThrown() throws GameException, PlayException {
        //GIVEN
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(false);
        allowUserToThrowDice(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        //Should generate dices values 2 & 6
        rvg.setNextGeneratedValue(0.3);
        rvg.setNextGeneratedValue(0.9);

        playService.processAction(GameUserActionCode.THROW_DICE, gameUser1.getUser(), "1");

        try {
            // WHEN
            playService.processAction(GameUserActionCode.THROW_DICE, gameUser1.getUser(), "1");

            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldFailWhenUserThrowDiceNotAtHisTurn() throws GameException, PlayException {
        //GIVEN
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(false);
        allowUserToThrowDice(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        //Should generate dices values 2 & 6
        rvg.setNextGeneratedValue(0.3);
        rvg.setNextGeneratedValue(0.9);

        playService.processAction(GameUserActionCode.THROW_DICE, gameUser1.getUser(), "1");

        try {
            // WHEN
            playService.processAction(GameUserActionCode.THROW_DICE, gameUser2.getUser(), "1");

            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldPassWhenPlayerThrowDice() throws GameException, PlayException {
        //GIVEN

        //Should generate dices values 2 & 6
        rvg.setNextGeneratedValue(0.3);
        rvg.setNextGeneratedValue(0.9);

        game.setStage(GameStage.MAIN);
        game.setDiceThrown(false);
        allowUserToThrowDice(gameUser1);

        hex_0_0.setDice(8);
        hex_0_0.setResourceType(HexType.WOOD);
        hex_0_0.getNodes().getTop().setBuilding(new Building<NodeBuiltType>(NodeBuiltType.SETTLEMENT, gameUser1));
        hex_0_0.getNodes().getBottomLeft().setBuilding(new Building<NodeBuiltType>(NodeBuiltType.CITY, gameUser2));

        hex_1_0.setDice(9);
        hex_1_0.getNodes().getTop().setBuilding(new Building<NodeBuiltType>(NodeBuiltType.CITY, gameUser1));
        hex_1_0.getNodes().getBottomRight().setBuilding(new Building<NodeBuiltType>(NodeBuiltType.SETTLEMENT, gameUser2));

        when(gameDao.getGameByGameId(1)).thenReturn(game);

        //WHEN
        playService.processAction(GameUserActionCode.THROW_DICE, gameUser1.getUser(), "1");

        //THEN
        assertNotNull(game);
        assertTrue(game.isDiceThrown());
        assertNotNull(game.getDiceFirstValue());
        assertTrue(game.getDiceFirstValue() == 2);
        assertNotNull(game.getDiceSecondValue());
        assertTrue(game.getDiceSecondValue() == 6);
        assertNotNull(game.calculateDiceSumValue());
        assertTrue(game.calculateDiceSumValue() == 8);

        assertNotNull(gameUser1.getResources());
        assertEquals(0, gameUser1.getResources().getBrick());
        assertEquals(1, gameUser1.getResources().getWood());
        assertEquals(0, gameUser1.getResources().getSheep());
        assertEquals(0, gameUser1.getResources().getStone());
        assertEquals(0, gameUser1.getResources().getWheat());

        assertNotNull(gameUser2.getResources());
        assertEquals(0, gameUser2.getResources().getBrick());
        assertEquals(2, gameUser2.getResources().getWood());
        assertEquals(0, gameUser2.getResources().getSheep());
        assertEquals(0, gameUser2.getResources().getStone());
        assertEquals(0, gameUser2.getResources().getWheat());
    }

    @Test
    public void shouldAllowToBuildRoadOnlyIfPlayerHasEnoughResources() throws GameException, PlayException {
        //GIVEN
        hex_0_0.getNodes().getTopRight().setBuilding(new Building<NodeBuiltType>(NodeBuiltType.SETTLEMENT, gameUser1));
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(false);
        allowUserToThrowDice(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        //Should generate dices values 2 & 6
        rvg.setNextGeneratedValue(0.3);
        rvg.setNextGeneratedValue(0.9);

        playService.processAction(GameUserActionCode.THROW_DICE, gameUser1.getUser(), "1");

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("edgeId", "7");

        try {
            playService.processAction(GameUserActionCode.BUILD_ROAD, gameUser1.getUser(), "1", params);
            fail("Exception should be thrown should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }

        gameUser1.setResources(new Resources(1, 1, 0, 0, 0));
        playUtil.updateAvailableActionsForAllUsers(game);

        // WHEN
        playService.processAction(GameUserActionCode.BUILD_ROAD, gameUser1.getUser(), "1", params);

        // THEN
        assertNotNull(gameUser1.getResources());
        assertEquals(0, gameUser1.getResources().getBrick());
        assertEquals(0, gameUser1.getResources().getWood());
        assertEquals(0, gameUser1.getResources().getSheep());
        assertEquals(0, gameUser1.getResources().getStone());
        assertEquals(0, gameUser1.getResources().getWheat());
    }

    @Test
    public void shouldAllowToBuildSettlementOnlyIfPlayerHasEnoughResources() throws GameException, PlayException {
        //GIVEN
        hex_0_0.getEdges().getTopRight().setBuilding(new Building<EdgeBuiltType>(EdgeBuiltType.ROAD, gameUser1));
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(false);
        allowUserToThrowDice(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        //Should generate dices values 2 & 6
        rvg.setNextGeneratedValue(0.3);
        rvg.setNextGeneratedValue(0.9);

        playService.processAction(GameUserActionCode.THROW_DICE, gameUser1.getUser(), "1");

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("nodeId", "3");

        try {
            playService.processAction(GameUserActionCode.BUILD_SETTLEMENT, gameUser1.getUser(), "1", params);
            fail("Exception should be thrown should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }

        gameUser1.setResources(new Resources(1, 1, 1, 1, 0));
        playUtil.updateAvailableActionsForAllUsers(game);

        // WHEN
        playService.processAction(GameUserActionCode.BUILD_SETTLEMENT, gameUser1.getUser(), "1", params);

        // THEN
        assertNotNull(gameUser1.getResources());
        assertEquals(0, gameUser1.getResources().getBrick());
        assertEquals(0, gameUser1.getResources().getWood());
        assertEquals(0, gameUser1.getResources().getSheep());
        assertEquals(0, gameUser1.getResources().getStone());
        assertEquals(0, gameUser1.getResources().getWheat());
    }

    @Test
    public void shouldAllowToBuildCityOnlyIfPlayerHasEnoughResources() throws GameException, PlayException {
        //GIVEN
        hex_0_0.getNodes().getTopRight().setBuilding(new Building<NodeBuiltType>(NodeBuiltType.SETTLEMENT, gameUser1));
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(false);
        allowUserToThrowDice(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        //Should generate dices values 2 & 6
        rvg.setNextGeneratedValue(0.3);
        rvg.setNextGeneratedValue(0.9);

        playService.processAction(GameUserActionCode.THROW_DICE, gameUser1.getUser(), "1");

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("nodeId", "3");

        try {
            playService.processAction(GameUserActionCode.BUILD_CITY, gameUser1.getUser(), "1", params);
            fail("Exception should be thrown should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }

        gameUser1.setResources(new Resources(0, 0, 0, 2, 3));
        playUtil.updateAvailableActionsForAllUsers(game);

        // WHEN
        playService.processAction(GameUserActionCode.BUILD_CITY, gameUser1.getUser(), "1", params);

        // THEN
        assertNotNull(gameUser1.getResources());
        assertEquals(0, gameUser1.getResources().getBrick());
        assertEquals(0, gameUser1.getResources().getWood());
        assertEquals(0, gameUser1.getResources().getSheep());
        assertEquals(0, gameUser1.getResources().getStone());
        assertEquals(0, gameUser1.getResources().getWheat());
    }
    
    @Test
    public void shouldAllowToBuyDevelopmentCardOnlyIfPlayerHasEnoughResources() throws GameException, PlayException {
        //GIVEN
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(false);
        allowUserToThrowDice(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        //Should generate dices values 2 & 6
        rvg.setNextGeneratedValue(0.3);
        rvg.setNextGeneratedValue(0.9);

        playService.processAction(GameUserActionCode.THROW_DICE, gameUser1.getUser(), "1");

        try {
            playService.processAction(GameUserActionCode.BUY_CARD, gameUser1.getUser(), "1");
            fail("Exception should be thrown should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }

        gameUser1.setResources(new Resources(0, 0, 1, 1, 1));
        playUtil.updateAvailableActionsForAllUsers(game);

        // WHEN
        playService.processAction(GameUserActionCode.BUY_CARD, gameUser1.getUser(), "1");

        // THEN
        assertNotNull(gameUser1.getResources());
        assertEquals(0, gameUser1.getResources().getBrick());
        assertEquals(0, gameUser1.getResources().getWood());
        assertEquals(0, gameUser1.getResources().getSheep());
        assertEquals(0, gameUser1.getResources().getStone());
        assertEquals(0, gameUser1.getResources().getWheat());
    }

    @Test
    public void shouldPassWhenBuildSomethingInPreparationStageAndResourcesDoesNotTakenFromPlayer() throws PlayException, GameException {
        // GIVEN
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        // WHEN

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("nodeId", "3");
        params.put("edgeId", "7");

        playService.processAction(GameUserActionCode.BUILD_SETTLEMENT, gameUser1.getUser(), "1", params);
        playService.processAction(GameUserActionCode.BUILD_ROAD, gameUser1.getUser(), "1", params);

        allowUserToBuildCity(gameUser1);
        params.put("nodeId", "8");
        playService.processAction(GameUserActionCode.BUILD_CITY, gameUser1.getUser(), "1", params);

        // THEN
        assertNotNull(gameUser1);
        assertNotNull(gameUser1.getResources());
        assertEquals(0, gameUser2.getResources().getBrick());
        assertEquals(0, gameUser2.getResources().getWood());
        assertEquals(0, gameUser2.getResources().getSheep());
        assertEquals(0, gameUser2.getResources().getStone());
        assertEquals(0, gameUser2.getResources().getWheat());
    }

    @Test
    public void shouldPassWhenUserBuyCardIfActionIsAllowed() throws PlayException, GameException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        allowUserToBuyCard(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        // WHEN
        playService.processAction(GameUserActionCode.BUY_CARD, gameUser1.getUser(), "1");

        // THEN
        assertNotNull(game);
        assertNotNull(gameUser1);
        assertNotNull(gameUser1.getDevelopmentCards());

        int obtainedCardsSum = gameUser1.getDevelopmentCards().getKnight()
                + gameUser1.getDevelopmentCards().getVictoryPoint()
                + gameUser1.getDevelopmentCards().getMonopoly()
                + gameUser1.getDevelopmentCards().getRoadBuilding()
                + gameUser1.getDevelopmentCards().getYearOfPlenty();
        assertEquals(obtainedCardsSum, 1);

        int availableCardsSum = game.getAvailableDevelopmentCards().getKnight()
                + game.getAvailableDevelopmentCards().getVictoryPoint()
                + game.getAvailableDevelopmentCards().getMonopoly()
                + game.getAvailableDevelopmentCards().getRoadBuilding()
                + game.getAvailableDevelopmentCards().getYearOfPlenty();
        assertEquals(availableCardsSum, 24);
    }

    @Test
    public void shouldPassWhenUserBuyCardAllCardsAndQuantityOfEachCardIsCorrect() throws PlayException, GameException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        gameUser1.setResources(new Resources(0, 0, 25, 25, 25));
        allowUserToBuyCard(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        // WHEN
        for (int i = 25; i > 0; i--) {
            playService.processAction(GameUserActionCode.BUY_CARD, gameUser1.getUser(), "1");
        }

        // THEN
        assertNotNull(game);
        assertNotNull(gameUser1);
        assertNotNull(gameUser1.getDevelopmentCards());

        assertEquals(gameUser1.getDevelopmentCards().getKnight(), 14);
        assertEquals(gameUser1.getDevelopmentCards().getVictoryPoint(), 5);
        assertEquals(gameUser1.getDevelopmentCards().getMonopoly(), 2);
        assertEquals(gameUser1.getDevelopmentCards().getRoadBuilding(), 2);
        assertEquals(gameUser1.getDevelopmentCards().getYearOfPlenty(), 2);

        assertEquals(game.getAvailableDevelopmentCards().getKnight(), 0);
        assertEquals(game.getAvailableDevelopmentCards().getVictoryPoint(), 0);
        assertEquals(game.getAvailableDevelopmentCards().getMonopoly(), 0);
        assertEquals(game.getAvailableDevelopmentCards().getRoadBuilding(), 0);
        assertEquals(game.getAvailableDevelopmentCards().getYearOfPlenty(), 0);
    }

    @Test
    public void shouldFailWhenUserBuyCardAtHisTurnButActionIsNotAllowed() throws PlayException, GameException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        gameUser1.setResources(new Resources(0, 0, 1, 1, 1));
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        try {
            // WHEN
            playService.processAction(GameUserActionCode.BUY_CARD, gameUser1.getUser(), "1");

            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldFailWhenUserBuyCardNotAtHisTurn() throws PlayException, GameException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        gameUser2.setResources(new Resources(0, 0, 1, 1, 1));
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        try {
            // WHEN
            playService.processAction(GameUserActionCode.BUY_CARD, gameUser2.getUser(), "1");

            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldFailWhenUserBuyCardIfDevelopmentCardsIsOver() throws PlayException, GameException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        gameUser1.setResources(new Resources(0, 0, 26, 26, 26));
        allowUserToBuyCard(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        for (int i = 25; i > 0; i--) {
            playService.processAction(GameUserActionCode.BUY_CARD, gameUser1.getUser(), "1");
        }

        try {
            // WHEN
            playService.processAction(GameUserActionCode.BUY_CARD, gameUser1.getUser(), "1");

            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(CardUtil.CARDS_ARE_OVER_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldPassWhenUserUseCardYearOfPlentyIfActionIsAllowed() throws PlayException, GameException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        gameUser1.setDevelopmentCards(new DevelopmentCards(0, 0, 0, 0, 1));
        gameUser1.setDevelopmentCardsReadyForUsing(new DevelopmentCards(0, 0, 0, 0, 1));
        allowUserToUseCardYearOfPlenty(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("firstResource", "WOOD");
        params.put("secondResource", "BRICK");

        // WHEN
        playService.processAction(GameUserActionCode.USE_CARD_YEAR_OF_PLENTY, gameUser1.getUser(), "1", params);

        // THEN
        assertNotNull(game);
        assertNotNull(gameUser1);
        assertEquals(0, gameUser1.getDevelopmentCards().getYearOfPlenty());
        assertEquals(1, gameUser1.getResources().getBrick());
        assertEquals(1, gameUser1.getResources().getWood());
    }

    @Test
    public void shouldFailWhenUserUseCardYearOfPlentyButItWasBoughtInThisTurn() throws PlayException, GameException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        gameUser1.setDevelopmentCards(new DevelopmentCards(0, 0, 0, 0, 0));
        gameUser1.setDevelopmentCardsReadyForUsing(new DevelopmentCards(0, 0, 0, 0, 0));
        gameUser1.setResources(new Resources(0, 0, 25, 25, 25));
        playUtil.updateAvailableActionsForAllUsers(game);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        String card;
        do {
            card = playService.processAction(GameUserActionCode.BUY_CARD, gameUser1.getUser(), "1").get("card");
        } while (!"YEAR_OF_PLENTY".equals(card));

        Map<String, String> params = new HashMap<String, String>();
        params.put("firstResource", "WOOD");
        params.put("secondResource", "BRICK");

        try {
            // WHEN
            playService.processAction(GameUserActionCode.USE_CARD_YEAR_OF_PLENTY, gameUser1.getUser(), "1", params);

            fail("PlayException with error code '" + CardUtil.CARD_BOUGHT_IN_CURRENT_TURN_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(CardUtil.CARD_BOUGHT_IN_CURRENT_TURN_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldPassWhenUserUseCardYearOfPlentyAlthoughAnotherOneYearOfPlentyCardWasBoughtInThisTurn() throws PlayException, GameException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        gameUser1.setDevelopmentCards(new DevelopmentCards(0, 0, 0, 0, 1));
        gameUser1.setDevelopmentCardsReadyForUsing(new DevelopmentCards(0, 0, 0, 0, 1));
        gameUser1.setResources(new Resources(0, 0, 25, 25, 25));
        playUtil.updateAvailableActionsForAllUsers(game);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        String card;
        do {
            card = playService.processAction(GameUserActionCode.BUY_CARD, gameUser1.getUser(), "1").get("card");
        } while (!"YEAR_OF_PLENTY".equals(card));

        Map<String, String> params = new HashMap<String, String>();
        params.put("firstResource", "WOOD");
        params.put("secondResource", "BRICK");

        // WHEN
        playService.processAction(GameUserActionCode.USE_CARD_YEAR_OF_PLENTY, gameUser1.getUser(), "1", params);

        // THEN
        assertNotNull(game);
        assertNotNull(gameUser1);
        assertEquals(1, gameUser1.getDevelopmentCards().getYearOfPlenty());
    }

    @Test
    public void shouldFailWhenUserUseCardYearOfPlentyButOneOfCardsAlreadyUsedInThisTurn() throws PlayException, GameException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        gameUser1.setDevelopmentCards(new DevelopmentCards(0, 0, 0, 0, 2));
        gameUser1.setDevelopmentCardsReadyForUsing(new DevelopmentCards(0, 0, 0, 0, 2));
        playUtil.updateAvailableActionsForAllUsers(game);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("firstResource", "WOOD");
        params.put("secondResource", "BRICK");

        playService.processAction(GameUserActionCode.USE_CARD_YEAR_OF_PLENTY, gameUser1.getUser(), "1", params);

        try {
            // WHEN
            playService.processAction(GameUserActionCode.USE_CARD_YEAR_OF_PLENTY, gameUser1.getUser(), "1", params);

            fail("PlayException with error code '" + CardUtil.CARD_ALREADY_USED_IN_CURRENT_TURN_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(CardUtil.CARD_ALREADY_USED_IN_CURRENT_TURN_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldFailWhenUserUseCardYearOfPlentyAtHisTurnButActionIsNotAllowed() throws PlayException, GameException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("firstResource", "WOOD");
        params.put("secondResource", "BRICK");

        try {
            // WHEN
            playService.processAction(GameUserActionCode.USE_CARD_YEAR_OF_PLENTY, gameUser1.getUser(), "1", params);

            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldFailWhenUserUseCardYearOfPlentyNotAtHisTurn() throws PlayException, GameException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("firstResource", "WOOD");
        params.put("secondResource", "BRICK");

        try {
            // WHEN
            playService.processAction(GameUserActionCode.USE_CARD_YEAR_OF_PLENTY, gameUser2.getUser(), "1", params);

            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }
    
    @Test
    public void shouldPassWhenUserUseCardMonopolyIfActionIsAllowed() throws PlayException, GameException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        gameUser2.setResources(new Resources(0, 5, 0, 0, 0));
        gameUser1.setDevelopmentCards(new DevelopmentCards(0, 0, 0, 1, 0));
        gameUser1.setDevelopmentCardsReadyForUsing(new DevelopmentCards(0, 0, 0, 1, 0));
        allowUserToUseCardMonopoly(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("resource", "WOOD");

        // WHEN
        String resourcesCount = playService.processAction(GameUserActionCode.USE_CARD_MONOPOLY, gameUser1.getUser(), "1", params).get("resourcesCount");

        // THEN
        assertNotNull(game);
        assertNotNull(gameUser1);
        assertEquals(0, gameUser1.getDevelopmentCards().getMonopoly());
        assertEquals(5, gameUser1.getResources().getWood());
        assertEquals(5, Integer.parseInt(resourcesCount));
        assertNotNull(gameUser2);
        assertEquals(0, gameUser2.getResources().getWood());
    }

    @Test
    public void shouldFailWhenUserUseCardMonopolyButItWasBoughtInThisTurn() throws PlayException, GameException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        gameUser1.setDevelopmentCards(new DevelopmentCards(0, 0, 0, 0, 0));
        gameUser1.setDevelopmentCardsReadyForUsing(new DevelopmentCards(0, 0, 0, 0, 0));
        gameUser1.setResources(new Resources(0, 0, 25, 25, 25));
        playUtil.updateAvailableActionsForAllUsers(game);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        while (!"MONOPOLY".equals(playService.processAction(GameUserActionCode.BUY_CARD, gameUser1.getUser(), "1").get("card"))) {}

        Map<String, String> params = new HashMap<String, String>();
        params.put("resource", "WOOD");

        try {
            // WHEN
            playService.processAction(GameUserActionCode.USE_CARD_MONOPOLY, gameUser1.getUser(), "1", params);

            fail("PlayException with error code '" + CardUtil.CARD_BOUGHT_IN_CURRENT_TURN_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(CardUtil.CARD_BOUGHT_IN_CURRENT_TURN_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldPassWhenUserUseCardMonopolyAlthoughAnotherOneMonopolyCardWasBoughtInThisTurn() throws PlayException, GameException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        gameUser2.setResources(new Resources(0, 5, 0, 0, 0));
        gameUser1.setDevelopmentCards(new DevelopmentCards(0, 0, 0, 1, 0));
        gameUser1.setDevelopmentCardsReadyForUsing(new DevelopmentCards(0, 0, 0, 1, 0));
        gameUser1.setResources(new Resources(0, 0, 25, 25, 25));
        playUtil.updateAvailableActionsForAllUsers(game);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        while (!"MONOPOLY".equals(playService.processAction(GameUserActionCode.BUY_CARD, gameUser1.getUser(), "1").get("card"))) {}

        Map<String, String> params = new HashMap<String, String>();
        params.put("resource", "WOOD");

        // WHEN
        playService.processAction(GameUserActionCode.USE_CARD_MONOPOLY, gameUser1.getUser(), "1", params);

        // THEN
        assertNotNull(game);
        assertNotNull(gameUser1);
        assertEquals(1, gameUser1.getDevelopmentCards().getMonopoly());
        assertEquals(5, gameUser1.getResources().getWood());
        assertNotNull(gameUser2);
        assertEquals(0, gameUser2.getResources().getWood());
    }

    @Test
    public void shouldFailWhenUserUseCardMonopolyButOneOfCardsAlreadyUsedInThisTurn() throws PlayException, GameException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        gameUser1.setDevelopmentCards(new DevelopmentCards(0, 0, 0, 1, 1));
        gameUser1.setDevelopmentCardsReadyForUsing(new DevelopmentCards(0, 0, 0, 1, 1));
        playUtil.updateAvailableActionsForAllUsers(game);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("firstResource", "WOOD");
        params.put("secondResource", "BRICK");
        params.put("resource", "BRICK");

        playService.processAction(GameUserActionCode.USE_CARD_YEAR_OF_PLENTY, gameUser1.getUser(), "1", params);

        try {
            // WHEN
            playService.processAction(GameUserActionCode.USE_CARD_MONOPOLY, gameUser1.getUser(), "1", params);

            fail("PlayException with error code '" + CardUtil.CARD_ALREADY_USED_IN_CURRENT_TURN_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(CardUtil.CARD_ALREADY_USED_IN_CURRENT_TURN_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldFailWhenUserUseCardMonopolyAtHisTurnButActionIsNotAllowed() throws PlayException, GameException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("resource", "WOOD");

        try {
            // WHEN
            playService.processAction(GameUserActionCode.USE_CARD_MONOPOLY, gameUser1.getUser(), "1", params);

            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldFailWhenUserUseCardMonopolyNotAtHisTurn() throws PlayException, GameException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("resource", "WOOD");

        try {
            // WHEN
            playService.processAction(GameUserActionCode.USE_CARD_MONOPOLY, gameUser2.getUser(), "1", params);

            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldPassWhenUserUseCardRoadBuildingIfActionIsAllowed() throws PlayException, GameException {
        //GIVEN
        hex_0_0.getEdges().getTopRight().setBuilding(new Building<EdgeBuiltType>(EdgeBuiltType.ROAD, gameUser1));
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        gameUser1.setDevelopmentCards(new DevelopmentCards(0, 0, 1, 0, 0));
        gameUser1.setDevelopmentCardsReadyForUsing(new DevelopmentCards(0, 0, 1, 0, 0));
        allowUserToUseCardRoadBuilding(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        // WHEN
        String roadsCount = playService.processAction(GameUserActionCode.USE_CARD_ROAD_BUILDING, gameUser1.getUser(), "1").get("roadsCount");

        // THEN
        assertNotNull(game);
        assertNotNull(gameUser1);
        assertEquals(0, gameUser1.getDevelopmentCards().getRoadBuilding());
        assertEquals("2", roadsCount);
        assertTrue(game.getRoadsToBuildMandatory() == 2);
        assertTrue(gameUser1.getAvailableActions().contains("\"code\":\"BUILD_ROAD\""));
        assertTrue(gameUser1.getAvailableActions().contains("\"isMandatory\":true"));
    }

    @Test
    public void shouldFailWhenUserUseCardRoadBuildingButItWasBoughtInThisTurn() throws PlayException, GameException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        gameUser1.setDevelopmentCards(new DevelopmentCards(0, 0, 0, 0, 0));
        gameUser1.setDevelopmentCardsReadyForUsing(new DevelopmentCards(0, 0, 0, 0, 0));
        gameUser1.setResources(new Resources(0, 0, 25, 25, 25));
        playUtil.updateAvailableActionsForAllUsers(game);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        while (!"ROAD_BUILDING".equals(playService.processAction(GameUserActionCode.BUY_CARD, gameUser1.getUser(), "1").get("card"))) {}

        try {
            // WHEN
            playService.processAction(GameUserActionCode.USE_CARD_ROAD_BUILDING, gameUser1.getUser(), "1");

            fail("PlayException with error code '" + CardUtil.CARD_BOUGHT_IN_CURRENT_TURN_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(CardUtil.CARD_BOUGHT_IN_CURRENT_TURN_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldPassWhenUserUseCardRoadBuildingAlthoughAnotherOneRoadBuildingCardWasBoughtInThisTurn() throws PlayException, GameException {
        //GIVEN
        hex_0_0.getEdges().getTopRight().setBuilding(new Building<EdgeBuiltType>(EdgeBuiltType.ROAD, gameUser1));
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        gameUser1.setDevelopmentCards(new DevelopmentCards(0, 0, 1, 0, 0));
        gameUser1.setDevelopmentCardsReadyForUsing(new DevelopmentCards(0, 0, 1, 0, 0));
        gameUser1.setResources(new Resources(0, 0, 25, 25, 25));
        playUtil.updateAvailableActionsForAllUsers(game);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        while (!"ROAD_BUILDING".equals(playService.processAction(GameUserActionCode.BUY_CARD, gameUser1.getUser(), "1").get("card"))) {}

        // WHEN
        playService.processAction(GameUserActionCode.USE_CARD_ROAD_BUILDING, gameUser1.getUser(), "1");

        // THEN
        assertNotNull(game);
        assertNotNull(gameUser1);
        assertEquals(1, gameUser1.getDevelopmentCards().getRoadBuilding());
    }

    @Test
    public void shouldFailWhenUserUseCardRoadBuildingButOneOfCardsAlreadyUsedInThisTurn() throws PlayException, GameException {
        //GIVEN
        hex_0_0.getEdges().getTopRight().setBuilding(new Building<EdgeBuiltType>(EdgeBuiltType.ROAD, gameUser1));
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        gameUser1.setDevelopmentCards(new DevelopmentCards(0, 0, 1, 0, 1));
        gameUser1.setDevelopmentCardsReadyForUsing(new DevelopmentCards(0, 0, 1, 0, 1));
        playUtil.updateAvailableActionsForAllUsers(game);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("firstResource", "WOOD");
        params.put("secondResource", "BRICK");

        playService.processAction(GameUserActionCode.USE_CARD_YEAR_OF_PLENTY, gameUser1.getUser(), "1", params);

        try {
            // WHEN
            playService.processAction(GameUserActionCode.USE_CARD_ROAD_BUILDING, gameUser1.getUser(), "1");

            fail("PlayException with error code '" + CardUtil.CARD_ALREADY_USED_IN_CURRENT_TURN_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(CardUtil.CARD_ALREADY_USED_IN_CURRENT_TURN_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldFailWhenUserUseCardRoadBuildingAtHisTurnButActionIsNotAllowed() throws PlayException, GameException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        try {
            // WHEN
            playService.processAction(GameUserActionCode.USE_CARD_ROAD_BUILDING, gameUser1.getUser(), "1");

            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldFailWhenUserUseCardRoadBuildingNotAtHisTurn() throws PlayException, GameException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        try {
            // WHEN
            playService.processAction(GameUserActionCode.USE_CARD_ROAD_BUILDING, gameUser2.getUser(), "1");

            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldPassWhenBuildTwoMandatoryRoadsWhenUsingRoadBuildingCardAndThereAreMoreThenOneAvailableEdges() throws PlayException, GameException {
        //GIVEN
        hex_0_0.getEdges().getTopRight().setBuilding(new Building<EdgeBuiltType>(EdgeBuiltType.ROAD, gameUser1));
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        gameUser1.setDevelopmentCards(new DevelopmentCards(0, 0, 1, 0, 0));
        gameUser1.setDevelopmentCardsReadyForUsing(new DevelopmentCards(0, 0, 1, 0, 0));
        playUtil.updateAvailableActionsForAllUsers(game);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();

        // WHEN
        playService.processAction(GameUserActionCode.USE_CARD_ROAD_BUILDING, gameUser1.getUser(), "1");
        params.put("edgeId", "1");
        playService.processAction(GameUserActionCode.BUILD_ROAD, gameUser1.getUser(), "1", params);
        params.put("edgeId", "6");
        playService.processAction(GameUserActionCode.BUILD_ROAD, gameUser1.getUser(), "1", params);

        try {
            params.put("edgeId", "5");
            playService.processAction(GameUserActionCode.BUILD_ROAD, gameUser1.getUser(), "1", params);
            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        } finally {
            assertNotNull(game);
            assertNotNull(gameUser1);
            assertEquals(0, gameUser1.getDevelopmentCards().getRoadBuilding());
            assertTrue(game.getRoadsToBuildMandatory() == 0);

            assertNotNull(hex_0_0.getEdges().getTopLeft().getBuilding());
            assertEquals(hex_0_0.getEdges().getTopLeft().getBuilding().getBuilt(), EdgeBuiltType.ROAD);
            assertEquals(hex_0_0.getEdges().getTopLeft().getBuilding().getBuildingOwner(), gameUser1);

            assertNotNull(hex_0_0.getEdges().getLeft().getBuilding());
            assertEquals(hex_0_0.getEdges().getLeft().getBuilding().getBuilt(), EdgeBuiltType.ROAD);
            assertEquals(hex_0_0.getEdges().getLeft().getBuilding().getBuildingOwner(), gameUser1);

            assertNull(hex_0_0.getEdges().getBottomLeft().getBuilding());
        }
    }

    @Test
    public void shouldPassWhenBuildOneMandatoryRoadWhenUsingRoadBuildingCardAndThereIsOnlyOneAvailableEdge() throws PlayException, GameException {
        //GIVEN
        hex_0_0.getEdges().getTopRight().setBuilding(new Building<EdgeBuiltType>(EdgeBuiltType.ROAD, gameUser1));
        hex_0_0.getEdges().getRight().setBuilding(new Building<EdgeBuiltType>(EdgeBuiltType.ROAD, gameUser2));
        hex_0_0.getEdges().getLeft().setBuilding(new Building<EdgeBuiltType>(EdgeBuiltType.ROAD, gameUser2));
        hex_1_0.getEdges().getTopLeft().setBuilding(new Building<EdgeBuiltType>(EdgeBuiltType.ROAD, gameUser2));
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        gameUser1.setDevelopmentCards(new DevelopmentCards(0, 0, 1, 0, 0));
        gameUser1.setDevelopmentCardsReadyForUsing(new DevelopmentCards(0, 0, 1, 0, 0));
        playUtil.updateAvailableActionsForAllUsers(game);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();

        // WHEN
        String roadsCount = playService.processAction(GameUserActionCode.USE_CARD_ROAD_BUILDING, gameUser1.getUser(), "1").get("roadsCount");
        params.put("edgeId", "1");
        playService.processAction(GameUserActionCode.BUILD_ROAD, gameUser1.getUser(), "1", params);

        try {
            params.put("edgeId", "6");
            playService.processAction(GameUserActionCode.BUILD_ROAD, gameUser1.getUser(), "1", params);
            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        } finally {
            assertNotNull(game);
            assertNotNull(gameUser1);
            assertEquals(0, gameUser1.getDevelopmentCards().getRoadBuilding());
            assertTrue(game.getRoadsToBuildMandatory() == 0);
            assertEquals("1", roadsCount);

            assertNotNull(hex_0_0.getEdges().getTopLeft().getBuilding());
            assertEquals(hex_0_0.getEdges().getTopLeft().getBuilding().getBuilt(), EdgeBuiltType.ROAD);
            assertEquals(hex_0_0.getEdges().getTopLeft().getBuilding().getBuildingOwner(), gameUser1);
        }
    }

    @Test
    public void shouldFailWhenUsingRoadBuildingCardAndThereAreNoAvailableEdges() throws PlayException, GameException {
        //GIVEN
        hex_0_0.getEdges().getTopRight().setBuilding(new Building<EdgeBuiltType>(EdgeBuiltType.ROAD, gameUser1));
        hex_0_0.getEdges().getRight().setBuilding(new Building<EdgeBuiltType>(EdgeBuiltType.ROAD, gameUser2));
        hex_0_0.getEdges().getTopLeft().setBuilding(new Building<EdgeBuiltType>(EdgeBuiltType.ROAD, gameUser2));
        hex_1_0.getEdges().getTopLeft().setBuilding(new Building<EdgeBuiltType>(EdgeBuiltType.ROAD, gameUser2));
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        gameUser1.setDevelopmentCards(new DevelopmentCards(0, 0, 1, 0, 0));
        gameUser1.setDevelopmentCardsReadyForUsing(new DevelopmentCards(0, 0, 1, 0, 0));
        playUtil.updateAvailableActionsForAllUsers(game);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        try {
            //WHEN
            playService.processAction(GameUserActionCode.USE_CARD_ROAD_BUILDING, gameUser1.getUser(), "1");
            fail("PlayException with error code '" + CardUtil.ROAD_CANNOT_BE_BUILT_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(CardUtil.ROAD_CANNOT_BE_BUILT_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        } finally {
            assertNotNull(game);
            assertNotNull(gameUser1);
            assertEquals(1, gameUser1.getDevelopmentCards().getRoadBuilding());
            assertTrue(game.getRoadsToBuildMandatory() == 0);
        }
    }

    @Test
    public void shouldFailWhenDiceValueIsSevenAndUserTriesToDoAnyActionExceptMoveRobber() throws GameException, PlayException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(false);
        allowUserToThrowDice(gameUser1);
        gameUser1.setResources(new Resources(0, 0, 1, 1, 1));
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        //Should generate dices values 2 & 5
        rvg.setNextGeneratedValue(0.3);
        rvg.setNextGeneratedValue(0.8);

        playService.processAction(GameUserActionCode.THROW_DICE, gameUser1.getUser(), "1");

        try {
            // WHEN
            playService.processAction(GameUserActionCode.BUY_CARD, gameUser1.getUser(), "1");

            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldPassWhenDiceValueIsSevenAndUserTriesToMoveRobber() throws GameException, PlayException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(false);
        allowUserToThrowDice(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        //Should generate dices values 2 & 5
        rvg.setNextGeneratedValue(0.3);
        rvg.setNextGeneratedValue(0.8);

        playService.processAction(GameUserActionCode.THROW_DICE, gameUser1.getUser(), "1");

        Map<String, String> params = new HashMap<String, String>();
        params.put("hexId", Integer.toString(hex_0_0.getId()));

        playService.processAction(GameUserActionCode.MOVE_ROBBER, gameUser1.getUser(), "1", params);

        assertNotNull(game);
        assertTrue(hex_0_0.isRobbed());
        assertFalse(hex_0_1.isRobbed());
    }

    @Test
    public void shouldFailWhenMoveRobberAndHexIdIsNotInteger() throws GameException, PlayException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(false);
        allowUserToMoveRobber(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("hexId", "XXX");

        try {
            // WHEN
            playService.processAction(GameUserActionCode.MOVE_ROBBER, gameUser1.getUser(), "1", params);

            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldFailWhenMoveRobberAndHexIdDoesNotBelongToGame() throws GameException, PlayException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(false);
        allowUserToMoveRobber(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("hexId", "0");

        try {
            // WHEN
            playService.processAction(GameUserActionCode.MOVE_ROBBER, gameUser1.getUser(), "1", params);

            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldFailWhenMoveRobberAndHexIsAlreadyRobbed() throws GameException, PlayException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(false);
        allowUserToMoveRobber(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("hexId", Integer.toString(hex_0_1.getId()));

        try {
            // WHEN
            playService.processAction(GameUserActionCode.MOVE_ROBBER, gameUser1.getUser(), "1", params);

            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldFailWhenMoveRobberAndHexIsEmptyType() throws GameException, PlayException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(false);
        allowUserToMoveRobber(gameUser1);
        hex_1_0.setResourceType(HexType.EMPTY);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("hexId", Integer.toString(hex_1_0.getId()));

        try {
            // WHEN
            playService.processAction(GameUserActionCode.MOVE_ROBBER, gameUser1.getUser(), "1", params);

            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldSuccessfullyRobPlayerWhenHeIsOnlyOneRivalAtRobbedHex() throws GameException, PlayException {
        //GIVEN
        hex_0_0.getNodes().getTopRight().setBuilding(new Building<NodeBuiltType>(NodeBuiltType.SETTLEMENT, gameUser2));
        gameUser2.setResources(new Resources(0, 0, 1, 0, 0));
        gameUser2.setAchievements(new Achievements(0, 1, 0, 0, 0));
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        allowUserToMoveRobber(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("hexId", Integer.toString(hex_0_0.getId()));

        playService.processAction(GameUserActionCode.MOVE_ROBBER, gameUser1.getUser(), "1", params);

        assertNotNull(game);
        assertEquals(1, gameUser1.getResources().getSheep());
        assertEquals(0, gameUser2.getResources().getSheep());
    }

    @Test
    public void shouldSuccessfullyRobPlayerWhenHeIsOnlyOneRivalAtRobbedHexAndUserHasHisOwnBuildingAtRobbedHex() throws GameException, PlayException {
        //GIVEN
        hex_0_0.getNodes().getTopRight().setBuilding(new Building<NodeBuiltType>(NodeBuiltType.SETTLEMENT, gameUser2));
        hex_0_0.getNodes().getTopLeft().setBuilding(new Building<NodeBuiltType>(NodeBuiltType.SETTLEMENT, gameUser1));
        gameUser2.setResources(new Resources(0, 0, 1, 0, 0));
        gameUser2.setAchievements(new Achievements(0, 1, 0, 0, 0));
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        allowUserToMoveRobber(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("hexId", Integer.toString(hex_0_0.getId()));

        playService.processAction(GameUserActionCode.MOVE_ROBBER, gameUser1.getUser(), "1", params);

        assertNotNull(game);
        assertEquals(1, gameUser1.getResources().getSheep());
        assertEquals(0, gameUser2.getResources().getSheep());
    }

    @Test
    public void shouldSuccessfullyChosePlayerToRobWhenThereAreSeveralRivalsAtRobbedHex() throws GameException, PlayException {
        //GIVEN
        hex_0_0.getNodes().getTopRight().setBuilding(new Building<NodeBuiltType>(NodeBuiltType.SETTLEMENT, gameUser2));
        hex_0_0.getNodes().getTopLeft().setBuilding(new Building<NodeBuiltType>(NodeBuiltType.SETTLEMENT, gameUser3));
        gameUser2.setResources(new Resources(0, 0, 1, 0, 0));
        gameUser2.setAchievements(new Achievements(0, 1, 0, 0, 0));
        gameUser3.setResources(new Resources(1, 0, 0, 0, 0));
        gameUser3.setAchievements(new Achievements(0, 1, 0, 0, 0));
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        allowUserToMoveRobber(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("hexId", Integer.toString(hex_0_0.getId()));

        playService.processAction(GameUserActionCode.MOVE_ROBBER, gameUser1.getUser(), "1", params);

        params.put("gameUserId", Integer.toString(gameUser3.getGameUserId()));
        playService.processAction(GameUserActionCode.CHOOSE_PLAYER_TO_ROB, gameUser1.getUser(), "1", params);

        assertNotNull(game);
        assertEquals(1, gameUser1.getResources().getBrick());
        assertEquals(1, gameUser2.getResources().getSheep());
        assertEquals(0, gameUser2.getResources().getBrick());
    }

    @Test
    public void shouldSuccessfullyChosePlayerToRobEvenIfHasNoResources() throws GameException, PlayException {
        //GIVEN
        hex_0_0.getNodes().getTopRight().setBuilding(new Building<NodeBuiltType>(NodeBuiltType.SETTLEMENT, gameUser2));
        hex_0_0.getNodes().getTopLeft().setBuilding(new Building<NodeBuiltType>(NodeBuiltType.SETTLEMENT, gameUser3));
        gameUser2.setResources(new Resources(0, 0, 1, 0, 0));
        gameUser2.setAchievements(new Achievements(0, 1, 0, 0, 0));
        gameUser3.setResources(new Resources(0, 0, 0, 0, 0));
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        allowUserToMoveRobber(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("hexId", Integer.toString(hex_0_0.getId()));

        playService.processAction(GameUserActionCode.MOVE_ROBBER, gameUser1.getUser(), "1", params);

        params.put("gameUserId", Integer.toString(gameUser3.getGameUserId()));
        playService.processAction(GameUserActionCode.CHOOSE_PLAYER_TO_ROB, gameUser1.getUser(), "1", params);

        assertNotNull(game);
        assertEquals(0, gameUser1.getResources().getBrick());
        assertEquals(0, gameUser1.getResources().getWood());
        assertEquals(0, gameUser1.getResources().getSheep());
        assertEquals(0, gameUser1.getResources().getWheat());
        assertEquals(0, gameUser1.getResources().getStone());

        assertEquals(1, gameUser2.getResources().getSheep());

        assertEquals(0, gameUser3.getResources().getBrick());
        assertEquals(0, gameUser3.getResources().getWood());
        assertEquals(0, gameUser3.getResources().getSheep());
        assertEquals(0, gameUser3.getResources().getWheat());
        assertEquals(0, gameUser3.getResources().getStone());
    }

    @Test
    public void shouldFailWhenUserRobHimself() throws GameException, PlayException {
        //GIVEN
        hex_0_0.getNodes().getTopRight().setBuilding(new Building<NodeBuiltType>(NodeBuiltType.SETTLEMENT, gameUser2));
        hex_0_0.getNodes().getTopLeft().setBuilding(new Building<NodeBuiltType>(NodeBuiltType.SETTLEMENT, gameUser3));
        gameUser2.setResources(new Resources(0, 0, 1, 0, 0));
        gameUser2.setAchievements(new Achievements(0, 1, 0, 0, 0));
        gameUser3.setResources(new Resources(1, 0, 0, 0, 0));
        gameUser3.setAchievements(new Achievements(0, 1, 0, 0, 0));
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        allowUserToMoveRobber(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("hexId", Integer.toString(hex_0_0.getId()));

        playService.processAction(GameUserActionCode.MOVE_ROBBER, gameUser1.getUser(), "1", params);

        params.put("gameUserId", Integer.toString(gameUser1.getGameUserId()));
        try {
            // WHEN
            playService.processAction(GameUserActionCode.CHOOSE_PLAYER_TO_ROB, gameUser1.getUser(), "1", params);

            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldFailWhenUserRobPlayerThatDoesNotHaveBuildingAtRobbedHex() throws GameException, PlayException {
        //GIVEN
        hex_0_0.getNodes().getTopRight().setBuilding(new Building<NodeBuiltType>(NodeBuiltType.SETTLEMENT, gameUser2));
        hex_0_0.getNodes().getTopLeft().setBuilding(new Building<NodeBuiltType>(NodeBuiltType.SETTLEMENT, gameUser3));
        hex_1_0.getNodes().getTopRight().setBuilding(new Building<NodeBuiltType>(NodeBuiltType.SETTLEMENT, gameUser4));
        gameUser2.setResources(new Resources(0, 0, 1, 0, 0));
        gameUser2.setAchievements(new Achievements(0, 1, 0, 0, 0));
        gameUser3.setResources(new Resources(1, 0, 0, 0, 0));
        gameUser3.setAchievements(new Achievements(0, 1, 0, 0, 0));
        gameUser4.setResources(new Resources(1, 0, 0, 0, 0));
        gameUser4.setAchievements(new Achievements(0, 1, 0, 0, 0));
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        allowUserToMoveRobber(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("hexId", Integer.toString(hex_0_0.getId()));

        playService.processAction(GameUserActionCode.MOVE_ROBBER, gameUser1.getUser(), "1", params);

        params.put("gameUserId", Integer.toString(gameUser4.getGameUserId()));
        try {
            // WHEN
            playService.processAction(GameUserActionCode.CHOOSE_PLAYER_TO_ROB, gameUser1.getUser(), "1", params);

            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldFailToRobPlayerIfGameUserIdIsNotInteger() throws GameException, PlayException {
        //GIVEN
        hex_0_0.getNodes().getTopRight().setBuilding(new Building<NodeBuiltType>(NodeBuiltType.SETTLEMENT, gameUser2));
        hex_0_0.getNodes().getTopLeft().setBuilding(new Building<NodeBuiltType>(NodeBuiltType.SETTLEMENT, gameUser3));
        gameUser2.setResources(new Resources(0, 0, 1, 0, 0));
        gameUser2.setAchievements(new Achievements(0, 1, 0, 0, 0));
        gameUser3.setResources(new Resources(1, 0, 0, 0, 0));
        gameUser3.setAchievements(new Achievements(0, 1, 0, 0, 0));
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        allowUserToMoveRobber(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("hexId", Integer.toString(hex_0_0.getId()));

        playService.processAction(GameUserActionCode.MOVE_ROBBER, gameUser1.getUser(), "1", params);

        params.put("gameUserId", "XXX");
        try {
            // WHEN
            playService.processAction(GameUserActionCode.CHOOSE_PLAYER_TO_ROB, gameUser1.getUser(), "1", params);

            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (GameException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldFailToRobPlayerIfGameUserDoesNotBelongToGame() throws GameException, PlayException {
        //GIVEN
        hex_0_0.getNodes().getTopRight().setBuilding(new Building<NodeBuiltType>(NodeBuiltType.SETTLEMENT, gameUser2));
        hex_0_0.getNodes().getTopLeft().setBuilding(new Building<NodeBuiltType>(NodeBuiltType.SETTLEMENT, gameUser3));
        gameUser2.setResources(new Resources(0, 0, 1, 0, 0));
        gameUser2.setAchievements(new Achievements(0, 1, 0, 0, 0));
        gameUser3.setResources(new Resources(1, 0, 0, 0, 0));
        gameUser3.setAchievements(new Achievements(0, 1, 0, 0, 0));
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        allowUserToMoveRobber(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("hexId", Integer.toString(hex_0_0.getId()));

        playService.processAction(GameUserActionCode.MOVE_ROBBER, gameUser1.getUser(), "1", params);

        params.put("gameUserId", "-1");
        try {
            // WHEN
            playService.processAction(GameUserActionCode.CHOOSE_PLAYER_TO_ROB, gameUser1.getUser(), "1", params);

            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (GameException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldFailWhenDiceValueIsSevenAndUserMoveRobberBeforeAllPlayersKickOffHalfOfTheirResources() throws GameException, PlayException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(false);
        gameUser2.setResources(new Resources(10, 0, 0, 0, 0));
        gameUser2.setAchievements(new Achievements(0, 10, 0, 0, 0));
        allowUserToThrowDice(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        //Should generate dices values 2 & 5
        rvg.setNextGeneratedValue(0.3);
        rvg.setNextGeneratedValue(0.8);

        playService.processAction(GameUserActionCode.THROW_DICE, gameUser1.getUser(), "1");

        Map<String, String> params = new HashMap<String, String>();
        params.put("hexId", Integer.toString(hex_1_0.getId()));

        try {
            // WHEN
            playService.processAction(GameUserActionCode.MOVE_ROBBER, gameUser1.getUser(), "1", params);

            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldPassWhenDiceValueIsSevenAndUsersKickOffHalfOfTheirResources() throws GameException, PlayException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(false);
        gameUser1.setResources(new Resources(9, 0, 0, 0, 0));
        gameUser1.setAchievements(new Achievements(0, 9, 0, 0, 0));
        gameUser2.setResources(new Resources(5, 0, 0, 0, 5));
        gameUser2.setAchievements(new Achievements(0, 10, 0, 0, 0));
        allowUserToThrowDice(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        //Should generate dices values 2 & 5
        rvg.setNextGeneratedValue(0.3);
        rvg.setNextGeneratedValue(0.8);

        playService.processAction(GameUserActionCode.THROW_DICE, gameUser1.getUser(), "1");

        Map<String, String> params = new HashMap<String, String>();
        params.put("brick", "4");
        params.put("wood", "0");
        params.put("sheep", "0");
        params.put("wheat", "0");
        params.put("stone", "0");

        playService.processAction(GameUserActionCode.KICK_OFF_RESOURCES, gameUser1.getUser(), "1", params);

        params.put("brick", "3");
        params.put("stone", "2");
        playService.processAction(GameUserActionCode.KICK_OFF_RESOURCES, gameUser2.getUser(), "1", params);

        assertNotNull(game);
        assertNotNull(gameUser1);
        assertEquals(5, gameUser1.getResources().getBrick());
        assertEquals(0, gameUser1.getResources().getWood());
        assertEquals(0, gameUser1.getResources().getSheep());
        assertEquals(0, gameUser1.getResources().getWheat());
        assertEquals(0, gameUser1.getResources().getStone());

        assertNotNull(gameUser2);
        assertEquals(2, gameUser2.getResources().getBrick());
        assertEquals(0, gameUser2.getResources().getWood());
        assertEquals(0, gameUser2.getResources().getSheep());
        assertEquals(0, gameUser2.getResources().getWheat());
        assertEquals(3, gameUser2.getResources().getStone());
    }

    @Test
    public void shouldPassWhenDiceValueIsSevenAndUserMoveRobberAfterAllPlayersKickOffHalfOfTheirResources() throws GameException, PlayException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(false);
        gameUser1.setResources(new Resources(10, 0, 0, 0, 0));
        gameUser1.setAchievements(new Achievements(0, 10, 0, 0, 0));
        gameUser2.setResources(new Resources(10, 0, 0, 0, 0));
        gameUser2.setAchievements(new Achievements(0, 10, 0, 0, 0));
        allowUserToThrowDice(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        //Should generate dices values 2 & 5
        rvg.setNextGeneratedValue(0.3);
        rvg.setNextGeneratedValue(0.8);

        playService.processAction(GameUserActionCode.THROW_DICE, gameUser1.getUser(), "1");

        Map<String, String> params = new HashMap<String, String>();
        params.put("brick", "5");
        params.put("wood", "0");
        params.put("sheep", "0");
        params.put("wheat", "0");
        params.put("stone", "0");

        playService.processAction(GameUserActionCode.KICK_OFF_RESOURCES, gameUser1.getUser(), "1", params);

        playService.processAction(GameUserActionCode.KICK_OFF_RESOURCES, gameUser2.getUser(), "1", params);


        params.put("hexId", Integer.toString(hex_1_0.getId()));
        playService.processAction(GameUserActionCode.MOVE_ROBBER, gameUser1.getUser(), "1", params);

        assertNotNull(game);
        assertTrue(hex_1_0.isRobbed());
    }

    @Test
    public void shouldFailWhenUserKickOffNotHalfOfHisResources() throws GameException, PlayException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(false);
        gameUser1.setResources(new Resources(10, 0, 0, 0, 0));
        gameUser1.setAchievements(new Achievements(0, 10, 0, 0, 0));
        allowUserKickOffResources(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("brick", "4");
        params.put("wood", "0");
        params.put("sheep", "0");
        params.put("wheat", "0");
        params.put("stone", "0");

        try {
            // WHEN
            playService.processAction(GameUserActionCode.KICK_OFF_RESOURCES, gameUser1.getUser(), "1", params);

            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldFailWhenUserKickOffResourcesButQuantityIsNotInteger() throws GameException, PlayException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(false);
        gameUser1.setResources(new Resources(10, 0, 0, 0, 0));
        gameUser1.setAchievements(new Achievements(0, 10, 0, 0, 0));
        allowUserKickOffResources(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("brick", "5");
        params.put("wood", "0");
        params.put("sheep", "XXX");
        params.put("wheat", "0");
        params.put("stone", "0");

        try {
            // WHEN
            playService.processAction(GameUserActionCode.KICK_OFF_RESOURCES, gameUser1.getUser(), "1", params);

            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldFailWhenUserKickOffResourcesButQuantityIsBelowZero() throws GameException, PlayException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(false);
        gameUser1.setResources(new Resources(10, 0, 0, 0, 0));
        gameUser1.setAchievements(new Achievements(0, 10, 0, 0, 0));
        allowUserKickOffResources(gameUser1);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("brick", "6");
        params.put("wood", "-1");
        params.put("sheep", "0");
        params.put("wheat", "0");
        params.put("stone", "0");

        try {
            // WHEN
            playService.processAction(GameUserActionCode.KICK_OFF_RESOURCES, gameUser1.getUser(), "1", params);

            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldPassWhenUserMakesTradePropose() throws GameException, PlayException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        gameUser1.setResources(new Resources(1, 0, 0, 0, 0));
        gameUser2.setResources(new Resources(0, 1, 0, 0, 0));
        playUtil.updateAvailableActionsForAllUsers(game);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("brick", "-1");
        params.put("wood", "1");
        params.put("sheep", "0");
        params.put("wheat", "0");
        params.put("stone", "0");

        playService.processAction(GameUserActionCode.TRADE_PROPOSE, gameUser1.getUser(), "1", params);

        assertNotNull(game);
        assertTrue(gameUser2.isAvailableTradeReply());
        assertNotNull(game.getTradeProposal());
        assertEquals(-1, game.getTradeProposal().getBrick());
        assertEquals(1, game.getTradeProposal().getWood());
        assertEquals(0, game.getTradeProposal().getSheep());
        assertEquals(0, game.getTradeProposal().getWheat());
        assertEquals(0, game.getTradeProposal().getStone());
        assertNotNull(game.getTradeProposal().getOfferId());
    }

    @Test
    public void shouldFailWhenUserMakesTradeProposeWithoutSellingAnyResource() throws GameException, PlayException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        gameUser1.setResources(new Resources(1, 0, 0, 0, 0));
        gameUser2.setResources(new Resources(0, 1, 0, 0, 0));
        playUtil.updateAvailableActionsForAllUsers(game);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("brick", "0");
        params.put("wood", "1");
        params.put("sheep", "0");
        params.put("wheat", "0");
        params.put("stone", "0");

        try {
            // WHEN
            playService.processAction(GameUserActionCode.TRADE_PROPOSE, gameUser1.getUser(), "1", params);

            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldFailWhenUserMakesTradeProposeWithoutBuyingAnyResource() throws GameException, PlayException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        gameUser1.setResources(new Resources(1, 0, 0, 0, 0));
        gameUser2.setResources(new Resources(0, 1, 0, 0, 0));
        playUtil.updateAvailableActionsForAllUsers(game);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("brick", "-1");
        params.put("wood", "0");
        params.put("sheep", "0");
        params.put("wheat", "0");
        params.put("stone", "0");

        try {
            // WHEN
            playService.processAction(GameUserActionCode.TRADE_PROPOSE, gameUser1.getUser(), "1", params);

            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldFailWhenUserMakesTradeProposeButParametersAreNotInteger() throws GameException, PlayException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        gameUser1.setResources(new Resources(1, 0, 0, 0, 0));
        gameUser2.setResources(new Resources(0, 1, 0, 0, 0));
        playUtil.updateAvailableActionsForAllUsers(game);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("brick", "XXX");
        params.put("wood", "XXX");
        params.put("sheep", "0");
        params.put("wheat", "0");
        params.put("stone", "0");

        try {
            // WHEN
            playService.processAction(GameUserActionCode.TRADE_PROPOSE, gameUser1.getUser(), "1", params);

            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldFailWhenUserMakesTradeProposeButQuantityOfResourceToSellIsMoreThenUserHas() throws GameException, PlayException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        gameUser1.setResources(new Resources(1, 0, 0, 0, 0));
        gameUser2.setResources(new Resources(0, 1, 0, 0, 0));
        playUtil.updateAvailableActionsForAllUsers(game);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("brick", "-2");
        params.put("wood", "1");
        params.put("sheep", "0");
        params.put("wheat", "0");
        params.put("stone", "0");

        try {
            // WHEN
            playService.processAction(GameUserActionCode.TRADE_PROPOSE, gameUser1.getUser(), "1", params);

            fail("PlayException with error code '" + PlayServiceImpl.ERROR_CODE_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.ERROR_CODE_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
    }

    @Test
    public void shouldPassWhenUserAcceptsTradeProposition() throws GameException, PlayException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        gameUser1.setResources(new Resources(1, 0, 0, 0, 0));
        gameUser2.setResources(new Resources(0, 1, 0, 0, 0));
        playUtil.updateAvailableActionsForAllUsers(game);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("brick", "-1");
        params.put("wood", "1");
        params.put("sheep", "0");
        params.put("wheat", "0");
        params.put("stone", "0");

        playService.processAction(GameUserActionCode.TRADE_PROPOSE, gameUser1.getUser(), "1", params);

        params.put("tradeReply", "accept");

        playService.processAction(GameUserActionCode.TRADE_REPLY, gameUser2.getUser(), "1", params);

        assertNotNull(game);
        assertFalse(gameUser2.isAvailableTradeReply());
        assertNotNull(game.getTradeProposal());
        assertNull(game.getTradeProposal().getOfferId());

        assertEquals(0, gameUser1.getResources().getBrick());
        assertEquals(1, gameUser1.getResources().getWood());
        assertEquals(0, gameUser1.getResources().getSheep());
        assertEquals(0, gameUser1.getResources().getWheat());
        assertEquals(0, gameUser1.getResources().getStone());

        assertEquals(1, gameUser2.getResources().getBrick());
        assertEquals(0, gameUser2.getResources().getWood());
        assertEquals(0, gameUser2.getResources().getSheep());
        assertEquals(0, gameUser2.getResources().getWheat());
        assertEquals(0, gameUser2.getResources().getStone());
    }

    @Test
    public void shouldPassWhenUserDeclinesTradeProposition() throws GameException, PlayException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        gameUser1.setResources(new Resources(1, 0, 0, 0, 0));
        gameUser2.setResources(new Resources(0, 1, 0, 0, 0));
        playUtil.updateAvailableActionsForAllUsers(game);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("brick", "-1");
        params.put("wood", "1");
        params.put("sheep", "0");
        params.put("wheat", "0");
        params.put("stone", "0");

        playService.processAction(GameUserActionCode.TRADE_PROPOSE, gameUser1.getUser(), "1", params);

        params.put("tradeReply", "decline");

        playService.processAction(GameUserActionCode.TRADE_REPLY, gameUser2.getUser(), "1", params);

        assertNotNull(game);
        assertFalse(gameUser2.isAvailableTradeReply());
        assertNotNull(game.getTradeProposal());
        assertNotNull(game.getTradeProposal().getOfferId());

        assertEquals(1, gameUser1.getResources().getBrick());
        assertEquals(0, gameUser1.getResources().getWood());
        assertEquals(0, gameUser1.getResources().getSheep());
        assertEquals(0, gameUser1.getResources().getWheat());
        assertEquals(0, gameUser1.getResources().getStone());

        assertEquals(0, gameUser2.getResources().getBrick());
        assertEquals(1, gameUser2.getResources().getWood());
        assertEquals(0, gameUser2.getResources().getSheep());
        assertEquals(0, gameUser2.getResources().getWheat());
        assertEquals(0, gameUser2.getResources().getStone());
    }

    @Test
    public void shouldFailWhenUserAcceptsAlreadyAcceptedTradeProposition() throws GameException, PlayException {
        //GIVEN
        game.setCurrentMove(gameUser1.getMoveOrder());
        game.setCurrentCycleBuildingNumber(null);
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(true);
        gameUser1.setResources(new Resources(1, 0, 0, 0, 0));
        gameUser2.setResources(new Resources(0, 1, 0, 0, 0));
        gameUser3.setResources(new Resources(0, 1, 0, 0, 0));
        playUtil.updateAvailableActionsForAllUsers(game);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        Map<String, String> params = new HashMap<String, String>();
        params.put("brick", "-1");
        params.put("wood", "1");
        params.put("sheep", "0");
        params.put("wheat", "0");
        params.put("stone", "0");

        playService.processAction(GameUserActionCode.TRADE_PROPOSE, gameUser1.getUser(), "1", params);

        params.put("tradeReply", "accept");

        playService.processAction(GameUserActionCode.TRADE_REPLY, gameUser2.getUser(), "1", params);

        try {
            // WHEN
            playService.processAction(GameUserActionCode.TRADE_REPLY, gameUser3.getUser(), "1", params);

            fail("PlayException with error code '" + PlayServiceImpl.OFFER_ALREADY_ACCEPTED_ERROR + "' should be thrown");
        } catch (PlayException e) {
            // THEN
            assertEquals(PlayServiceImpl.OFFER_ALREADY_ACCEPTED_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        } finally {
            assertFalse(gameUser2.isAvailableTradeReply());
            assertFalse(gameUser3.isAvailableTradeReply());
            assertNotNull(game.getTradeProposal());
            assertNull(game.getTradeProposal().getOfferId());

            assertEquals(0, gameUser1.getResources().getBrick());
            assertEquals(1, gameUser1.getResources().getWood());
            assertEquals(0, gameUser1.getResources().getSheep());
            assertEquals(0, gameUser1.getResources().getWheat());
            assertEquals(0, gameUser1.getResources().getStone());

            assertEquals(1, gameUser2.getResources().getBrick());
            assertEquals(0, gameUser2.getResources().getWood());
            assertEquals(0, gameUser2.getResources().getSheep());
            assertEquals(0, gameUser2.getResources().getWheat());
            assertEquals(0, gameUser2.getResources().getStone());

            assertEquals(0, gameUser3.getResources().getBrick());
            assertEquals(1, gameUser3.getResources().getWood());
            assertEquals(0, gameUser3.getResources().getSheep());
            assertEquals(0, gameUser3.getResources().getWheat());
            assertEquals(0, gameUser3.getResources().getStone());
        }
    }
    
    private void allowUserKickOffResources(GameUserBean user) {
        allowUserAction(user, new Action(GameUserActionCode.KICK_OFF_RESOURCES));
    }

    private void allowUserToMoveRobber(GameUserBean user) {
        allowUserAction(user, new Action(GameUserActionCode.MOVE_ROBBER));
    }

    private void allowUserToUseCardMonopoly(GameUserBean user) {
        allowUserAction(user, new Action(GameUserActionCode.USE_CARD_MONOPOLY));
    }
    
    private void allowUserToUseCardYearOfPlenty(GameUserBean user) {
        allowUserAction(user, new Action(GameUserActionCode.USE_CARD_YEAR_OF_PLENTY));
    }

    private void allowUserToUseCardRoadBuilding(GameUserBean user) {
        allowUserAction(user, new Action(GameUserActionCode.USE_CARD_ROAD_BUILDING));
    }

    private void allowUserToBuyCard(GameUserBean user) {
        allowUserAction(user, new Action(GameUserActionCode.BUY_CARD));
    }

    private void allowUserToThrowDice(GameUserBean user) {
        allowUserAction(user, new Action(GameUserActionCode.THROW_DICE));
    }

    private void allowUserToBuildCity(GameUserBean user) {
        allowUserAction(user, new Action(GameUserActionCode.BUILD_CITY));
    }

    private void allowUserToBuildSettlement(GameUserBean user) {
        allowUserAction(user, new Action(GameUserActionCode.BUILD_SETTLEMENT));
    }

    private void allowUserAction(GameUserBean user, Action actionToAllow) {
        List<Action> actionsList = new ArrayList<Action>();
        actionsList.add(actionToAllow);

        AvailableActions availableActions = new AvailableActions();
        availableActions.setList(actionsList);
        availableActions.setIsMandatory(true);

        String availableActionsString = GSON.toJson(availableActions, AvailableActions.class);
        user.setAvailableActions(availableActionsString);
    }

    private void buildClearTriangleMapAndSetAlreadyPlayingGame() throws GameException {
        // GIVEN
        game = new GameBean();

        UserBean user1 = new UserBean(USER_NAME1, PASSWORD1, false);
        UserBean user2 = new UserBean(USER_NAME2, PASSWORD2, false);
        UserBean user3 = new UserBean(USER_NAME3, PASSWORD3, false);
        UserBean user4 = new UserBean(USER_NAME4, PASSWORD4, false);

        gameUser1 = new GameUserBean(user1, 1, game);
        gameUser2 = new GameUserBean(user2, 2, game);
        gameUser3 = new GameUserBean(user3, 3, game);
        gameUser4 = new GameUserBean(user4, 4, game);

        hex_0_0 = new HexBean(game, new Coordinates(0, 0), HexType.BRICK, 2, false);
        hex_1_0 = new HexBean(game, new Coordinates(1, 0), HexType.WOOD, 10, false);
        hex_0_1 = new HexBean(game, new Coordinates(0, 1), HexType.STONE, 11, true);

        NodeBean node_1_1 = new NodeBean(game, NodePortType.ANY);
        NodeBean node_1_2 = new NodeBean(game, NodePortType.NONE);
        NodeBean node_1_3 = new NodeBean(game, NodePortType.NONE);
        NodeBean node_1_4 = new NodeBean(game, NodePortType.NONE);
        NodeBean node_1_5 = new NodeBean(game, NodePortType.NONE);
        NodeBean node_1_6 = new NodeBean(game, NodePortType.NONE);
        NodeBean node_2_2 = new NodeBean(game, NodePortType.NONE);
        NodeBean node_2_3 = new NodeBean(game, NodePortType.STONE);
        NodeBean node_2_4 = new NodeBean(game, NodePortType.NONE);
        NodeBean node_2_5 = new NodeBean(game, NodePortType.NONE);
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
        gameUser1.setKickingOffResourcesMandatory(false);
        gameUser1.setAvailableTradeReply(false);

        gameUser2.setGameUserId(2);
        gameUser2.setMoveOrder(2);
        gameUser2.setReady(true);
        gameUser2.setKickingOffResourcesMandatory(false);
        gameUser2.setAvailableTradeReply(false);

        gameUser3.setGameUserId(3);
        gameUser3.setMoveOrder(3);
        gameUser3.setReady(true);
        gameUser3.setKickingOffResourcesMandatory(false);
        gameUser3.setAvailableTradeReply(false);

        gameUser4.setGameUserId(4);
        gameUser4.setMoveOrder(4);
        gameUser4.setReady(true);
        gameUser4.setKickingOffResourcesMandatory(false);
        gameUser4.setAvailableTradeReply(false);


        //
        //   / \ / \
        //  |0,0|1,0|
        //   \ / \ /                    2.          7.
        //    |0,1|                    /   \       /   \
        //     \ /                    /1   2\     /7   8\
        //                        1. /       \ .3/       \ .8
        //       2                 |           |           |
        //    1 / \ 3              |6  (0,0)  3|   (1,0)  9|
        //     |   |               |           |           |
        //    6 \ / 4             6' \       / '4\       / '9
        //       5                    \5   4/     \11 10/
        //                             \   /       \   /
        //     1/ \2                    5'           '10
        //    6|   |3                    |           |
        //     5\ /4                     |15 (0,1) 12|
        //                               |           |
        //                             13' \       / '11
        //                                  \14 13/
        //                                   \   /
        //                                     '12
        //
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
        hex_1_0.getNodes().setTopLeft(node_1_3);
        hex_1_0.getNodes().setTop(node_2_2);
        hex_1_0.getNodes().setTopRight(node_2_3);
        hex_1_0.getNodes().setBottomRight(node_2_4);
        hex_1_0.getNodes().setBottom(node_2_5);
        hex_1_0.getNodes().setBottomLeft(node_1_4);
        hex_1_0.getEdges().setTopLeft(edge_2_1);
        hex_1_0.getEdges().setTopRight(edge_2_2);
        hex_1_0.getEdges().setRight(edge_2_3);
        hex_1_0.getEdges().setBottomRight(edge_2_4);
        hex_1_0.getEdges().setBottomLeft(edge_2_5);
        hex_1_0.getEdges().setLeft(edge_1_3);
        hex_0_1.setId(3);
        hex_0_1.getNodes().setTopLeft(node_1_5);
        hex_0_1.getNodes().setTop(node_1_4);
        hex_0_1.getNodes().setTopRight(node_2_5);
        hex_0_1.getNodes().setBottomRight(node_3_4);
        hex_0_1.getNodes().setBottom(node_3_5);
        hex_0_1.getNodes().setBottomLeft(node_3_6);
        hex_0_1.getEdges().setTopLeft(edge_1_4);
        hex_0_1.getEdges().setTopRight(edge_2_5);
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
        node_2_2.setId(7);
        node_2_2.setOrientation(NodeOrientationType.SINGLE_BOTTOM);
        node_2_2.getHexes().setBottom(hex_1_0);
        node_2_2.getEdges().setBottomLeft(edge_2_1);
        node_2_2.getEdges().setBottomRight(edge_2_2);

        node_2_3.setId(8);
        node_2_3.setOrientation(NodeOrientationType.SINGLE_TOP);
        node_2_3.getHexes().setBottomLeft(hex_1_0);
        node_2_3.getEdges().setTopLeft(edge_2_2);
        node_2_3.getEdges().setBottom(edge_2_3);

        node_2_4.setId(9);
        node_2_4.setOrientation(NodeOrientationType.SINGLE_BOTTOM);
        node_2_4.getHexes().setTopLeft(hex_1_0);
        node_2_4.getEdges().setTop(edge_2_3);
        node_2_4.getEdges().setBottomLeft(edge_2_4);

        node_2_5.setId(10);
        node_2_5.setOrientation(NodeOrientationType.SINGLE_TOP);
        node_2_5.getHexes().setTop(hex_1_0);
        node_2_5.getHexes().setBottomLeft(hex_0_1);
        node_2_5.getEdges().setTopRight(edge_2_4);
        node_2_5.getEdges().setTopLeft(edge_2_5);

        // Nodes of Hex 0,1
        node_3_4.setId(11);
        node_3_4.setOrientation(NodeOrientationType.SINGLE_BOTTOM);
        node_3_4.getHexes().setTopLeft(hex_0_1);
        node_3_4.getEdges().setTop(edge_3_3);
        node_3_4.getEdges().setBottomLeft(edge_3_4);

        node_3_5.setId(12);
        node_3_5.setOrientation(NodeOrientationType.SINGLE_TOP);
        node_3_5.getHexes().setTop(hex_0_1);
        node_3_5.getEdges().setTopRight(edge_3_4);
        node_3_5.getEdges().setTopLeft(edge_3_5);

        node_3_6.setId(13);
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
        edge_2_1.getNodes().setBottomLeft(node_1_3);
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
        edge_2_5.getNodes().setTopLeft(node_1_4);

        // Edges of Hex 0,1
        edge_3_3.setId(12);
        edge_3_3.setOrientation(EdgeOrientationType.VERTICAL);
        edge_3_3.getHexes().setLeft(hex_0_1);
        edge_3_3.getNodes().setTop(node_2_3);
        edge_3_3.getNodes().setBottom(node_2_4);

        edge_3_4.setId(13);
        edge_3_4.setOrientation(EdgeOrientationType.BOTTOM_LEFT);
        edge_3_4.getHexes().setTopLeft(hex_0_1);
        edge_3_4.getNodes().setTopRight(node_2_4);
        edge_3_4.getNodes().setBottomLeft(node_2_5);

        edge_3_5.setId(14);
        edge_3_5.setOrientation(EdgeOrientationType.BOTTOM_RIGHT);
        edge_3_5.getHexes().setTopRight(hex_0_1);
        edge_3_5.getNodes().setBottomRight(node_2_5);
        edge_3_5.getNodes().setTopLeft(node_1_4);

        edge_3_6.setId(15);
        edge_3_6.setOrientation(EdgeOrientationType.VERTICAL);
        edge_3_6.getHexes().setRight(hex_0_1);
        edge_3_6.getNodes().setBottom(node_1_4);
        edge_3_6.getNodes().setTop(node_1_3);

        game.setGameId(1);
        game.setCreator(user1);
        game.setStatus(GameStatus.PLAYING);
        game.setStage(GameStage.PREPARATION);
        game.setPreparationCycle(1);
        game.setCurrentCycleBuildingNumber(1);
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
                node_2_2, node_2_3, node_2_4, node_2_5,
                node_3_4, node_3_5, node_3_6));
        game.getEdges().addAll(Arrays.asList(
                edge_1_1, edge_1_2, edge_1_3, edge_1_4, edge_1_5, edge_1_6,
                edge_2_1, edge_2_2, edge_2_3, edge_2_4, edge_2_5,
                edge_3_3, edge_3_4, edge_3_5, edge_3_6));
        game.setInitialBuildingsSet("[[BUILD_SETTLEMENT, BUILD_ROAD], [BUILD_SETTLEMENT, BUILD_ROAD]]");
        game.setAvailableDevelopmentCards(new DevelopmentCards(14, 5, 2, 2, 2));
        game.setDevelopmentCardUsed(false);
        game.setRobberShouldBeMovedMandatory(false);
        game.setChoosePlayerToRobMandatory(false);
        game.setRoadsToBuildMandatory(0);
        playUtil.updateAvailableActionsForAllUsers(game);
    }
}