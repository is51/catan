package catan.services.impl;

import catan.dao.GameDao;
import catan.domain.exception.GameException;
import catan.domain.exception.PlayException;
import catan.domain.model.dashboard.*;
import catan.domain.model.dashboard.types.*;
import catan.domain.model.game.AvailableDevelopmentCards;
import catan.domain.model.game.GameBean;
import catan.domain.model.game.GameUserBean;
import catan.domain.model.game.actions.Action;
import catan.domain.model.game.actions.AvailableActions;
import catan.domain.model.game.types.GameStage;
import catan.domain.model.game.types.GameStatus;
import catan.domain.model.game.types.GameUserActionCode;
import catan.domain.model.user.UserBean;
import catan.services.util.game.GameUtil;
import catan.services.util.play.BuildUtil;
import catan.services.util.play.MainStageUtil;
import catan.services.util.play.PlayUtil;
import catan.services.util.play.PreparationStageUtil;
import catan.services.util.random.RandomUtil;
import com.google.gson.Gson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.*;
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
    private PreparationStageUtil preparationStageUtil;
    @InjectMocks
    private MainStageUtil mainStageUtil;

    private GameBean game;
    private HexBean hex_0_0;
    private HexBean hex_1_0;
    private GameUserBean gameUser1;
    private GameUserBean gameUser2;

    private static final Gson GSON = new Gson();


    @Before
    public void setUp() throws GameException {
        buildUtil.setGameUtil(gameUtil);

        playService.setRandomUtil(randomUtil);
        playService.setGameUtil(gameUtil);
        playService.setPlayUtil(playUtil);
        playService.setBuildUtil(buildUtil);
        playService.setPreparationStageUtil(preparationStageUtil);

        playUtil.setMainStageUtil(mainStageUtil);
        playUtil.setPreparationStageUtil(preparationStageUtil);

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
        HashMap<String, String> params = new HashMap<String, String>();
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
        game.setCurrentCycleBuildingNumber(2);
        playUtil.updateAvailableActionsForAllUsers(game);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        // WHEN

        HashMap<String, String> params = new HashMap<String, String>();
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

            HashMap<String, String> params = new HashMap<String, String>();
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

            HashMap<String, String> params = new HashMap<String, String>();
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
        game.setCurrentCycleBuildingNumber(2);
        playUtil.updateAvailableActionsForAllUsers(game);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        // WHEN

        HashMap<String, String> params = new HashMap<String, String>();
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
        game.setCurrentCycleBuildingNumber(2);
        playUtil.updateAvailableActionsForAllUsers(game);
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        try {
            // WHEN

            HashMap<String, String> params = new HashMap<String, String>();
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

            HashMap<String, String> params = new HashMap<String, String>();
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

            HashMap<String, String> params = new HashMap<String, String>();
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

            HashMap<String, String> params = new HashMap<String, String>();
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

            HashMap<String, String> params = new HashMap<String, String>();
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

            HashMap<String, String> params = new HashMap<String, String>();
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
    public void shouldPassWhenBuildingSettlementOnTheWayOfOtherPlayerButNearOwnNeighbourRoad() throws GameException, PlayException{
        // GIVEN
        hex_0_0.getEdges().getTopRight().setBuilding(new Building<EdgeBuiltType>(EdgeBuiltType.ROAD, gameUser2));
        hex_0_0.getEdges().getRight().setBuilding(new Building<EdgeBuiltType>(EdgeBuiltType.ROAD, gameUser2));
        hex_1_0.getEdges().getTopLeft().setBuilding(new Building<EdgeBuiltType>(EdgeBuiltType.ROAD, gameUser1));

        when(gameDao.getGameByGameId(1)).thenReturn(game);

        // WHEN

        HashMap<String, String> params = new HashMap<String, String>();
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

            HashMap<String, String> params = new HashMap<String, String>();
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

        HashMap<String, String> params = new HashMap<String, String>();
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
    public void shouldFailWhenBuildSettlementByNonActivePlayer(){
        try {
            //GIVEN
            game.setCurrentMove(gameUser2.getMoveOrder());
            playUtil.updateAvailableActionsForAllUsers(game);
            when(gameDao.getGameByGameId(1)).thenReturn(game);

            // WHEN

            HashMap<String, String> params = new HashMap<String, String>();
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

        HashMap<String, String> params = new HashMap<String, String>();
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

            HashMap<String, String> params = new HashMap<String, String>();
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

            HashMap<String, String> params = new HashMap<String, String>();
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

            HashMap<String, String> params = new HashMap<String, String>();
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
            HashMap<String, String> params = new HashMap<String, String>();
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
            HashMap<String, String> params = new HashMap<String, String>();
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
    public void shouldPassWhenBuildCityInPreparationStage() throws GameException, PlayException{
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
    public void shouldFailWhenBuildCityByNonActivePlayer(){
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
    public void shouldPassWhenBuildCityInMainStage() throws PlayException, GameException{
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

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("nodeId", "3");

        playService.processAction(GameUserActionCode.BUILD_SETTLEMENT, gameUser1.getUser(), "1", params);

        assertEquals(1, gameUser1.getAchievements().getDisplayVictoryPoints());
    }

    @Test
    public void shouldUpdateVictoryPointsOnMultipleBuildingsInPreparationStage() throws Exception {
        when(gameDao.getGameByGameId(1)).thenReturn(game);

        HashMap<String, String> params = new HashMap<String, String>();
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
        game.setStage(GameStage.MAIN);
        game.setDiceThrown(false);
        allowUserToThrowDice(gameUser1);

        when(gameDao.getGameByGameId(1)).thenReturn(game);
        Map<String, String> params = new HashMap<String, String>();
        params.put("nodeId", "3");

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
        when(gameDao.getGameByGameId(1)).thenReturn(game);
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
        game.setCurrentCycleBuildingNumber(2);
        playUtil.updateAvailableActionsForAllUsers(game);
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
            assertEquals(PlayServiceImpl.CARDS_ARE_OVER_ERROR, e.getErrorCode());
        } catch (Exception e) {
            fail("No other exceptions should be thrown");
        }
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
        GameUserBean gameUser3 = new GameUserBean(user3, 3, game);
        GameUserBean gameUser4 = new GameUserBean(user4, 4, game);

        hex_0_0 = new HexBean(game, new Coordinates(0, 0), HexType.BRICK, 2, false);
        hex_1_0 = new HexBean(game, new Coordinates(1, 0), HexType.WOOD, 10, false);
        HexBean hex_0_1 = new HexBean(game, new Coordinates(0, 1), HexType.STONE, 11, true);

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
        game.setAvailableDevelopmentCards(new AvailableDevelopmentCards(14, 5, 2, 2, 2));
        playUtil.updateAvailableActionsForAllUsers(game);
    }
}